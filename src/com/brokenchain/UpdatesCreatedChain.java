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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brokenchain.POJO.UpdatesPOJO;
import com.brokenchain.POJO.UpdatesPOJOList;

public class UpdatesCreatedChain extends Activity {

	ImageView imageBack;
	public static ArrayList<UpdatesPOJO> listUpdateMessages;
	UpdatesListAdapter mAdapter;
	ListView listViewContest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatescreatedchain);

		initWidgets();

		imageBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listViewContest.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				if (listUpdateMessages.size() > 0) {

					Intent i = new Intent(getApplicationContext(),
							UpdateMessageHistory.class);
					i.putExtra("position", pos);
					startActivity(i);
				}
			}
		});
		doUpdates();
	}

	public void initWidgets() {
		imageBack = (ImageView) findViewById(R.id.imageBack);
		listUpdateMessages = new ArrayList<UpdatesPOJO>();
		listViewContest = (ListView) findViewById(R.id.listViewUpdates);
		mAdapter = new UpdatesListAdapter(UpdatesCreatedChain.this);

		listViewContest.setAdapter(mAdapter);
	}

	public void doUpdates() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(UpdatesCreatedChain.this, "",
						"Loading...", true, false);
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

					 String url = Cons.globalUrl + "chainupdate.php?userid="
					 + userid;

					//String url = Cons.globalUrl + "chainupdate.php?userid=3";

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

								String update_message = "";
								String original_message = main.getJSONObject(
										"0").getString("msg_text");
								// String original_username =
								// main.getJSONObject("0").getString("msg_text");
								// For Username
								ArrayList<UpdatesPOJOList> listO = new ArrayList<UpdatesPOJOList>();
								listO.add(new UpdatesPOJOList("",
										original_message));

								JSONArray chain = main.getJSONArray("chian");
								int k = chain.length();
								for (int j = 0; j < k; j++) {

									listO.add(new UpdatesPOJOList(chain
											.getJSONObject(j).getString(
													"Username"), chain
											.getJSONObject(j).getString(
													"msg_text")));

									if (j == k - 1) {
										update_message = chain.getJSONObject(0)
												.getString("msg_text");
									}
								}
								listUpdateMessages
										.add(new UpdatesPOJO(original_message,
												update_message, listO));

							}
						} else {
							Toast.makeText(getApplicationContext(),
									result.getString("msg"), Toast.LENGTH_SHORT)
									.show();
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

			}
		};
		if (isInternetAvailable()) {
			waitForCompletion.execute();
		} else {
			Toast.makeText(getApplicationContext(),
					"Internet service not available", Toast.LENGTH_LONG).show();
		}
	}

	public class UpdatesListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public UpdatesListAdapter(Activity context) {
			inflater = (LayoutInflater) UpdatesCreatedChain.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return listUpdateMessages.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_updatemessage,
						null);
			}

			UpdatesPOJO item = listUpdateMessages.get(position);

			final ViewHolder holder = new ViewHolder();
			holder.txtOriginal = (TextView) convertView
					.findViewById(R.id.txtOriginal);
			holder.txtShortMessage = (TextView) convertView
					.findViewById(R.id.txtShortMessage);

			holder.txtOriginal.setText(item.original_msg);
			holder.txtShortMessage.setText(item.updated_msg);

			return convertView;
		}

		private class ViewHolder {

			TextView txtOriginal, txtShortMessage;
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