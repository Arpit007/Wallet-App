package com.bhatnagar.arpit.wallet.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bhatnagar.arpit.wallet.R;

public class Splash extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				SharedPreferences preferences=getSharedPreferences("Account",MODE_PRIVATE);
				String Phone=preferences.getString("Phone","");
				/*Todo:Replace with digit*/
				if(Phone.isEmpty())
					startActivity(new Intent(Splash.this,SetNum.class));
				else startActivity(new Intent(Splash.this,MainActivity.class));
				finish();
			}
		},100);
	}
}
