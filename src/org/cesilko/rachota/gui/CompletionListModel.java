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
 * Created on September 8, 2006  7:12 PM
 * CompletionListModel.java
 */

package org.cesilko.rachota.gui;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractListModel;

/** Model that takes care of current list of completion items.
 * @author Jiri Kovalsky
 */
public class CompletionListModel extends AbstractListModel {
    
    /** List of items that are suggested in completion window. */
    Vector completionItems = new Vector();
    
    /** Returns number of completion items.
     * @return Number of completion items.
     */
    public int getSize() {
        return completionItems.size();
    }
    
    /** Returns completion item at given index.
     * @param index Index whose completion item should be returned.
     * @return Completion item at given index.
     */
    public Object getElementAt(int index) {
        return completionItems.get(index);
    }
    
    /** Sets completion items to given list.
     * @param items Vector of new completion items to be used from now.
     */
    public void setItems(Vector items) {
        completionItems = (Vector) sortItems((Vector) items.clone());
        fireContentsChanged(this, 0, getSize());
    }
    
    /** Returns list of completion item sorted alphabetically.
     * @param items Completion items that should be sorted alphabetically.
     * @return Sorted list of completion items.
     */
    private Vector sortItems(Vector items) {
        Vector sortedItems = new Vector();
        while (items.size() > 0) {
            String selectedItem = (String) items.get(0);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                String item = (String) iterator.next();
                if (item.compareToIgnoreCase(selectedItem) < 0) selectedItem = item;
            }
            items.remove(selectedItem);
            sortedItems.add(selectedItem);
        }
        return sortedItems;
    }
}