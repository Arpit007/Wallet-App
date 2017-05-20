package com.bhatnagar.arpit.wallet.Util.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bhatnagar.arpit.wallet.Data.SocketConnection;

/**
 * Created by Home Laptop on 20-May-17.
 */

public class NetworkChanged extends BroadcastReceiver
{
	public NetworkChanged()
	{
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		SocketConnection.getInstance().reconnect();
	}
}