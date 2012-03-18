/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
	
	private Cursor reMinderCursor;
	private Calendar mToday;
	private Calendar mEventDate;

	private ArrayList<CursorAdapterData> mArrayList = new ArrayList<CursorAdapterData>();

	public ListCursorSorter (Cursor reMinderCursor) throws ParseException{	
		this.reMinderCursor=reMinderCursor;	
		this.mToday=Calendar.getInstance();
		this.mEventDate=Calendar.getInstance();
		sort();
	}

	private void sort() throws ParseException{
		
		
		ArrayList<CursorAdapterData> overDueList = new ArrayList<CursorAdapterData>();
		ArrayList<CursorAdapterData> todayList = new ArrayList<CursorAdapterData>();
		ArrayList<CursorAdapterData> otherList = new ArrayList<CursorAdapterData>();
		ArrayList<CursorAdapterData> noDateList = new ArrayList<CursorAdapterData>();
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

		reMinderCursor.moveToFirst();
		while(!reMinderCursor.isAfterLast()) {
				
			
            Date date = null;
			try {
				int rowId=reMinderCursor.getInt(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_ROWID));
				String dateString = reMinderCursor.getString(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_EVENT_START_DATE_TIME));
				String titleString = reMinderCursor.getString(reMinderCursor.getColumnIndexOrThrow(EventsDbAdapter.KEY_TITLE));
				
				
				
				
				if(" ".equalsIgnoreCase(dateString)){
					noDateList.add(new CursorAdapterData(rowId,titleString, dateString));
				}else{
					date = dateTimeFormat.parse(dateString);
					mEventDate.setTime(date);
		            
					if(mToday.get(Calendar.YEAR)>mEventDate.get(Calendar.YEAR)||mToday.get(Calendar.MONTH)>mEventDate.get(Calendar.MONTH)||mToday.get(Calendar.DAY_OF_MONTH)>mEventDate.get(Calendar.DAY_OF_MONTH)){						
						overDueList.add(new CursorAdapterData(rowId,titleString, dateString));
					}else if(mToday.get(Calendar.YEAR)==mEventDate.get(Calendar.YEAR)&&mToday.get(Calendar.MONTH)==mEventDate.get(Calendar.MONTH)&&mToday.get(Calendar.DAY_OF_MONTH)==mEventDate.get(Calendar.DAY_OF_MONTH)){
						todayList.add(new CursorAdapterData(rowId,titleString, dateString));
					}else{
						otherList.add(new CursorAdapterData(rowId,titleString, dateString));
					}
				}
				}catch (ParseException e) {
					Log.e("EventSorter", e.getMessage(), e); 
				} 
			
		     reMinderCursor.moveToNext();
		}
		
		if (!overDueList.isEmpty()) {
			mArrayList.add(new CursorAdapterData(-1,"Overdue", " "));
			Collections.sort(overDueList, new mComparator());
			mArrayList.addAll(overDueList);   
			}
		if (!todayList.isEmpty()) {
			mArrayList.add(new CursorAdapterData(-1,"Today", " "));
			Collections.sort(todayList, new mComparator());
			mArrayList.addAll(todayList);   
		}
		if (!otherList.isEmpty()||!noDateList.isEmpty()) {
			mArrayList.add(new CursorAdapterData(-1,"Other", " "));
			if(!otherList.isEmpty()){
				Collections.sort(otherList, new mComparator());
				mArrayList.addAll(otherList);   
			}
			if(!noDateList.isEmpty()){
				mArrayList.addAll(noDateList);
			}
		}
		

	}
	
	public List <CursorAdapterData> getSectionList(){
		
		return mArrayList;
	}

	private class mComparator implements Comparator<CursorAdapterData>{

		@Override
		public int compare(CursorAdapterData lhs, CursorAdapterData rhs) {			
			return lhs.getEventdateCalendar().compareTo(rhs.getEventdateCalendar());
		}
		
	}
	
}