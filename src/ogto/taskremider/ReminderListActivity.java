package ogto.taskremider;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReminderListActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
        
        addTestData();					//adding data till the implementation of the data base
        registerForContextMenu(getListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
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
		}
    	return super.onMenuItemSelected(featureId, item);
    }
    
  

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.list_menu_item_longpress, menu);
    }
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			//delete task
			return true;
			
		}
		return super.onContextItemSelected(item);
	}
    
    
	private void addTestData() {
		String [] items= new String[] {"foo","bar","fizz","bin"};
        
        ArrayAdapter <String> adapter=new ArrayAdapter <String> (this,R.layout.reminder_row,R.id.text1,items);
        setListAdapter(adapter);
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {      //context menu on long click
    	// TODO Auto-generated method stub
    	super.onListItemClick(l, v, position, id);
    	Intent intent=new Intent(this,ReminderEditActivity.class);
    	intent.putExtra("RowId", id);
    	startActivity(intent);
    	
    }
    
    private static final int ACTIVITY_CREATE=0;
    private void createReminder() {
		Intent intent=new Intent(this, ReminderEditActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
		
	}
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//reload the list here
    }
}