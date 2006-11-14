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
 * Created on 28 August 2005  23:05
 * PrivateFilter.java
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter private tasks.
 *
 * @author Jiri Kovalsky
 */
public class PrivateFilter extends AbstractTaskFilter {
    
    /** Rule requiring given content to be equal to task property. */
    public static final int RULE_EQUALS = 0;
    /** Rule requiring given content NOT to be greater than task property. */
    public static final int RULE_EQUALS_NOT = 1;
    
    /** Creates new private filter. Filter accepts only RULE_EQUALS and RULE_EQUALS_NOT
     * content rules. Other rules will cause that tasks will not be filtered at all.
     * @param contentRule One of two content rules determining private property of task.
     * @param privateProperty Required value of task private property.
     */
    public PrivateFilter(int contentRule, Boolean privateProperty) {
        super(contentRule, privateProperty.toString());
    }
    
    /** Creates new default private filter which is preset to RULE_EQUALS
     * content rule and Boolean.TRUE.
     */
    public PrivateFilter() {
        this(RULE_EQUALS, new Boolean(true));
    }
    
    /** Returns both two available content rules of private filter.
     * @return RULE_EQUALS and RULE_EQUALS_NOT content rules.
     */
    public Vector getContentRules() {
        Vector contentRules = new Vector();
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS"));
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS_NOT"));
        return contentRules;
    }
    
    /** Returns all available content values of private filter.
     * @return All content values of private filter.
     */
    public Vector getContentValues() {
        Vector contentValues = new Vector();
        contentValues.add(Translator.getTranslation("FILTER.YES"));
        contentValues.add(Translator.getTranslation("FILTER.NO"));
        return contentValues;
    }
    
    /** Returns required private property of task.
     * @return Required private property of task.
     */
    public String getContent() {
        int index = Boolean.parseBoolean(super.getContent()) ? 0 : 1;
        return (String) getContentValues().get(index);
    }
    
    /** Applies private filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = tasks.iterator();
        boolean privateProperty = Boolean.parseBoolean(super.getContent());
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            switch (contentRule) {
                case RULE_EQUALS:
                    if (task.privateTask() != privateProperty) filteredTasks.remove(task);
                    break;
                case RULE_EQUALS_NOT:
                    if (task.privateTask() == privateProperty) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task priority can't be filtered by content rule: " + getContentRules().get(contentRule));
            }
        }
        return filteredTasks;
    }
    
    /** Returns name of filter as text.
     * @return Name of filter as text.
     */
    public String toString() {
        return Translator.getTranslation("TASK_PRIVATE");
    }
}