package dk.au.cs.skatespots;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonObject;

public class DeleteReminders extends Activity {
	private SkateSpots app;
	ListView reminderListView;
	ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_reminders);
		
		reminderListView = (ListView) findViewById(R.id.deleteReminderListView);		
		arrayAdapter =  new ArrayAdapter<String>(this, R.layout.simplerow);
		reminderListView.setAdapter(arrayAdapter);


		//HashSet med reminder
		Iterator<Integer> it = app.getCurrentSReminders().iterator();
		while(it.hasNext()){
			int myReminder = it.next();
			JsonObject obj = app.getCurrentSkateSpots().get(myReminder);
			String skateSpotName = obj.get("name").getAsString();
			arrayAdapter.add(skateSpotName);

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
