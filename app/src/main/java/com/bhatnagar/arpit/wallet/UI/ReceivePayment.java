package com.bhatnagar.arpit.wallet.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Network.Connectivity;
import com.bhatnagar.arpit.wallet.Util.Network.RequestHandler;
import com.bhatnagar.arpit.wallet.Util.Network.ResponseCode;
import com.bhatnagar.arpit.wallet.Util.QRScanner;
import com.bhatnagar.arpit.wallet.Util.SMS;

import java.util.HashMap;
import java.util.Map;

public class ReceivePayment extends AppCompatActivity
{
	private Model model;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_payment);

		Scan();

		findViewById(R.id.Scan).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Scan();
			}
		});
	}

	public void Scan()
	{
		Intent intent = new Intent(ReceivePayment.this, QRScanner.class);
		intent.putExtra("Type", QrStatus.Pending);
		intent.putExtra("Caption", "Hi, Scan QR from Customer");
		startActivityForResult(intent, QRScanner.SCANNER);
	}

	void doTransaction()
	{
		if (!model.getVendor().equals(Account.getPhoneNumber(this)))
		{
			Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_LONG).show();
		}
		else
		{
			dialog = new ProgressDialog(ReceivePayment.this);
			dialog.setMessage(getString(R.string.WaitMessage));
			dialog.show();

			try
			{
				final String rawData = model.encrypt();
				if (Connectivity.isNetworkAvailable(this))
				{
					if (Connectivity.isOnline())
					{
						new RequestHandler(ReceivePayment.this, true, RequestHandler.LONG)
						{
							@Override
							public void body()
							{
								StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.TransactionUrl), new Response.Listener<String>()
								{
									@Override
									public void onResponse(String response)
									{
										try
										{
											model = Model.decrypt(response);
											if (Account.Transact(ReceivePayment.this, model))
											{
												setResponse(ResponseCode.Success, model);
											}
											else
											{
												setResponse(ResponseCode.Failed);
											}
										}
										catch (Exception e)
										{
											e.printStackTrace();
											setResponse(ResponseCode.Internal);
										}
									}
								}, new Response.ErrorListener()
								{
									@Override
									public void onErrorResponse(VolleyError error)
									{
										setResponse(ResponseCode.getExceptionResponseCode(error));
									}
								})
								{
									@Override
									protected Map<String, String> getParams() throws AuthFailureError
									{
										HashMap<String, String> map = new HashMap<>();
										map.put("Data", rawData);
										return map;
									}
								};
								stringRequest.setRetryPolicy(new DefaultRetryPolicy(
										0,
										DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
										DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

								RequestQueue requestQueue = Volley.newRequestQueue(ReceivePayment.this);
								requestQueue.add(stringRequest);
							}

							@Override
							protected void onResponse(ResponseCode code, Object response)
							{
								super.onResponse(code, response);
								dialog.dismiss();
							}

							@Override
							protected void onSuccess(Object response)
							{
								super.onSuccess(response);

								Intent intent = new Intent(ReceivePayment.this, TransactionComplete.class);
								intent.putExtra("Model", (Model) response);
								startActivity(intent);
								finish();
							}
						}.start();
					}
					else
					{
						dialog.setMessage("Sending SMS");
						try
						{
							model.setRandomOTP();
							String sms = model.encrypt();
							SMS.sendSMS(Account.ServerPhoneNumber, sms);

							Intent intent = new Intent(ReceivePayment.this, Otp.class);
							intent.putExtra("Model", model);
							startActivity(intent);
							dialog.dismiss();
						}
						catch (Exception e)
						{
							e.printStackTrace();
							dialog.dismiss();
							Toast.makeText(ReceivePayment.this, "Failed", Toast.LENGTH_LONG).show();
						}
					}
				}
				else
				{
					dialog.dismiss();
					Toast.makeText(this, "No Network, Transaction Incomplete", Toast.LENGTH_LONG).show();
				}
			}
			catch (Exception e)
			{
				dialog.dismiss();
				e.printStackTrace();
				Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case QRScanner.SCANNER:
				if (resultCode == Activity.RESULT_OK)
				{
					model = (Model) data.getSerializableExtra("Model");
					doTransaction();
				}
				break;
		}
	}

}
