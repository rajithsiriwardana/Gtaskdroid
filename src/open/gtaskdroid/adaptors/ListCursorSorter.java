/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import open.gtaskdroid.dataaccess.EventsDbAdapter;

import android.database.Cursor;
import android.util.Log;

/**
 * @author rajith
 *
 */
public class ListCursorSorter {
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
	//private static final String DATE_OF_THE_WEEK_FORMAT="EEE MMM dd, yy";
	//private static final String TIME_FORMAT = "kk:mm";
	
	private Cursor reMinderCursor;
	private Calendar today;
	private Calendar mCalendar;
	//private ListCursorData[] mSectionList;
	private ArrayList<ListCursorData> mArrayList = new ArrayList<ListCursorData>();

	public ListCursorSorter (Cursor reMinderCursor){	
		this.reMinderCursor=reMinderCursor;	
		this.today=Calendar.getInstance();
		this.mCalendar=Calendar.getInstance();
		sort();
	}

	private void sort(){
		
		
		ArrayList<ListCursorData> overDueList = new ArrayList<ListCursorData>();
		ArrayList<ListCursorData> todayList = new ArrayList<ListCursorData>();
		ArrayList<ListCursorData> otherList = new ArrayList<ListCursorData>();
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		//SimpleDateFormat dueDateFormat=new SimpleDateFormat(DATE_OF_THE_WEEK_FORMAT);
		//SimpleDateFormat dueTimeFormat=new SimpleDateFormat(TIME_FORMAT);
		reMinderCursor.moveToFirst();
		while(!reMinderCursor.isAfterLast()) {
				
			
            Date date = null;
			try {
				int rowId=reMinderCursor.getInt(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_ROWID));
				String dateString = reMinderCursor.getString(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_START_DATE_TIME));
				String titleString = reMinderCursor.getString(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_TITLE));
				
				
				
				//mArrayList.add(new ListCursorData(rowId,titleString, dateString, noteString));
				if(" ".equalsIgnoreCase(dateString)){
					otherList.add(new ListCursorData(rowId,titleString, dateString));
				}else{
					date = dateTimeFormat.parse(dateString);
					mCalendar.setTime(date);
		            
					if(today.get(Calendar.DAY_OF_MONTH)>mCalendar.get(Calendar.DAY_OF_MONTH)){						
						overDueList.add(new ListCursorData(rowId,titleString, dateString));
					}else if(today.get(Calendar.DAY_OF_MONTH)==mCalendar.get(Calendar.DAY_OF_MONTH)){
						todayList.add(new ListCursorData(rowId,titleString, dateString));
					}else{
						otherList.add(new ListCursorData(rowId,titleString, dateString));
					}
				}
				}catch (ParseException e) {
					Log.e("EventSorter", e.getMessage(), e); 
				} 
			
		     reMinderCursor.moveToNext();
		}
		
		if (!overDueList.isEmpty()) mArrayList.addAll(overDueList); 
		if (!todayList.isEmpty()) mArrayList.addAll(todayList);
		if (!otherList.isEmpty()) mArrayList.addAll(otherList);
		

	}
	
	public List <ListCursorData> getSectionList(){
		
		return mArrayList;

	}
	
	
}