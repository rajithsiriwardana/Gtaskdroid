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
import android.widget.TextView;

/**
 * @author rajith
 *
 */
public class ListArrayAdapter extends ArrayAdapter<ListDataModel> {

	
	private final List<ListDataModel> list;
	private final Activity context;
	
	public ListArrayAdapter(Activity context, List <ListDataModel> list) {
		super(context,R.layout.gtask_event_row, list);
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
			view = inflator.inflate(R.layout.gtask_event_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.taskTitle = (TextView) view.findViewById(R.id.taskTitle);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
		/*	viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							ListDataModel element = (ListDataModel) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());

						}
					});*/
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.taskTitle.setText(list.get(position).getTaskTitle());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
	
	
	static class ViewHolder {
		
	
		protected TextView taskTitle;
		protected CheckBox checkbox;
	}

}
