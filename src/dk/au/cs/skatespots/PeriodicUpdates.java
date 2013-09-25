package dk.au.cs.skatespots;

public class PeriodicUpdates implements Runnable{
	public boolean markerBoolean = true;
	
	@Override
	public void run() {
		while(markerBoolean){
			MainActivity.updateMarkers();
			try{
				Thread.sleep(5000);
			} catch (InterruptedException e){
				//Placeholder
			}
		}
	}
}
