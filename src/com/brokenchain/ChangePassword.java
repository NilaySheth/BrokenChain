package com.brokenchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ChangePassword extends Activity {

	Button buttonChange;
	ImageView imageBack;
	EditText oldP, newP, confirmP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepass);

		initWidgets();

		buttonChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (validate()) {

					doRegistration();
				}
			}
		});

	}

	public void initWidgets() {
		oldP = (EditText) findViewById(R.id.oldpass);
		newP = (EditText) findViewById(R.id.newpass);
		confirmP = (EditText) findViewById(R.id.confirmpass);
		imageBack = (ImageView) findViewById(R.id.imageBack);

		buttonChange = (Button) findViewById(R.id.buttonChange);
	}

	public void doRegistration() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(ChangePassword.this, "",
						"Loading...", true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

					// http://mihirvadalia.com/vp/services/change_password.php?username=mihirrvadalia&oldpassword=123456&newpassword=Mihir@123

					SharedPreferences current_user = getApplicationContext()
							.getSharedPreferences("CURRENT_USER", 0); // 0 - for
																		// private
																		// mode
					String usernaame = current_user.getString("username", "");

					String url = Cons.globalUrl
							+ "change_password.php?username=" + usernaame
							+ "&oldpassword="
							+ oldP.getText().toString().trim()
							+ "&newpassword="
							+ newP.getText().toString().trim();

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

	private boolean validate() {
		boolean result = true;

		clearError();

		String oldPS = oldP.getText().toString().trim();
		String newPS = newP.getText().toString().trim();
		String confirmPS = confirmP.getText().toString().trim();

		if (TextUtils.isEmpty(oldPS)) {
			result = false;
			oldP.setError("Enter Old Password");
		}
		if (TextUtils.isEmpty(newPS)) {
			result = false;
			newP.setError("Enter New password");
		}
		if (TextUtils.isEmpty(confirmPS)) {
			result = false;
			confirmP.setError("Enter Confirm Password");
		}

		if (!newPS.equals(confirmPS)) {
			result = false;
			confirmP.setError("Passwords doesn't Match");
		}

		return result;
	}

	private void clearError() {
		oldP.setError(null);
		newP.setError(null);
		confirmP.setError(null);
	}
}
