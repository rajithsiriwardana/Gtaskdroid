/**
 * 
 */
package ogto.taskremider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.R.string;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * @author rajith
 *
 */
public class ReminderEditActivity extends Activity {
	
	
	private Button mDateButton;
	private Button mTimeButton;
	private static final int DATE_PICKER_DIALOG=0;
	private static final int TIME_PICKER_DIALOG=1;
	private static final String DATE_FORMAT="YYYY-MM-DD";
	private static final String TIME_FORMAT="kk:mm";
	private Calendar mCalender;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_edit);
		
		if (getIntent()!=null) {
			Bundle extras=getIntent().getExtras();
			int rowId=extras != null ?extras.getInt("RowId") : -1;
			//Do stuff with the RowId here
		}
		
		registerButtonListenersAndSetDefaultText();
		mDateButton=(Button)findViewById(R.id.reminder_date);
		mTimeButton=(Button)findViewById(R.id.reminder_time);
		mCalender=Calendar.getInstance();
		
	}

	private void registerButtonListenersAndSetDefaultText() {
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);			
			}
		});
		
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG);
				
			}
		});
		updateDateButtonText();
		updateTimeButtonText();
		
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_DIALOG:
			
			return showDatePicker();
			
		case TIME_PICKER_DIALOG:
			
			return showTimePicker();
		}
		return super.onCreateDialog(id);		
	}
	
	

	private TimePickerDialog showTimePicker() {
		
		TimePickerDialog timePicker=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalender.set(Calendar.MINUTE, minute);
				updateTimeButtonText();
				
			}
		},mCalender.get(Calendar.HOUR_OF_DAY),mCalender.get(Calendar.MINUTE),true);
		
		return timePicker;
	}

	private DatePickerDialog showDatePicker() {
		
		DatePickerDialog datePicker=new DatePickerDialog(ReminderEditActivity.this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mCalender.set(Calendar.YEAR,year);
				mCalender.set(Calendar.MONTH, monthOfYear);
				mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDateButtonText();
			}
		},mCalender.get(Calendar.YEAR),mCalender.get(Calendar.MONTH),mCalender.get(Calendar.DAY_OF_MONTH));	
		
		return datePicker;
	}

	private void updateTimeButtonText() {
		SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
		String timeForButton=timeFormat.format(mCalender.getTime());
		mTimeButton.setText(timeForButton);
	}

	private void updateDateButtonText() {
		SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
		String dateForButton=dateFormat.format(mCalender.getTime());
		mDateButton.setText(dateForButton);
		
	}
	
	

}
