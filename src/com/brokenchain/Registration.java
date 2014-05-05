package com.brokenchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class Registration extends Activity {

	Button buttonLogin;
	ImageView imageBack;
	EditText firstname, lastname, username, email, password, confirmpassword;
	GoogleCloudMessaging gcm;
	String regid;
	String SENDER_ID = "817544987904";

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);

		initWidgets();

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(getApplicationContext());
			// mDisplay.setText(regid);
			// Toast.makeText(getApplicationContext(), regid, Toast.LENGTH_LONG)
			// .show();
			if (regid.isEmpty()) {
				getRegistrationId();
			}

		}

		imageBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		buttonLogin.setOnClickListener(new OnClickListener() {

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
		imageBack = (ImageView) findViewById(R.id.imageBack);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);

		firstname = (EditText) findViewById(R.id.firstname);
		lastname = (EditText) findViewById(R.id.lastname);
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
	}

	public void getRegistrationId() {
		class RegisterBackground extends AsyncTask<String, String, String> {

			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(SENDER_ID);
					msg = "Dvice registered, registration ID=" + regid;
					Log.d("111", msg);
					// sendRegistrationIdToBackend();

					// Persist the regID - no need to register again.
					storeRegistrationId(getApplicationContext(), regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

//				Toast.makeText(getApplicationContext(), regid,
//						Toast.LENGTH_LONG).show();
				Log.i("RegId", regid);
			}

			private void storeRegistrationId(Context context, String regId) {
				final SharedPreferences prefs = getGCMPreferences(context);
				int appVersion = getAppVersion(context);
				Log.i("DEMO", "Saving regId on app version " + appVersion);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(PROPERTY_REG_ID, regId);
				editor.putInt(PROPERTY_APP_VERSION, appVersion);
				editor.commit();
			}
		}

		new RegisterBackground().execute();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("DEMO", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
			String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i("DEMO", "Registration not found.");
			return "";
		}

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("DEMO", "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {

		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public void doRegistration() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(Registration.this, "", "Loading...",
						true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

//					String url = Cons.globalUrl + "register.php?firstname="
//							+ firstname.getText().toString().trim()
//							+ "&lastname="
//							+ lastname.getText().toString().trim()
//							+ "&username="
//							+ username.getText().toString().trim() + "&email="
//							+ email.getText().toString().trim() + "&password="
//							+ password.getText().toString().trim();
//
//					HttpGet httpPost = new HttpGet(url);
//					Log.i("URL", url);

					HttpPost post = new HttpPost(Cons.globalUrl
							+ "register.php");
					List<NameValuePair> pairs = new ArrayList<NameValuePair>();

//					JSONObject json2 = new JSONObject();
//					json2.put("firstname", firstname.getText().toString().trim());
//					json2.put("lastname", lastname.getText().toString().trim());
//					json2.put("username", username.getText().toString().trim());
//					json2.put("email", email.getText().toString().trim());
//					json2.put("password", password.getText().toString().trim());
//					json2.put("DeviceId", regid);
//					
//					Log.i("registed JSON", json2.toString());
					//pairs.add(new BasicNameValuePair("data", json2.toString()));
					
					pairs.add(new BasicNameValuePair("firstname", firstname.getText().toString().trim()));
					pairs.add(new BasicNameValuePair("lastname", lastname.getText().toString().trim()));
					pairs.add(new BasicNameValuePair("username", username.getText().toString().trim()));
					pairs.add(new BasicNameValuePair("email", email.getText().toString().trim()));
					pairs.add(new BasicNameValuePair("password", password.getText().toString().trim()));
					pairs.add(new BasicNameValuePair("DeviceType", "android"));
					pairs.add(new BasicNameValuePair("DeviceId", regid));
					
		            post.setHeader("Content-type", "application/x-www-form-urlencoded");  
		            post.setEntity(new UrlEncodedFormEntity(pairs));
		            
					HttpResponse response = httpClient.execute(post);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent(), "UTF-8"));
					String json = reader.readLine();
					//Log.i("Response JSON", "" + json);
					
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
							
							SharedPreferences prefs = getApplicationContext().getSharedPreferences(
								      "CURRENT_USER", Context.MODE_PRIVATE);
							
							Editor e = prefs.edit();
							e.putString("userid", result.getString("UserId"));
							e.commit();
							
							Intent i = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(),
									result.getString("msg"), Toast.LENGTH_SHORT)
									.show();
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

		String firstnameS = firstname.getText().toString().trim();
		String lastnameS = lastname.getText().toString().trim();
		String usernameS = username.getText().toString().trim();
		String emailS = email.getText().toString().trim();
		String passwordS = password.getText().toString().trim();
		String confirmpasswordS = confirmpassword.getText().toString().trim();

		if (TextUtils.isEmpty(firstnameS)) {
			result = false;
			firstname.setError("Enter Firstname");
		}
		if (TextUtils.isEmpty(lastnameS)) {
			result = false;
			lastname.setError("Enter Lastname");
		}
		if (TextUtils.isEmpty(usernameS)) {
			result = false;
			username.setError("Enter Username");
		}
		if (TextUtils.isEmpty(emailS)) {
			result = false;
			email.setError("Enter Email Id");
		}
		if (TextUtils.isEmpty(passwordS)) {
			result = false;
			password.setError("Enter Password");
		}
		if (TextUtils.isEmpty(confirmpasswordS)) {
			result = false;
			confirmpassword.setError("Enter Confirm Password");
		}
		if (!TextUtils.isEmpty(emailS)) {
			result = android.util.Patterns.EMAIL_ADDRESS.matcher(emailS)
					.matches();

			if (!result)
				email.setError("Invalid Email Id");
		}
		if (!passwordS.equals(confirmpasswordS)) {
			result = false;
			confirmpassword.setError("Passwords doesn't Match");
		}

		return result;
	}

	private void clearError() {
		firstname.setError(null);
		lastname.setError(null);
		username.setError(null);
		email.setError(null);
		password.setError(null);
		confirmpassword.setError(null);
	}
}
