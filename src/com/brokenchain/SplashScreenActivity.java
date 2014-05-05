
package com.brokenchain;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

	String userid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		 new Thread() {
				@Override
				public void run() {
					try {
						sleep(2000);
						
					} catch (Exception e) {
						// do nothing
						e.printStackTrace();
					} finally {
						SharedPreferences current_user = getApplicationContext().getSharedPreferences("CURRENT_USER", 0); // 0 - for private mode
				    	if(!current_user.getString("userid", "").equals(""))
				    	{
				    		userid = current_user.getString("username", "");
				    	}
				    	
				    	if(userid.equals(""))
				    	{
				    		startActivity(new Intent(SplashScreenActivity.this,
									LoginActivity.class));
							SplashScreenActivity.this.finish();
				    	} else {
				    		startActivity(new Intent(SplashScreenActivity.this,
									MainActivity.class));
							SplashScreenActivity.this.finish();
				    	}
						
					}
				}
			}.start();
	}
}