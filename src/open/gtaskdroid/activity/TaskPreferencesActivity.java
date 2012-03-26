/**
 * 
 */
package open.gtaskdroid.activity;

import open.Gtaskdroid.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * @author rajith
 *
 */
public class TaskPreferencesActivity extends PreferenceActivity {

	/**
	 * pop preferences
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);	

	}
	
	
}
