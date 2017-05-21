package com.bhatnagar.arpit.wallet.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Home Laptop on 18-May-17.
 */

public class SqlDatabase extends SQLiteOpenHelper
{
	private static final String DbName = "Wallet";
	private static SqlDatabase database;
	private SQLiteDatabase sqLiteDatabase;

	private SqlDatabase(Context context)
	{
		super(context, DbName, null, 1);
		database = this;
		sqLiteDatabase = context.openOrCreateDatabase(DbName, android.content.Context.MODE_PRIVATE, null);
		onCreate(sqLiteDatabase);
	}

	public static synchronized SQLiteDatabase getSqLiteDatabase(Context context)
	{
		if (database == null)
		{
			database = new SqlDatabase(context);
		}
		return database.sqLiteDatabase;
	}

	public static synchronized SqlDatabase getInstance(Context context)
	{
		if (database == null)
		{
			database = new SqlDatabase(context);
		}
		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		String Query = "CREATE TABLE IF NOT EXISTS Transaction(ID INT primary key, Amount INT, Status INT DEFAULT 0);";
		sqLiteDatabase.execSQL(Query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
	{
		String Query = "DROP TABLE IF EXISTS Transaction;";
		sqLiteDatabase.execSQL(Query);
	}

	public void closeDatabase()
	{
		if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
		{
			sqLiteDatabase.close();
			sqLiteDatabase = null;
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		closeDatabase();

		super.finalize();
	}

	public void Reset()
	{
		String Query = "DELETE FROM Transaction;";
		sqLiteDatabase.execSQL(Query);
	}

	public Model getTransaction(String ID)
	{
		Model model = new Model();
		String Query = "SELECT * FROM Transaction WHERE Status = 0;";
		Cursor cursor = sqLiteDatabase.rawQuery(Query, new String[]{ ID });
		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			model.setID(cursor.getString(0));
			model.setAmount(cursor.getString(1));
			model.setUpdated(cursor.getInt(2) == 1);
		}
		cursor.close();
		return model;
	}

	public ArrayList<Model> getUnUpdatedTransaction()
	{
		ArrayList<Model> models = new ArrayList<>();
		String Query = "SELECT * FROM Transaction WHERE Status = 0;";
		Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			do
			{
				Model model = new Model();
				model.setID(cursor.getString(0));
				model.setAmount(cursor.getString(1));
				model.setUpdated(cursor.getInt(2) == 1);
				models.add(model);
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		return models;
	}

	public void addOrUpdateTransaction(Model model)
	{
		String ID = model.getTransactionID();
		ContentValues values = new ContentValues();
		values.put("ID", ID);
		values.put("Amount", model.getAmount());
		values.put("Status", model.isUpdated() ? 1 : 0);

		if (sqLiteDatabase.update("Transaction", values, "ID = " + ID, null) < 1)
		{
			sqLiteDatabase.insert("Transaction", null, values);
		}
	}

	public void addOrUpdateTransaction(ArrayList<Model> models)
	{
		for (Model model : models)
		{
			String ID = model.getTransactionID();
			ContentValues values = new ContentValues();
			values.put("ID", ID);
			values.put("Amount", model.getAmount());
			values.put("Status", model.isUpdated() ? 1 : 0);

			if (sqLiteDatabase.update("Transaction", values, "ID = " + ID, null) < 1)
			{
				sqLiteDatabase.insert("Transaction", null, values);
			}
		}
	}

	public void deleteTransaction(String TransactionID)
	{
		String Query = "DELETE FROM Transaction WHERE ID = ?;";
		sqLiteDatabase.rawQuery(Query, new String[]{ TransactionID });
	}
}
