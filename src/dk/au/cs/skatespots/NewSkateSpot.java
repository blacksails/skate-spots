package dk.au.cs.skatespots;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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

public class NewSkateSpot extends Activity {
	SkateSpots app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_skate_spot);
		setUpTypeSpinner();
		app = (SkateSpots) this.getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_skate_spot, menu);
		return true;
	}

	public void setUpTypeSpinner(){
		ArrayList<String> skateSpotTypes = new ArrayList<String>();
		skateSpotTypes.add("Street");
		skateSpotTypes.add("Park");
		skateSpotTypes.add("Indoor");

		Spinner spinner = (Spinner) findViewById(R.id.new_skatespot_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, skateSpotTypes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);		
	}

	public void createNewSkateSpot(View view){
		//Finds the author of the SkateSpot
		String author = app.getCurrentUser();

		//Finds the name of the SkateSpot
		EditText skatespot_name = (EditText) findViewById(R.id.new_skatespot_name);
		String name = skatespot_name.getText().toString();

		//Finds the description of the SkateSpot
		EditText skatespot_description = (EditText) findViewById(R.id.new_skatespot_description);
		String description = skatespot_description.getText().toString();

		//Finds the chosen type from the spinner
		Spinner spinner = (Spinner) findViewById(R.id.new_skatespot_spinner);
		String type = spinner.getSelectedItem().toString();

		//Finds the current location
		Location loc = app.getLocation();
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
	
		//Adds needed elements to JsonObject
		JsonObject obj = new JsonObject();
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a secret key
		obj.add("type", new JsonPrimitive(4));
		obj.add("name", new JsonPrimitive(name));
		obj.add("author", new JsonPrimitive(author));
		obj.add("description", new JsonPrimitive(description));
		obj.add("spottype", new JsonPrimitive(type));
		obj.add("latitude", new JsonPrimitive(latitude));
		obj.add("longitude", new JsonPrimitive(longitude));
		obj.add("wifi", app.getCurrentWifi());
		
		Log.w("WIFIARRAY", "FØR ARRAY");
		
		
		//Creates response for the server
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {				
				mainActivity();

				//Display to the user that he created a new Skatespot
				Context context = getApplicationContext();
				CharSequence text = "You have created a new Skatespot!";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();

			}
			@Override
			public void onFailure(Throwable error, String content) {

				//Makes a toast to display failure
				Context context = getApplicationContext();
				CharSequence text = "Failed to create a new Skatespot";
				int duration = Toast.LENGTH_LONG;
				
				Log.w("onFailure", error.fillInStackTrace());

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

