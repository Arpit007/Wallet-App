package com.bhatnagar.arpit.wallet;

import android.app.Application;

import com.bhatnagar.arpit.wallet.Data.SocketConnection;

/**
 * Created by Home Laptop on 17-May-17.
 */

public class App extends Application
{
	public static App app;
	public App()
	{
		app=this;
	}
	public static App getInstance()
	{
		return app;
	}
}
