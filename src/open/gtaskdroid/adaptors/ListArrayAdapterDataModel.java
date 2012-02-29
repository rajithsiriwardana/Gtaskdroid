/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import open.gtaskdroid.activity.EventEditActivity;

import com.google.api.client.util.DateTime;

/**
 * @author rajith
 *
 */
public class ListArrayAdapterDataModel {
	

	private static final String DATE_OF_THE_WEEK_FORMAT="EEE MMM dd, yy";
	private static final String DATE_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	//2012-03-01T00:00:00.000Z
	
	private String taskTitle;
	private String eventStartDateTime;
	//private Calendar eventEndDateTime;
	private String taskNote;
	//private String location;
	
	
	//displaying data
	private String eventStartDate;	
	private boolean selected;
	
	
	
	
	/**
	 * @param taskTitle
	 */
	public ListArrayAdapterDataModel(String taskTitle,DateTime taskDue, String taskNote){
		this.taskTitle=taskTitle;
		convertDateTimeFormat(taskDue);
		this.taskNote=taskNote;
		this.selected=false;
	}
	
	public ListArrayAdapterDataModel(String taskTitle){
		this.taskTitle=taskTitle;
		this.selected=false;
		
	}
	
	/**
	 * @return the taskTitle
	 */
	public String getTaskTitle() {
		return taskTitle!=null? taskTitle:"";
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


	public String getTaskDue() {
		return eventStartDate!=null? eventStartDate:"";
	}

	public String getEventStartDateTime(){
		return eventStartDateTime!=null? eventStartDateTime:"";
	}
	
	/**
	 * @return the taskNotes
	 */
	public String getEventNote(){
		return taskNote!=null? taskNote:"";
	}
	
	
	private void convertDateTimeFormat(DateTime taskDue){
		if(taskDue!=null){
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		SimpleDateFormat dateDueStringSave= new SimpleDateFormat(EventEditActivity.DATE_TIME_FORMAT);
		SimpleDateFormat dateDueString = new SimpleDateFormat(DATE_OF_THE_WEEK_FORMAT);
		
		Date date = null;
		
		try {
			date = dateTimeFormat.parse(taskDue.toStringRfc3339());	
			eventStartDateTime = dateDueStringSave.format(date);
			eventStartDate=dateDueString.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			eventStartDateTime="";
			eventStartDate="";
		}
		}else{
			
			eventStartDateTime="";
			eventStartDate="";
		}
		
		
		
	}

}
