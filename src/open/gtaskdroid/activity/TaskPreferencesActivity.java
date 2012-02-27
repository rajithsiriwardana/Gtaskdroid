/**
 * 
 */
package open.gtaskdroid.activity;

import open.Gtaskdroid.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;

/**
 * @author rajith
 *
 */
public class TaskPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.task_preferences);
		
		EditTextPreference timeDefault=(EditTextPreference)
				findPreference(getString(R.string.pref_default_time_from_now_key));
		timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance());
	}
	
	
}
