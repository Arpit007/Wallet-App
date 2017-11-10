package com.bhatnagar.arpit.wallet.Data;

import com.bhatnagar.arpit.wallet.App;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Security;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Home Laptop on 20-May-17.
 */

public class SocketConnection
{
	private static SocketConnection socketConnection;
	private Socket socket;

	private SocketConnection()
	{
		socketConnection = this;
	}

	public static synchronized SocketConnection getInstance()
	{
		if (socketConnection == null)
		{
			socketConnection = new SocketConnection();
		}
		return socketConnection;
	}

	public void initialize()
	{
		try
		{
			socket = IO.socket(App.getInstance().getString(R.string.Url));
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener()
			{
				@Override
				public void call(Object... args)
				{
					try
					{
						socket.emit("verify", Security.encrypt(Account.getPhoneNumber(App.getInstance())));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener()
			{
				@Override
				public void call(Object... args)
				{
				}
			});
			socket.connect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void reconnect()
	{
		if (socket == null)
		{
			initialize();
		}

		if (!socket.connected())
		{
			socket.connect();
		}
	}

	public void registerEvent(String Name, final SocketEvent event) {
		if (socket != null) {
			socket.on(Name, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					if (event != null) {
						event.onEventRaised(socket, args);
					}
				}
			});
		}
	}

}
