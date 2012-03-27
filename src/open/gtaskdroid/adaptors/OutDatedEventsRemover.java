/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import open.gtaskdroid.activity.EventEditActivity;
import open.gtaskdroid.dataaccess.EventsDbAdapter;
import android.database.Cursor;

/**
 * @author rajith
 *
 */
public class OutDatedEventsRemover {

	public static final int MONTH_OLD = 1;
	public static final int WEEK_OLD = 2;
	public static final int DAY_OLD = 3;
	
	private int removePeriod;
	private EventsDbAdapter mDbHelper;
	private Cursor eventsCursor;
	private Calendar mToday;
	private Calendar mEventEndTime;
	
	/**
	 * 
	 * @param mDbHelper
	 * @param period
	 */
	public OutDatedEventsRemover (EventsDbAdapter mDbHelper, int period){
		
		this.mDbHelper=mDbHelper;
		this.removePeriod=period;
		this.eventsCursor=mDbHelper.fetchAllEvents();
		mToday=Calendar.getInstance();
		mEventEndTime=Calendar.getInstance();
	}
	
	/**
	 * removing data from database
	 */
	public void removeData(){
		eventsCursor.moveToFirst();
		while(!eventsCursor.isAfterLast()) {
			int rowId=eventsCursor.getInt(eventsCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_ROWID));
			String dateString = eventsCursor.getString(eventsCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_END_DATE_TIME));
			if(!" ".equalsIgnoreCase(dateString)){
				
	            Date date = parseToDate(dateString);
				
				Calendar compareCalendar=Calendar.getInstance();
				compareCalendar.setTime(date);
				if (compareCalendar.before(removeBefore())){
					mDbHelper.deleteEvent(rowId);
				}
				
				
				
			}
			eventsCursor.moveToNext();
		}
	}

	/**
	 * @param dateString
	 * @return
	 */
	private Date parseToDate(String dateString) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
		Date date = null;
		try {
			date=dateTimeFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * getting the limitations
	 */
	private Calendar removeBefore() {
		if(removePeriod==MONTH_OLD){
			mEventEndTime.set(Calendar.WEEK_OF_YEAR, mToday.get(Calendar.WEEK_OF_YEAR)-4);
		}else if(removePeriod==WEEK_OLD){
			mEventEndTime.set(Calendar.WEEK_OF_YEAR, (mToday.get(Calendar.WEEK_OF_YEAR)-1));
		}else if(removePeriod==DAY_OLD){
			mEventEndTime.set(Calendar.DAY_OF_YEAR, mToday.get(Calendar.DAY_OF_YEAR)-1);
		}
		return mEventEndTime;
	}
	
}
