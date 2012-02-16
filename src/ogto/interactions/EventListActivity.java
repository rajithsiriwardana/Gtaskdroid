package ogto.interactions;


import ogto.dataaccess.EventsDbAdapter;
import ogto.taskOrganizer.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EventListActivity extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	
	private EventsDbAdapter mDbHelper;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);        
        
        mDbHelper=new EventsDbAdapter(this);
        mDbHelper.open();
        fillData();
                
        registerForContextMenu(getListView());
    }
    
    
    private void fillData(){
    	
    	Cursor reMinderCursor=mDbHelper.fetchAllEvents();
    	startManagingCursor(reMinderCursor);
    	
    	//create an array to specify fields we want (only the TITLE)
    	String [] from=new String[] {EventsDbAdapter.KEY_TITLE};
    	
    	//And array of the field that want to bind in the view
    	int [] to=new int[]{R.id.text1};
    	
    	//create a simple cursor adaptor and set it to display
    	SimpleCursorAdapter reminders=new SimpleCursorAdapter(this, R.layout.event_row, reMinderCursor,
    			from, to);
    	
    	setListAdapter(reminders);
    	
    	
    }
    
       
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.list_menu, menu);
    	return true;    	
    }
   
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_insert:			
			createReminder();
			return true;
			
		case R.id.menu_settings:			
			Intent intent=new Intent(this, TaskPreferences.class);
			startActivity(intent);
			return true;
			
		}
    	return super.onMenuItemSelected(featureId, item);
    }
    
  //handling context menu

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	
    	super.onCreateContextMenu(menu, v, menuInfo);   	
    	
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
	    	  		
	    	setEventDeleteDialog(item);	    	
			return true;
			
		}
		return super.onContextItemSelected(item);
	}
    
	
	
	//deleting an event
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
	
	
	
	
	//list item clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {      //context menu on long click
    	
    	super.onListItemClick(l, v, position, id);
    	Intent intent=new Intent(this,EventEditActivity.class);
    	intent.putExtra(EventsDbAdapter.KEY_ROWID, id);
    	startActivityForResult(intent, ACTIVITY_EDIT);
    	
    }
    
    //new event creating
    private void createReminder() {
		Intent intent=new Intent(this, EventEditActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
		
	}
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	fillData();
    	
    }
}