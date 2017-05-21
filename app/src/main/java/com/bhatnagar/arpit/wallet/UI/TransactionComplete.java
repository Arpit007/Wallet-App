package com.bhatnagar.arpit.wallet.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bhatnagar.arpit.wallet.Data.Model;
import com.bhatnagar.arpit.wallet.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionComplete extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_complete);

		Model model = (Model) getIntent().getSerializableExtra("Model");

		String date = new SimpleDateFormat("h:mm a, d MMM yyyy", Locale.getDefault()).format(Long.parseLong(Long.toString(model.getTimeStamp()))).replace("AM", "Am").replace("PM", "Pm");

		String string = new StringBuilder("Vendor:\t").append(model.getVendor())
				.append("\n\nCustomer:\t").append(model.getCustomer())
				.append("\n\nAmount:\t").append(model.getAmount())
				.append("\n\n").append(date).toString();

		if (!model.getOTP().isEmpty())
		{
			string += "\nOTP: " + model.getOTP();
		}

		( (TextView) findViewById(R.id.Content) ).setText(string);

		findViewById(R.id.Done).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startActivity(new Intent(TransactionComplete.this, MainActivity.class));
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
