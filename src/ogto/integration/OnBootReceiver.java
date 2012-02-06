/**
 * 
 */
package ogto.integration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ogto.dataaccess.EventsDbAdapter;
import ogto.interactions.EventEditActivity;
import ogto.logic.ReminderManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

/**
 * @author rajith
 *
 */
public class OnBootReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		ReminderManager reminderMngr=new ReminderManager(context);
		EventsDbAdapter dbHelper=new EventsDbAdapter(context);
		dbHelper.open();
		Cursor cursor=dbHelper.fetchAllEvents();
		if (cursor!=null) {
			cursor.moveToFirst();
			int rowIdColumnIndex=cursor.getColumnIndex(EventsDbAdapter.KEY_ROWID);
			int dateTimeColumnIndex=cursor.getColumnIndex(EventsDbAdapter.KEY_REMINDER_DATE_TIME);
			
			while (cursor.isAfterLast()==false) {
				
				Long rowId=cursor.getLong(rowIdColumnIndex);
				String dateTime=cursor.getString(dateTimeColumnIndex);
				Calendar calendar=Calendar.getInstance();
				SimpleDateFormat format=new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
				
				try {
					Date date=format.parse(dateTime);
					calendar.setTime(date);
					reminderMngr.setReminder(rowId, calendar);
				} catch (Exception e) {
					Log.e("OnBootReceiver", e.getMessage(), e);
					
				}cursor.moveToNext();
			}cursor.close();
		}dbHelper.close();

	}

}
