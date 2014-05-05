package com.brokenchain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	ImageView imagecreatechain, imagechaininbox, imagehainupdates,
			imageleaderboard;
	TextView back, changePass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initWidgets();

		imagecreatechain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						CreateChain.class);
				startActivity(i);
			}
		});

		imagechaininbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), ChainInbox.class);
				startActivity(i);
			}
		});

		imagehainupdates.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						UpdatesCreatedChain.class);
				startActivity(i);
			}
		});

		imageleaderboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						LeaderBoard.class);
				startActivity(i);
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		changePass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						ChangePassword.class);
				startActivity(i);
			}
		});
	}

	public void initWidgets() {
		imagecreatechain = (ImageView) findViewById(R.id.imagcreatechain);
		imagechaininbox = (ImageView) findViewById(R.id.imagechaininbox);
		imagehainupdates = (ImageView) findViewById(R.id.imagehainupdates);
		imageleaderboard = (ImageView) findViewById(R.id.imageleaderboard);

		back = (TextView) findViewById(R.id.textViewBack);
		changePass = (TextView) findViewById(R.id.textViewChange);
	}

	@Override
	public void onBackPressed() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		// set title
		alertDialogBuilder.setTitle("Broken Chain");

		// set dialog message
		alertDialogBuilder
				.setMessage("Are you sure you want to Log Out ?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								SharedPreferences current_user = getApplicationContext()
										.getSharedPreferences("CURRENT_USER", 0); // 0
																					// -
																					// for
																					// private
																					// mode
								Editor editor = current_user.edit();
								editor.clear();
								editor.commit(); // commit changes
								MainActivity.this.finish();

								Intent i = new Intent(MainActivity.this,
										LoginActivity.class);
								startActivity(i);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();
						Intent i = new Intent();
						i.setAction(Intent.ACTION_MAIN);
						i.addCategory(Intent.CATEGORY_HOME);
						startActivity(i);
						finish();
						System.exit(0);
					}
				});

		// show it
		alertDialogBuilder.show();

	}
}
