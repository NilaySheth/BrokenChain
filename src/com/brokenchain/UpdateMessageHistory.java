package com.brokenchain;

import java.util.ArrayList;

import com.brokenchain.POJO.UpdatesPOJOList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateMessageHistory extends Activity {

	ImageView imageBack;
	ArrayList<UpdatesPOJOList> listUpdateMessages;
	UpdatesListAdapter mAdapter;
	ListView listViewContest;
	int position = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatemessagehistory);

		initWidgets();
		
		if(getIntent().hasExtra("position"))
		{
			position = getIntent().getIntExtra("position", 0);
			listUpdateMessages.addAll(UpdatesCreatedChain.listUpdateMessages.get(position).list_messages);
			mAdapter.notifyDataSetChanged();
		}
		
		

		imageBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void initWidgets() {
		imageBack = (ImageView) findViewById(R.id.imageBack);
		listUpdateMessages = new ArrayList<UpdatesPOJOList>();
		listViewContest = (ListView) findViewById(R.id.listViewFavorites);
		mAdapter = new UpdatesListAdapter(UpdateMessageHistory.this);

		listViewContest.setAdapter(mAdapter);
	}

	public class UpdatesListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public UpdatesListAdapter(Activity context) {
			inflater = (LayoutInflater) UpdateMessageHistory.this
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
				convertView = inflater.inflate(R.layout.item_updatehistory,
						null);
			}

			UpdatesPOJOList item = listUpdateMessages.get(position);

			final ViewHolder holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.textView2);

			holder.txtName.setText("Update By: "+item.name);
			holder.txtMessage.setText(item.msg);

			return convertView;
		}

		private class ViewHolder {

			TextView txtName, txtMessage;
		}
	}
}
