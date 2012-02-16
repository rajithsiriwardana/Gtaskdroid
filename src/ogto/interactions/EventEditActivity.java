
package ogto.interactions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ogto.dataaccess.EventsDbAdapter;
import ogto.reminderHandler.ReminderManager;
import ogto.taskOrganizer.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EventEditActivity extends Activity {

	/* 
	* Dialog Constants
	*/
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;
	/*
	 * calendar constants
	 */	
	private static final int UPDATE_ALL_CALENDARS= 0;
	private static final int UPDATE_EVENT_START_CALENDAR= 1;
	private static final int UPDATE_EVENT_END_CALENDAR= 2;
	private static final int UPDATE_REMINDER_CALENDAR= 3;
	/* 
	* Date Format 
	*/
	private static final String DATE_FORMAT = "yyyy MM dd"; 
	private static final String TIME_FORMAT = "kk:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
	
	
	
	private EditText mTitleText;
    private EditText mNoteText;
    private EditText mLocationText;
    private Button mReminderDateButton;
    private Button mReminderTimeButton;
    private Button mEventStartDateButton;
    private Button mEventStartTimeButton;
    private Button mEventEndDateButton;
    private Button mEventEndTimeButton;
    private Button mSaveButton;
    private Button mCancelButton;    
    private CheckBox mAddReminderCheckBox;
    private Long mRowId;
    private EventsDbAdapter mDbHelper;
    
    private Calendar mCalendar;
    private Calendar mEventStartCalendar;
    private Calendar mEventEndCalendar;
    private Calendar mReminderCalendar;
    
    private boolean mReminderSet; 
    private int mCalendarSwitch;									
    /*buttons get updated according to the value given here. 0 = update all the button values
     *  1 = event start dateTime
     *  2 = event end dateTime
     *  3 = reminder dateTime
     */
       
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new EventsDbAdapter(this);        
        setContentView(R.layout.event_edit);        
                
        initContent();							//initialize buttons textFields and check boxes
        
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(EventsDbAdapter.KEY_ROWID) 
                							: null;
      
        registerButtonListenersAndSetDefaultText();
    }

	private void initContent() {
		
		mCalendar = Calendar.getInstance();
	    mEventStartCalendar = Calendar.getInstance();
	    mEventEndCalendar = Calendar.getInstance();
	    mReminderCalendar = Calendar.getInstance();
		
		mTitleText = (EditText) findViewById(R.id.title);
        mNoteText = (EditText) findViewById(R.id.note);
        mLocationText = (EditText) findViewById(R.id.location);
        
        mReminderDateButton = (Button) findViewById(R.id.reminder_date);
        mReminderTimeButton = (Button) findViewById(R.id.reminder_time);
        mEventStartDateButton= (Button) findViewById(R.id.event_start_date);
        mEventStartTimeButton= (Button) findViewById(R.id.event_start_time);
        mEventEndDateButton= (Button) findViewById(R.id.event_end_date);
        mEventEndTimeButton= (Button) findViewById(R.id.event_end_time);        
        mSaveButton = (Button) findViewById(R.id.save);
        mCancelButton= (Button) findViewById(R.id.cancel);
        
        mAddReminderCheckBox=(CheckBox) findViewById(R.id.add_reminder_checkbox);
       
        
	}

	//setting the value of the RowId
	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();            
			mRowId = extras != null ? extras.getLong(EventsDbAdapter.KEY_ROWID) 
									: null;
			
		}
	}
    
	//if the activity get paused
    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close(); 
    }
    
    //the activity resumed
    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open(); 
    	setRowIdFromIntent();
		populateFields();
    }
    
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case DATE_PICKER_DIALOG: 
    			return showDatePicker();
    		case TIME_PICKER_DIALOG: 
    			return showTimePicker(); 
    	}
    	return super.onCreateDialog(id);
    }
    
    
    
    //date picker dialog spawning
 	private DatePickerDialog showDatePicker() {
		
		
		DatePickerDialog datePicker = new DatePickerDialog(EventEditActivity.this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDateButtonText(); 
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)); 
		return datePicker; 
	}
 	
   //time picker dialog spawning
   private TimePickerDialog showTimePicker() {
		
    	TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute); 
				updateTimeButtonText(); 
			}
		}, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true); 
		
    	return timePicker; 
	}
 	
   
   
   	//button listeners 
 	private void registerButtonListenersAndSetDefaultText() {

		mEventStartDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCalendarSwitch=UPDATE_EVENT_START_CALENDAR;
				showDialog(DATE_PICKER_DIALOG);  
			}
		}); 
		
		
		mEventStartTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCalendarSwitch=UPDATE_EVENT_START_CALENDAR;
				showDialog(TIME_PICKER_DIALOG); 
			}
		}); 
		
		mEventEndDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCalendarSwitch=UPDATE_EVENT_END_CALENDAR;
				showDialog(DATE_PICKER_DIALOG);  
			}
		}); 
		
		
		mEventEndTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCalendarSwitch=UPDATE_EVENT_END_CALENDAR;
				showDialog(TIME_PICKER_DIALOG); 
			}
		});
		
		mReminderDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAddReminderCheckBox.isChecked()){
					mReminderSet=true;	
					mCalendarSwitch=UPDATE_REMINDER_CALENDAR;
					showDialog(DATE_PICKER_DIALOG);
				}
			}
		}); 
		
		
		mReminderTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAddReminderCheckBox.isChecked()){
					mReminderSet=true;
					mCalendarSwitch=UPDATE_REMINDER_CALENDAR;
					showDialog(TIME_PICKER_DIALOG); 
				}
			}
		});
		
				
		mSaveButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {        		
        		saveState(); 
        		setResult(RESULT_OK);        	   
        		Toast.makeText(EventEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
        	    finish();         		
        	}
          
        });
		
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		/*
		 * update specific calendar or all the calendars 
		 */
		  updateDateButtonText(); 						
	      updateTimeButtonText();
	}
   

 	
	
    private void populateFields()  {
  	
    	mCalendarSwitch=UPDATE_ALL_CALENDARS;
    	// Only populate the text boxes and change the calendar date
    	// if the row is not null from the database. 
        if (mRowId != null) {
            Cursor event = mDbHelper.fetchEvent(mRowId);
            startManagingCursor(event);
            mTitleText.setText(event.getString(
            		event.getColumnIndexOrThrow(EventsDbAdapter.KEY_TITLE)));
            mNoteText.setText(event.getString(
            		event.getColumnIndexOrThrow(EventsDbAdapter.KEY_NOTE)));
            mLocationText.setText(event.getString(
            		event.getColumnIndexOrThrow(EventsDbAdapter.KEY_LOCATION)));
            
            mReminderSet = event.getInt(
            		event.getColumnIndexOrThrow(EventsDbAdapter.KEY_IS_REMINDER_SET))==1? true : false;
            mAddReminderCheckBox.setChecked(mReminderSet);
            
            // Get the date from the database and format it for our use. 
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
			try {
				String dateString = event.getString(event.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_START_DATE_TIME)); 
				date = dateTimeFormat.parse(dateString);
	            mEventStartCalendar.setTime(date);
	            dateString=event.getString(event.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_END_DATE_TIME));
	            date = dateTimeFormat.parse(dateString);
	            mEventEndCalendar.setTime(date);
	            
	            if (mReminderSet) {
	            	dateString=event.getString(event.getColumnIndexOrThrow(EventsDbAdapter.KEY_REMINDER_DATE_TIME));
	            	date = dateTimeFormat.parse(dateString);
	            	mReminderCalendar.setTime(date);
				}  
			} catch (ParseException e) {
				Log.e("ReminderEditActivity", e.getMessage(), e); 
			} 
        } else {
        	// This is a new task - add defaults from preferences if set. 
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        	String defaultTitleKey = getString(R.string.pref_task_title_key); 
        	String defaultTimeKey = getString(R.string.pref_default_time_from_now_key); 
        	
        	String defaultTitle = prefs.getString(defaultTitleKey, null);
        	String defaultTime = prefs.getString(defaultTimeKey, null); 
        	
        	if(defaultTitle != null)
        		mTitleText.setText(defaultTitle); 
        	
        	if(defaultTime != null)
        		mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
        	
        	mReminderSet=false;
        	
        }
        
        updateDateButtonText(); 
        updateTimeButtonText();
        
        	
    }

	private void updateTimeButtonText() {
		// Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT); 
        String timeForButton = timeFormat.format(mCalendar.getTime()); 
  
        switch (mCalendarSwitch) {
        
		case UPDATE_ALL_CALENDARS:
        	timeForButton = timeFormat.format(mEventStartCalendar.getTime());
        	mEventStartTimeButton.setText(timeForButton);
        	timeForButton = timeFormat.format(mEventEndCalendar.getTime());
        	mEventEndTimeButton.setText(timeForButton);
        	if (mReminderSet) {
        		timeForButton = timeFormat.format(mReminderCalendar.getTime());
        		mReminderTimeButton.setText(timeForButton);
			}else {
				mReminderTimeButton.setText("");
			}			
			break;
			
		case UPDATE_EVENT_START_CALENDAR:			
        	mEventStartTimeButton.setText(timeForButton);
        	mEventStartCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY));
        	mEventStartCalendar.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE));        	
        	break;
        	
		case UPDATE_EVENT_END_CALENDAR:			
        	mEventEndTimeButton.setText(timeForButton);
        	mEventEndCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY));
        	mEventEndCalendar.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE));
        	break;
        	
		case UPDATE_REMINDER_CALENDAR:
        	mReminderTimeButton.setText(timeForButton);
        	mReminderCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY));
        	mReminderCalendar.set(Calendar.MINUTE, mCalendar.get(Calendar.MINUTE));
        	break;
        	
		}
        
	}
	
	

	private void updateDateButtonText() {
		// Set the date button text based upon the value from the database 
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
        String dateForButton = dateFormat.format(mCalendar.getTime()); 
     //   mReminderDateButton.setText(dateForButton);
        
        
        switch (mCalendarSwitch) {
        
		case UPDATE_ALL_CALENDARS:
        	dateForButton = dateFormat.format(mEventStartCalendar.getTime());
        	mEventStartDateButton.setText(dateForButton);
        	dateForButton = dateFormat.format(mEventEndCalendar.getTime());
        	mEventEndDateButton.setText(dateForButton);
        	if (mReminderSet) {
        		dateForButton = dateFormat.format(mReminderCalendar.getTime());
        		mReminderDateButton.setText(dateForButton);
			}else {
				mReminderDateButton.setText("");
			}
			break;
			
		case UPDATE_EVENT_START_CALENDAR:
        	mEventStartDateButton.setText(dateForButton);
        	mEventStartCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        	mEventStartCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        	mEventStartCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));
			break;
			
		case UPDATE_EVENT_END_CALENDAR:
        	mEventEndDateButton.setText(dateForButton);
        	mEventEndCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        	mEventEndCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        	mEventEndCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));
			break;
			
		case UPDATE_REMINDER_CALENDAR:
        	mReminderDateButton.setText(dateForButton);
        	mReminderCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        	mReminderCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        	mReminderCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));
			break;


		}
        
		
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EventsDbAdapter.KEY_ROWID, mRowId);
    }
    

    //save the event in database
    private void saveState() {
    	    	
        String title = mTitleText.getText().toString();
        String description = mNoteText.getText().toString();
        String location = mLocationText.getText().toString();        

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT); 
    	String eventStartDateTime = dateTimeFormat.format(mEventStartCalendar.getTime());
    	String eventEndDateTime= dateTimeFormat.format(mEventEndCalendar.getTime());
    	
    	mReminderSet=mAddReminderCheckBox.isChecked();
    	
    	int isReminderChkBxSelected = mReminderSet? 1:0;
    	String reminderDateTime= dateTimeFormat.format(mReminderCalendar.getTime());

        if (mRowId == null) {
        	
        	long id = mDbHelper.createEvent(title, description, location,
        			eventStartDateTime, eventEndDateTime,isReminderChkBxSelected, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateEvent(mRowId, title, description,location,
            		eventStartDateTime, eventEndDateTime,isReminderChkBxSelected, reminderDateTime);
        }
       
        //if the reminder calendar set. then add a reminder
        if (mReminderSet) {
        	new ReminderManager(this).setReminder(mRowId, mReminderCalendar);
		} 
         
    	

		
    }
    
}