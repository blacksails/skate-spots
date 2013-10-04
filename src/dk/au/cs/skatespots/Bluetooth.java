package dk.au.cs.skatespots;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Bluetooth extends Activity {
	private ArrayAdapter<String> arrayAdapter;
	private ListView bluetoothListView;
	private BluetoothAdapter bluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);

		bluetoothListView = (ListView) findViewById(R.id.bluetoothListView);
		getBluetoothDevices();
	}

	public void getBluetoothDevices(){
				
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = bluetoothAdapter.getBondedDevices();
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		bluetoothListView.setAdapter(arrayAdapter);

		//Ser på bonded devices
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				// Tilføjer bonded devices til vores listView gennem arrayAdapter.
				arrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}

		// Laver en BroadcastReceiver for ACTION_FOUND
		final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Tilføjer bluetooth devices til vores listView gennem arrayAdapter.
					arrayAdapter.add(device.getName() + "\n" + device.getAddress());
					//Tilføjer alle bluetoothDevices til listen bluetoothDevices.
					bluetoothDevices.add(device);
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(broadcastReceiver, intentFilter); // Don't forget to unregister during onDestroy

		bluetoothAdapter.startDiscovery();

		//Mangler måske at implementere metoder til onPause og onResume for broadcastReciever? Samme gælder i Wifi.
	}
}
