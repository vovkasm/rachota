/*
 * StateFilter.java
 *
 * Created on 11 August 2005, 08:23
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter tasks by state.
 *
 * @author Jiri Kovalsky
 */
public class StateFilter extends AbstractTaskFilter {
    
    /** Rule requiring given content to be equal to task property. */
    public static final int RULE_EQUALS = 0;
    /** Rule requiring given content NOT to be eual to task property. */
    public static final int RULE_EQUALS_NOT = 1;
    
    /** Creates new state filter. Filter accepts only RULE_EQUALS and RULE_EQUALS_NOT
     * content rules. Other rules will cause that tasks will not be filtered at all.
     * @param contentRule One of three content rules determining allowed value of task state.
     * @param state State level that must be equal/greater/smaller than task state.
     */
    public StateFilter(int contentRule, Integer state) {
        super(contentRule, state.toString());
    }
    
    /** Creates new default state filter which is preset to RULE_EQUALS
     * content rule and state Task.STATE_NEW.
     */
    public StateFilter() {
        this(RULE_EQUALS, new Integer(Task.STATE_NEW));
    }
    
    /** Returns all three available content rules of state filter.
     * @return RULE_EQUALS and RULE_EQUALS_NOT content rules.
     */
    public Vector getContentRules() {
        Vector contentRules = new Vector();
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS"));
        contentRules.add(Translator.getTranslation("FILTER.RULE_EQUALS_NOT"));
        return contentRules;
    }
    
    /** Returns all available content values of state filter.
     * @return All content values of state filter.
     */
    public Vector getContentValues() {
        Vector contentValues = new Vector();
        contentValues.add(Translator.getTranslation("TASK_STATE_0"));
        contentValues.add(Translator.getTranslation("TASK_STATE_1"));
        contentValues.add(Translator.getTranslation("TASK_STATE_2"));
        return contentValues;
    }
    
    /** Returns required state value of task.
     * @return Required state value of task.
     */
    public String getContent() {
        int index = Integer.parseInt(super.getContent());
        return (String) getContentValues().get(index);
    }
    
    /** Applies state filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = tasks.iterator();
        int requiredState = getContentValues().indexOf(getContent());
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            switch (contentRule) {
                case RULE_EQUALS:
                    if (task.getState() != requiredState) filteredTasks.remove(task);
                    break;
                case RULE_EQUALS_NOT:
                    if (task.getState() == requiredState) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task state can't be filtered by content rule: " + getContentRules().get(contentRule));
            }
        }
        return filteredTasks;
    }
    
    /** Returns name of filter as text.
     * @return Name of filter as text.
     */
    public String toString() {
        return Translator.getTranslation("TASK_STATE");
    }
}