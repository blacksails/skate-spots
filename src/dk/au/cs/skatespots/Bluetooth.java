package dk.au.cs.skatespots;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Bluetooth extends Activity {
	private ArrayAdapter<String> arrayAdapter;
	private ListView bluetoothListView;
	private BluetoothAdapter bluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);

		bluetoothListView = (ListView) findViewById(R.id.bluetoothListView);
		getBluetoothDevices();

	}

	public void getBluetoothDevices(){
		//Our own device's bluetooth adapter. There's only one for the whole system. If = null, then system doesn't support bluetooth.		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = bluetoothAdapter.getBondedDevices();
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		bluetoothListView.setAdapter(arrayAdapter);

		//Receives bonded devices and puts them into the arrayAdapter.
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a ListView
				arrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}



		// Create a BroadcastReceiver for ACTION_FOUND
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a ListView
					arrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			}
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

		bluetoothAdapter.startDiscovery();

	}
	



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth, menu);
		return true;
	}

}
