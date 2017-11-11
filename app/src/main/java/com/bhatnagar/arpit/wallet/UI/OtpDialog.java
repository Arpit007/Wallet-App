package com.bhatnagar.arpit.wallet.UI;

/**
 * Created by Home Laptop on 11-Nov-17.
 */

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.bhatnagar.arpit.wallet.R;


public abstract class OtpDialog {
	private Dialog dialog;

	public OtpDialog(final Context context) {
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_otp);
		dialog.setCancelable(false);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		( (EditText) dialog.findViewById(R.id.OTP) ).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.length() == 6) {
					dialog.findViewById(R.id.Positive).setEnabled(true);
				}
				else {
					dialog.findViewById(R.id.Positive).setEnabled(false);
				}
				hideInvalid();
			}
		});

		dialog.findViewById(R.id.Positive).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OnPositiveClicked();
			}
		});
		dialog.findViewById(R.id.Negative).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OnNegativeClicked();

				if (dialog.isShowing()) {
					dismiss();
				}
			}
		});
	}

	public abstract void OnPositiveClicked();

	public abstract void OnNegativeClicked();

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public Dialog getDialog() {
		return dialog;
	}

	public String getOtpCode() {
		return ( (EditText) dialog.findViewById(R.id.OTP) ).getText().toString();
	}

	public void showInvalid() {
		dialog.findViewById(R.id.Invalid).setVisibility(View.VISIBLE);
	}

	public void hideInvalid() {
		dialog.findViewById(R.id.Invalid).setVisibility(View.GONE);
	}
}
