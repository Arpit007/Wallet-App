package com.bhatnagar.arpit.wallet.Data;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.App;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.UI.MainActivity;
import com.bhatnagar.arpit.wallet.Util.Security;

import java.util.Date;

import io.socket.client.Socket;

/**
 * Created by Home Laptop on 08-Nov-17.
 */

public class SocketRegister {
	private static SocketRegister socketRegister = null;
	SocketEvent Reflect = null;

	private SocketRegister() {
	}

	public static synchronized SocketRegister getInstance() {
		if (socketRegister == null) {
			socketRegister = new SocketRegister();
		}
		return socketRegister;
	}

	private static void sendNotification(String messageBody, Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Payment")
				.setContentText(messageBody)
				.setWhen(new Date().getTime())
				.setAutoCancel(true)
				.setPriority(Notification.PRIORITY_MAX)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}

	public void setReflect(SocketEvent event) {
		Reflect = event;
	}

	public void registerSocketEvents() {
		SocketConnection.getInstance().registerEvent("Balance", new SocketEvent() {
			@Override
			public void onEventRaised(Socket socket, Object[] Data) {
				try {
					String Amount = Security.decrypt((String) Data[0]);

					SharedPreferences preferences = App.getInstance().getSharedPreferences("Account", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putInt("Amount", Integer.parseInt(Amount));
					editor.apply();
					if (Reflect != null) {
						Reflect.onEventRaised(null, null);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		SocketConnection.getInstance().registerEvent("Update", new SocketEvent() {
			@Override
			public void onEventRaised(Socket socket, Object[] Data) {
				try {

					Model result = Model.decrypt((String) Data[0]);
					Account.Transact(App.getInstance(), result, true);

					if (Reflect != null) {
						Reflect.onEventRaised(null, null);
					}

					if (result.getStatus() == QrStatus.Success) {
						String Message;
						if (result.getVendor().equals(Account.getPhoneNumber(App.getInstance()))) {
							if (result.getCustomer().equals("0000000000")) {
								Message = "Top of Rs. " + result.getAmount() + " successful.";
							}
							else {
								Message = "Amount of Rs. " + result.getAmount() + " received from " + result.getCustomer();
							}
						}
						else {
							Message = "Rs. " + result.getAmount() + " successfully paid to " + result.getVendor();
						}

						final String Msg = Message;

						new Handler(Looper.getMainLooper())
								.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(App.getInstance(), Msg, Toast.LENGTH_LONG).show();
										sendNotification(Msg, App.getInstance());
									}
								});
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
