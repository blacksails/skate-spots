package dk.au.cs.skatespots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
	private HashMap<Marker,JsonObject> skateSpots;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (SkateSpots) this.getApplication();
		setContentView(R.layout.activity_main);
		setUpStuffIfNeeded();
		email = app.getCurrentUser();
		currentOtherUsers = new ArrayList<Marker>();
		skateSpots = new HashMap<Marker,JsonObject>();
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
				map.setInfoWindowAdapter(new InfoWindowAdapter() {

					@Override
					public View getInfoContents(Marker arg0) {
						if (arg0.getTitle() == null) { // means this is a skatespot and not a user
							View v = getLayoutInflater().inflate(R.layout.info_window, null);
							JsonObject obj = skateSpots.get(arg0);
							TextView tv;
							String name = obj.get("name").getAsString();
							tv = (TextView) v.findViewById(R.id.info_name);
							tv.setText(name);
							String description = obj.get("description").getAsString();
							tv = (TextView) v.findViewById(R.id.info_description);
							tv.setText(description);
							String spottype = obj.get("spottype").getAsString();
							tv = (TextView) v.findViewById(R.id.info_type);
							tv.setText(spottype);
							String author = obj.get("author").getAsString();
							tv = (TextView) v.findViewById(R.id.info_author);
							tv.setText(author);
							return v;
						} else {
							return null;
						}
					}

					@Override
					public View getInfoWindow(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}

				});
				
				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

					@Override
					public void onInfoWindowClick(Marker arg0) {
						if (arg0.getTitle() == null) {
							if (!app.getCurrentWifi().contains(skateSpots.get(arg0).get("id").getAsInt())) {
								newSkateSpotReminder(skateSpots.get(arg0));
								app.getCurrentSReminders().add(skateSpots.get(arg0).get("id").getAsInt());
							} else {
								removeSkateSpotReminder(skateSpots.get(arg0));
							}
						} else {
							// newPersonReminder
						}
					}

				});
			}
		}

		if (app.getLocationClient() != null && app.getLocationClient().isConnected()) {
			app.getLocationClient().disconnect();
		}
		locationClient = new LocationClient(this,this,this);
		app.setLocationClient(locationClient);
		locationClient.connect();
	}


	@Override
	public void onConnected(Bundle arg0) {
		location = locationClient.getLastLocation();

		locationRequest = LocationRequest.create();
		locationRequest.setInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationClient.requestLocationUpdates(locationRequest, this);

		//Zooms in on our current position
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		map.animateCamera(cameraUpdate);
	}


	//Updates the database, and checks for updates on the database whenever the user moves.
	@Override
	public void onLocationChanged(Location arg0) {
		// Keep the global var updated
		app.setLocation(locationClient.getLastLocation());

		sendMyLocation();
		getAllLocations();
		findNearbyWifi();
		getSkateSpots();
		getReminders();
		reminderCheck();
		
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
	}


	private void sendMyLocation(){
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

	//Retrieves locations of all users on the database that have been on within the past hour.
	private void getAllLocations() {

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
					LatLng latLng = new LatLng(latitude,longitude);
					Marker marker = map.addMarker(new MarkerOptions()
					.position(latLng)
					.title(displayname));
					currentOtherUsers.add(marker);
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
	
	@SuppressLint("UseSparseArrays")
	private void getSkateSpots() {

		JsonObject obj = new JsonObject();
		obj.add("email", new JsonPrimitive(email));
		obj.add("key", new JsonPrimitive("ourKey"));
		obj.add("type", new JsonPrimitive(5));

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			public void onSuccess(String response) {
				JsonParser parser = new JsonParser();
				JsonElement jsonElement = parser.parse(response);
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				HashMap<Integer,JsonObject> currentSkateSpots = new HashMap<Integer,JsonObject>();
				Iterator<JsonElement> it = jsonArray.iterator();
				while (it.hasNext()) {
					JsonObject obj = it.next().getAsJsonObject();

					String spottype = obj.get("spottype").getAsString();
					double latitude = obj.get("latitude").getAsDouble();
					double longitude = obj.get("longitude").getAsDouble();

					BitmapDescriptor bitmapDescriptor;
					if (spottype.equals("street")) {
						bitmapDescriptor 
						= BitmapDescriptorFactory.defaultMarker(
								BitmapDescriptorFactory.HUE_GREEN);
					} else if (spottype.equals("park")) {
						bitmapDescriptor 
						= BitmapDescriptorFactory.defaultMarker(
								BitmapDescriptorFactory.HUE_GREEN);
					} else { // type.equals("indoor")
						bitmapDescriptor 
						= BitmapDescriptorFactory.defaultMarker(
								BitmapDescriptorFactory.HUE_GREEN);
					}

					Marker marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude,longitude))
					.icon(bitmapDescriptor));
					skateSpots.put(marker, obj);
					currentSkateSpots.put(obj.get("id").getAsInt(), obj);
				}
				app.setCurrentSkateSpots(currentSkateSpots);
			}
		};
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}
	
	private void newSkateSpotReminder(JsonObject skateSpot) {
		
		JsonObject obj = new JsonObject();
		obj.add("id", skateSpot.get("id"));
		obj.add("email", new JsonPrimitive(email));
		obj.add("key", new JsonPrimitive("ourKey"));
		obj.add("type", new JsonPrimitive(6));
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler();
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	private void getReminders() {
		JsonObject obj = new JsonObject();
		obj.add("email", new JsonPrimitive(email));
		obj.add("key", new JsonPrimitive("ourKey"));
		obj.add("type", new JsonPrimitive(7));
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler(){
			public void onSuccess(String response) {
				JsonParser parser = new JsonParser();
				JsonArray jsonArray = parser.parse(response).getAsJsonArray();
				HashSet<Integer> currentSReminders = new HashSet<Integer>();
				Iterator<JsonElement> it = jsonArray.iterator();
				while (it.hasNext()) {
					Integer id = it.next().getAsInt();
					currentSReminders.add(id);
				}
				app.setCurrentSReminders(currentSReminders);
			}
		};
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	private void removeSkateSpotReminder(JsonObject jsonObject) {
		JsonObject obj = new JsonObject();
		obj.add("id", jsonObject.get("id"));
		obj.add("email", new JsonPrimitive(email));
		obj.add("key", new JsonPrimitive("ourKey"));
		obj.add("type", new JsonPrimitive(8));
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler();
		SkateSpotsHttpClient.post(getApplicationContext(), obj, responseHandler);
	}

	private void findNearbyWifi(){
		final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
			public void onReceive(Context c, Intent i){
				HashSet<String> wifiSet = new HashSet<String>();
				WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> scanResults = wifiManager.getScanResults();

				for(ScanResult s : scanResults){
					String wifiName = s.BSSID;
					wifiSet.add(wifiName);
				}

				app.setCurrentWifi(wifiSet);
			}
		};	

		IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//Sets the android's WiFi options to enabled.
		wifiManager.setWifiEnabled(true);
		wifiManager.startScan();	
	}

	private void reminderCheck(){
		if (app.getCurrentWifi() != null || app.getCurrentSkateSpots() != null || app.getCurrentSReminders() != null) {
			HashSet<String> wifi = app.getCurrentWifi();
			HashMap<Integer,JsonObject> skateSpots = app.getCurrentSkateSpots();
			HashSet<Integer> reminders = app.getCurrentSReminders();
			for (Integer i : reminders) {
				JsonObject skateSpot = skateSpots.get(i);
				Iterator<JsonElement> ssids = skateSpot.get("wifi").getAsJsonArray().iterator(); 
				while (ssids.hasNext()) {
					if (wifi.contains(ssids.next().getAsString())) {
						Context context = getApplicationContext();
						CharSequence text = "REMINDER: You are close to the skatespot "+skateSpot.get("name").getAsString();
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						return;
					}
				}
			}
		} else {
			return;
		}
	}


	//METHOD FOR HANDLING MENU ITEMS
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_create:
			goToCreateNew();
			return true;
		case R.id.menu_modify:
			//TODO Specify modify in the menu
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	// Methods for activity changing
	private void goToCreateNew(){
		Intent intent = new Intent(this, NewSkateSpot.class);
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
