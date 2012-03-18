/**
 * 
 */
package open.gtaskdroid.adaptors;

import java.util.List;

import open.Gtaskdroid.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author rajith
 *
 */
public class CursorArrayAdapter extends ArrayAdapter<CursorAdapterData> {

	
	private final List<CursorAdapterData> list;
	private final Activity context;
	
	public CursorArrayAdapter(Activity context, List <CursorAdapterData> list) {
		super(context,R.layout.event_list_row, list);
		this.context=context;
		this.list=list;		
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position,View convertView, ViewGroup parent ){
		View view= null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.event_list_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.taskTitle = (TextView) view.findViewById(R.id.taskTitle);
			viewHolder.taskDueDate = (TextView) view.findViewById(R.id.taskDueDate);
			viewHolder.taskDueTime = (TextView) view.findViewById(R.id.taskDueTime);

			view.setTag(viewHolder);

		} else {
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		
		if(list.get(position).getRowId()<0){
			view.setFocusable(true);
			view.setBackgroundColor(0xff444444);
		}
		
		holder.taskTitle.setText(list.get(position).getEventTitle());
		holder.taskDueDate.setText(list.get(position).getWeekDate());
		holder.taskDueTime.setText(list.get(position).getEventTime());

		return view;
	}
	
	
	static class ViewHolder {
		
	
		protected TextView taskTitle;
		protected TextView taskDueDate;
		protected TextView taskDueTime;
	}

}
