package open.gtaskdroid.activity;


import java.text.ParseException;

import open.gtaskdroid.adaptors.CursorAdapterData;
import open.gtaskdroid.adaptors.CursorArrayAdapter;
import open.gtaskdroid.adaptors.ListCursorSorter;
import open.gtaskdroid.adaptors.OutDatedEventsRemover;
import open.gtaskdroid.dataaccess.EventsDbAdapter;
import open.Gtaskdroid.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class EventListActivity extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	
	private EventsDbAdapter mDbHelper;
   
	/**
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);        
        
        mDbHelper=new EventsDbAdapter(this);
        mDbHelper.open();
        removeOutDatedEvents();
        fillData();
                
        registerForContextMenu(getListView());
    }
    
    /**
     * 
     */
    @Override
    protected void onDestroy() {    	
    	super.onDestroy();
        mDbHelper.close();
    }
    /**
     * remove outdated data from data base as user specified
     */
    private void removeOutDatedEvents(){
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String defaultOverdueKey = getString(R.string.pref_task_overdue_delete);
    	String removeTimeValue=prefs.getString(defaultOverdueKey, "1");

    	OutDatedEventsRemover mRemover=new OutDatedEventsRemover(mDbHelper,Integer.parseInt(removeTimeValue));
    	mRemover.removeData();
    }
    
    /**
     * populating list view
     */
    private void fillData(){
    	
      	Cursor reMinderCursor=mDbHelper.fetchAllEvents();
    	ListCursorSorter sorter;
		try {
			sorter = new ListCursorSorter(reMinderCursor);
	    	ArrayAdapter<CursorAdapterData> adapter=new CursorArrayAdapter(this, sorter.getSectionList());
	    	setListAdapter(adapter);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
    	

    	
    	
    }
    
       
    /**
     * menu for the ListTaskActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.list_menu, menu);
    	return true;    	
    }
   
    /**
     * if menu item selected
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_insert:			
			createReminder();
			return true;
			
		case R.id.menu_settings:			
			Intent intent=new Intent(this, TaskPreferencesActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.menu_google_sync:
			syncAccount();
			return true;
			
		case R.id.quick_guide:
			viewGuide();
		}
    	return super.onMenuItemSelected(featureId, item);
    }
    
 
    /**
     * handling context menu
     * long press
     */
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	
    	super.onCreateContextMenu(menu, v, menuInfo);   	
    	
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

	
	/**
	 * if item long pressed
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_delete:	    	  		
	    	setEventDeleteDialog(item);	    	
			return true;
		}
		return super.onContextItemSelected(item);

	}
    

	
	/**
	 * deleting an event
	 * @param item
	 */
	private void setEventDeleteDialog(final MenuItem item) {
		
		AlertDialog.Builder builder=
    			new AlertDialog.Builder(EventListActivity.this);
    	builder.setMessage(R.string.event_delete_message)
    	.setTitle(R.string.event_delete_title)
    	.setCancelable(false)
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
				mDbHelper.deleteEvent(info.id);
				fillData();
				
			}
		})
    	.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
    	builder.create().show();
	} 
	
	
	
	
	/**
	 * list item clicked
	 */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {      
    	
    	super.onListItemClick(l, v, position, id);    	
    	CursorAdapterData mItem = (CursorAdapterData)this.getListAdapter().getItem(position);
    	if(mItem.getRowId()>0){
    	Intent intent=new Intent(this,EventEditActivity.class);
    	intent.putExtra(EventsDbAdapter.KEY_ROWID,mItem.getRowId());  
    	startActivityForResult(intent, ACTIVITY_EDIT);
    	}
    }
    
    private void viewGuide(){
    	Intent intent=new Intent(this, QuickGuideActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
    }
    
    /**
     * new event creating
     */
    private void createReminder() {
		Intent intent=new Intent(this, EventEditActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
		
	}
    
    /**
     * getting Gtask list
     */
    private void syncAccount(){    	
    	Intent intent=new Intent(this, GtaskListActivity.class);
    	startActivityForResult(intent, ACTIVITY_CREATE);
    }
    
    /**
     * 
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	fillData();
    	
    }
}