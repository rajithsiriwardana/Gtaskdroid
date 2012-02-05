/**
 * 
 */
package ogto.taskremider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author rajith
 *
 */
public class OnAlarmReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		long rowId=intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		WakeReminderIntentService.acquireStaticLock(context);
		Intent i=new Intent(context, ReminderService.class);
		i.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
		context.startService(i);
	}

}
