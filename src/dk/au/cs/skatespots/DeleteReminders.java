package dk.au.cs.skatespots;

import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeleteReminders extends Activity {
	ListView reminderListView;
	ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_reminders);


		reminderListView = (ListView) findViewById(R.id.deleteReminderListView);		
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		reminderListView.setAdapter(arrayAdapter);

		Iterator<JsonElement> it = iterator.DataDerSkalIterereresOver();
		while(it.hasNext()){
			JsonObject obj = it.next().getAsJsonObject();
			String skateSpotNavn = obj.get("SKATESPOTNAVN").getAsString();
			
			arrayAdapter.add(skateSpotNavn);
		}
		
		reminderListView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	        	//Delete fra arrayAdapter
	        	arrayAdapter.remove(arrayAdapter.getItem(position));
	        	arrayAdapter.notifyDataSetChanged();
	        	//Delete fra databasen
	        	
	        }
	    });
		
	

	}


}
