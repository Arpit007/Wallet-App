package com.bhatnagar.arpit.wallet.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.bhatnagar.arpit.wallet.Util.Network.Connectivity;

/**
 * Created by Home Laptop on 14-May-17.
 */

public class Account
{
	public static final String ServerPhoneNumber = "+12568889256";
	public static final String SecurityKey = "MGUCAQACEQDvU7VJQZCOJiSi7RHt9A+r";

	public static String getPhoneNumber(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
		return preferences.getString("Phone", "");
	}

	public static boolean Transact(Context context, Model data) throws Exception
	{
		if (data.getCustomer().equals(Account.getPhoneNumber(context)))
		{
			if (data.getStatus() == QrStatus.Pending)
			{
				SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
				int Amount = preferences.getInt("Amount", 0);
				int transAmount = Integer.parseInt(data.getAmount());
				return transAmount <= Amount;
			}
			else if (data.getStatus() == QrStatus.Success)
			{
				if (!Connectivity.isOnline())
				{
					SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					int Balance = preferences.getInt("Amount", 0);
					int amount = Integer.parseInt(data.getAmount());
					editor.putInt("Amount", Balance - amount);
					editor.apply();
				}
				else
				{
					SocketConnection.getInstance().reconnect();
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (data.getStatus() == QrStatus.Success)
			{
				if (!Connectivity.isOnline())
				{
					SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					int Balance = preferences.getInt("Amount", 0);
					int amount = Integer.parseInt(data.getAmount());
					editor.putInt("Amount", Balance + amount);
					editor.apply();
				}
				else
				{
					SocketConnection.getInstance().reconnect();
				}
				return true;
			}
		}
		return false;
	}
}
