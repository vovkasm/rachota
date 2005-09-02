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
        sortTable(DURATION_TIME);
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
        String suffix = sortingOrder == ASCENDING ? " [+]" : " [-]";
        switch (column) {
            case DESCRIPTION:
                suffix = sortedColumn == DESCRIPTION ? suffix : "";
                return Translator.getTranslation("TASKS.DESCRIPTION") + suffix;
            case DURATION_TIME:
                suffix = sortedColumn == DURATION_TIME ? suffix : "";
                return Translator.getTranslation("TASKS.DURATION_TIME") + suffix;
            case DURATION_DAYS:
                suffix = sortedColumn == DURATION_DAYS ? suffix : "";
                return Translator.getTranslation("TASKS.DURATION_DAYS") + suffix;
        } return null;
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
    
    /** Returns vector of three objects that should be displayed at given row
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
    
    /** Currently selected sorting column. */
    private int sortedColumn = DURATION_TIME;
    /** Currently selected sorting order. */
    private boolean sortingOrder = DESCENDING;
    /** Ascending sorting order. */
    private static final boolean ASCENDING = true;
    /** Descending sorting order. */
    private static final boolean DESCENDING = false;
    
    /** Sorts table according to given column and known order.
     * @param column Column that will be used for sorting.
     */
    public void sortTable(int column) {
        int count = getRowCount();
        Vector sortedRows = new Vector();
        for (int i=0; i<count; i++) {
            Object maxValue = null;
            int maxRow = -1;
            for (int row=0; row<count; row++) {
                if (sortedRows.contains(new Integer(row))) continue;
                switch(column) {
                    case DESCRIPTION:
                        String description = (String) getValueAt(row, column);
                        if ((maxValue == null) || (description.compareTo((String) maxValue) > 0)) {
                            maxValue = description;
                            maxRow = row;
                        } break;
                    case DURATION_TIME:
                        long time = Tools.getTime((String) getValueAt(row, column));
                        if ((maxValue == null) || (time > ((Long) maxValue).longValue())) {
                            maxValue = new Long(time);
                            maxRow = row;
                        } break;
                    case DURATION_DAYS:
                        int days = (Integer.parseInt((String) getValueAt(row, column)));
                        if ((maxValue == null) || (days > ((Integer) maxValue).intValue())) {
                            maxValue = new Integer(days);
                            maxRow = row;
                        } break;
                }
            }
            sortedRows.add(new Integer(maxRow));
        }
        Vector newSortedTasks = new Vector();
        sortingOrder = sortedColumn == column ? !sortingOrder : ASCENDING;
        sortedColumn = column;
        for (int i=0; i<count; i++) {
            Integer row = (Integer) sortedRows.get(i);
            String description = (String) getValueAt(row.intValue(), DESCRIPTION);
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                Task task = (Task) iterator.next();
                if (task.getDescription().equals(description)) {
                    if (sortingOrder == DESCENDING) newSortedTasks.add(task);
                    else newSortedTasks.insertElementAt(task, 0);
                }
            }
        }
        tasks = newSortedTasks;
        fireTableDataChanged();
    }
}