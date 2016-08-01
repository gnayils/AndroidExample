package com.itheima.safeguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {

	private ListView lv_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_select_contact);
		this.getActionBar().setTitle("选择联系人");
		this.lv_contact = (ListView) this.findViewById(R.id.lv_contact);
		final List<Map<String, String>> contactList = this.getContact();
		this.lv_contact.setAdapter(new SimpleAdapter(this, contactList,
				android.R.layout.simple_list_item_2, new String[] {
						"contactName", "phoneNumber" }, new int[] {
						android.R.id.text1, android.R.id.text2 }));
		this.lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phoneNumber = contactList.get(position).get("phoneNumber");
				Intent data = new Intent();
				data.putExtra("phoneNumber", phoneNumber);
				SelectContactActivity.this.setResult(0, data);
				SelectContactActivity.this.finish();
			}
		});
	}

	private List<Map<String, String>> getContact() {
		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
		Cursor contactCursor = this.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (contactCursor.moveToNext()) {
			Map<String, String> contactMap = new HashMap<String, String>();
			String contactId = contactCursor.getString(contactCursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String contactName = contactCursor.getString(contactCursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			Cursor phoneCursor = this.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			if (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				contactMap.put("contactName", contactName);
				contactMap.put("phoneNumber", phoneNumber);
			}
			phoneCursor.close();
			contactList.add(contactMap);
		}
		contactCursor.close();
		return contactList;
	}
}
