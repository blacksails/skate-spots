package dk.au.cs.skatespots;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
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

public class MainActivity extends Activity implements ConnectionCallbacks, 
													  OnConnectionFailedListener,
													  LocationListener
													  //OnAddGeofencesResultListener 
													  {
	LocationClient locationClient;
	private GoogleMap map;
	private Location location;	

	
	//En metode der kan kaldes der hedder map.clear(), der sletter alle nuværende markers.

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
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
		
		
		//Adds a marker of our current position to our map.
		map.addMarker(new MarkerOptions()
		.position(latLng)
		.title(LoginActivity.selectedUser));
		
		map.animateCamera(cameraUpdate);
		

		//Adds a toast that pops up with our current coordinates once connected.
		String currentCoordinates = location.toString();
		Context context = getApplicationContext();
		CharSequence text = currentCoordinates;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// What to do when the users current position is changed.
		// TODO Auto-generated method stub
		
	}
}
