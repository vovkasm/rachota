/*
 * ChangeHandler.java
 *
 * Created on July 23, 2004, 8:56 PM
 */

package org.cesilko.rachota.core;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/** Singleton responsible for distribution of all change events fired in the system.
 *
 * @author  Jiri Kovalsky
 */
public class ChangeHandler {
    
    /** The only instance of ChangeHandler object in the system. */
    private static ChangeHandler ChangeHandler;
    
    /** Table of all change event listeners in the system grouped by the changing objects. */
    private Hashtable changeEventListeners;
    
    /** Creates a new instance of ChangeHandler class. */
    private ChangeHandler() {
        changeEventListeners = new Hashtable();
    }
    
    /** Returns default instance of ChangeHandler class.
     * @return Default instance of ChangeHandler class.
     */
    public static ChangeHandler getDefault() {
        if (ChangeHandler == null)
            ChangeHandler = new ChangeHandler();
        return ChangeHandler;
    }
    
    /** Registers new change event listener by given object of interest.
     * @param changeEventListener New change event listener for registration.
     * @param object Object that is listener interested in.
     */
    public void addChangeEventListener(ChangeListener changeEventListener, Object object) {
        Vector allObjectChangeEventListeners = (Vector) changeEventListeners.get(object);
        if (allObjectChangeEventListeners == null) {
            allObjectChangeEventListeners = new Vector();
            allObjectChangeEventListeners.add(changeEventListener);
            changeEventListeners.put(object, allObjectChangeEventListeners);
        } else
            allObjectChangeEventListeners.add(changeEventListener);
    }
    
    /** Removes change event listener with given object of interest from list of registered listeners.
     * @param changeEventListener Registered change event listener.
     * @param object Object that is listener interested in.
     */
    public void removeChangeEventListener(ChangeListener changeEventListener, Object object) {
        Vector allObjectChangeEventListeners = (Vector) changeEventListeners.get(object);
        if (allObjectChangeEventListeners != null)
            allObjectChangeEventListeners.remove(changeEventListener);
        if (allObjectChangeEventListeners.size() == 0)
            changeEventListeners.remove(object);
    }
    
    /** Informs all of registered listeners with interest in given object about the event.
     * @param object Object that fired the event.
     * @param changeType Type of change that happened on object.
     */
    public void fireEvent(Object object, int changeType) {
        Vector allObjectChangeEventListeners = (Vector) changeEventListeners.get(object);
        if (allObjectChangeEventListeners != null) {
            Iterator iterator = allObjectChangeEventListeners.iterator();
            while (iterator.hasNext()) {
                ChangeListener changeEventListener = (ChangeListener) iterator.next();
                changeEventListener.eventFired(object, changeType);
            }
        }
    }
}