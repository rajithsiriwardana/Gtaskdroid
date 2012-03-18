/**
 * 
 */
package open.gtaskdroid.reminderHandler;

import open.gtaskdroid.dataaccess.EventsDbAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author rajith
 *
 */
public class OnAlarmReceiver extends BroadcastReceiver {

	/**
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		long rowId=intent.getExtras().getLong(EventsDbAdapter.KEY_ROWID);
		WakeReminderIntentService.acquireStaticLock(context);
		Intent i=new Intent(context, ReminderService.class);
		i.putExtra(EventsDbAdapter.KEY_ROWID, rowId);
		context.startService(i);
	}

}
