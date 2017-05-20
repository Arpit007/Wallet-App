package com.bhatnagar.arpit.wallet.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.QRGenerator;

public class OfflineQR extends AppCompatActivity
{
	private Model model;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_qr);

		model=(Model)getIntent().getSerializableExtra("Model");

		try
		{
			Bitmap QrCode = QRGenerator.getQRCode(model.encrypt(), QRGenerator.getAppropriateSize(this));
			ImageView view1=(ImageView)findViewById(R.id.QR);
			view1.setImageBitmap(QrCode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(OfflineQR.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
			finish();
		}
		findViewById(R.id.Next).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent=new Intent(OfflineQR.this,Otp.class);
				intent.putExtra("Model",model);
				startActivity(intent);
			}
		});
	}
}
