package com.bhatnagar.arpit.wallet.Util.Network;

import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

/**
 * Created by Home Laptop on 06-May-17.
 */

public enum ResponseCode
{
	Success,
	Failed,
	Timeout,
	ServerError,
	NoNetwork,
	Internal,
	Other;

	public static ResponseCode getExceptionResponseCode(Exception e)
	{
		if (e instanceof ServerError)
		{
			return ServerError;
		}
		if (e instanceof TimeoutError)
		{
			return Timeout;
		}
		if (e instanceof NetworkError)
		{
			return NoNetwork;
		}
		return Other;
	}
}
