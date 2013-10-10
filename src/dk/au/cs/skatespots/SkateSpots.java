package dk.au.cs.skatespots;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.google.gson.JsonArray;

public class SkateSpots extends Application {
	private String currentUser;
	private String currentDisplayName;
	private Location location;
	private LocationClient locationClient;
	private JsonArray currentWifi;
	
	public String getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	public String getCurrentDisplayName() {
		return currentDisplayName;
	}

	public void setCurrentDisplayName(String currentDisplayName) {
		this.currentDisplayName = currentDisplayName;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public LocationClient getLocationClient() {
		return locationClient;
	}

	public void setLocationClient(LocationClient locationClient) {
		this.locationClient = locationClient;
	}

	public JsonArray getCurrentWifi() {
		return currentWifi;
	}

	public void setCurrentWifi(JsonArray currentWifi) {
		this.currentWifi = currentWifi;
	}
}
