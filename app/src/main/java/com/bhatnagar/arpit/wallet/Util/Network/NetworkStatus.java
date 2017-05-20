package com.bhatnagar.arpit.wallet.Util.Network;

/**
 * Created by Home Laptop on 17-May-17.
 */

public enum NetworkStatus
{
	Online,
	Network,
	Offline;

	public static NetworkStatus getStatus(String status)
	{
		if(status.equals(Online.toString()))
			return Online;
		if(status.equals(Network.toString()))
			return Network;
		return Offline;
	}
}
