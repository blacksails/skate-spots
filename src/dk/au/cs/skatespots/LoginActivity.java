package dk.au.cs.skatespots;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setUpAccountDropDown();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private void setUpAccountDropDown() {
		
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		ArrayList<String> accountStrings = new ArrayList<String>();
		for (Account a : accounts) {
			accountStrings.add(a.name);
		}
		
		if (accountStrings.isEmpty()) {
			// Lav en dialog som siger at man skal logge ind på en google konto
		} else {
			Spinner user_name = (Spinner) findViewById(R.id.user_name);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, accountStrings);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user_name.setAdapter(dataAdapter);
		}
	}
	
	public void login(View view) {
		mainActivity();
	}
	
	private void mainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
