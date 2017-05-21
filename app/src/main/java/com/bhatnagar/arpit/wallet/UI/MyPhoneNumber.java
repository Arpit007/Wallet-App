package com.bhatnagar.arpit.wallet.UI;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.QRGenerator;

public class MyPhoneNumber extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number);

		boolean isNextVisible = getIntent().getBooleanExtra("Receive", false);
		findViewById(R.id.Next).setVisibility(isNextVisible ? View.VISIBLE : View.INVISIBLE);

		( (TextView) findViewById(R.id.Number) ).setText(( "Phone Number\n" + Account.getPhoneNumber(this) ));

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Please Wait");
		dialog.show();

		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final Bitmap bitmap = QRGenerator.getQRCode(createObject(), QRGenerator.getAppropriateSize(MyPhoneNumber.this));
					new Handler(Looper.getMainLooper()).post(new Runnable()
					{
						@Override
						public void run()
						{
							( (ImageView) findViewById(R.id.QR) ).setImageBitmap(bitmap);
							dialog.dismiss();
						}
					});
				}
				catch (Exception e)
				{
					e.printStackTrace();
					dialog.dismiss();
				}
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	String createObject()
	{
		Model model = Model.createModel("0000000000", Account.getPhoneNumber(this), "0", QrStatus.PhoneNumber);
		try
		{
			return model.encrypt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

}
