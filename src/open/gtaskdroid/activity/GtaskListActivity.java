/**
 * 
 */
package open.gtaskdroid.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksRequest;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import open.Gtaskdroid.R;
import open.gtaskdroid.adaptors.ListArrayAdapter;
import open.gtaskdroid.adaptors.ListArrayAdapterDataModel;
import open.gtaskdroid.dataaccess.EventsDbAdapter;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * @author rajith
 *
 */
public class GtaskListActivity extends ListActivity {
	
	private static final Level LOGGING_LEVEL = Level.OFF;
	private static final String TAG = "OGTO";

	/**
	 * API key is to identify the app. this API is generated for Gtaskdroid testing. rajithsiriwardana@gmail.com
	 */
	private final String API_KEY = "AIzaSyDedxiWSiIlcfwKqtNVNJkA6gtOU750_go";
	private String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/tasks";

	
	private static final String PREF = "OGTOPref";
	private static final int DIALOG_ACCOUNTS = 0;	
	public static final int REQUEST_AUTHENTICATE = 0;
	private static final int SET_REMINDER_CBOX_DESELECT = 0;
	private final HttpTransport transport = AndroidHttp.newCompatibleTransport();

	private Tasks service;
	private GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
			null);

	private GoogleAccountManager accountManager;
	private List<ListArrayAdapterDataModel>  events;
	private EventsDbAdapter mDbHelper;


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = Tasks.builder(transport, new JacksonFactory())
				.setApplicationName("Gtaskdroid/1.0")
				.setHttpRequestInitializer(accessProtectedResource)
				.setJsonHttpRequestInitializer(
						new JsonHttpRequestInitializer() {

							public void initialize(JsonHttpRequest request)
									throws IOException {
								TasksRequest tasksRequest = (TasksRequest) request;
								tasksRequest.setKey(API_KEY);
							}
						}).build();
		accountManager = new GoogleAccountManager(this);
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		gotAccount(false);		
		mDbHelper = new EventsDbAdapter(this);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ACCOUNTS:
			return spawnDialogAccounts();
		}
		return super.onCreateDialog(id);
	}

	/**
	 * @return
	 */
	public Dialog spawnDialogAccounts() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_account);
		final Account[] accounts = accountManager.getAccounts();
		final int size = accounts.length;
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = accounts[i].name;
		}
		builder.setItems(names, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				gotAccount(accounts[which]);
				update();
			}
		});
		return builder.create();
	}

	@Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close(); 
    }
	
	@Override								//need to add more
    protected void onResume() {
        super.onResume();
        mDbHelper.open(); 
    }
	
	
	void gotAccount(boolean tokenExpired) {
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		String accountName = settings.getString("accountName", null);
		Account account = accountManager.getAccountByName(accountName);
		if (account != null) {
			if (tokenExpired) {
				accountManager.invalidateAuthToken(accessProtectedResource
						.getAccessToken());
				accessProtectedResource.setAccessToken(null);
			}
			gotAccount(account);
			update();
			return;
		}
		showDialog(DIALOG_ACCOUNTS);
		
	}

	void gotAccount(final Account account) {
		SharedPreferences settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accountName", account.name);
		editor.commit();
		accountManager.manager.getAuthToken(account, AUTH_TOKEN_TYPE, true,
				new Manager(), null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_AUTHENTICATE:
			if (resultCode == RESULT_OK) {
				gotAccount(false);				
			} else {
				showDialog(DIALOG_ACCOUNTS);
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   	super.onCreateOptionsMenu(menu);
    	MenuInflater mi=getMenuInflater();
    	mi.inflate(R.menu.sync_menu, menu);			
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.switch_account:
			showDialog(DIALOG_ACCOUNTS);
			return true;
			
		case R.id.sync_selected:
			syncSelectedTasks();
			return true;
		}
		return false;
	}

	void handleException(Exception e) {
		e.printStackTrace();
		if (e instanceof HttpResponseException) {
			HttpResponse response = ((HttpResponseException) e).getResponse();
			int statusCode = response.getStatusCode();
			try {
				response.ignore();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (statusCode == 401) {
				gotAccount(true);
				return;
			}
		}
		Log.e(TAG, e.getMessage(), e);
	}

	void onAuthToken() {
		try {
			insertTaskList();
			events = new ArrayList<ListArrayAdapterDataModel>();			
			List<TaskList> taskLists = service.tasklists().list().execute()
					.getItems();
			if (taskLists != null) {
				for (TaskList taskList : taskLists) {
					List<Task> tasks = service.tasks().list(taskList.getId())
							.execute().getItems();
					if (tasks != null) {
						for (Task task : tasks) {
							events.add(new ListArrayAdapterDataModel(task.getTitle(),task.getDue(),task.getNotes()));							
							
						}
					} 
				}
			}

		} catch (IOException e) {
			Log.d(TAG, "Exception ::"+e.getMessage());
			handleException(e);
		}
		
	}
	
	private void update(){
		
		if(events==null){
			events= new ArrayList<ListArrayAdapterDataModel>();
			events.add(new ListArrayAdapterDataModel("No available tasks"));
		}
		
		ArrayAdapter<ListArrayAdapterDataModel> adapter=new ListArrayAdapter(this, events);
		setListAdapter(adapter);
		
	}
	
	

	private void insertTaskList(){

		try{
		TaskList tempTaskList=new TaskList();
		tempTaskList.setTitle("Task List :-"+new Random().nextInt(30));
		TaskList newTaskList=service.tasklists().insert(tempTaskList).execute();
		Log.d(TAG, "Inserted TaskList ::"+newTaskList.toString());
		}catch(ClientProtocolException exp){
			Log.d(TAG, "ClientProtocolException msg="+exp.getMessage());
		}
		catch(Exception exp){
			Log.d(TAG, "Exception msg="+exp.getMessage());
			handleException(exp);
		}
	}

	private void syncSelectedTasks(){
		
		if(events!=null){
			Iterator<ListArrayAdapterDataModel> list= events.iterator();
			while(list.hasNext()){
				ListArrayAdapterDataModel data=list.next();
				if(data.isSelected()){
				mDbHelper.createEvent(data.getTaskTitle(), data.getEventNote(), "", data.getEventStartDateTime(), "", SET_REMINDER_CBOX_DESELECT, "");
				
				}
			}
		}
  		setResult(RESULT_OK);        	   
		Toast.makeText(GtaskListActivity.this, getString(R.string.event_sync_toast_message), Toast.LENGTH_SHORT).show();
	    finish();
		
	}
	
	
	
	
	
	private final class Manager implements AccountManagerCallback<Bundle> {
		
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				Bundle bundle = future.getResult();
				if (bundle.containsKey(AccountManager.KEY_INTENT)) {
					
					Intent intent = bundle
							.getParcelable(AccountManager.KEY_INTENT);
					intent.setFlags(intent.getFlags()
							& ~Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivityForResult(intent,
							REQUEST_AUTHENTICATE);
				} else if (bundle
						.containsKey(AccountManager.KEY_AUTHTOKEN)) {
					accessProtectedResource
							.setAccessToken(bundle
									.getString(AccountManager.KEY_AUTHTOKEN));
					String authToken=bundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
					Log.d("TasksSample", "Autho Toke ="+authToken);
					onAuthToken();
					update();
				}
			} catch (Exception e) {
				handleException(e);
			}
		}
	}
	
	
	
}
