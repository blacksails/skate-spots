package dk.au.cs.skatespots;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Wifi extends Activity {

	private ArrayAdapter<String> arrayAdapter;
	private ListView wifiListView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);

		wifiListView = (ListView) findViewById(R.id.wifiListView);		
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		wifiListView.setAdapter(arrayAdapter);




		final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
			public void onReceive(Context c, Intent i){
				WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
				//Listen over netværk
				List<ScanResult> scanResults = wifiManager.getScanResults();
				
				//Tilføjer vores devices pr. navn og addresse til vores ListView gennem vores arrayAdapter.
				for(ScanResult s : scanResults){
					arrayAdapter.add(s.SSID + "\n" + s.BSSID);
				}
			}
		};	

		IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		//Sets the android's WiFi options to enabled.
		wifiManager.setWifiEnabled(true);
		wifiManager.startScan();
	}




}
