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
 * Portions created by Jiri Kovalsky are Copyright (C) 2008
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on 31 January 2008  20:23
 * LimitedCapacityStack.java
 */

package org.cesilko.rachota.gui;

import java.util.Iterator;
import java.util.Vector;

/** Stack with ability to keep only limited number of objects.
 * Object is stored only if there is another object with smaller
 * key or some capacity left.
 * @author Jiri Kovalsky
 */
public class LimitedCapacityStack {

    /** Maximum number of objects that stack can keep. */
    int capacity;
    /** Vector of keys stored in stack. */
    Vector keys = new Vector();
    /** Vector of objects stored in stack. */
    Vector objects = new Vector();

    /** Creates new instance of stack with given capacity.
     * @param capacity Maximum number of objects to be stored in stack.
     */
    public LimitedCapacityStack(int capacity) {
        this.capacity = capacity;
    }

    /** Tries to store given object using provided key. If stack is not
     * full or given key is greater than some other key, both object and
     * key are stored in the stack. The smaller key and object are gone.
     * @param key Key to be compared with the stored keys.
     * @param object Object that wants to be stored in the stack.
     */
    public void put(Comparable key, Object object) {
        if (keys.size() < capacity) {
            keys.add(key);
            objects.add(object);
        }
        int smallestKeyIndex = -1;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable storedKey = (Comparable) iterator.next();
            if (key.compareTo(storedKey) > 0) keys.indexOf(storedKey);
        }
        if (smallestKeyIndex != -1) {
            keys.set(smallestKeyIndex, key);
            objects.set(smallestKeyIndex, object);
        }
    }

    /** Returns set of stored keys.
     * @return All keys stored in the stack.
     */
    public Vector getKeys() {
        return keys;
    }

    /** Returns set of stored objects.
     * @return All objects stored in the stack.
     */
    public Vector getObjects() {
        return objects;
    }
}
