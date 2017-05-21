package com.bhatnagar.arpit.wallet.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.QRScanner;

public class OfflineQrResponse extends AppCompatActivity
{
	private Model model;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_qr_response);

		model = (Model) getIntent().getSerializableExtra("Model");

		Scan();

		findViewById(R.id.Scan).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Scan();
			}
		});

	}

	public void Scan()
	{
		Intent intent = new Intent(this, QRScanner.class);
		intent.putExtra("Type", QrStatus.Success);
		intent.putExtra("Caption", "Scan Payment Qr Code from Vendor to Complete");
		startActivityForResult(intent, QRScanner.SCANNER);
	}

	void Check(Model scan)
	{
		if (!model.getTransactionID().equals(scan.getTransactionID()))
		{
			Toast.makeText(this, "Invalid Qr Code", Toast.LENGTH_LONG).show();
		}
		else
		{
			if (!scan.getCustomer().equals(Account.getPhoneNumber(this)))
			{
				Toast.makeText(this, "Invalid Qr Code", Toast.LENGTH_LONG).show();
			}
			else
			{
				try
				{
					if (Account.Transact(OfflineQrResponse.this, model))
					{
						Intent intent = new Intent(OfflineQrResponse.this, TransactionComplete.class);
						intent.putExtra("Model", model);
						startActivity(intent);
						finish();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Toast.makeText(this, "Failed, try Again", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case QRScanner.SCANNER:
				if (resultCode == Activity.RESULT_OK)
				{
					Model model = (Model) data.getSerializableExtra("Model");
					Check(model);
				}
				break;
		}
	}
}
