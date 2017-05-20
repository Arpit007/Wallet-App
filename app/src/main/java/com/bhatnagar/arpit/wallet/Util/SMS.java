package com.bhatnagar.arpit.wallet.Util;

import android.telephony.SmsManager;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.App;

/**
 * Created by Home Laptop on 18-May-17.
 */

public class SMS
{
	public static void sendSMS(String phoneNo, String msg)
	{
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNo, null, msg, null, null);
		Toast.makeText(App.getInstance().getApplicationContext(), "Message Sent to Server",Toast.LENGTH_LONG).show();
	}
}
