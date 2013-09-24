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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class LoginActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setUpAccountDropDown();
		// Lav setUpAccountDropDown(); når man klikker på spinderen.
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
            //AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                
            alertDialog.setTitle("You need a google account to login!");
            alertDialog.setMessage("You need to create a google account on your phone in order to create an account. " +
            		"Click 'redirect me!' to create a google account. Click 'update' once you added your google account.");
            alertDialog.setCancelable(false);
            
            AlertDialog dialog = alertDialog.create();
            
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                  {            
                      @Override
                      public void onClick(View v)
                      {
                    	  startActivity(new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT));
                      }
                  });
            
            alertDialog.setPositiveButton("Redirect me!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {	
                	//Do  nothing, overwriting later
                }
            });
           
            alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
                	 
                	setUpAccountDropDown();
                }
            });

            alertDialog.show();
            
            
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                  {            
                      @Override
                      public void onClick(View v)
                      {
                      }
                  });
		}
		
	  else {
		Spinner user_name = (Spinner) findViewById(R.id.user_name);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, accountStrings);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		user_name.setAdapter(dataAdapter);

	}		
}





public void login(View view) {
	Spinner user_name = (Spinner) findViewById(R.id.user_name);
	String selectedUser = user_name.getSelectedItem().toString();
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
	obj.add("type", new JsonPrimitive("login"));
	obj.add("email", new JsonPrimitive(selectedUser));
	obj.add("password", new JsonPrimitive(ePassword));

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(String response) {
			mainActivity();
			// TODO Set a flag somewhere to mark that we are logged in
		}
		@Override
		public void onFailure(Throwable error, String content) {
			sendFailureMessage(error, content);
			// TODO Invoke a method that tells the user that either the email or the password was wrong
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
