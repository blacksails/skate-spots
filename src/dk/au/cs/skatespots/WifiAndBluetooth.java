package dk.au.cs.skatespots;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WifiAndBluetooth extends Activity {
	
	private ListView blueToothDevices;
	private String[] blueToothDevicesArray;
	private ArrayAdapter<String> arrayAdapter;
	private Context c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_and_bluetooth);
		c = this;
		
		blueToothDevices = (ListView) findViewById(R.id.wifi_and_bluetooth);
		blueToothDevicesArray = new String[10];
		
		for(int i=0; i < blueToothDevicesArray.length; i++){
        	blueToothDevicesArray[i] = "Song " + i;
        }
		
		arrayAdapter = new ArrayAdapter<String>(c, R.id.wifi_and_bluetooth, blueToothDevicesArray);
		
		blueToothDevices.setAdapter(arrayAdapter);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_and_bluetooth, menu);
		return true;
	}

}
