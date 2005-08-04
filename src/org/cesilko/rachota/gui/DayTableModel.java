/*
 * DayTableModel.java
 *
 * Created on December 17, 2004, 8:18 PM
 */

package org.cesilko.rachota.gui;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.RegularTask;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/**
 * Table model for day view of tasks.
 * @author Jiri Kovalsky
 */
public class DayTableModel extends AbstractTableModel {
    
    /** Identification of priority column. */
    public static final int TASK_PRIORITY = 0;
    
    /** Identification of description column. */
    public static final int TASK_DESCRIPTION = 1;
    
    /** Identification of duration column. */
    public static final int TASK_DURATION = 2;
    
    /** Identification of state column. */
    public static final int TASK_STATE = 3;
    
    /** Identification of regular tasks column. */
    public static final int TASK_REGULAR = 4;
    
    /** Identification of notification column. */
    public static final int TASK_NOTIFICATION = 5;
    
    /** Identification of keyword column. */
    public static final int TASK_KEYWORD = 6;
    
    /** Identification of keyword column. */
    public static final int TASK_PRIVATE = 7;
    
    /** Which column is used to sort tasks. */
    private int sortedColumn;
    
    /** Does sorted column have ascending order ? */
    private boolean sortedAscending;
    
    /** Table with information if columns are selected i.e. visible or not. */
    Hashtable columns;
    
    /** Day that is currently selected. */
    private Day day;
    
    /** Translations of all column names. */
    String[] columnNames = {
        Translator.getTranslation("TASK_PRIORITY"),
                Translator.getTranslation("TASK_DESCRIPTION"),
                Translator.getTranslation("TASK_DURATION"),
                Translator.getTranslation("TASK_STATE"),
                Translator.getTranslation("TASK_REGULAR"),
                Translator.getTranslation("TASK_NOTIFICATION"),
                Translator.getTranslation("TASK_KEYWORD"),
                Translator.getTranslation("TASK_PRIVATE")
    };
    
    /**
     * Creates a new instance of DayTableModel.
     * @param day Day that this table model represents.
     */
    public DayTableModel(Day day) {
        setDay(day);
        columns = new Hashtable();
        setSelectedColumn(TASK_PRIORITY, true);
        setSelectedColumn(TASK_DESCRIPTION, true);
        setSelectedColumn(TASK_DURATION, true);
        setSelectedColumn(TASK_STATE, true);
        setSelectedColumn(TASK_REGULAR, false);
        setSelectedColumn(TASK_NOTIFICATION, false);
        setSelectedColumn(TASK_KEYWORD, false);
        setSelectedColumn(TASK_PRIVATE, false);
        sortedColumn = TASK_PRIORITY;
        sortedAscending = true;
    }
    
    /**
     * Sets day to be represented by this model.
     * @param day Day that this table model represents.
     */
    public void setDay(Day day) {
        this.day = day;
    }
    
    /**
     * Returns how many columns are currently selected and should be displayed.
     * @return Number of columns that are currently selected and should be displayed.
     */
    public int getColumnCount() {
        int length = columnNames.length;
        int count = 0;
        for (int i=0; i<length; i++) {
            Boolean state = (Boolean) columns.get(new Integer(i));
            if (state.booleanValue()) count++;
        }
        return count;
    }
    
    /**
     * Returns all translated column names.
     * @return All translated column names.
     */
    public String[] getAllColumnNames() {
        return columnNames;
    }
    
    /**
     * Returns how many rows should be displayed depending on visibility of finished tasks.
     * @return Number of rows to be displayed depending on visibility of finished tasks.
     */
    public int getRowCount() {
        Boolean displayFinishedTasks = (Boolean) Settings.getDefault().getSetting("displayFinishedTasks");
        Vector tasks = day.getTasks();
        int count = tasks.size();
        if (displayFinishedTasks.booleanValue()) return count;
        else {
            int openTasks = 0;
            for (int i=0; i<count; i++) {
                Task task = (Task) tasks.get(i);
                if (task.getState() != Task.STATE_DONE) openTasks++;
            }
            return openTasks;
        }
    }
    
    /**
     * Returns class that should be used to render all values in given column.
     * @param column Column whose class should be returned.
     * @return Class best describing values in given column i.e. Boolean for regular
     * tasks indication column and String for other columns.
     */
    public Class getColumnClass(int column) {
        switch (getColumnID(column)) {
            case TASK_REGULAR:
                return Boolean.class;
            case TASK_PRIVATE:
                return Boolean.class;
            default:
                return String.class;
        }
    }
    
