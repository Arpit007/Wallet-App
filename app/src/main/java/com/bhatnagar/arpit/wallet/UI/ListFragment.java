package com.bhatnagar.arpit.wallet.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bhatnagar.arpit.wallet.Data.Transaction;
import com.bhatnagar.arpit.wallet.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListFragment extends Fragment {
	private String title;
	private ArrayList<Transaction> models;
	private TransactionAdapter adapter;
	private boolean isCredit;

	public ListFragment() {
		models = new ArrayList<>();
		adapter = new TransactionAdapter();
	}

	public static ListFragment newInstance(String Title, boolean isCredit) {
		ListFragment fragment = new ListFragment();
		fragment.title = Title;
		fragment.isCredit = isCredit;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ListView listView = (ListView) view.findViewById(R.id.List);
		listView.setAdapter(adapter);
	}

	public String getTitle() {
		return title;
	}

	public void setModels(ArrayList<Transaction> models) {
		this.models = models;
		getView().findViewById(R.id.None).setVisibility(( ( models.size() == 0 ) ? View.VISIBLE : View.GONE ));
		adapter.notifyDataSetChanged();
	}

	private class TransactionAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return models.size();
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				LayoutInflater inflater = LayoutInflater.from(ListFragment.this.getActivity());
				view = inflater.inflate(R.layout.item_list, viewGroup, false);
			}

			TextView Caption = (TextView) view.findViewById(R.id.Caption);
			TextView Number = (TextView) view.findViewById(R.id.Number);
			TextView Date = (TextView) view.findViewById(R.id.Date);
			TextView Amount = (TextView) view.findViewById(R.id.Amount);

			Transaction transaction = models.get(i);

			if (isCredit) {
				if (transaction.getTopUp()) {
					Caption.setText("Top Up");
					Number.setText("");
				}
				else {
					Caption.setText("From:");
					Number.setText(transaction.getClient());
				}
			}
			else {
				Caption.setText("To:");
				Number.setText(transaction.getVendor());
			}
			Amount.setText("Amount: " + transaction.getAmount());
			String date = new SimpleDateFormat("h:mm a, d MMM yyyy", Locale.getDefault())
					.format(Long.parseLong(Long.toString(transaction.getDate()))).replace("AM", "Am").replace("PM", "Pm");
			Date.setText("Date: " + date);
			return view;
		}
	}
}
