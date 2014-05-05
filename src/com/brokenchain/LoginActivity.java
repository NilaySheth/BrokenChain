package com.brokenchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Button buttonLogin;
	TextView textViewForgotPass, textViewCreateaccount;
	EditText username, password;
	CheckBox rememberme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		initWidgets();

		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (validate()) {

					doLogin();
				}
			}
		});

		textViewForgotPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater li = LayoutInflater
						.from(getApplicationContext());
				View promptsView = li.inflate(R.layout.forgotpass, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						LoginActivity.this);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
				alertDialogBuilder.setTitle("Forgot Password ?");
				alertDialogBuilder.setMessage("Enter your email id");
				// set dialog message
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@SuppressLint("NewApi")
									public void onClick(DialogInterface dialog,
											int id) {
										// get user input and set it to result
										// edit text
										if (!userInput.getText().toString()
												.isEmpty()) {

											doForgotPass(userInput.getText()
													.toString().trim());
										} else {
											Toast.makeText(
													getApplicationContext(),
													"Please enter email id",
													Toast.LENGTH_SHORT).show();
										}

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});

		textViewCreateaccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						Registration.class);
				startActivity(i);
			}
		});

	}

	public void initWidgets() {
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		textViewForgotPass = (TextView) findViewById(R.id.textViewForgotPass);
		textViewCreateaccount = (TextView) findViewById(R.id.textViewCreateaccount);

		rememberme = (CheckBox) findViewById(R.id.rememberme);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		
		SharedPreferences current_user = getApplicationContext().getSharedPreferences("Remember_CURRENT_USER", 0); // 0 - for private mode
    	if(!current_user.getString("username", "").equals(""))
    	{

    		username.setText(current_user.getString("username", ""));
    		password.setText(current_user.getString("password", ""));
    		rememberme.setChecked(true);
    	}
	}

	public void doLogin() {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(LoginActivity.this, "", "Loading...", true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

					String url = Cons.globalUrl + "login.php?username="
							+ username.getText().toString().trim()
							+ "&password="
							+ password.getText().toString().trim();

					HttpPost httpPost = new HttpPost(url);
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

							JSONObject obj = result.getJSONArray("data").getJSONObject(0);
							String id = obj.getString("UserId");
							String name = obj.getString("Username");
							SharedPreferences prefs = getApplicationContext().getSharedPreferences(
								      "CURRENT_USER", Context.MODE_PRIVATE);
							
							Editor e = prefs.edit();
							e.putString("userid", id);
							e.putString("username", name);
							
							if (rememberme.isChecked()) {
								
								SharedPreferences Rprefs = getApplicationContext().getSharedPreferences(
									      "Remember_CURRENT_USER", Context.MODE_PRIVATE);
								
								Editor Re = Rprefs.edit();
								Re.putString("username", username.getText().toString().trim());
								Re.putString("password", password.getText().toString().trim());
								Re.commit();
								
							} else {
								SharedPreferences current_user = getApplicationContext().getSharedPreferences("Remember_CURRENT_USER", 0); // 0 - for private mode
		                    	Editor editor = current_user.edit();	 	            		
		                    	editor.clear();
		 	            		editor.commit(); // commit changes
							}
							
							e.commit();
							Intent i = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(),
									"Username/ Password incorrect. Try again.",
									Toast.LENGTH_SHORT).show();
							username.setText("");
							password.setText("");
							username.setFocusable(true);
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

	public void doForgotPass(final String forgotemail) {
		AsyncTask<Void, Void, JSONObject> waitForCompletion = new AsyncTask<Void, Void, JSONObject>() {

			ProgressDialog pd;
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = ProgressDialog.show(LoginActivity.this, "", "Loading...", true, false);
			}

			@Override
			protected JSONObject doInBackground(Void... params) {

				JSONObject finalResult = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();

					String url = Cons.globalUrl + "forgot.php?username="
							+ forgotemail;

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

		String usernameS = username.getText().toString().trim();
		String passwordS = password.getText().toString().trim();

		if (TextUtils.isEmpty(usernameS)) {
			result = false;
			username.setError("Enter Username");
		}
		if (TextUtils.isEmpty(passwordS)) {
			result = false;
			password.setError("Enter Password");
		}

		return result;
	}

	private void clearError() {
		username.setError(null);
		password.setError(null);
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
		finish();
		System.exit(0);
		
	}
}
