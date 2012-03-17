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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * @author rajith
 *
 */
public class ListCursorAdapter extends ArrayAdapter<ListCursorData> {

	
	private final List<ListCursorData> list;
	private final Activity context;
	
	public ListCursorAdapter(Activity context, List <ListCursorData> list) {
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
			viewHolder.taskDue = (TextView) view.findViewById(R.id.taskDueTime);
			//viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			/*viewHolder.checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							ListArrayAdapterDataModel element = (ListArrayAdapterDataModel) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());
							
						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));*/
		} else {
			view = convertView;
			//((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.taskTitle.setText(list.get(position).getTaskTitle());
		holder.taskDue.setText(list.get(position).getTaskDue());
		//holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
	
	
	static class ViewHolder {
		
	
		protected TextView taskTitle;
		protected TextView taskDue;
		//protected CheckBox checkbox;
	}

}