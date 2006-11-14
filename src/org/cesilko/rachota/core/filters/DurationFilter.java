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
 * Created on 11 August 2005  07:41
 * DurationFilter.java
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.gui.Tools;

/** Task filter allowing to filter tasks by duration.
 *
 * @author Jiri Kovalsky
 */
public class DurationFilter extends AbstractTaskFilter {
    
    /** Rule requiring given content NOT to be greater than task property. */
    public static final int RULE_MORE_THAN = 0;
    /** Rule requiring given content NOT to be smaller than task property. */
    public static final int RULE_LESS_THAN = 1;
    
    /** Creates new duration filter. Filter accepts only RULE_LESS_THAN and
     * RULE_MORE_THAN content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of two content rules determining allowed value of task duration.
     * @param duration Time duration in millisecondss that must be greater/smaller than task duration.
     */
    public DurationFilter(int contentRule, Long duration) {
        super(contentRule, Tools.getTime(duration.longValue()));
    }
    
    /** Creates new default duration filter which is preset to RULE_MORE_THAN
     * content rule and zero duration.
     */
    public DurationFilter() {
        this(RULE_MORE_THAN, new Long(0));
    }
    
    /** Returns both available content rules of duration filter.
     * @return RULE_LESS_THAN and RULE_MORE_THAN content rules.
     */
    public Vector getContentRules() {
        Vector contentRules = new Vector();
        contentRules.add(Translator.getTranslation("FILTER.RULE_LESS_THAN"));
        contentRules.add(Translator.getTranslation("FILTER.RULE_MORE_THAN"));
        return contentRules;
    }
    
    /** Applies duration filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = tasks.iterator();
        long requiredDuration = Tools.getTime(getContent());
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            boolean taskLonger = task.getDuration() > requiredDuration;
            switch (contentRule) {
                case RULE_LESS_THAN:
                    if (!taskLonger) filteredTasks.remove(task);
                    break;
                case RULE_MORE_THAN:
                    if (taskLonger) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task duration can't be filtered by content rule: " + getContentRules().get(contentRule));
            }
        }
        return filteredTasks;
    }
    
    /** Returns name of filter as text.
     * @return Name of filter as text.
     */
    public String toString() {
        return Translator.getTranslation("TASK_DURATION");
    }
}