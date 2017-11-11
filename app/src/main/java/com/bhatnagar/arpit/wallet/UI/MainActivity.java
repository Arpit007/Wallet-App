package com.bhatnagar.arpit.wallet.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.SocketConnection;
import com.bhatnagar.arpit.wallet.Data.SocketEvent;
import com.bhatnagar.arpit.wallet.Data.SocketRegister;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Permission;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import io.socket.client.Socket;


public class MainActivity extends AppCompatActivity {
	private static final int PermissionRequest = 100;
	private static final int REQUEST_INVITE = 234;
	private TextView Amount, Connectivity;
	private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		Connectivity = (TextView) findViewById(R.id.Connectivity);

		SocketConnection.getInstance().reconnect();

		SocketConnection.getInstance().setOnConnectListener(new SocketEvent() {
			@Override
			public void onEventRaised(Socket socket, Object[] Data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Connectivity.setText("Connected");
					}
				});
			}
		});

		SocketConnection.getInstance().setOnDisconnectListener(new SocketEvent() {
			@Override
			public void onEventRaised(Socket socket, Object[] Data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Connectivity.setText("Disconnected");
					}
				});
			}
		});

		SocketRegister.getInstance().setReflect(new SocketEvent() {
			@Override
			public void onEventRaised(Socket socket, Object[] Data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							SharedPreferences preferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
							Amount.setText(( "Balance: " + preferences.getInt("Amount", 0) ));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		Amount = (TextView) findViewById(R.id.Amount);

		( (TextView) findViewById(R.id.Number) ).setText(Account.getPhoneNumber(this));

		String[] Permissions = Permission.getUnGrantedPermissions(this);
		if (Permissions != null && Permissions.length > 0) {
			//this.requestPermissions(Permissions, PermissionRequest);
			ActivityCompat.requestPermissions(this, Permissions, PermissionRequest);
		}

		findViewById(R.id.MyNumber).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, MyPhoneNumber.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.Receive).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, ReceivePayment.class);
				intent.putExtra("Receive", true);
				startActivity(intent);
			}
		});
		findViewById(R.id.Pay).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, GetAmount.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.AddAmount).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, AddAmount.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.History).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, History.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.Invite).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
						.setMessage(getString(R.string.invitation_message))
						.build();
				startActivityForResult(intent, REQUEST_INVITE);
			}
		});


		if (SocketConnection.getInstance().isConnected()) {
			Connectivity.setText("Connected");
		}
		else {
			Connectivity.setText("Disconnected");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.Logout) {
			FirebaseAuth.getInstance().signOut();
			getSharedPreferences("Account", MODE_PRIVATE).edit().clear().apply();
			startActivity(new Intent(MainActivity.this, SetNum.class));
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case PermissionRequest:
				String[] Perms = Permission.getUnGrantedPermissions(this);
				if (Perms != null && Perms.length != 0) {
					Toast.makeText(this, "Requested Permission Not granted, Exiting", Toast.LENGTH_LONG).show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							System.exit(-1);
						}
					}, 2000);
				}
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		SocketConnection.getInstance().reconnect();
		SharedPreferences preferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
		Amount.setText(( "Balance: " + preferences.getInt("Amount", 0) ));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_INVITE) {
			if (resultCode == RESULT_OK) {
				String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
				int Count = ( ids != null ) ? ids.length : 0;
				if (Count > 0) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("UserId", Account.getPhoneNumber(getApplicationContext()));
					bundle.putInt(FirebaseAnalytics.Param.QUANTITY, Count);
					mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
				}
			}
		}
	}
}