    /**
     * Returns cell value located by given row and column.
     * @param row Row number to locate the interested cell.
     * @param column Column number to locate the interested cell.
     * @return Value of cell located by given row and column.
     */
    public Object getValueAt(int row, int column) {
        Task task = getTask(row);
        switch (getColumnID(column)) {
            case TASK_PRIORITY:
                return task.getPriority(task.getPriority());
            case TASK_DESCRIPTION:
                return task.getDescription();
            case TASK_DURATION:
                return Tools.getTime(task.getDuration());
            case TASK_STATE:
                return task.getState(task.getState());
            case TASK_REGULAR:
                return new Boolean(task instanceof RegularTask);
            case TASK_NOTIFICATION:
                Date notification = task.getNotificationTime();
                return (notification == null ? "N/A" : Tools.getTime(notification));
            case TASK_KEYWORD:
                return task.getKeyword();
            case TASK_PRIVATE:
                return new Boolean(task.privateTask());
            default:
                return "N/A";
        }
    }
    
    /**
     * Returns translated name of column identified by given visible column
     * number. The value depends on selected columns and currently sorted column
     * that gets [+] or [-] appended.
     * @param column Visible column number whose name will be returned.
     * @return Translated name of column identified by given visible column number.
     */
    public String getColumnName(int column) {
        int columnID = getColumnID(column);
        String name = columnNames[columnID];
        if (columnID == sortedColumn)
            name = name + (sortedAscending ? " [+]" : " [-]");
        return name;
    }
    
    /**
     * Returns absolute column number identified by given visible column number
     * i.e. if first two columns are not visible getColumnID(0) returns 2.
     * @param column Visible column number whose absolute column number will be calculated.
     * @return Absolute column number of given visible column.
     */
    public int getColumnID(int column) {
        int length = columnNames.length;
        int selectedColumns = 0;
        for (int i=0; i<length; i++) {
            if (isSelectedColumn(i)) {
                if (selectedColumns == column) {
                    selectedColumns = i;
                    break;
                }
                selectedColumns++;
            }
        }
        return selectedColumns;
    }
    
    /**
     * Finds out if given column is visible i.e. if first two columns are not
     * visible isSelectedColumn(1) returns false.
     * @param columnID Absolute column number to be checked for visibility.
     * @return True if column is visible or false otherwise.
     */
    public boolean isSelectedColumn(int columnID) {
        Boolean state = (Boolean) columns.get(new Integer(columnID));
        return state.booleanValue();
    }
    
    /**
     * Sets visibility for given column.
     * @param columnID Absolute column number to be set.
     * @param state True if column should be visible or false otherwise.
     */
    public void setSelectedColumn(int columnID, boolean state) {
        columns.remove(new Integer(columnID));
        columns.put(new Integer(columnID), new Boolean(state));
    }
    
    /**
     * Returns how many columns are selected i.e. visible.
     * @return Number of selected columns.
     */
    public int getSelectedColumnsCount() {
        int size = columns.size();
        int selectedColumns = 0;
        for (int i=0; i<size; i++)
            if (isSelectedColumn(i)) selectedColumns++;
        return selectedColumns;
    }
    
    
    /**
     * Changes sorting order (ascending <-> descending) for given column.
     * @param column Visible column number.
     * @param changeOrder True if sorting order should be changed.
     */
    public void setSortedColumn(int column, boolean changeOrder) {
        column = getColumnID(column);
        if (column == sortedColumn) {
            if (changeOrder) sortedAscending = !sortedAscending;
        } else {
            sortedColumn = column;
            sortedAscending = true;
        }
        day.sortTasks(sortedColumn, sortedAscending);
    }
    
    /**
     * Manually invokes resorting of all rows.
     */
    public void resortRows() {
        setSortedColumn(sortedColumn, false);
    }
    
    /**
     * Returns task located at given row depending on whether finished tasks
     * are displayed or not.
     * @param row Number of row whose task should be returned.
     * @return Task located at given row.
     */
    public Task getTask(int row) {
        Boolean displayFinishedTasks = (Boolean) Settings.getDefault().getSetting("displayFinishedTasks");
        Vector tasks = day.getTasks();
        Vector visibleTasks = new Vector();
        if (displayFinishedTasks.booleanValue()) visibleTasks = tasks;
        else {
            int count = tasks.size();
            for (int i=0; i<count; i++) {
                Task task = (Task) tasks.get(i);
                if (task.getState() != Task.STATE_DONE) visibleTasks.add(task);
            }
        }
        return (Task) visibleTasks.get(row);
    }
    
    /**
     * Returns number of row occupied by given task.
     * @param task Task whose row number should be returned.
     * @return Number of row occupied by given task.
     */
    public int getRow(Task task) {
        Boolean displayFinishedTasks = (Boolean) Settings.getDefault().getSetting("displayFinishedTasks");
        Vector tasks = day.getTasks();
        Vector visibleTasks = new Vector();
        if (displayFinishedTasks.booleanValue()) visibleTasks = tasks;
        else {
            int count = tasks.size();
            for (int i=0; i<count; i++) {
                Task indexedTask = (Task) tasks.get(i);
                if (indexedTask.getState() != Task.STATE_DONE) visibleTasks.add(indexedTask);
            }
        }
        return visibleTasks.indexOf(task);
    }
}