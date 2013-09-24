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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements ConnectionCallbacks, 
													  OnConnectionFailedListener 
													  //OnAddGeofencesResultListener 
															  {
	LocationClient locationClient;
	private GoogleMap map;
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
	        
	        	 //map.animateCamera(CameraUpdateFactory.zoomIn()); //Zooms in
	        	 //map.setMyLocationEnabled(true);
	        }
	    }
	}


	@Override
	public void onConnected(Bundle arg0) {
		location = locationClient.getLastLocation();
		
		//Zooms in on our current position
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
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
}
