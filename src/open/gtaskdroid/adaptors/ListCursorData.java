/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import open.gtaskdroid.activity.EventEditActivity;

/**
 * @author rajith
 *
 */
public class ListCursorData {
	
	private static final String DATE_FORMAT="MMM dd, yy";
	private static final String DATE_OF_THE_WEEK_FORMAT="EEE";
	private static final String TIME_FORMAT="HH:mm";

	private long rowId;
	private String taskTitle;
	private String eventStartDateTime;	
	
	//displaying data
	private String eventStartDate;
	private String eventStartTime;					//displaying today events
	private boolean selected;
	

	
	
	/**
	 * @param taskTitle
	 */
	public ListCursorData (int rowId, String taskTitle, String taskDue){
		this.rowId=(long)rowId;
		this.taskTitle=taskTitle;
		this.eventStartDateTime=taskDue;					//in DB format
		this.selected=false;
		convertDateTimeFormat();
	}
	
	
	/**
	 *
	 * @return the taskTitle
	 */
	public String getTaskTitle() {
		return taskTitle!=null? taskTitle:" ";
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

   /**
   * To List view
   */
	public String getTaskDue() {
		return eventStartDate!=null? eventStartDate:" ";
	}
	
	public String getTaskDueToday(){
		return eventStartTime!=null? eventStartTime:" ";
	}
	

	
	
	private void convertDateTimeFormat(){
		if(eventStartDateTime!=null){
		
		SimpleDateFormat dateDueStringSave= new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
		SimpleDateFormat dateDueString = new SimpleDateFormat(DATE_OF_THE_WEEK_FORMAT);
		SimpleDateFormat dateDueTime= new SimpleDateFormat(TIME_FORMAT);
		
		Date date = null;
		
		try {
			if(" ".equalsIgnoreCase(eventStartDateTime)){
			date = dateDueStringSave.parse(eventStartDateTime);	
			eventStartDate = dateDueString.format(date);
			eventStartTime = dateDueTime.format(date);
			}else{
				eventStartDateTime=" ";
				eventStartDate=" ";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			eventStartDateTime=" ";
			eventStartDate=" ";
		}
		}else{
			
			eventStartDateTime=" ";
			eventStartDate=" ";
		}
		
		
		
	}


	/**
	 * @return the rowId
	 */
	public long getRowId() {
		return rowId;
	}


}