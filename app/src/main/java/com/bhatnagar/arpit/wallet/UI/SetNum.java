package com.bhatnagar.arpit.wallet.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.SocketConnection;
import com.bhatnagar.arpit.wallet.R;

public class SetNum extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_num);

		findViewById(R.id.Set).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				EditText text = ( (EditText) findViewById(R.id.Num) );
				String Num = text.getText().toString();
				if (Num.length() != 10)
				{
					Toast.makeText(SetNum.this, "Invalid Number", Toast.LENGTH_LONG).show();
				}
				else
				{
					SharedPreferences.Editor editor = getSharedPreferences("Account", Context.MODE_PRIVATE).edit();
					editor.putString("Phone", Num);
					editor.apply();
					SocketConnection.getInstance().initialize();
					startActivity(new Intent(SetNum.this, MainActivity.class));
					finish();
				}
			}
		});
	}
}
