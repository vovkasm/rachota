/*
 * CompletionListModel.java
 *
 * Created on September 8, 2006, 7:12 PM
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
        completionItems = (Vector) sortItems(items).clone();
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