package com.bhatnagar.arpit.wallet.Util;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;

/**
 * Created by Home Laptop on 18-May-17.
 */

public class Permission
{
	public static String[] retrievePermissions(Context context)
	{
		try
		{
			return context
					.getPackageManager()
					.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
					.requestedPermissions;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String[] getUnGrantedPermissions(Context context)
	{
		ArrayList<String> UnGranted = new ArrayList<>();
		String[] Permissions = retrievePermissions(context);
		try
		{
			for (String Permission : Permissions)
			{
				if (context.checkCallingOrSelfPermission(Permission) != PackageManager.PERMISSION_GRANTED)
				{
					UnGranted.add(Permission);
				}
			}
			return UnGranted.toArray(new String[0]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
