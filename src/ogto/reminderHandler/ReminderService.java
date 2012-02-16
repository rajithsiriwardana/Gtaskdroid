/**
 * 
 */
package ogto.reminderHandler;

import ogto.dataaccess.EventsDbAdapter;
import ogto.interactions.EventEditActivity;
import ogto.taskOrganizer.R;
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
		Long rowId=intent.getExtras().getLong(EventsDbAdapter.KEY_ROWID);
		NotificationManager nMngr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent=new Intent(this, EventEditActivity.class);
		
		notificationIntent.putExtra(EventsDbAdapter.KEY_ROWID, rowId);
		
		PendingIntent pIntent=PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		
		Notification note=new Notification(android.R.drawable.stat_sys_warning,
				getString(R.string.notify_new_task_msg), System.currentTimeMillis());
		
		note.setLatestEventInfo(this, 
				getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_msg), pIntent);
		
		note.defaults |=Notification.DEFAULT_SOUND;
		note.flags |=Notification.FLAG_AUTO_CANCEL;		
		
		
		int id=(int)((long) rowId);
		nMngr.notify(id, note);
	}

}
