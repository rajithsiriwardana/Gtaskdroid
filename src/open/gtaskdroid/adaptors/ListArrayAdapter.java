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
public class ListArrayAdapter extends ArrayAdapter<ListArrayAdapterDataModel> {

	
	private final List<ListArrayAdapterDataModel> list;
	private final Activity context;
	
	/**
	 * 
	 * @param context
	 * @param list
	 */
	public ListArrayAdapter(Activity context, List <ListArrayAdapterDataModel> list) {
		super(context,R.layout.gtask_event_row, list);
		this.context=context;
		this.list=list;		
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position,View convertView, ViewGroup parent ){
		View view= null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.gtask_event_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.taskTitle = (TextView) view.findViewById(R.id.taskTitle);
			viewHolder.taskDue = (TextView) view.findViewById(R.id.taskDue);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
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
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.taskTitle.setText(list.get(position).getTaskTitle());
		holder.taskDue.setText(list.get(position).getTaskDue());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
	
	/**
	 * to hold row data without requesting again and again
	 * @author rajith
	 *
	 */
	static class ViewHolder {
		
	
		protected TextView taskTitle;
		protected TextView taskDue;
		protected CheckBox checkbox;
	}

}
