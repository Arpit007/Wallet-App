package com.bhatnagar.arpit.wallet.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.bhatnagar.arpit.wallet.Data.SocketConnection;
import com.bhatnagar.arpit.wallet.Data.SocketRegister;
import com.bhatnagar.arpit.wallet.R;

public class Splash extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				SharedPreferences preferences = getSharedPreferences("Account", MODE_PRIVATE);
				String Phone = preferences.getString("Phone", "");
				/*Todo:Replace with digit*/
				if (Phone.isEmpty()) {
					startActivity(new Intent(Splash.this, SetNum.class));
				}
				else {
					SocketConnection.getInstance().initialize();
					SocketRegister.getInstance().registerSocketEvents();
					startActivity(new Intent(Splash.this, MainActivity.class));
				}
				finish();
			}
		}, 100);
	}
}
