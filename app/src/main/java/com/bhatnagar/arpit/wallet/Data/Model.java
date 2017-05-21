package com.bhatnagar.arpit.wallet.Data;

import com.bhatnagar.arpit.wallet.Util.Security;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * Created by Home Laptop on 18-May-17.
 */

public class Model implements Serializable
{
	private String OTP = "";
	private String Vendor = "";
	private String Customer = "";
	private String Amount = "";
	private Long TimeStamp = 0L;

	private boolean Updated = false;
	private QrStatus status = QrStatus.Invalid;

	public static Model createModel(String object) throws Exception
	{
		return createModel(new JSONObject(object));
	}

	public static Model createModel(JSONObject object) throws Exception
	{

		Model model = new Model();
		if (object.has("ID"))
		{
			String ID = object.getString("ID");
			model.Customer = ID.substring(0, 10);
			model.Vendor = ID.substring(10, 20);
			model.TimeStamp = Long.parseLong(ID.substring(20));
		}
		if (object.has("Amount"))
		{
			model.Amount = object.getString("Amount");
		}
		if (object.has("OTP"))
		{
			model.OTP = object.getString("OTP");
		}
		if (object.has("Type"))
		{
			model.status = QrStatus.getQrType(object.getString("Type"));
		}
		return model;
	}

	public static Model createModel(String customer, String vendor, String amount, QrStatus status)
	{
		Model model = new Model();
		model.Customer = customer;
		model.Vendor = vendor;
		model.Amount = amount;
		model.status = status;
		return model;
	}

	public static Model decrypt(String data) throws Exception
	{
		String Temp = Security.decrypt(data);
		return createModel(Temp);
	}

	public String getOTP()
	{
		return OTP;
	}

	public void setOTP(String OTP)
	{
		this.OTP = OTP;
	}

	public String getVendor()
	{
		return Vendor;
	}

	public void setVendor(String vendor)
	{
		Vendor = vendor;
	}

	public String getCustomer()
	{
		return Customer;
	}

	public void setCustomer(String customer)
	{
		Customer = customer;
	}

	public String getAmount()
	{
		return Amount;
	}

	public void setAmount(String amount)
	{
		Amount = amount;
	}

	public Long getTimeStamp()
	{
		return TimeStamp;
	}

	public void setTimeStamp(Long timeStamp)
	{
		TimeStamp = timeStamp;
	}

	public boolean isUpdated()
	{
		return Updated;
	}

	public void setUpdated(boolean updated)
	{
		Updated = updated;
	}

	public QrStatus getStatus()
	{
		return status;
	}

	public void setStatus(QrStatus status)
	{
		this.status = status;
	}

	public String getTransactionID()
	{
		return Customer + Vendor + TimeStamp;
	}

	public JSONObject getJSONObject()
	{
		JSONObject object = new JSONObject();
		try
		{
			object.put("ID", getTransactionID());
			object.put("Amount", Amount);
			object.put("Type", status);
			if (!OTP.isEmpty())
			{
				object.put("OTP", OTP);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return object;
	}

	public String encrypt() throws Exception
	{
		return Security.encrypt(getJSONObject().toString());
	}

	public void setRandomOTP()
	{
		Random random = new Random(new Date().getTime());
		OTP = Integer.toString(random.nextInt(10000));
	}

	public void setID(String ID)
	{
		Customer = ID.substring(0, 10);
		Vendor = ID.substring(10, 20);
		TimeStamp = Long.parseLong(ID.substring(20));
	}
}
