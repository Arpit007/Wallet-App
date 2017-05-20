package com.bhatnagar.arpit.wallet.Util.Network;

import android.content.Context;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.R;


/**
 * Created by Home Laptop on 06-May-17.
 */

public abstract class RequestHandler
{
	public static final int LONG = Toast.LENGTH_LONG;
	public static final int SHORT = Toast.LENGTH_SHORT;

	private int DURATION=Toast.LENGTH_SHORT;
	private boolean showToast=true;
	private Context context;

	public RequestHandler(Context context, boolean showToast)
	{
		this.context=context;
		this.showToast=showToast;
	}

	public RequestHandler(Context context, boolean showToast, int Duration)
	{
		this.context=context;
		this.showToast=showToast;
		this.DURATION=Duration;
	}

	protected void onResponse(ResponseCode code, Object response)
	{
		/*Empty Method*/
	}

	protected void onSuccess(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_Success),DURATION).show();
	}

	protected void onFailure(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_Failure),DURATION).show();
	}

	protected void onTimeout(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_Timeout),DURATION).show();
	}

	protected void onServerError(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_ServerError),DURATION).show();
	}

	protected void onNoNetwork(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_NoNetwork),DURATION).show();
	}

	protected void onOtherFailure(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_OtherError),DURATION).show();
	}

	protected void onInternalError(Object response)
	{
		if(showToast)
			Toast.makeText(context,context.getText(R.string.Network_InternalError),DURATION).show();
	}

	public abstract void body();

	final protected void setResponse(ResponseCode code, Object response)
	{
		onResponse(code,response);
		switch (code)
		{
			case Success:onSuccess(response);break;
			case Failed:onFailure(response);break;
			case Timeout:onTimeout(response);break;
			case ServerError:onServerError(response);break;
			case Internal:onInternalError(response);break;
			case NoNetwork:onNoNetwork(response);break;
			default:onOtherFailure(response);break;
		}
	}

	final protected void setResponse(ResponseCode code)
	{
		setResponse(code,null);
	}

	public final void start()
	{
		body();
	}
}
