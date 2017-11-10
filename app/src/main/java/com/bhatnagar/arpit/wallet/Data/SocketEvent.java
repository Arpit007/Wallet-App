package com.bhatnagar.arpit.wallet.Data;

import io.socket.client.Socket;

/**
 * Created by Home Laptop on 08-Nov-17.
 */
public interface SocketEvent {
	void onEventRaised(Socket socket, Object[] Data);
}
