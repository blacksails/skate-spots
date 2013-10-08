package dk.au.cs.skatespots;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class CreateUserActivity extends Activity {
	
	private SkateSpots app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		setUpAccountDropDown();	
		app = (SkateSpots) this.getApplication();
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
			// Lav en dialog som siger at man skal logge ind paa en google konto
		} else {
			Spinner user_name = (Spinner) findViewById(R.id.user_name);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, accountStrings);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user_name.setAdapter(dataAdapter);
		}
	}
	
	public void checkEligibilityLogin(View view){
		Spinner user_name = (Spinner) findViewById(R.id.user_name);
		String email = user_name.getSelectedItem().toString();
		app.setCurrentUser(email);
		EditText display_name = (EditText) findViewById(R.id.display_name);
		String displayname = display_name.getText().toString();
		app.setCurrentDisplayName(displayname);
		EditText password = (EditText) findViewById(R.id.password);
		String uePassword = password.getText().toString();
		String ePassword = null;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String bluid = bluetoothAdapter.getAddress();
		
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(uePassword.getBytes(), 0, uePassword.length());
			ePassword = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		JsonObject obj = new JsonObject();
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a secret key
		obj.add("type", new JsonPrimitive(1));
		obj.add("email", new JsonPrimitive(email));
		obj.add("password", new JsonPrimitive(ePassword));
		obj.add("displayname", new JsonPrimitive(displayname));
		obj.add("bluid", new JsonPrimitive(bluid));
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			public void onSuccess(String response) {
				// TODO Tell the user that he succeeded
				mainActivity();
			}
			public void onFailure(Throwable e, String response) {
				//Makes a toast to display that an account already exists
				Context context = getApplicationContext();
				CharSequence text = "An account with that email already exists";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}
		};
		
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}
	
	private void mainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
