package com.bhatnagar.arpit.wallet.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetAmount extends AppCompatActivity
{
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_amount);

		findViewById(R.id.Scan).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(GetAmount.this, QRScanner.class);
				intent.putExtra("Caption", "Scan Merchant QR Code");
				intent.putExtra("Type", QrStatus.PhoneNumber);
				startActivityForResult(intent, QRScanner.SCANNER);
			}
		});

		findViewById(R.id.Next).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				String VendorNumber = ( (EditText) findViewById(R.id.Number) ).getText().toString();
				String Amount = ( (EditText) findViewById(R.id.Amount) ).getText().toString();

				if (VendorNumber.equals(Account.getPhoneNumber(getBaseContext())))
				{
					Toast.makeText(getBaseContext(), "Vendor's Number should not be same as Customer's Number", Toast.LENGTH_LONG).show();
					return;
				}

				if (VendorNumber.length() != 10)
				{
					Toast.makeText(GetAmount.this, "Invalid Number", Toast.LENGTH_SHORT).show();
					return;
				}

				if (Amount.length() == 0 || Integer.parseInt(Amount) <= 0)
				{
					Toast.makeText(GetAmount.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
					return;
				}

				if (Integer.parseInt(Amount) > getSharedPreferences("Account", MODE_PRIVATE).getInt("Amount", 0))
				{
					Toast.makeText(GetAmount.this, "Insufficient Funds", Toast.LENGTH_SHORT).show();
					return;
				}

				dialog = new ProgressDialog(GetAmount.this);
				dialog.setMessage(getString(R.string.WaitMessage));
				dialog.show();

				final Model model = Model.createModel(Account.getPhoneNumber(GetAmount.this), VendorNumber, Amount, QrStatus.Pending);
				model.setTimeStamp(new Date().getTime());
				final String data;

				try
				{
					data = model.encrypt();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Toast.makeText(GetAmount.this, "Failed", Toast.LENGTH_LONG).show();
					dialog.dismiss();
					return;
				}

				if (Connectivity.isNetworkAvailable(GetAmount.this) || Connectivity.isOnline(getBaseContext()))
				{
					if (Connectivity.isOnline(getBaseContext()))
					{
						new RequestHandler(GetAmount.this, true, RequestHandler.LONG)
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
											Model result = Model.decrypt(response);
											if (Account.Transact(GetAmount.this, result, false))
											{
												setResponse(ResponseCode.Success, result);
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
										map.put("Data", data);
										return map;
									}
								};

								stringRequest.setRetryPolicy(new DefaultRetryPolicy(
										0,
										DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
										DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
								RequestQueue requestQueue = Volley.newRequestQueue(GetAmount.this);
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

								Intent intent = new Intent(GetAmount.this, TransactionComplete.class);
								intent.putExtra("Model", model);
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
							SMS.sendSMS(Account.ServerPhoneNumber, model.encrypt());

							Intent intent = new Intent(GetAmount.this, Otp.class);
							intent.putExtra("Model", model);
							startActivity(intent);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							Toast.makeText(GetAmount.this, "Failed", Toast.LENGTH_LONG).show();
						}
						finally
						{
							dialog.dismiss();
						}
					}
				}
				else
				{
					dialog.dismiss();
					Intent intent = new Intent(GetAmount.this, OfflineQR.class);
					model.setRandomOTP();
					intent.putExtra("Model", model);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
			case QRScanner.SCANNER:
				if (resultCode == RESULT_OK)
				{
					Model model = (Model) data.getSerializableExtra("Model");
					if (model.getVendor().equals(Account.getPhoneNumber(getBaseContext())))
					{
						Toast.makeText(getBaseContext(), "Vendor's Number should not be same as Customer's Number", Toast.LENGTH_LONG).show();
					}
					else
					{
						( (EditText) findViewById(R.id.Number) ).setText(model.getVendor());
					}
				}
				break;
		}
	}
}
