package com.bhatnagar.arpit.wallet.Data;

import org.json.JSONObject;

/**
 * Created by Home Laptop on 10-Nov-17.
 */

public class Transaction {
	private String Amount = "";
	private Long Date = 0L;
	private String Client = "";
	private String Vendor = "";
	private Boolean isTopUp = false;

	public static Transaction parse(JSONObject object) {
		Transaction transaction = new Transaction();
		try {
			transaction.Amount = object.getString("amount");
			transaction.Date = object.getLong("date");
			transaction.Client = object.getString("client");
			transaction.Vendor = object.getString("vendor");
			transaction.isTopUp = transaction.Client.equals("0000000000");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return transaction;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public Long getDate() {
		return Date;
	}

	public void setDate(Long date) {
		Date = date;
	}

	public String getClient() {
		return Client;
	}

	public void setClient(String client) {
		Client = client;
	}

	public String getVendor() {
		return Vendor;
	}

	public void setVendor(String vendor) {
		Vendor = vendor;
	}

	public Boolean getTopUp() {
		return isTopUp;
	}

	public void setTopUp(Boolean topUp) {
		isTopUp = topUp;
	}
}
