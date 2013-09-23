package dk.au.cs.skatespots;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MainActivity extends Activity {
	public static Context c;
	LocationClient locationClient;

	//Check om LocationAccess er slået til, hvis ikke da bed brugeren om at slå det til og send videre.
	//Få zoomet ind fra start.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		c = this;
		locationClient = new LocationClient(MainActivity.c, this, this);
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
	        	map.setMyLocationEnabled(true);
	        }
	    }
	}

	private GoogleMap map;
}
