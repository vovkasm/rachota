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
 * Created on April 10, 2003  21:15
 * Clock.java
 */

package org.cesilko.rachota.core;

import java.util.Iterator;
import java.util.Vector;

/** System clock ticking every second by default.
 * @author Jiri Kovalsky
 */
public class Clock extends Thread {
    
    /** Clock period i.e. time gap between two ticks. */
    private long tick;
    /** Set of clock listeners that want to be notified at each tick. */
    private Vector listeners;
    /** True if clock is ticking. */
    private boolean ticking;
    /** The only instance of clock object in the system. */
    private static Clock clock;
    
    /** Returns default instance of Clock object. Tick period is initially set to 1000 ms.
     * @return The only instance of clock in the system.
     */
    public static Clock getDefault() {
	if (clock == null) clock = new Clock(1000);
	return clock;
    }
    
    /** Clock constructor.
     * @param tick Required clock tick in milliseconds.
     */
    private Clock(long tick) {
	listeners = new Vector();
	setTick(tick);
	resumeClock();
    }
    
    /** Sets period of one clock tick.
     * @param tick Clock period i.e. time between two notifications to listeners.
     */
    public void setTick(long tick) {
	this.tick = tick;
    }
    
    /** Adds new clock listener.
     * @param listener Object who whats to be notified after each tick.
     */
    public synchronized void addListener(ClockListener listener) {
	if (!listeners.contains(listener)) listeners.add(listener);
    }
    
    /** Removes already registered clock listener.
     * @param listener Object who no longer whats to be notified after each tick.
     */
    public synchronized void removeListener(ClockListener listener) {
	if (listeners.contains(listener)) listeners.remove(listener);
    }
    
    /** Main clock loop. Nothing happens for given tick period and then all
     * listeners are notified about that fact.
     */
    public void run() {
	while (true) {
	    try { sleep(tick); } catch (InterruptedException e) {
		System.out.println("Warning: System clock terminated by user.");
		e.printStackTrace();
	    }
	    if (ticking) {
		synchronized (this) {
		    Iterator iterator = listeners.iterator();
		    while (iterator.hasNext()) {
			ClockListener listener = (ClockListener) iterator.next();
			listener.tick();
		    }
		}
	    }
	}
    }
    
    /** Temporarily stops clock. */
    public void suspendClock() {
	ticking = false;
    }
    
    /** Starts clock again. */
    public void resumeClock() {
	ticking = true;
    }
    
    /** Returns whether clock is ticking or not.
     * @return True if clock is ticking or false otherwise.
     */
    public boolean isTicking() {
	return ticking;
    }
}