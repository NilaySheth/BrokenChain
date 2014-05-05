package com.brokenchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brokenchain.POJO.InboxPOJO;

public class ChainInbox extends Activity {

	ImageView imageBack;
	TextView textHighestScore;
	public static ArrayList<InboxPOJO> listMessages;
	InboxListAdapter mAdapter;
	ListView listViewContest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chainbox);

		initWidgets();

		imageBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

//		listViewContest.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
//					long arg3) {
//				// TODO Auto-generated method stub
//				if (listMessages.size() > 0) {
//
//					Intent i = new Intent(getApplicationContext(),
//							JoinaChain.class);
//					i.putExtra("msg_id", pos);
//					startActivity(i);
//				}
//			}
//		});

		doLeaderBoard();
	}

	public void initWidgets() {
		imageBack = (ImageView) findViewById(R.id.imageBack);
		listMessages = new ArrayList<InboxPOJO>();
		listViewContest = (ListView) findViewById(R.id.listViewInbox);
		mAdapter = new InboxListAdapter(ChainInbox.this);

		listViewContest.setAdapter(mAdapter);
	}

	public void doLeaderBoard() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(ChainInbox.this, "", "Loading...",
						true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

					// http://mihirvadalia.com/vp/services/create.php?message=hi,%20%20first%20message&userid=2

					SharedPreferences current_user = getApplicationContext()
							.getSharedPreferences("CURRENT_USER", 0); // 0 - for
																		// private
																		// mode
					String userid = current_user.getString("userid", "");

					 String url = Cons.globalUrl + "inbox.php?userid="
					 + userid;

					//String url = Cons.globalUrl + "inbox.php?userid=17";

					HttpGet httpPost = new HttpGet(url);
					Log.i("URL", url);

					HttpResponse response = httpClient.execute(httpPost);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent(), "UTF-8"));
					String json = reader.readLine();
					JSONTokener tokener = new JSONTokener(json);
					finalResult = new JSONObject(tokener);
					Log.i("Response", "" + finalResult.toString());

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Log.i("Exception",
							"ClientProtocolException : " + e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("Exception", "IOException : " + e.getMessage());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Log.i("Exception", "Http Response : " + e.getMessage());
				}
				return finalResult;
			};

			@Override
			protected void onPostExecute(JSONObject result) {

				pd.dismiss();
				if (result != null) {
					try {
						if (result.getString("error").equals("0")) {


							JSONArray data = result.getJSONArray("data");
							int n = data.length();

							for (int i = 0; i < n; i++) {
								JSONObject main = data.getJSONObject(i);
								listMessages.add(new InboxPOJO(main
										.getString("msg_id"),main
										.getString("msg_text"), main
										.getString("Username")));

							}
						} else {
							
							Toast.makeText(getApplicationContext(),
									result.getString("msg"),
									Toast.LENGTH_SHORT).show();
						}

						mAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Error Occured. Try Again!", Toast.LENGTH_SHORT)
							.show();
				}
				mAdapter.notifyDataSetChanged();

			}
		};
		if (isInternetAvailable()) {
			waitForCompletion.execute();
		} else {
			Toast.makeText(getApplicationContext(),
					"Internet service not available", Toast.LENGTH_LONG).show();
		}
	}

	public class InboxListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public InboxListAdapter(Activity context) {
			inflater = (LayoutInflater) ChainInbox.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return listMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_inbox, null);
			}

			final InboxPOJO item = listMessages.get(position);

			final ViewHolder holder = new ViewHolder();
			holder.txtuser = (TextView) convertView
					.findViewById(R.id.txtUsername);
			holder.txtmessage = (TextView) convertView
					.findViewById(R.id.txtShortMessage);
			holder.txtjoin = (ImageView) convertView
					.findViewById(R.id.imgJoin);

			holder.txtuser.setText(item.msg_username);
			holder.txtmessage.setText(item.msg_text);
			holder.txtjoin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(getApplicationContext(),
							JoinaChain.class);
					i.putExtra("msg_id", item.msg_id);
					i.putExtra("msg", item.msg_text);
					startActivity(i);
				}
			});

			return convertView;
		}

		private class ViewHolder {

			TextView txtuser, txtmessage;
			ImageView txtjoin;
		}
	}

	public boolean isInternetAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
			return (cm.getActiveNetworkInfo().isConnected() && cm
					.getActiveNetworkInfo().isAvailable());
		else
			return false;
	}
}
