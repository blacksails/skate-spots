package dk.au.cs.skatespots;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends Activity implements ConnectionCallbacks, 
OnConnectionFailedListener,
LocationListener
//OnAddGeofencesResultListener 
{
	LocationClient locationClient;
	private static GoogleMap map;
	private Location location;	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
		}
	}


	@Override
	public void onConnected(Bundle arg0) {
		location = locationClient.getLastLocation();

		//Zooms in on our current position
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		map.animateCamera(cameraUpdate);

		//Adds a marker of our current position to our map.
		map.addMarker(new MarkerOptions()
		.position(latLng)
		.title(LoginActivity.selectedUser)); //Cannot currently get email, due to it being commented out.

		//Adds a toast that pops up with our current coordinates once connected.
		String currentCoordinates = latLng.toString();
		Context context = getApplicationContext();
		CharSequence text = currentCoordinates;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}


	public void sendMyLocation(View view){
		String email = LoginActivity.selectedUser;

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();


		JsonObject obj = new JsonObject();
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a secret key
		obj.add("type", new JsonPrimitive(2));
		obj.add("email", new JsonPrimitive(email));
		obj.add("latitude", new JsonPrimitive(latitude));
		obj.add("longitude", new JsonPrimitive(longitude));

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			public void onSuccess(String response) {
				// TODO Tell the user that he succeeded			
			}

			public void onFailure(Throwable e, String response) {
				// TODO Tell the user that the app can't find him
				sendFailureMessage(e, response);
			}
		};
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	//Takes a location and displayname as input, and puts a marker down for that.
	public void addMarker(Location loc, String displayname) {
		map.addMarker(new MarkerOptions()
		.position(new LatLng(loc.getLatitude(), loc.getLongitude())) 	//Position of user
		.title(displayname));
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// What to do when the users current position is changed.
		// TODO Auto-generated method stub
	}


	//public void setMarkers{
	//Should browse through the received set, and then take the JSON element and convert it to an location.

	//ResultSet = xx

	//for(int i=0; i<ResultSet.length; i++){
	//ResultSet = Divide up into LocationString and DisplayName
	//Location loca = JSONtoLocation(ResultSet[i]);
	//String displayName = recieve displayName from ResultSet
	//addMarker(loca, displayName);
	//}
	//x	
	//}

	//Takes the LocationString from Json and makes it back to a Location object


	//METHOD FOR HANDLING MENU ITEMS
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	createUserActivity();
	            return true;
	        case R.id.menu_create:
	        	//TODO Specify create in the menu
	        	return true;
	        case R.id.menu_modify:
	        	//TODO Specify modify in the menu
	        	return true;
	        case R.id.menu_delete:
	        	//TODO Specify delete in the menu
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private void createUserActivity() {
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivity(intent);
	}

	//NOT CURRENTLY USED METHODS:
	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}


}
