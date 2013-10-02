package dk.au.cs.skatespots;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class Wifi extends Activity {
	private WifiP2pManager wifiManager;
	private Channel channel;
	private BroadcastReceiver broadcastReceiver; 
	private IntentFilter intentFilter;

	private ArrayAdapter<String> arrayAdapter;
	private ListView wifiListView;

	private List<WifiP2pDevice> wifiList = new ArrayList<WifiP2pDevice>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);

		wifiListView = (ListView) findViewById(R.id.wifiListView);		
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		wifiListView.setAdapter(arrayAdapter);

		wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel =  (Channel) wifiManager.initialize(this, getMainLooper(), null); //It was necessary to make a cast (Channel)
		broadcastReceiver = new WifiReceiver(wifiManager, channel, this, this);

		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


		wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.w("PEERS WERE SUCCESSFULLY DISCOVERED", "PEERS DISCOVERED");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.w("MISTAKE", "MISTAKE");
			}
		});
	}

	private class WifiReceiver extends BroadcastReceiver {

		private WifiP2pManager mManager;
		private Channel mChannel;
		private Wifi mActivity;
		//For toast, add also context
		private Context mContext;

		public WifiReceiver(WifiP2pManager manager, Channel channel, Wifi activity, Context context) {
			super();
			this.mManager = manager;
			this.mChannel = channel;
			this.mActivity = activity;
			this.mContext= context;
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();


			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

				// Check to see if Wi-Fi is enabled and notify appropriate activity
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

					Toast.makeText(context, "Wi-Fi Direct is enable", Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(context, "Wi-Fi Direct is not enable", Toast.LENGTH_LONG).show();
				}      

			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				// Call WifiP2pManager.requestPeers() to get a list of current peers
				if (wifiManager != null){
					wifiManager.requestPeers(channel, new PeerListListener(){
						@Override
						public void onPeersAvailable(WifiP2pDeviceList peers) {
							Log.w(peers.toString(), peers.toString());
							wifiList.addAll(peers.getDeviceList());

							for(WifiP2pDevice d : wifiList){
								arrayAdapter.add(d.deviceName +"\n" + d.deviceAddress);
							}
						}
					});
				}
				else{
					Log.w("VALUE IS NULL", "VALUE IS NULL");
				}

			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				// Respond to new connection or disconnections
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				// Respond to this device's wifi state changing
			}
		}
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(broadcastReceiver, intentFilter);
	}
	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}



}
