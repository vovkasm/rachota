/*
 * RegularTasksTableModel.java
 *
 * Created on June 18, 2005, 11:03 PM
 */

package org.cesilko.rachota.gui;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.cesilko.rachota.core.RegularTask;
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
    
    /** Creates new table model for regular tasks in settings dialog.
     * @param regularTasks Set of currently planned regular tasks.
     */
    public RegularTasksTableModel(Vector regularTasks) {
        this.regularTasks = regularTasks;
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
        switch (column) {
            case TASK_PRIORITY:
                return Translator.getTranslation("TASK_PRIORITY");
            case TASK_DESCRIPTION:
                return Translator.getTranslation("TASK_DESCRIPTION");
            case TASK_REGULAR:
                return Translator.getTranslation("TASK_REGULAR");
            default:
                return "N/A";
        }
    }
}