/*
 * ClockListener.java
 *
 * Created on 12. duben 2003, 20:43
 */

package org.cesilko.rachota.core;

/** Object that wants to be notified at each clock tick.
 * @author Jiri Kovalsky
 */
public interface ClockListener {

    /** Method called when one clock tick is over.
     */
    public void tick();
}