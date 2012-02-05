/**
 * 
 */
package ogto.taskremider;

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
public class RemindersDbAdapter {
	
	
	private static final String DATABASE_NAME="data";
	private static final String DATABASE_TABLE="reminders";
	private static final int DATABASE_VERSION=1;
	
	public static final String KEY_TITLE="title";
	public static final String KEY_NOTE="note";
	public static final String KEY_DATE_TIME="reminder_date_time";
	public static final String KEY_ROWID="_id";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	
	private static final String DATABASE_CREATE=
			"create table "+ DATABASE_TABLE +" ("
			+ KEY_ROWID +" integer primary key autoincrement, "
			+ KEY_TITLE +" text not null, "
			+ KEY_NOTE +" text not null, "
			+ KEY_DATE_TIME +" text not null);";
	
	private final Context mCtx;
	
	public RemindersDbAdapter (Context mCtx){		
		this.mCtx=mCtx;		
	}
	
	
	public RemindersDbAdapter open() throws android.database.SQLException {
		mDbHelper =new DatabaseHelper(mCtx);
		mDb= mDbHelper.getWritableDatabase();
		return this;
	}
	
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



	public long createReminder(String title, String note,
			String reminderDateTime) {
		
		ContentValues initialValue= new ContentValues();
		initialValue.put(KEY_TITLE, title);
		initialValue.put(KEY_NOTE, note);
		initialValue.put(KEY_DATE_TIME, reminderDateTime);
		return mDb.insert(DATABASE_TABLE, null, initialValue);
	}
	
	public boolean deleteReminder(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null)>0;
	}
	
	public Cursor fetchAllReminders() {		
		return mDb.query(DATABASE_TABLE, new String []{KEY_ROWID, KEY_TITLE, KEY_NOTE, KEY_DATE_TIME},
				null, null, null, null, null);
	}
	
	public Cursor fetchReminder(long rowId) throws SQLException{
		
		Cursor mCursor=mDb.query(true, DATABASE_TABLE, new String []{KEY_ROWID, KEY_TITLE, KEY_NOTE, 
				KEY_DATE_TIME}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		
		if (mCursor!=null){
			mCursor.moveToFirst();
		}
		return mCursor;		
	}
	
	public boolean updateReminder(long rowId, String title, String note, String reminderDateTime){
		
		ContentValues args=new ContentValues();		
		args.put(KEY_TITLE, title);
		args.put(KEY_NOTE, note);
		args.put(KEY_DATE_TIME, reminderDateTime);
		
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" +rowId, null)>0;
	}
	
	
}

