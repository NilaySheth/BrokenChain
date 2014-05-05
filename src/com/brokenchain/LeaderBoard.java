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

import com.brokenchain.POJO.LeaderBoardpojo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LeaderBoard extends Activity {

	ImageView imageBack;
	TextView textHighestScore;
	public static ArrayList<LeaderBoardpojo> listMessages;
	LeaderBoardListAdapter mAdapter;
	ListView listViewContest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);

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
				if (listMessages.size() > 0) {

					LayoutInflater li = LayoutInflater
							.from(getApplicationContext());
					View promptsView = li.inflate(R.layout.leaderboarddialog, null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							LeaderBoard.this);

					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(promptsView);

					final TextView txtOriginal = (TextView) promptsView
							.findViewById(R.id.txtOriginal);
					final TextView txtShortMessage = (TextView) promptsView
							.findViewById(R.id.txtShortMessage);
					
					txtShortMessage.setText(listMessages.get(pos).msg_text);
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			}
		});
		
//		textHighestScore.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				LayoutInflater li = LayoutInflater
//						.from(getApplicationContext());
//				View promptsView = li.inflate(R.layout.leaderboarddialog, null);
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						LeaderBoard.this);
//
//				// set prompts.xml to alertdialog builder
//				alertDialogBuilder.setView(promptsView);
//
//				final TextView txtOriginal = (TextView) promptsView
//						.findViewById(R.id.txtOriginal);
//				final TextView txtShortMessage = (TextView) promptsView
//						.findViewById(R.id.txtShortMessage);
//				// set dialog message
//
//				// create alert dialog
//				AlertDialog alertDialog = alertDialogBuilder.create();
//
//				// show it
//				alertDialog.show();
//			}
//		});
		doLeaderBoard();
	}

	public void initWidgets() {
		imageBack = (ImageView) findViewById(R.id.imageBack);
		listMessages = new ArrayList<LeaderBoardpojo>();
		textHighestScore = (TextView) findViewById(R.id.textHighestScore);
		listViewContest = (ListView) findViewById(R.id.listViewLeaderboard);
		mAdapter = new LeaderBoardListAdapter(LeaderBoard.this);

		listViewContest.setAdapter(mAdapter);
	}

	public void doLeaderBoard() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(LeaderBoard.this, "", "Loading...",
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

					String url = Cons.globalUrl + "leaderboard.php?userid="
							+ userid;

					// String url = Cons.globalUrl + "leaderboard.php?userid=8";

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

							String score = result.getJSONArray("userdetail")
									.getJSONObject(0)
									.getString("msg_s_counter");
							textHighestScore.setText("Your Highest Score is "
									+ Integer.parseInt(score) + " Points");

							JSONArray data = result.getJSONArray("data");
							int n = data.length();

							for (int i = 0; i < n; i++) {
								JSONObject main = data.getJSONObject(i);
								listMessages.add(new LeaderBoardpojo(main
										.getString("msg_text"), main
										.getString("msg_s_counter"), main
										.getString("Username"), ""));

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

	public class LeaderBoardListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public LeaderBoardListAdapter(Activity context) {
			inflater = (LayoutInflater) LeaderBoard.this
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
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_leaderboard, null);
			}

			LeaderBoardpojo item = listMessages.get(position);

			final ViewHolder holder = new ViewHolder();
			holder.txtuser = (TextView) convertView
					.findViewById(R.id.txtUsrname);
			holder.txtmessage = (TextView) convertView
					.findViewById(R.id.txtShortMessage);
			holder.txtpoints = (TextView) convertView
					.findViewById(R.id.imgJoin);

			holder.txtuser.setText(item.msg_username);
			holder.txtmessage.setText(item.msg_text);
			holder.txtpoints.setText(item.msg_score);

			return convertView;
		}

		private class ViewHolder {

			TextView txtuser, txtmessage, txtpoints;
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