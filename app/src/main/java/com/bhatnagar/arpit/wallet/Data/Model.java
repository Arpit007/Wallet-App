package com.bhatnagar.arpit.wallet.Data;

import com.bhatnagar.arpit.wallet.Util.Security;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * Created by Home Laptop on 18-May-17.
 */

public class Model implements Serializable {
	private String OTP = "";
	private String Vendor = "";
	private String Customer = "";
	private String Amount = "";
	private String CustomerBalance = "";
	private String VendorBalance = "";
	private Long TimeStamp = 0L;
	private boolean Updated = false;
	private QrStatus status = QrStatus.Invalid;

	public static Model createModel(String object) throws Exception {
		return createModel(new JSONObject(object));
	}

	static Model createModel(JSONObject object) throws Exception {
		Model model = new Model();
		if (object.has("i")) {
			String ID = object.getString("i");
			model.Vendor = ID.substring(0, 10);
			model.Customer = ID.substring(10);
		}

		model.TimeStamp = object.has("t") ? object.getLong("t") : new Date().getTime();
		model.Amount = object.has("a") ? object.getString("a") : "0";
		model.OTP = object.has("o") ? object.getString("o") : "";
		model.CustomerBalance = object.has("cb") ? object.getString("cb") : "0";
		model.VendorBalance = object.has("vb") ? object.getString("vb") : "0";
		model.status = object.has("s") ? QrStatus.getQrType(object.getString("s")) : QrStatus.Invalid;

		return model;
	}

	public static Model createModel(String customer, String vendor, String amount, QrStatus status) {
		Model model = new Model();
		model.Customer = customer;
		model.Vendor = vendor;
		model.Amount = amount;
		model.status = status;
		return model;
	}

	public static Model decrypt(String data) throws Exception {
		String Temp = Security.decrypt(data);
		return createModel(Temp);
	}

	public String getCustomerBalance() {
		return CustomerBalance;
	}

	public void setCustomerBalance(String customerBalance) {
		CustomerBalance = customerBalance;
	}

	public String getVendorBalance() {
		return VendorBalance;
	}

	public void setVendorBalance(String vendorBalance) {
		VendorBalance = vendorBalance;
	}

	public String getOTP() {
		return OTP;
	}

	public void setOTP(String OTP) {
		this.OTP = OTP;
	}

	public String getVendor() {
		return Vendor;
	}

	public void setVendor(String vendor) {
		Vendor = vendor;
	}

	public String getCustomer() {
		return Customer;
	}

	public void setCustomer(String customer) {
		Customer = customer;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public Long getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		TimeStamp = timeStamp;
	}

	public boolean isUpdated() {
		return Updated;
	}

	public void setUpdated(boolean updated) {
		Updated = updated;
	}

	public QrStatus getStatus() {
		return status;
	}

	public void setStatus(QrStatus status) {
		this.status = status;
	}

	public String getTransactionID() {
		return Vendor + Customer;
	}

	public JSONObject getJSONObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("i", getTransactionID());
			object.put("a", Amount);
			object.put("tp", status);
			object.put("cb", CustomerBalance);
			object.put("vb", VendorBalance);
			if (!OTP.isEmpty()) {
				object.put("o", OTP);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public String encrypt() throws Exception {
		return Security.encrypt(getJSONObject().toString());
	}

	public void setRandomOTP() {
		Random random = new Random(new Date().getTime());
		OTP = Integer.toString(random.nextInt(1000) + 1000);
	}
}
