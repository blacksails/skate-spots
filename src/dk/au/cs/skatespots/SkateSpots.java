package dk.au.cs.skatespots;

import android.app.Application;
import android.location.Location;

public class SkateSpots extends Application {
	private String currentUser;
	private String currentDisplayName;
	private Location location;
	
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
}
