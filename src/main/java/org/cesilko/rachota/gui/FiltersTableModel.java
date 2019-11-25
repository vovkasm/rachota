/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://rachota.sourceforge.net/license.txt.
 * 
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://rachota.sourceforge.net/license.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is Rachota.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on August 18, 2005, 9:06 PM
 * FiltersTableModel.java
 */

package org.cesilko.rachota.gui;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.AbstractTaskFilter;

/** Table model for task filters.
 * @author Jiri Kovalsky
 */
public class FiltersTableModel extends AbstractTableModel {
    
    /** Identification of filter name column. */
    public static final int FILTER_NAME = 0;
    /** Identification of filter content rule column. */
    public static final int FILTER_CONTENT_RULE = 1;
    /** Identification of filter content column. */
    public static final int FILTER_CONTENT = 2;
    /** Set of filters for selecting only particular tasks. */
    private Vector filters;
    
    /** Creates a new instance of FiltersTableModel */
    public FiltersTableModel() {
        filters = new Vector();
    }
    
    /** Adds new task filter to table.
     * @param taskFilter New task filter to be added.
     */
    public void addFilter(AbstractTaskFilter taskFilter) {
        filters.add(taskFilter);
        fireTableDataChanged();
    }
    
    /** Removes existing task filter from table.
     * @param taskFilter Existing task filter to be removed.
     */
    public void removeFilter(AbstractTaskFilter taskFilter) {
        filters.remove(taskFilter);
        fireTableDataChanged();
    }
    
    /** Replaces existing task filter by another filter.
     * @param oldTaskFilter Existing task filter to be removed.
     * @param newTaskFilter New task filter to be added instead of the old one.
     */
    public void replaceFilter(AbstractTaskFilter oldTaskFilter, AbstractTaskFilter newTaskFilter) {
        int index = filters.indexOf(oldTaskFilter);
        filters.setElementAt(newTaskFilter, index);
        fireTableDataChanged();
    }
    
    /** Returns filter at given row.
     * @param row Number of row whose filter should be returned.
     * @return Filter at given row or null if there is no filter.
     */
    public AbstractTaskFilter getFilter(int row) {
        AbstractTaskFilter taskFilter = null;
        if (row < filters.size()) taskFilter = (AbstractTaskFilter) filters.get(row);
        return taskFilter;
    }
    
    /** Returns all defined filters.
     * @return All defined filters.
     */
    public Vector getFilters() {
        return filters;
    }
    
    /** Returns number of columns in the table i.e. 3.
     * @return Always 3 since table has three columns.
     */
    public int getColumnCount() {
        return 3;
    }
    
    /** Returns number of rows (task filters) in the table.
     * @return Number of rows in the table i.e. number of task filters.
     */
    public int getRowCount() {
        if (filters == null) return 0;
        return filters.size();
    }
    
    /** Returns value of cell located in given row and column.
     * @param row Number of row.
     * @param column Number of column.
     * @return Value of cell at specified location.
     */
    public Object getValueAt(int row, int column) {
        AbstractTaskFilter taskFilter = (AbstractTaskFilter) filters.get(row);
        switch (column) {
            case FILTER_NAME:
                return taskFilter.toString();
            case FILTER_CONTENT_RULE:
                return taskFilter.getContentRules().get(taskFilter.getContentRule());
            case FILTER_CONTENT:
                return taskFilter.getContent();
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
            case FILTER_NAME:
                return Translator.getTranslation("FILTER.NAME");
            case FILTER_CONTENT_RULE:
                return Translator.getTranslation("FILTER.CONTENT_RULE");
            case FILTER_CONTENT:
                return Translator.getTranslation("FILTER.CONTENT");
            default:
                return "N/A";
        }
    }
}