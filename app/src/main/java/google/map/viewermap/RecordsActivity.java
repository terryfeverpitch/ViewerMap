package google.map.viewermap;

import google.map.viewermap.ui.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RecordsActivity extends Activity {
	private ArrayList<MapView.Place> records;
	private ListView records_lv_display;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_records);
		
		records_lv_display = (ListView) findViewById(R.id.records_lv_display);
		
		Bundle bundle;
		bundle = this.getIntent().getExtras();
		records = (ArrayList<MapView.Place>) bundle.getSerializable("records_list");
		ArrayList<HashMap<String,String>> formatedList = new ArrayList<HashMap<String,String>>();
		
		for(MapView.Place place : records) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("name", place.getName());
			item.put("location", place.getLocation());
			formatedList.add(item);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, formatedList, android.R.layout.simple_list_item_2, 
				new String[] { "name", "location" },  new int[] { android.R.id.text1, android.R.id.text2 } );

		records_lv_display.setAdapter(adapter);
		records_lv_display.setOnItemClickListener(new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putInt("currentIndex", position);
				
				Intent intent = new Intent();
				intent.setClass(RecordsActivity.this, MapActivity.class);
				intent.putExtras(bundle);
				
				MapView map_mv_map = (MapView) MapFragment.getRootView().findViewById(R.id.map_mv_map);
				map_mv_map.getFocus(position);
//				startActivity(intent);
				finish();
			}
		});				
	}
}
