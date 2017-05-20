package com.bhatnagar.arpit.wallet.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.bhatnagar.arpit.wallet.Util.Network.RequestHandler;
import com.bhatnagar.arpit.wallet.Util.Network.ResponseCode;
import com.bhatnagar.arpit.wallet.Util.Network.SSL.ExtHttpClientStack;
import com.bhatnagar.arpit.wallet.Util.Network.SSL.SslHttpClient;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddAmount extends AppCompatActivity
{
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_amount);

		findViewById(R.id.Done).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View view)
			{
				EditText text=(( EditText)findViewById(R.id.Amount));
				if(text.length()<1)
				{
					Toast.makeText(AddAmount.this,"Invalid Amount",Toast.LENGTH_SHORT).show();
					return;
				}
				final int Amount=Integer.parseInt(text.getText().toString());
				if(Amount<=0)
				{
					Toast.makeText(AddAmount.this,"Invalid Amount",Toast.LENGTH_SHORT).show();
					return;
				}
				new RequestHandler(getApplicationContext(),true,RequestHandler.LONG){
					@Override
					public void body()
					{
						dialog=new ProgressDialog(AddAmount.this);
						dialog.setMessage("Please Wait, Adding Amount");
						dialog.show();

						Model model=Model.createModel("0000000000", Account.getPhoneNumber(AddAmount.this),Integer.toString(Amount), QrStatus.Pending);
						model.setTimeStamp(new Date().getTime());

						try
						{
							final String data=model.encrypt();
							StringRequest request=new StringRequest(Request.Method.POST, getString(R.string.TransactionUrl), new Response.Listener<String>()
							{
								@Override
								public void onResponse(String response)
								{
									try
									{
										Model result = Model.decrypt(response);
										if(Account.Transact(AddAmount.this,result))
											setResponse(ResponseCode.Success);
										else setResponse(ResponseCode.Failed);
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
									error.printStackTrace();
									setResponse(ResponseCode.getExceptionResponseCode(error));
								}
							})
							{
								@Override
								protected Map<String, String> getParams() throws AuthFailureError
								{
									HashMap<String,String> map = new HashMap<>();
									map.put("Data",data);
									return map;
								}
							};
							request.setRetryPolicy(new DefaultRetryPolicy(
									0,
									DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
									DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
							RequestQueue queue= Volley.newRequestQueue(AddAmount.this);
							queue.add(request);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							setResponse(ResponseCode.Failed);
						}
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
						SharedPreferences preferences=getBaseContext().getSharedPreferences("Account", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor=preferences.edit();
						int oldAmount=preferences.getInt("Amount",0);
						oldAmount+=Amount;
						editor.putInt("Amount",oldAmount);
						editor.apply();
						finish();
					}
				}.start();
			}
		});
	}
}
