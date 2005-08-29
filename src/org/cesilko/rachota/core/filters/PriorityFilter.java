/*
 * PriorityFilter.java
 *
 * Created on 11 August 2005, 08:05
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter tasks by priority.
 *
 * @author Jiri Kovalsky
 */
public class PriorityFilter extends AbstractTaskFilter {
    
    /** Rule requiring given content to be equal to task property. */
    public static final int RULE_EQUALS = 0;
    /** Rule requiring given content NOT to be greater than task property. */
    public static final int RULE_EQUALS_NOT = 1;
    
    /** Creates new priority filter. Filter accepts only RULE_EQUALS and RULE_EQUALS_NOT
     * content rules. Other rules will cause that tasks will not be filtered at all.
     * @param contentRule One of two content rules determining allowed value of task priority.
     * @param priority Priority level that must be equal/different than task priority.
     */
    public PriorityFilter(int contentRule, Integer priority) {
        super(contentRule, priority.toString());
    }
    
    /** Creates new default priority filter which is preset to RULE_EQUALS
     * content rule and Task.PRIORITY_LOW.
     */
    public PriorityFilter() {
        this(RULE_EQUALS, new Integer(Task.PRIORITY_LOW));
    }
    
    /** Returns both two available content rules of priority filter.
     * @return RULE_EQUALS and RULE_EQUALS_NOT content rules.
     */
    public Vector getContentRules() {
        Vector contentRules = new Vector();
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS"));
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS_NOT"));
        return contentRules;
    }
    
    /** Returns all available content values of priority filter.
     * @return All content values of priority filter.
     */
    public Vector getContentValues() {
        Vector contentValues = new Vector();
        contentValues.add(Translator.getTranslation("TASK_PRIORITY_0"));
        contentValues.add(Translator.getTranslation("TASK_PRIORITY_1"));
        contentValues.add(Translator.getTranslation("TASK_PRIORITY_2"));
        return contentValues;
    }
    
    /** Returns required priority value of task.
     * @return Required priority value of task.
     */
    public String getContent() {
        int index = Integer.parseInt(super.getContent());
        return (String) getContentValues().get(index);
    }
    
    /** Applies priority filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = tasks.iterator();
        int requiredPriority = getContentValues().indexOf(getContent());
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            switch (contentRule) {
                case RULE_EQUALS:
                    if (task.getPriority() != requiredPriority) filteredTasks.remove(task);
                    break;
                case RULE_EQUALS_NOT:
                    if (task.getPriority() == requiredPriority) filteredTasks.remove(task);
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
        return Translator.getTranslation("TASK_PRIORITY");
    }
}