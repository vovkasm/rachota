/*
 * ChangeListener.java
 *
 * Created on July 23, 2004, 8:50 PM
 */

package org.cesilko.rachota.core;

/** ChangeListener interface represents objects that are interested
 * in change events on certain objects.
 * @author  Jiri Kovalsky
 */
public interface ChangeListener {
    
    /** Given object fired a change event.
     * @param object Object that was changed.
     * @param changeType Type of change.
     */
    public void eventFired(Object object, int changeType);
    
    /** Generic type of change. */
    public static final int GENERIC_CHANGE = 0;
    
    /** Task changed its duration. Usually fired every second by currently running task. */
    public static final int TASK_DURATION_CHANGED = 1;
    
    /** New task was created and added to day. */
    public static final int TASK_CREATED = 2;
    
    /** Existing task was changed. This event is fired by editing dialog not the task. */
    public static final int TASK_CHANGED = 3;
}