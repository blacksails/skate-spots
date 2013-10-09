package dk.au.cs.skatespots;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends Activity implements ConnectionCallbacks, 
OnConnectionFailedListener,
LocationListener,
OnAddGeofencesResultListener 
{
	private SkateSpots app;
	private LocationClient locationClient;
	private GoogleMap map;
	private Location location;
	private Marker myLocation;
	private String email;
	private ArrayList<Marker> currentOtherUsers;
	private LocationRequest locationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpStuffIfNeeded();
		email = app.getCurrentUser();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setUpStuffIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
		}
		if (locationClient == null) {
			locationClient = new LocationClient(this,this,this);
			locationClient.connect();
		}
		if (app == null) {
			app = (SkateSpots) this.getApplication();
		}
		if (currentOtherUsers == null) {
			currentOtherUsers = new ArrayList<Marker>();
		}
	}


	@Override
	public void onConnected(Bundle arg0) {
		location = locationClient.getLastLocation();
		
		if (locationRequest == null) {
			locationRequest = LocationRequest.create();
			locationRequest.setInterval(5000);
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationClient.requestLocationUpdates(locationRequest, this);
		}
		
		//Zooms in on our current position
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		map.animateCamera(cameraUpdate);
	}


	public void sendMyLocation(){
		location = locationClient.getLastLocation();

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		JsonObject obj = new JsonObject();
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a secret key
		obj.add("type", new JsonPrimitive(2));
		obj.add("email", new JsonPrimitive(email));
		obj.add("latitude", new JsonPrimitive(latitude));
		obj.add("longitude", new JsonPrimitive(longitude));

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler();	
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	
	//Takes a location and displayname as input, and puts a marker down for that.
	public void addMarkerOtherUser(double latitude, double longitude, String displayname) {
		LatLng latLng = new LatLng(latitude,longitude);
		Marker marker = map.addMarker(new MarkerOptions()
		.position(latLng)
		.title(displayname));
		currentOtherUsers.add(marker);
	}

	//Updates the database, and checks for updates on the database whenever the user moves.
	@Override
	public void onLocationChanged(Location arg0) {
		app.setLocation(locationClient.getLastLocation());
		sendMyLocation();
		// Add our current location to the map
		if (myLocation != null) {myLocation.remove();}
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		BitmapDescriptor bitmapDescriptor 
			= BitmapDescriptorFactory.defaultMarker(
					BitmapDescriptorFactory.HUE_AZURE);
		myLocation = map.addMarker(new MarkerOptions()
		.position(latLng)
		.icon(bitmapDescriptor)
		.title(app.getCurrentDisplayName()));
		getAllLocations();
	}


	//Retrieves locations of all users on the database that have been on within the past hour.
	public void getAllLocations() {
		
		JsonObject obj = new JsonObject();
		obj.add("email", new JsonPrimitive(email));
		obj.add("key", new JsonPrimitive("ourKey")); // TODO create a secret key
		obj.add("type", new JsonPrimitive(3));
	
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			public void onSuccess(String response) {
				JsonParser parser = new JsonParser();	
				JsonElement jsonElement = parser.parse(response);
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				Iterator<JsonElement> it = jsonArray.iterator();
				// We remove current markers and clear the array
				for (Marker m : currentOtherUsers) {
					m.remove();
				}
				currentOtherUsers.clear();
				// We add the updated ones
				while (it.hasNext()) {
					JsonObject obj = it.next().getAsJsonObject();
					String displayname = obj.get("displayname").getAsString();
					double latitude = obj.get("latitude").getAsDouble();
					double longitude = obj.get("longitude").getAsDouble();
					addMarkerOtherUser(latitude,longitude,displayname);
				}
			}
	
			public void onFailure(Throwable e, String response) {
				// TODO 
				Context context = getApplicationContext();
				CharSequence text = e.toString();
				int duration = Toast.LENGTH_LONG;
	
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				sendFailureMessage(e, response);
			}
		};
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);	
	}

	//METHOD FOR HANDLING MENU ITEMS
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.wifi_and_bluetooth:
			return true;
		case R.id.wifi:
			goToWifi();
			return true;
		case R.id.bluetooth:
			goToBluetooth();
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
		case R.id.action_settings:
			createUserActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void goToBluetooth() {
		Intent intent = new Intent(this, Bluetooth.class);
		startActivity(intent);
	}
	
	private void goToWifi() {
		Intent intent = new Intent(this, Wifi.class);
		startActivity(intent);
	}
	
	
	//FOR TESTING PURPOSES: SEE onOptionsItemSelected -> Action_settings
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

	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		// TODO Auto-generated method stub
		
	}


}
