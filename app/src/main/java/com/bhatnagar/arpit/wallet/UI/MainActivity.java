package com.bhatnagar.arpit.wallet.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.SocketConnection;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Permission;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity
{
	private static final int PermissionRequest = 100;
	private static final int REQUEST_INVITE = 234;
	private TextView Amount;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SocketConnection.getInstance().initialize();

		SocketConnection.getInstance().listener = new SocketConnection.Listener()
		{
			@Override
			public void Update()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							SharedPreferences preferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
							Amount.setText(( "Balance: " + preferences.getInt("Amount", 0) ));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		};

		Amount = (TextView) findViewById(R.id.Amount);

		( (TextView) findViewById(R.id.Number) ).setText(Account.getPhoneNumber(this));

		String[] Permissions = Permission.getUnGrantedPermissions(this);
		if (Permissions != null && Permissions.length > 0)
		{
			ActivityCompat.requestPermissions(this, Permissions, PermissionRequest);
		}

		findViewById(R.id.MyNumber).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, MyPhoneNumber.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.Receive).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, ReceivePayment.class);
				intent.putExtra("Receive", true);
				startActivity(intent);
			}
		});
		findViewById(R.id.Pay).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, GetAmount.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.AddAmount).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(MainActivity.this, AddAmount.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.Invite).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
						.setMessage(getString(R.string.invitation_message))
						.build();
				startActivityForResult(intent, REQUEST_INVITE);
			}
		});

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		switch (requestCode)
		{
			case PermissionRequest:
				String[] Perms = Permission.getUnGrantedPermissions(this);
				if (Perms != null && Perms.length != 0)
				{
					Toast.makeText(this, "Requested Permission Not granted, Exiting", Toast.LENGTH_LONG).show();
					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							System.exit(-1);
						}
					}, 2000);
				}

		}
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		SocketConnection.getInstance().reconnect();
		SharedPreferences preferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
		Amount.setText(( "Balance: " + preferences.getInt("Amount", 0) ));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_INVITE)
		{
			if (resultCode == RESULT_OK)
			{
				FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getApplicationContext());
				Bundle bundle = new Bundle();
				bundle.putSerializable("UserId", Account.getPhoneNumber(getApplicationContext()));
				analytics.logEvent("Invite", bundle);
			}
		}
	}
}
