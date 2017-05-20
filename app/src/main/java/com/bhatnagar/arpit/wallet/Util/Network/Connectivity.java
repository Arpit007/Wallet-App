package com.bhatnagar.arpit.wallet.Util.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

/**
 * Created by Home Laptop on 17-May-17.
 */

public class Connectivity
{
	public static boolean isNetworkAvailable(Context context)
	{
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return (tel.getNetworkOperator() != null && !tel.getNetworkOperator().equals(""));
	}

	public static boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try
		{
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}