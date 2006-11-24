/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is Rachota.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on June 18, 2005  11:03 PM
 * RegularTasksTableModel.java
 */

package org.cesilko.rachota.gui;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.cesilko.rachota.core.RegularTask;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Translator;

/** Table model for regular tasks in settings dialog.
 * @author Jiri Kovalsky
 */
public class RegularTasksTableModel extends AbstractTableModel {
    
    /** Identification of task priority column. */
    public static final int TASK_PRIORITY = 0;
    /** Identification of task description column. */
    public static final int TASK_DESCRIPTION = 1;
    /** Identification of task frequency column. */
    public static final int TASK_REGULAR = 2;
    /** Vector of regular tasks. */
    private Vector regularTasks;
    /** Currently selected sorting column. */
    private int sortedColumn = TASK_REGULAR;
    /** Currently selected sorting order. */
    private boolean sortingOrder = ASCENDING;
    /** Ascending sorting order. */
    private static final boolean ASCENDING = true;
    /** Descending sorting order. */
    private static final boolean DESCENDING = false;
    
    /** Sorts table according to given column and known order.
     * @param column Column that will be used for sorting.
     * @return Vector of sorted regular tasks.
     */
    public Vector sortTable(final int column) {
        sortingOrder = !sortingOrder;
        sortedColumn = column;
        Settings.getDefault().setSetting("regularTasks.sorting.column", new Integer(column).toString());
        Settings.getDefault().setSetting("regularTasks.sorting.order", new Boolean(sortingOrder));
        Vector sortedRegularTasks = new Vector();
        int count = regularTasks.size();
        for (int i = 0; i < count; i++) { // Let's take all tasks one by one
            Iterator iterator = regularTasks.iterator();
            RegularTask selectedTask = (RegularTask) iterator.next();
            while (iterator.hasNext()) {
                RegularTask task = (RegularTask) iterator.next();
                switch(column) {
                    case TASK_PRIORITY:
                        if (sortingOrder == ASCENDING) {
                            if (task.getPriority() > selectedTask.getPriority()) selectedTask = task;
                        } else if (task.getPriority() < selectedTask.getPriority()) selectedTask = task;
                        break;
                    case TASK_DESCRIPTION:
                        if (sortingOrder == ASCENDING) {
                            if (task.getDescription().compareTo(selectedTask.getDescription()) < 0) selectedTask = task;
                        } else if (task.getDescription().compareTo(selectedTask.getDescription()) > 0) selectedTask = task;
                        break;
                    default:
                        if (sortingOrder == ASCENDING) {
                            if (task.getFrequency() < selectedTask.getFrequency()) selectedTask = task;
                        } else if (task.getFrequency() > selectedTask.getFrequency()) selectedTask = task;
                        break;
                }
            }
            sortedRegularTasks.add(selectedTask);
            regularTasks.remove(selectedTask);
        }
        regularTasks = sortedRegularTasks;
        return regularTasks;
    }
    
    /** Creates new table model for regular tasks in settings dialog.
     * @param regularTasks Set of currently planned regular tasks.
     */
    public RegularTasksTableModel(Vector regularTasks) {
        this.regularTasks = regularTasks;
        Settings settings = Settings.getDefault();
        try {
            sortedColumn = Integer.parseInt((String) settings.getSetting("regularTasks.sorting.column"));
            sortingOrder = ((Boolean) settings.getSetting("regularTasks.sorting.order")).booleanValue();
        } catch (NumberFormatException e) {
            System.out.println("Error: Can't load sorting of regular tasks. Using default values.");
            sortedColumn = TASK_REGULAR;
            sortingOrder = ASCENDING;
            settings.setSetting("regularTasks.sorting.column", new Integer(sortedColumn).toString());
            settings.setSetting("regularTasks.sorting.order", new Boolean(sortingOrder));
        }
    }
    
    /** Returns number of columns in the table i.e. 3.
     * @return Always 3 since table has three columns.
     */
    public int getColumnCount() {
        return 3;
    }
    
    /** Returns number of rows (regular tasks) in the table.
     * @return Number of rows in the table i.e. number of regular tasks.
     */
    public int getRowCount() {
        return regularTasks.size();
    }
    
    /** Returns value of cell located in given row and column.
     * @param row Number of row.
     * @param column Number of column.
     * @return Value of cell at specified location.
     */
    public Object getValueAt(int row, int column) {
        RegularTask task = (RegularTask) regularTasks.get(row);
        switch (column) {
            case TASK_PRIORITY:
                return task.getPriority(task.getPriority());
            case TASK_DESCRIPTION:
                return task.getDescription();
            case TASK_REGULAR:
                return task.getFrequency(task.getFrequency());
            default:
                return "N/A";
        }
    }
    
    /** Returns name of column by given column number.
     * @param column Number of column.
     * @return Name of column by given column number.
     */
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case TASK_PRIORITY:
                name = Translator.getTranslation("TASK_PRIORITY");
                break;
            case TASK_DESCRIPTION:
                name = Translator.getTranslation("TASK_DESCRIPTION");
                break;
            default:
                name = Translator.getTranslation("TASK_REGULAR");
        }
        if (column == sortedColumn)
            name = name + (sortingOrder == ASCENDING ? " [+]" : " [-]");
        return name;
    }
}