package com.brokenchain.POJO;

import java.util.ArrayList;

public class UpdatesPOJO {
	public String original_msg, updated_msg;
	public ArrayList<UpdatesPOJOList> list_messages;

	public UpdatesPOJO(String original_msg, String updated_msg,ArrayList<UpdatesPOJOList> list_messages) {
		// TODO Auto-generated constructor stub
		this.list_messages = list_messages;
		this.original_msg = original_msg;
		this.updated_msg = updated_msg;
	}
}