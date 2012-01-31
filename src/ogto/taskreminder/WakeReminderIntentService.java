/**
 * 
 */
package ogto.taskreminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * @author rajith
 *
 */
public abstract class WakeReminderIntentService extends IntentService {

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	
	abstract void doReminderWork(Intent intent);
	
	public static final String LOCK_NAME_STATIC="ogto.taskreminder.Static";	
	private static PowerManager.WakeLock lockStatic=null;
	
	
	public static void acquireStaticLock(Context context){
		getLock(context).acquire();
	}
	
	synchronized private static PowerManager.WakeLock getLock(Context context){
		if (lockStatic==null) {
			PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true); 
		} return (lockStatic);
	}
	
	public WakeReminderIntentService(String name) {
		super(name);
	}

	
	@Override
	protected void onHandleIntent(Intent arg0) {
		try {
			doReminderWork(arg0);
		} finally {
			getLock(this).release();
		}

	}

}
