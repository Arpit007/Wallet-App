package com.bhatnagar.arpit.wallet.Util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

import java.util.List;

public class QRScanner extends AppCompatActivity
{
	private QrStatus status;
	private BarcodeView barcodeView;
	public static final int SCANNER=2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);

		Bundle bundle=getIntent().getExtras();
		String caption=bundle.getString("Caption","Scan a QR Code");
		status =(QrStatus)bundle.getSerializable("Type");

		(( TextView)findViewById(R.id.Caption)).setText(caption);

		barcodeView=(BarcodeView)findViewById(R.id.Scan);
		barcodeView.decodeContinuous(new BarcodeCallback()
		{
			@Override
			public void barcodeResult(BarcodeResult result)
			{
				if(result.getText()!=null)
				{
					try
					{
						Model model=Model.decrypt(result.getText());
						if (model.getStatus().equals( status))
						{
							barcodeView.pause();
							Intent intent=new Intent();
							intent.putExtra("Model",model);
							setResult(RESULT_OK,intent);
							finish();
						}
						else Toast.makeText(QRScanner.this,"Invalid QR Code, Try again",Toast.LENGTH_SHORT).show();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Toast.makeText(QRScanner.this,"Invalid QR Code, Try again",Toast.LENGTH_SHORT).show();
					}
				}
				else Toast.makeText(QRScanner.this,"Invalid QR Code, Try again",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void possibleResultPoints(List<ResultPoint> resultPoints)
			{
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		barcodeView.resume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		barcodeView.pause();
	}
}
