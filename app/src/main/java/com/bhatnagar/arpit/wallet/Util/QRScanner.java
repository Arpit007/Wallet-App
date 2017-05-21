package com.bhatnagar.arpit.wallet.Util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.Data.QrStatus;
import com.bhatnagar.arpit.wallet.R;

import java.util.Arrays;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRScanner extends AppCompatActivity implements ZBarScannerView.ResultHandler
{
	public static final int SCANNER = 2;
	private int Cam = 0;
	private boolean Flash = false;
	private QrStatus status;
	private ZBarScannerView zBarScannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);

		Bundle bundle = getIntent().getExtras();
		String caption = bundle.getString("Caption", "Scan a QR Code");
		status = (QrStatus) bundle.getSerializable("Type");

		( (TextView) findViewById(R.id.Caption) ).setText(caption);

		zBarScannerView = (ZBarScannerView) findViewById(R.id.Scan);
		zBarScannerView.setFormats(Arrays.asList(BarcodeFormat.QRCODE));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		zBarScannerView.setResultHandler(this);
		zBarScannerView.startCamera(0);
		zBarScannerView.setAutoFocus(true);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		zBarScannerView.stopCamera();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.Flash:
				Flash = !Flash;
				zBarScannerView.setFlash(Flash);
				if (Flash)
				{
					item.setIcon(R.drawable.ic_flash_off_black_24dp);
				}
				else
				{
					item.setIcon(R.drawable.ic_flash_on_black_24dp);
				}
				return true;
			case R.id.Switch:
				Cam = ( Cam == 0 ) ? 1 : 0;
				zBarScannerView.stopCamera();
				zBarScannerView.startCamera(Cam);
				if (Cam == 0)
				{
					item.setIcon(R.drawable.ic_camera_front_black_24dp);
				}
				else
				{
					item.setIcon(R.drawable.ic_camera_rear_black_24dp);
				}
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleResult(Result result)
	{
		if (result.getContents() != null)
		{
			try
			{
				Model model = Model.decrypt(result.getContents());
				if (model.getStatus().equals(status))
				{
					Intent intent = new Intent();
					intent.putExtra("Model", model);
					setResult(RESULT_OK, intent);
					finish();
				}
				else
				{
					Toast.makeText(QRScanner.this, "Invalid QR Code, Try again", Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(QRScanner.this, "Invalid QR Code, Try again", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(QRScanner.this, "Invalid QR Code, Try again", Toast.LENGTH_SHORT).show();
		}
	}
}
