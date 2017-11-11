package com.bhatnagar.arpit.wallet.Util.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by Home Laptop on 17-May-17.
 */

public class Connectivity
{
	public static boolean isNetworkAvailable(Context context)
	{
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ( tel.getNetworkOperator() != null && !tel.getNetworkOperator().equals("") );
	}

	public static boolean isOnline(Context context)
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				if (activeNetwork.isConnected()) {
					haveConnectedWifi = true;
				}
			}
			else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				if (activeNetwork.isConnected()) {
					haveConnectedMobile = true;
				}
			}
		}

		return haveConnectedWifi || haveConnectedMobile;
	}
}
