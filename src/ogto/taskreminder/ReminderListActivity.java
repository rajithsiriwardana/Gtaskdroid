package ogto.taskreminder;


import android.app.ListActivity;
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

public class ReminderListActivity extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	
	private RemindersDbAdapter mDbHelper;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
        
        //addTestData();					//adding data till the implementation of the data base
        mDbHelper=new RemindersDbAdapter(this);
        mDbHelper.open();
        fillData();
                
        registerForContextMenu(getListView());
    }
    
    
    private void fillData(){
    	
    	Cursor reMinderCursor=mDbHelper.fetchAllReminders();
    	startManagingCursor(reMinderCursor);
    	
    	//create an array to specify fields we want (only the TITLE)
    	String [] from=new String[] {RemindersDbAdapter.KEY_TITLE};
    	
    	//And array of the field that want to bind in the view
    	int [] to=new int[]{R.id.text1};
    	
    	//create a simple cursor adaptor and set it to display
    	SimpleCursorAdapter reminders=new SimpleCursorAdapter(this, R.layout.reminder_row, reMinderCursor,
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
			AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
			mDbHelper.deleteReminder(info.id);
			fillData();
			return true;
			
		}
		return super.onContextItemSelected(item);
	}
    
    
	   
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {      //context menu on long click
    	
    	super.onListItemClick(l, v, position, id);
    	Intent intent=new Intent(this,ReminderEditActivity.class);
    	intent.putExtra(RemindersDbAdapter.KEY_ROWID, id);
    	startActivityForResult(intent, ACTIVITY_EDIT);
    	
    }
    
    
    private void createReminder() {
		Intent intent=new Intent(this, ReminderEditActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
		
	}
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	fillData();
    	
    }
}