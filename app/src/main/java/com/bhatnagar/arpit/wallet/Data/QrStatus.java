package com.bhatnagar.arpit.wallet.Data;

import java.io.Serializable;

/**
 * Created by Home Laptop on 14-May-17.
 */

public enum QrStatus implements Serializable
{
	Invalid,
	PhoneNumber,
	Pending,
	Success;

	public static QrStatus getQrType(String type)
	{
		if(type.equals(PhoneNumber.toString()))
			return PhoneNumber;
		if(type.equals(Pending.toString()))
			return Pending;
		if(type.equals(Success.toString()))
			return Success;
		return Invalid;
	}
}
