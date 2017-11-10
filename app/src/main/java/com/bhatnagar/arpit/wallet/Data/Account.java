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

	public static boolean Transact(Context context, Model data, boolean IsSocket) throws Exception
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
				if (!Connectivity.isOnline() || IsSocket)
				{
					SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					int Balance = Integer.parseInt(data.getCustomerBalance());
					editor.putInt("Amount", Balance);
					editor.apply();
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
				if (!Connectivity.isOnline() || IsSocket)
				{
					SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					int Balance = Integer.parseInt(data.getVendorBalance());
					editor.putInt("Amount", Balance);
					editor.apply();
				}
				return true;
			}
		}
		return false;
	}
}
