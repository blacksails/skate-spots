package dk.au.cs.skatespots;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class LoginActivity extends Activity {


	final Context context = this;
	public static String selectedUser;

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

		//Hvis der ingen google accounts eksisterer på telefonen
		if (accountStrings.isEmpty()) {
			
			//AlertDialog oprettes
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setTitle("You need a google account to login!");
			alertDialog.setMessage("You need to create a google account on your phone in order to create an account. " +
					"Click 'redirect me!' to create a google account. Click 'update' once you added your google account.");
			alertDialog.setCancelable(false);

			//Knap til redirect. Bliver overwritet, så den ikke kan lukkes når den bliver klikket.
			alertDialog.setPositiveButton("Redirect me!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {	
					context.startActivity(new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT));
				}
			});

			//Knap til update. Der bliver tjekket for accounts når denne trykkes.
			alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					setUpAccountDropDown();
				}
			});
			alertDialog.show();
		}

		//Hvis der eksisterer google accounts på telefonen
		else {
			Spinner user_name = (Spinner) findViewById(R.id.user_name);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, accountStrings);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user_name.setAdapter(dataAdapter);

		}		
	}


	public void login(View view) {
		Spinner user_name = (Spinner) findViewById(R.id.user_name);
		selectedUser = user_name.getSelectedItem().toString();
		EditText password = (EditText) findViewById(R.id.password);
		String uePassword = password.getText().toString();
		String ePassword = null;

		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(uePassword.getBytes(), 0, uePassword.length());
			ePassword = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		JsonObject obj = new JsonObject();
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a key
		obj.add("type", new JsonPrimitive(0));
		obj.add("email", new JsonPrimitive(selectedUser));
		obj.add("password", new JsonPrimitive(ePassword));

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				mainActivity();
				
				//Makes a toast to display success
				Context context = getApplicationContext();
				CharSequence text = "You succesfully logged in as " + selectedUser;
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();

			}
			@Override
			public void onFailure(Throwable error, String content) {
				
				//Makes a toast to display failure
				Context context = getApplicationContext();
				CharSequence text = "Username or password was wrong";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			}
		};	

		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	public void createUser(View view){
		createUserActivity();
	}

	private void mainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void createUserActivity() {
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivity(intent);
	}

}
