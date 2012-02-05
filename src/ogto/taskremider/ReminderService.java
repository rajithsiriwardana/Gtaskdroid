/**
 * 
 */
package ogto.taskremider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

/**
 * @author rajith
 *
 */
public class ReminderService extends WakeReminderIntentService {

	public ReminderService() {
		super("ReminderService");
	}

	/* (non-Javadoc)
	 * @see ogto.taskreminder.WakeReminderIntentService#doReminderWork(android.content.Intent)
	 */
	@Override
	void doReminderWork(Intent intent) {
		Long rowId=intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		NotificationManager nMngr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent=new Intent(this, ReminderEditActivity.class);
		
		notificationIntent.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
		
		PendingIntent pIntent=PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		
		Notification note=new Notification(android.R.drawable.stat_sys_warning,
				getString(R.string.notify_new_task_msg), System.currentTimeMillis());
		
		note.setLatestEventInfo(this, 
				getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_msg), pIntent);
		
		note.defaults |=Notification.DEFAULT_SOUND;
		note.flags |=Notification.FLAG_AUTO_CANCEL;
		
		
		//since max int value is 2,147,483,647 issue could occur if user enters more than that num of tasks ;-)
		
		int id=(int)((long) rowId);
		nMngr.notify(id, note);
	}

}
