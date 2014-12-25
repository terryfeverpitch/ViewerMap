package google.map.viewermap;

import google.map.viewermap.adapter.NavDrawerListAdapter;
import google.map.viewermap.model.NavDrawerItem;
import google.map.viewermap.ui.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Spinner;

public class MapActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	private MapFragment mapFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggleExtension(this, mDrawerLayout, R.drawable.ic_drawer,
				R.string.app_name, R.string.app_name // nav drawer close - description for accessibility
		);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			mapFragment = new MapFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, mapFragment).commit();
		}
	}

	private final class ActionBarDrawerToggleExtension extends ActionBarDrawerToggle {
		private ActionBarDrawerToggleExtension(Activity activity, 
				DrawerLayout drawerLayout, int drawerImageRes,
				int openDrawerContentDescRes, int closeDrawerContentDescRes) {
			super(activity, drawerLayout, drawerImageRes,
					openDrawerContentDescRes, closeDrawerContentDescRes);
		}

		public void onDrawerClosed(View view) {
			getActionBar().setTitle(mTitle);
			// calling onPrepareOptionsMenu() to show action bar icons
			invalidateOptionsMenu();
		}

		public void onDrawerOpened(View drawerView) {
			getActionBar().setTitle(mDrawerTitle);
			// calling onPrepareOptionsMenu() to hide action bar icons
			invalidateOptionsMenu();
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			doFunction(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_records:
			MapView map_mv_map = (MapView)mapFragment.getRootView().findViewById(R.id.map_mv_map);
			ArrayList<MapView.Place> records = map_mv_map.getRecords();
			Intent intent = new Intent();
    		intent.setClass(MapActivity.this, RecordsActivity.class);
    		
    		Bundle bundle = new Bundle();
    		bundle.putSerializable("records_list", records);
    		intent.putExtras(bundle);	    		
    		startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_records).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private void doFunction(int position) {
		MapView map_mv_map = (MapView)mapFragment.getRootView().findViewById(R.id.map_mv_map);
		switch (position) {
		case 0:
			final Dialog dialog = createDialog();
			dialog.setTitle("Search...");
			dialog.show();
			break;
		case 1:
			map_mv_map.markPlace();
			break;
		case 2:
			map_mv_map.recordPlace();
			break;
        case 3:
            final Dialog pathDialog  = createSettingPathDialog();
            pathDialog.setTitle("Search...");
            pathDialog.show();
            map_mv_map.setStart();
            break;
        case 4:
            map_mv_map.setEnd();
            break;
		default:
			break;
		}

		if (mapFragment != null) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	
	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	public Dialog createDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    View content = inflater.inflate(R.layout.search_dialog, null);
	    final EditText search_dialog_textbox = (EditText) content.findViewById(R.id.search_dialog_textbox);
	    final MapView map_mv_map = (MapView)mapFragment.getRootView().findViewById(R.id.map_mv_map);
	    final ListView search_dialog_lv_history = (ListView) content.findViewById(R.id.search_dialog_lv_history);
		ArrayList<String> historyList = map_mv_map.getHistory();
		
		if(!historyList.isEmpty()) {
			ArrayAdapter listAdapter = new ArrayAdapter<String>(content.getContext(), android.R.layout.simple_list_item_1, historyList);
	        
			search_dialog_lv_history.setSelected(true);
			search_dialog_lv_history.setAdapter(listAdapter);
			search_dialog_lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener () {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object listItem = search_dialog_lv_history.getItemAtPosition(position);
					search_dialog_textbox.setText(listItem.toString());
				}
			});				
		}
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(content)
	    // Add action buttons
	           .setPositiveButton("Go", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   String place = search_dialog_textbox.getText().toString();
	            	   map_mv_map.goSearch(place);
	            	   Toast.makeText(MapActivity.this, place,Toast.LENGTH_SHORT).show();
	               }
	           })
	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.cancel();
	               }
	           });      
	    return builder.create();
	}

    public Dialog createSettingPathDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View content = inflater.inflate(R.layout.path_dialog, null);
//        final EditText search_dialog_textbox = (EditText) content.findViewById(R.id.search_dialog_textbox);
        final MapView map_mv_map = (MapView)mapFragment.getRootView().findViewById(R.id.map_mv_map);
        final Spinner path_dialog_sp_start = (Spinner) content.findViewById(R.id.path_dialog_sp_start);
        final Spinner path_dialog_sp_end = (Spinner) content.findViewById(R.id.path_dialog_sp_end);
        final EditText path_dialog_tb_start = (EditText) content.findViewById(R.id.path_dialog_tb_start);
        final EditText path_dialog_tb_end = (EditText) content.findViewById(R.id.path_dialog_tb_end);

        ArrayList<MapView.Place> records = map_mv_map.getRecords();
        records.add(0, new MapView.Place("custom...", 0, 0));

        if(!records.isEmpty()) {
            ArrayList<HashMap<String,String>> formatedList = new ArrayList<HashMap<String,String>>();

            for(MapView.Place place : records) {
                HashMap<String,String> item = new HashMap<String,String>();
                item.put("name", place.getName());
                item.put("location", place.getLocation());
                formatedList.add(item);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, formatedList, android.R.layout.simple_list_item_2,
                    new String[] { "name", "location" },  new int[] { android.R.id.text1, android.R.id.text2 } );

            path_dialog_sp_start.setSelected(true);
            path_dialog_sp_start.setAdapter(adapter);

            path_dialog_sp_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0)
                        path_dialog_tb_start.setText("");
                    else {
                        HashMap<String, String> item = (HashMap<String, String>) parent.getSelectedItem();
                        String name = item.get("name");
                        path_dialog_tb_start.setText(name);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    path_dialog_tb_start.setText("");
                }
            });

            path_dialog_sp_end.setSelected(true);
            path_dialog_sp_end.setAdapter(adapter);

            path_dialog_sp_end.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0)
                        path_dialog_tb_start.setText("");
                    else {
                        HashMap<String, String> item = (HashMap<String, String>) parent.getSelectedItem();
                        String name = item.get("name");
                        path_dialog_tb_end.setText(name);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    path_dialog_tb_end.setText("");
                }
            });
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(content)
                // Add action buttons
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        String place = search_dialog_textbox.getText().toString();
//                        map_mv_map.goSearch(place);
//                        Toast.makeText(MapActivity.this, place,Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}