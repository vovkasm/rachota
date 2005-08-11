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
    
    /** Creates new priority filter. Filter accepts only RULE_EQUALS, RULE_LESS_THAN
     * and RULE_MORE_THAN content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of three content rules determining allowed value of task priority.
     * @param priority Priority level that must be equal/greater/smaller than task priority.
     */
    public PriorityFilter(int contentRule, Integer priority) {
        super(Translator.getTranslation("TASK_PRIORITY"), contentRule, priority);
    }
    
    /** Applies priority filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = filteredTasks.iterator();
        Integer priority = (Integer) getContent();
        int requiredPriority = priority.intValue();
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            switch (contentRule) {
                case RULE_EQUALS:
                    if (task.getPriority() != requiredPriority) filteredTasks.remove(task);
                    break;
                case RULE_LESS_THAN:
                    if (task.getPriority() > requiredPriority) filteredTasks.remove(task);
                    break;
                case RULE_MORE_THAN:
                    if (task.getPriority() < requiredPriority) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task priority can't be filtered by content rule: " + getContentRule(contentRule));
            }
        }
        return filteredTasks;
    }
}