package com.bhatnagar.arpit.wallet.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;

public class Otp extends AppCompatActivity
{
	private boolean back = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp);

		final Model model = (Model) getIntent().getExtras().getSerializable("Model");

		findViewById(R.id.Done).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				EditText text = ( (EditText) findViewById(R.id.OtpCode) );
				String tempOtp = text.getText().toString();

				if (model.getOTP().equals(tempOtp))
				{
					try
					{
						model.setStatus(QrStatus.Success);
						if (Account.Transact(Otp.this, model, false))
						{
							Intent intent = new Intent(Otp.this, TransactionComplete.class);
							intent.putExtra("Model", model);
							startActivity(intent);
							finish();
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Toast.makeText(Otp.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(Otp.this, "Invalid OTP code, Try again", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		if (back)
		{
			finish();
		}
		else
		{
			back = true;
			Toast.makeText(this, "Going Back may not represent current account State", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					back = false;
				}
			}, 2000);
		}
	}
}
