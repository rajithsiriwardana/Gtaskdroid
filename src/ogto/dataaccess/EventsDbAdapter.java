/**
 * 
 */
package ogto.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author rajith
 *
 */
public class EventsDbAdapter {
	
	//database info
	private static final String DATABASE_NAME="ogtodata";
	private static final String DATABASE_TABLE="events";
	private static final int DATABASE_VERSION=1;
	
	//database table columns
	public static final String KEY_TITLE="title";
	public static final String KEY_NOTE="note";
	public static final String KEY_LOCATION="location";
	public static final String KEY_EVENT_START_DATE_TIME="event_start_date_time";
	public static final String KEY_EVENT_END_DATE_TIME="event_end_date_time";
	public static final String KEY_REMINDER_DATE_TIME="reminder_date_time";
	public static final String KEY_IS_REMINDER_SET="is_reminder_set";
	public static final String KEY_ROWID="_id";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	//create database 
	private static final String DATABASE_CREATE=
			"create table "+ DATABASE_TABLE +" ("
			+ KEY_ROWID +" integer primary key autoincrement, "
			+ KEY_TITLE +" text not null, "
			+ KEY_NOTE +" text not null, "
			+ KEY_LOCATION +" text not null, "
			+ KEY_EVENT_START_DATE_TIME + " text not null, "
			+ KEY_EVENT_END_DATE_TIME + " text not null, "
			+ KEY_IS_REMINDER_SET + " int not null, "
			+ KEY_REMINDER_DATE_TIME +" text not null );";
	
	private final Context mCtx;
	
	public EventsDbAdapter (Context mCtx){		
		this.mCtx=mCtx;		
	}
	
	//open database connection
	public EventsDbAdapter open() throws android.database.SQLException {
		mDbHelper =new DatabaseHelper(mCtx);
		mDb= mDbHelper.getWritableDatabase();
		return this;
	}
	
	//close database connection
	public void close(){
		mDbHelper.close();
	}
	
	
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//not used
			//you can upgrade the Database with ALTER 
			
		}	
	}


    //add event to the database
	public long createEvent(String title, String note, String location, String eventStartDateTime,
			String eventEndDateTime, int reminderSet, String reminderDateTime) {
		
		ContentValues initialValue= new ContentValues();
		initialValue.put(KEY_TITLE, title);
		initialValue.put(KEY_NOTE, note);
		initialValue.put(KEY_LOCATION, location);
		initialValue.put(KEY_EVENT_START_DATE_TIME, eventStartDateTime);
		initialValue.put(KEY_EVENT_END_DATE_TIME, eventEndDateTime);
		initialValue.put(KEY_IS_REMINDER_SET, reminderSet);
		initialValue.put(KEY_REMINDER_DATE_TIME, reminderDateTime);
		
		return mDb.insert(DATABASE_TABLE, null, initialValue);
	}
	
	//delete event from database
	public boolean deleteEvent(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null)>0;
	}
	
	
	//fetch all events 
	public Cursor fetchAllEvents() {		
		return mDb.query(DATABASE_TABLE, new String []{KEY_ROWID, KEY_TITLE, 
				KEY_NOTE, KEY_LOCATION, KEY_EVENT_START_DATE_TIME, KEY_EVENT_END_DATE_TIME,KEY_IS_REMINDER_SET, KEY_REMINDER_DATE_TIME},
				null, null, null, null, null);		
	}
	
	
	//fetch specific event specified by the rowId
	public Cursor fetchEvent(long rowId) throws SQLException{
		
		Cursor mCursor=mDb.query(true, DATABASE_TABLE, new String []{KEY_ROWID, KEY_TITLE, KEY_NOTE, KEY_LOCATION, KEY_EVENT_START_DATE_TIME,
				KEY_EVENT_END_DATE_TIME, KEY_IS_REMINDER_SET, KEY_REMINDER_DATE_TIME}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		
		if (mCursor!=null){
			mCursor.moveToFirst();
		}
		return mCursor;		
	}
	
	
	//update an existing event
	public boolean updateEvent(long rowId, String title, String note, String location, String eventStartDateTime,
			String eventEndDateTime, int reminderSet,  String reminderDateTime){
		
		ContentValues args=new ContentValues();		
		args.put(KEY_TITLE, title);
		args.put(KEY_NOTE, note);
		args.put(KEY_LOCATION, location);
		args.put(KEY_EVENT_START_DATE_TIME, eventStartDateTime);
		args.put(KEY_EVENT_END_DATE_TIME, eventEndDateTime);
		args.put(KEY_IS_REMINDER_SET, reminderSet);
		args.put(KEY_REMINDER_DATE_TIME, reminderDateTime);
		
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" +rowId, null)>0;
	}
	
	
}

