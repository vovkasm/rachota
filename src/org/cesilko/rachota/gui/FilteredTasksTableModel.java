/*
 * FilteredTasksTableModel.java
 *
 * Created on August 18, 2005, 9:14 PM
 */

package org.cesilko.rachota.gui;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Table model for filtered tasks.
 * @author Jiri Kovalsky
 */
public class FilteredTasksTableModel extends AbstractTableModel {
    
    /** Identification of filter name column. */
    public static final int DESCRIPTION = 0;
    /** Identification of filter content rule column. */
    public static final int DURATION_TIME = 1;
    /** Identification of filter content column. */
    public static final int DURATION_DAYS = 2;
    /** Whether to group tasks with same description. */
    private boolean groupSameTasks;
    /** Vector of tasks to be displayed by this table model. */
    private Vector tasks;
    
    /** Creates a new instance of FilteredTasksTableModel */
    public FilteredTasksTableModel() {
        setGroupSameTasks(true);
        setTasks(new Vector());
    }
    
    /** Sets whether to group tasks with same description or not.
     * @param groupSameTasks If true, tasks with same description will occupy one row.
     */
    public void setGroupSameTasks(boolean groupSameTasks) {
        this.groupSameTasks = groupSameTasks;
        fireTableDataChanged();
    }
    
    /** Sets filtered tasks with new vector and refreshes the table.
     * @param tasks Vector of filtered tasks to be displayed in the table.
     */
    public void setTasks(Vector tasks) {
        this.tasks = tasks;
        fireTableDataChanged();
    }
    
    /** Returns value of cell located in given row and column.
     * @param row Number of row.
     * @param column Number of column.
     * @return Value of cell at specified location.
     */
    public Object getValueAt(int row, int column) {
        switch(column) {
            case DESCRIPTION:
                if (!groupSameTasks) {
                    Task task = (Task) tasks.get(row);
                    return task.getDescription();
                } else return getRow(row).get(DESCRIPTION);
            case DURATION_TIME:
                if (!groupSameTasks) {
                    Task task = (Task) tasks.get(row);
                    return Tools.getTime(task.getDuration());
                } else return getRow(row).get(DURATION_TIME);
            case DURATION_DAYS:
                if (!groupSameTasks) return "1";
                else return getRow(row).get(DURATION_DAYS);
            default:
                return "N/A";
        }
    }
    
    /** Returns number of rows in the table i.e. filtered tasks. The
     * number depends on the groupSameTasks setting.
     * @return Number of rows in the table i.e. number of filtered tasks
     * either unique or same depending on whether they should be grouped.
     */
    public int getRowCount() {
        if (!groupSameTasks) return tasks.size();
        return getUniqueTaskDescriptions().size();
    }
    
    /** Returns number of columns in the table i.e. 3.
     * @return Always 3 since table has three columns.
     */
    public int getColumnCount() {
        return 3;
    }
    
    /** Returns name of column by given column number.
     * @param column Number of column.
     * @return Name of column by given column number.
     */
    public String getColumnName(int column) {
        switch (column) {
            case DESCRIPTION:
                return Translator.getTranslation("TASKS.DESCRIPTION");
            case DURATION_TIME:
                return Translator.getTranslation("TASKS.DURATION_TIME");
            case DURATION_DAYS:
                return Translator.getTranslation("TASKS.DURATION_DAYS");
            default:
                return "N/A";
        }
    }
    
    /** Returns vector of all unique task descriptions.
     * @return Vector of all unique task descriptions.
     */
    private Vector getUniqueTaskDescriptions() {
        Vector uniqueTasks = new Vector();
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (!uniqueTasks.contains(task.getDescription()))
                uniqueTasks.add(task.getDescription());
        }
        return uniqueTasks;
    }
    
    /** Returns total time spent on all displayed tasks.
     * @return Total time spent on all displayed tasks in miliseconds.
     */
    public long getTotalTime() {
        Vector uniqueTasks = new Vector();
        Iterator iterator = tasks.iterator();
        long duration = 0;
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            duration = duration + task.getDuration();
        }
        return duration;
    }
    
    /**
     * Returns vector of three objects that should be displayed at given row
     * when tasks with same description are displayed.
     * @param row Number of row whose data will be returned.
     * @return Vector consisting of task description, total duration for all
     * tasks with the description and number of days the tasks were planned for.
     */
    private Vector getRow(int row) {
        String description = (String) getUniqueTaskDescriptions().get(row);
        Iterator iterator = tasks.iterator();
        int days = 0;
        long duration = 0;
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.getDescription().equals(description)) {
                duration = duration + task.getDuration();
                days = days + 1;
            }
        }
        Vector rowData = new Vector();
        rowData.add(description);
        rowData.add(Tools.getTime(duration));
        rowData.add("" + days);
        return rowData;
    }
}