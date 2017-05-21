package com.bhatnagar.arpit.wallet.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.App;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Security;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Home Laptop on 20-May-17.
 */

public class SocketConnection
{
	private static SocketConnection socketConnection;
	public Listener listener;
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
						socket.emit("ID", Security.encrypt(Account.getPhoneNumber(App.getInstance())));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

			}).on("Update", new Emitter.Listener()
			{

				@Override
				public void call(Object... args)
				{
					try
					{
						String Data = (String) args[0];
						JSONObject object = new JSONObject(Security.decrypt(Data));

						SharedPreferences preferences = App.getInstance().getSharedPreferences("Account", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("Amount", object.getInt("Balance"));
						editor.apply();

						final String Message = object.getString("Message");

						if (listener != null)
						{
							listener.Update();
						}

						new Handler(Looper.getMainLooper())
								.post(new Runnable()
								{
									@Override
									public void run()
									{
										Toast.makeText(App.getInstance(), Message, Toast.LENGTH_LONG).show();
									}
								});
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

			}).on("Amount", new Emitter.Listener()
			{
				@Override
				public void call(Object... args)
				{
					try
					{
						String Amount = Security.decrypt((String) args[0]);

						SharedPreferences preferences = App.getInstance().getSharedPreferences("Account", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("Amount", Integer.parseInt(Amount));
						editor.apply();
						if (listener != null)
						{
							listener.Update();
						}
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
		try
		{
			socket.emit("ID", Security.encrypt(Account.getPhoneNumber(App.getInstance())));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public interface Listener
	{
		void Update();
	}
}
