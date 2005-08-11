/*
 * DescriptionFilter.java
 *
 * Created on 05 August 2005, 23:30
 */

package org.cesilko.rachota.core.filters;

import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Task filter allowing to filter tasks by description.
 *
 * @author Jiri Kovalsky
 */
public class DescriptionFilter extends AbstractTaskFilter {
    
    /** Creates new description filter. Filter accepts only RULE_CONTAINS and
     * RULE_CONTAINS_NOT content rules. Other rules will cause that tasks will not
     * be filtered at all.
     * @param contentRule One of two content rules determining allowed content in task description.
     * @param subString Text that must/mustn't be present in task description.
     */
    public DescriptionFilter(int contentRule, String subString) {
        super(Translator.getTranslation("TASK_DESCRIPTION"), contentRule, subString);
    }
    
    /** Applies description filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        Vector filteredTasks = (Vector) tasks.clone();
        Iterator iterator = filteredTasks.iterator();
        String subString = (String) getContent();
        int contentRule = getContentRule();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            boolean containsSubString = task.getDescription().indexOf(subString) != -1;
            switch (contentRule) {
                case RULE_CONTAINS:
                    if (!containsSubString) filteredTasks.remove(task);
                    break;
                case RULE_CONTAINS_NOT:
                    if (containsSubString) filteredTasks.remove(task);
                    break;
                default:
                    System.out.println("Error: Task description can't be filtered by content rule: " + getContentRule(contentRule));
            }
        }
        return filteredTasks;
    }
}