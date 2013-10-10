package dk.au.cs.skatespots;

import java.util.HashMap;
import java.util.HashSet;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.google.gson.JsonObject;

public class SkateSpots extends Application {
	private String currentUser;
	private String currentDisplayName;
	private Location location;
	private LocationClient locationClient;;
	private HashMap<Integer,JsonObject> currentSkateSpots;
	private HashSet<Integer> currentSReminders;
	private HashSet<String> currentWifi;
	
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

	public HashMap<Integer,JsonObject> getCurrentSkateSpots() {
		return currentSkateSpots;
	}

	public void setCurrentSkateSpots(HashMap<Integer,JsonObject> currentSkateSpots) {
		this.currentSkateSpots = currentSkateSpots;
	}

	public HashSet<Integer> getCurrentSReminders() {
		return currentSReminders;
	}

	public void setCurrentSReminders(HashSet<Integer> currentSReminders) {
		this.currentSReminders = currentSReminders;
	}

	public HashSet<String> getCurrentWifi() {
		return currentWifi;
	}

	public void setCurrentWifi(HashSet<String> currentWifi) {
		this.currentWifi = currentWifi;
	}
}
