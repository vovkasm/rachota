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
    
    /** Creates new state filter. Filter accepts only RULE_EQUALS, RULE_LESS_THAN
     * and RULE_MORE_THAN content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of three content rules determining allowed value of task state.
     * @param state State level that must be equal/greater/smaller than task state.
     */
    public StateFilter(int contentRule, Integer state) {
        super(Translator.getTranslation("TASK_STATE"), contentRule, state);
    }
    
    /** Applies state filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = filteredTasks.iterator();
        Integer state = (Integer) getContent();
        int requiredState = state.intValue();
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            switch (contentRule) {
                case RULE_EQUALS:
                    if (task.getState() != requiredState) filteredTasks.remove(task);
                    break;
                case RULE_LESS_THAN:
                    if (task.getState() > requiredState) filteredTasks.remove(task);
                    break;
                case RULE_MORE_THAN:
                    if (task.getState() < requiredState) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task state can't be filtered by content rule: " + getContentRule(contentRule));
            }
        }
        return filteredTasks;
    }
}