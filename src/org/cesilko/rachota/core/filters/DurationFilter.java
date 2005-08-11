/*
 * DurationFilter.java
 *
 * Created on 11 August 2005, 07:41
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter tasks by duration.
 *
 * @author Jiri Kovalsky
 */
public class DurationFilter extends AbstractTaskFilter {
    
    /** Creates new duration filter. Filter accepts only RULE_LESS_THAN and
     * RULE_MORE_THAN content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of two content rules determining allowed value of task duration.
     * @param duration Time duration in millisecondss that must be greater/smaller than task duration.
     */
    public DurationFilter(int contentRule, Long duration) {
        super(Translator.getTranslation("TASK_DURATION"), contentRule, duration);
    }
    
    /** Applies duration filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = filteredTasks.iterator();
        Long duration = (Long) getContent();
        long requiredDuration = duration.longValue();
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            boolean taskLonger = task.getDuration() > requiredDuration;
            switch (contentRule) {
                case RULE_LESS_THAN:
                    if (taskLonger) filteredTasks.remove(task);
                    break;
                case RULE_MORE_THAN:
                    if (!taskLonger) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task duration can't be filtered by content rule: " + getContentRule(contentRule));
            }
        }
        return filteredTasks;
    }
}