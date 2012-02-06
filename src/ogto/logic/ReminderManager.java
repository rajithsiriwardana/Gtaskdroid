/**
 * 
 */
package ogto.logic;

import java.util.Calendar;

import ogto.dataaccess.EventsDbAdapter;
import ogto.integration.OnAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author rajith
 *
 */
public class ReminderManager {
	
	private Context mContext;
	private AlarmManager mAlarmManager;
	
	public ReminderManager(Context context) {
		mContext=context;
		mAlarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setReminder(long taskId, Calendar when) {
		
		Intent intent=new Intent(mContext, OnAlarmReceiver.class);
		intent.putExtra(EventsDbAdapter.KEY_ROWID, (long)taskId);
		
		PendingIntent pIntent=PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pIntent);		
		
	}

}
