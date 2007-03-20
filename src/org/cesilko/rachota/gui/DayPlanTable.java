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
 * Created on March 9, 2007  9:05 PM
 * DayPlanTable.java
 */

package org.cesilko.rachota.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.cesilko.rachota.core.Task;

/** Table containing plan of tasks.
 * @author Jiri Kovalsky
 */
public class DayPlanTable extends JTable {
    
    private Task selectedTask = null;
    
    /** Sets given task as selected to render it properly.
     * @param selectedTask Task that was selected by user.
     */
    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }

    public TableCellRenderer getCellRenderer(int row, int col) {
        return new DayPlanRenderer();
    }

    private class DayPlanRenderer extends DefaultTableCellRenderer {
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                              boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (table.getSelectedRow() == row) return renderer;
            if (selectedTask == null) return renderer;
            DayTableModel dayTableModel = (DayTableModel) getModel();
            if (dayTableModel.getRow(selectedTask) != row) return renderer;
            renderer.setBackground(Color.DARK_GRAY);
            renderer.setForeground(Color.GREEN);
            return renderer;
        }
    }
}