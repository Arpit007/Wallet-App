package com.bhatnagar.arpit.wallet.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bhatnagar.arpit.wallet.Data.Account;
import com.bhatnagar.arpit.wallet.Data.Transaction;
import com.bhatnagar.arpit.wallet.R;
import com.bhatnagar.arpit.wallet.Util.Network.Connectivity;
import com.bhatnagar.arpit.wallet.Util.Network.RequestHandler;
import com.bhatnagar.arpit.wallet.Util.Network.ResponseCode;
import com.bhatnagar.arpit.wallet.Util.Security;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History extends AppCompatActivity {
	private FragmentAdapter adapter;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		adapter = new FragmentAdapter(getSupportFragmentManager());
		ViewPager viewPager = (ViewPager) findViewById(R.id.Pager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.Tabs);

		dialog = new ProgressDialog(History.this);
		dialog.setMessage(getString(R.string.WaitMessage));
		dialog.show();

		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);

		fetch();
	}

	public void fetch() {
		if (Connectivity.isOnline()) {
			new RequestHandler(History.this, true, RequestHandler.LONG) {
				@Override
				public void body() {
					StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.HistoryUrl), new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							try {
								JSONObject object = new JSONObject(response);
								ArrayList<Transaction> Credit = new ArrayList<>();
								ArrayList<Transaction> Debit = new ArrayList<>();

								JSONArray creditArray = object.getJSONArray("credit");
								JSONArray debitArray = object.getJSONArray("debit");

								for (int x = 0; x < creditArray.length(); x++) {
									Credit.add(Transaction.parse(creditArray.getJSONObject(x)));
								}

								for (int x = 0; x < debitArray.length(); x++) {
									Debit.add(Transaction.parse(debitArray.getJSONObject(x)));
								}
								adapter.fragments.get(0).setModels(Credit);
								adapter.fragments.get(1).setModels(Debit);

								setResponse(ResponseCode.Success);
							}
							catch (Exception e) {
								e.printStackTrace();
								setResponse(ResponseCode.Internal);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							setResponse(ResponseCode.getExceptionResponseCode(error));
						}
					}) {
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							HashMap<String, String> map = new HashMap<>();
							try {
								map.put("number", Security.encrypt(Account.getPhoneNumber(getApplicationContext())));
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							return map;
						}
					};

					stringRequest.setRetryPolicy(new DefaultRetryPolicy(
							0,
							DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
							DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
					RequestQueue requestQueue = Volley.newRequestQueue(History.this);
					requestQueue.add(stringRequest);
				}

				@Override
				protected void onResponse(ResponseCode code, Object response) {
					super.onResponse(code, response);
					dialog.dismiss();
				}

				@Override
				protected void onSuccess(Object response) {
					super.onSuccess(response);
				}
			}.start();
		}
	}


	class FragmentAdapter extends FragmentStatePagerAdapter {
		ArrayList<ListFragment> fragments;

		public FragmentAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);

			fragments = new ArrayList<>();
			fragments.add(ListFragment.newInstance("Credit", true));
			fragments.add(ListFragment.newInstance("Debit", false));
		}

		@Override
		public Fragment getItem(int i) {
			return fragments.get(i);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments.get(position).getTitle();
		}
	}
}
