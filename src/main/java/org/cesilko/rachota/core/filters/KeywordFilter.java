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
 * Created on 11 August 2005  07:28
 * KeywordFilter.java
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter tasks by keyword.
 *
 * @author Jiri Kovalsky
 */
public class KeywordFilter extends AbstractTaskFilter {
    
    /** Rule requiring given content to be part of task property. */
    public static final int RULE_CONTAINS = 0;
    /** Rule requiring given content not to be part of task property. */
    public static final int RULE_CONTAINS_NOT = 1;
    
    /** Creates new keyword filter. Filter accepts only RULE_CONTAINS and
     * RULE_CONTAINS_NOT content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of two content rules determining allowed content in task keyword.
     * @param subString Text that must/mustn't be present in task keyword.
     */
    public KeywordFilter(int contentRule, String subString) {
        super(contentRule, subString);
    }
    
    /** Creates new default keyword filter which is preset to RULE_CONTAINS
     * content rule and no text.
     */
    public KeywordFilter() {
        this(RULE_CONTAINS, "");
    }
    
    /** Returns both available content rules of keyword filter.
     * @return RULE_CONTAINS and RULE_CONTAINS_NOT content rules.
     */
    public Vector getContentRules() {
        Vector contentRules = new Vector();
        contentRules.add(Translator.getTranslation("FILTER.RULE_CONTAINS"));
        contentRules.add(Translator.getTranslation("FILTER.RULE_CONTAINS_NOT"));
        return contentRules;
    }
    
    /** Applies keyword filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = tasks.iterator();
        String subString = (String) getContent();
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            boolean containsSubString = task.getKeyword().indexOf(subString) != -1;
            switch (contentRule) {
                case RULE_CONTAINS:
                    if (!containsSubString) filteredTasks.remove(task);
                    break;
                case RULE_CONTAINS_NOT:
                    if (containsSubString) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task keyword can't be filtered by content rule: " + getContentRules().get(contentRule));
            }
        }
        return filteredTasks;
    }
    
    /** Returns name of filter as text.
     * @return Name of filter as text.
     */
    public String toString() {
        return Translator.getTranslation("TASK_KEYWORD");
    }
}