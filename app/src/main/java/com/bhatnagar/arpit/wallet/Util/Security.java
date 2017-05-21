package com.bhatnagar.arpit.wallet.Util;

import android.util.Base64;

import com.bhatnagar.arpit.wallet.Data.Account;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Home Laptop on 12-May-17.
 */

public class Security
{
	public static SecretKeySpec generateKey(String password) throws Exception
	{
		return new SecretKeySpec(password.getBytes(), "AES");
	}

	public static String encrypt(String message) throws Exception
	{
		SecretKeySpec secret = generateKey(Account.SecurityKey);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
		return Base64.encodeToString(cipherText, Base64.DEFAULT);
	}

	public static String decrypt(String message) throws Exception
	{
		byte[] cipherText = message.getBytes();
		SecretKeySpec secret = generateKey(Account.SecurityKey);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret);
		return new String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)), "UTF-8");
	}
}