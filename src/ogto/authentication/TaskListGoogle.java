/**
 * 
 */
package ogto.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;

import ogto.taskOrganizer.R;
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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * @author rajith
 *
 */
public class TaskListGoogle extends ListActivity {
	private static final Level LOGGING_LEVEL = Level.OFF;

	private static final String TAG = "OGTO";

	/**
	 * API key is to identify the app. this API is generated for OGTO testing. rajithsiriwardana@gmail.com
	 */
	private final String API_KEY = "AIzaSyDedxiWSiIlcfwKqtNVNJkA6gtOU750_go";
	String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/tasks";

	
	private static final String PREF = "OGTOPref";
	private static final int DIALOG_ACCOUNTS = 0;	
	public static final int REQUEST_AUTHENTICATE = 0;
	private final HttpTransport transport = AndroidHttp
			.newCompatibleTransport();

	Tasks service;
	GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
			null);

	// TODO: save auth token in preferences?
	GoogleAccountManager accountManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = Tasks.builder(transport, new JacksonFactory())
				.setApplicationName("Open Google Task Organizer/1.0")
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
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ACCOUNTS:
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
				}
			});
			return builder.create();
		}
		return null;
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
				new AccountManagerCallback<Bundle>() {

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
							}
						} catch (Exception e) {
							handleException(e);
						}
					}
				}, null);
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
			//have to implement
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
			//  should only try this once to avoid infinite loop
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
			List<String> taskTitles = new ArrayList<String>();
			List<TaskList> taskLists = service.tasklists().list().execute()
					.getItems();
			if (taskLists != null) {
				for (TaskList taskList : taskLists) {
					List<Task> tasks = service.tasks().list(taskList.getId())
							.execute().getItems();
					if (tasks != null) {
						for (Task task : tasks) {
							taskTitles.add(task.getTitle());
						}
					} else {
						//taskTitles.add("No sub tasks.");
						
					}
				}
			} else {
				taskTitles.add("No tasks.");
			}
			setListAdapter(new ArrayAdapter<String>(this, R.layout.event_row,
					taskTitles));

		} catch (IOException e) {
			Log.d(TAG, "Exception ::"+e.getMessage());
			handleException(e);
		}
		setContentView(R.layout.event_list);
	}
	
	public void addTaskListAction(View view){
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				insertTaskList();
			}
		});
		thread.start();
		 ProgressDialog.show(TaskListGoogle.this,    
	              "Please wait...", "Retrieving data ...", true);
	}
	
	private void insertTaskList(){
		//TODO: testing
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

}
