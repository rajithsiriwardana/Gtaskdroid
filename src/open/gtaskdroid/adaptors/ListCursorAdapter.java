/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import open.Gtaskdroid.R;
import open.gtaskdroid.activity.EventEditActivity;
import open.gtaskdroid.dataaccess.EventsDbAdapter;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @author rajith
 *
 */
public class ListCursorAdapter extends SimpleCursorAdapter {

	private int layout;	
	private static final String DATE_OF_THE_WEEK_FORMAT="EEE MMM dd, yy";
	
	public ListCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.layout=layout;
	}

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View v, Context context, Cursor cursor) {		
		updateRow(cursor, v);
	 }

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);
		updateRow(cursor, v);
		return v;

	}

	/**
	 * @param cursor
	 * @param v
	 */
	private void updateRow(Cursor cursor, View v) {
		
		String title=cursor.getString(cursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_TITLE));
		String dateDue=cursor.getString(cursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_START_DATE_TIME));
		  /**
		  * Next set the name of the entry.
		  */    
		TextView taskTitleView= (TextView) v.findViewById(R.id.taskTitle);
		TextView taskDueView= (TextView) v.findViewById(R.id.taskDue);
		 
		taskTitleView.setText(title);
		if(!" ".equalsIgnoreCase(dateDue)){	 
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
		SimpleDateFormat dateDueString = new SimpleDateFormat(DATE_OF_THE_WEEK_FORMAT);
				
				Date date = null;
				
				try {
					date = dateTimeFormat.parse(dateDue);					 
					taskDueView.setText(dateDueString.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
					Log.e("listCursor", "here");
				}
		}else {
			taskDueView.setText(" ");
		}
	}

}
