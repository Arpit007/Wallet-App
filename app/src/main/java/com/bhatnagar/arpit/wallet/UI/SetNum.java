package com.bhatnagar.arpit.wallet.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bhatnagar.arpit.wallet.Data.SocketConnection;
import com.bhatnagar.arpit.wallet.Data.SocketRegister;
import com.bhatnagar.arpit.wallet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SetNum extends AppCompatActivity {
	private FirebaseAuth mAuth;
	private String Number;
	private String TAG = "Firebase";
	private String mVerificationId = "";
	private ProgressDialog progressDialog;
	private OtpDialog dialog;
	private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

		@Override
		public void onVerificationCompleted(PhoneAuthCredential credential) {
			Log.d(TAG, "onVerificationCompleted:" + credential);
			progressDialog.dismiss();
			dialog.dismiss();
			signInWithPhoneAuthCredential(credential);
		}

		@Override
		public void onVerificationFailed(FirebaseException e) {
			Log.w(TAG, "onVerificationFailed");
			Toast.makeText(getApplicationContext(), "Failed, Try Again", Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}

		@Override
		public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
			Log.d(TAG, "onCodeSent:" + verificationId);
			mVerificationId = verificationId;
			progressDialog.dismiss();
			dialog.show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_num);

		mAuth = FirebaseAuth.getInstance();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please Wait");

		dialog = new OtpDialog(SetNum.this) {
			@Override
			public void OnPositiveClicked() {
				progressDialog.show();
				verifyPhoneNumberWithCode(mVerificationId, getOtpCode());
			}

			@Override
			public void OnNegativeClicked() {
			}
		};

		findViewById(R.id.Set).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText text = ( (EditText) findViewById(R.id.Num) );
				Number = text.getText().toString();
				if (Number.length() != 10)
					Toast.makeText(SetNum.this, "Invalid Number", Toast.LENGTH_LONG).show();
				else {
					progressDialog.show();
					PhoneAuthProvider.getInstance().verifyPhoneNumber(Number, 60, TimeUnit.SECONDS, SetNum.this, mCallbacks);
				}
			}
		});
	}

	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
		mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				progressDialog.dismiss();
				if (task.isSuccessful()) {
					SharedPreferences.Editor editor = getSharedPreferences("Account", Context.MODE_PRIVATE).edit();
					editor.putString("Phone", Number);
					editor.apply();
					SocketConnection.getInstance().initialize();
					SocketRegister.getInstance().registerSocketEvents();
					startActivity(new Intent(SetNum.this, MainActivity.class));
					finish();
				}
				else {
					Log.w(TAG, "signInWithCredential:failure", task.getException());
					if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
						dialog.showInvalid();
					}
				}
			}
		});
	}

	private void verifyPhoneNumberWithCode(String verificationId, String code) {
		PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
		signInWithPhoneAuthCredential(credential);
	}
}
