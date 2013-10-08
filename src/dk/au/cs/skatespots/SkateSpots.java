package dk.au.cs.skatespots;

import android.app.Application;

public class SkateSpots extends Application {
	private String currentUser;
	
	public String getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
}
