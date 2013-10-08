package dk.au.cs.skatespots;

import android.app.Application;

public class SkateSpots extends Application {
	private String currentUser;
	private String currentDisplayName;
	
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
}
