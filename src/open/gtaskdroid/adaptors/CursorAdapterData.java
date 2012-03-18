/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import open.gtaskdroid.activity.EventEditActivity;

/**
 * @author rajith
 *
 */
public class CursorAdapterData {
	
	private static final String DATE_FORMAT="MMM dd, yy";
	private static final String DATE_OF_THE_WEEK_FORMAT="EEE";
	private static final String TIME_FORMAT="HH:mm";
	
	
	private int rowId;
	private String eventTitle;
	private String eventStartTime;
	
	
	private Date date;
	private Calendar mToday;
	private Calendar mEventDate;
	
	/**
	 * 
	 * @param rowId
	 * @param eventTitle
	 * @param eventStartTime
	 * @throws ParseException
	 */
	public CursorAdapterData (int rowId, String eventTitle, String eventStartTime) throws ParseException{
		this.rowId=rowId;
		this.eventTitle=eventTitle;
		this.eventStartTime=eventStartTime;
		convertToDate();
	}
	
	/**
	 * 
	 * @return RowId
	 */
	public long getRowId(){
		return (long)rowId;
	}
	
	/**
	 * 
	 * @return eventTitle
	 */
	public String getEventTitle(){
		return eventTitle!=null ? eventTitle : " ";		
	}
	
	/**
	 * get date in EEE format ex: WED
	 * @return
	 */
	public String getWeekDate(){
		return eventStartTime!=null ? convertToDateOfWeekFormat() : " ";
	}
	
	/**
	 * get time in HH:ss format ex:16:30 for today tasks
	 * @return
	 */
	public String getEventTime(){
		
		return eventStartTime!=null ? convertToEventTimeFormat() : " ";
	}
	
	/**
	 * 
	 * @return Calendar date
	 */
	public Calendar getEventdateCalendar(){
		return mEventDate;
	}
	
	
	/**
	 * parsing database string to calendar
	 * @throws ParseException
	 */
	private void convertToDate() throws ParseException{
		
		mToday=Calendar.getInstance();
		mEventDate=Calendar.getInstance();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
		date = null;
		
		if(!" ".equalsIgnoreCase(eventStartTime)){
			date=dateTimeFormat.parse(eventStartTime);
			mEventDate.setTime(date);	
		}else eventStartTime=null;
	}
	
	/**
	 * converting to EEE format
	 * @return
	 */
	private String convertToDateOfWeekFormat(){
		
		SimpleDateFormat dueDateOfWeekFormat = new SimpleDateFormat(DATE_OF_THE_WEEK_FORMAT);			
		if(mToday.get(Calendar.YEAR)==mEventDate.get(Calendar.YEAR)&&mToday.get(Calendar.MONTH)==mEventDate.get(Calendar.MONTH)&&mToday.get(Calendar.DAY_OF_MONTH)==mEventDate.get(Calendar.DAY_OF_MONTH)){
			return " ";
		}else return dueDateOfWeekFormat.format(date);
				
	}
	
	/**
	 * converting to HH:ss format
	 * @return
	 */
	private String convertToEventTimeFormat(){
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_FORMAT);
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		
		if(mToday.get(Calendar.YEAR)==mEventDate.get(Calendar.YEAR)&&mToday.get(Calendar.MONTH)==mEventDate.get(Calendar.MONTH)&&mToday.get(Calendar.DAY_OF_MONTH)==mEventDate.get(Calendar.DAY_OF_MONTH)){		
			return timeFormat.format(date);
			
		}else return dateTimeFormat.format(date);
				
	}
	

}
