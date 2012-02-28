/**
 * 
 */
package open.gtaskdroid.adaptors;

/**
 * @author rajith
 *
 */
public class ListDataModel {
	
	private String taskTitle;
	private boolean selected;
	
	
	/**
	 * @param taskTitle
	 */
	public ListDataModel(String taskTitle){
		this.taskTitle=taskTitle;
		this.selected=false;
	}
	
	/**
	 * @return the taskTitle
	 */
	public String getTaskTitle() {
		return taskTitle;
	}
	/**
	 * @param taskTitle the taskTitle to set
	 */
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
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

}
