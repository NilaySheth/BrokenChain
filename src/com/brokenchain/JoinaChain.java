package com.brokenchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JoinaChain extends Activity {

	ImageView imageBack;
	TextView textRemain;
	EditText editTextMessage;
	int count = 100;
	String msg_id = "";
	String message = "";
	Button buttonSend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joinchain);

		initWidgets();

		if(getIntent().hasExtra("msg_id"))
		{
			msg_id = getIntent().getStringExtra("msg_id");
			message = getIntent().getStringExtra("msg");
			
			editTextMessage.setText(message);
			textRemain.setText(String.valueOf((count - editTextMessage.length()))
					+ " Characters Remain");
		}
		imageBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		editTextMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				textRemain.setText(String.valueOf((count - s.length()))
						+ " Characters Remain");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		buttonSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!editTextMessage.getText().toString().trim().equals(""))
				{
					doStartChain();
				} else if(editTextMessage.getText().toString().trim().equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please enter message", Toast.LENGTH_SHORT).show();
				} 
			}
		});
	}

	public void initWidgets() {
		imageBack = (ImageView) findViewById(R.id.imageBack);
		editTextMessage = (EditText) findViewById(R.id.editTextMessage);
		textRemain = (TextView) findViewById(R.id.textRemain);
		buttonSend = (Button) findViewById(R.id.buttonSend);
	}
	
	public void doStartChain() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(JoinaChain.this, "", "Loading...",
						true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

					//http://mihirvadalia.com/vp/services/create.php?message=hi,%20%20first%20message&userid=2

					SharedPreferences current_user = getApplicationContext()
							.getSharedPreferences("CURRENT_USER", 0); // 0 - for
																		// private
																		// mode
					String userid = current_user.getString("userid", "");

					String url = Cons.globalUrl
							+ "create.php?message=" + URLEncoder.encode(editTextMessage.getText().toString().trim(),"UTF-8")
							+ "&userid="+ userid
							+ "&msg_update_id="+ msg_id;

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
							Toast.makeText(getApplicationContext(),
									result.getString("msg"), Toast.LENGTH_SHORT)
									.show();
							Intent i = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(),
									"Error Occured. Try Again!",
									Toast.LENGTH_SHORT).show();
						}
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

	public boolean isInternetAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
			return (cm.getActiveNetworkInfo().isConnected() && cm
					.getActiveNetworkInfo().isAvailable());
		else
			return false;
	}
}
