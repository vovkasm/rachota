/*
 * AbstractTaskFilter.java
 *
 * Created on 05 August 2005, 23:10
 */

package org.cesilko.rachota.core.filters;

import java.util.Vector;
import org.cesilko.rachota.core.Translator;

/** Abstract task filter forwarding all provided tasks.
 * Can't be instantiated because of its empty implementation.
 * @author Jiri Kovalsky
 */
public abstract class AbstractTaskFilter {
    
    /** Localized name of filter. */
    private String filterName;
    /** One of content rules determining allowed content. */
    private int contentRule;
    /** Content that must comply with content rule. */
    private Object content;
    /** Rule requiring given content to be part of task property. */
    public static final int RULE_CONTAINS = 0;
    /** Rule requiring given content not to be part of task property. */
    public static final int RULE_CONTAINS_NOT = 1;
    /** Rule requiring given content NOT to be greater than task property. */
    public static final int RULE_MORE_THAN = 2;
    /** Rule requiring given content NOT to be smaller than task property. */
    public static final int RULE_LESS_THAN = 3;
    /** Rule requiring given content be same as task property. */
    public static final int RULE_EQUALS = 4;
    
    /** Default filter constructor.
     * @param filterName Localized name of filter.
     * @param contentRule One of content rules determining allowed content.
     * @param content Required content that must comply with content rule.
     */
    public AbstractTaskFilter(String filterName, int contentRule, Object content) {
        this.filterName = filterName;
        setContentRule(contentRule);
        setContent(content);
    }
    
    /** Sets content rule of filter.
     * @param contentRule New content rule.
     */
    public void setContentRule(int contentRule) {
        this.contentRule = contentRule;
    }
    
    /**
     * Sets required content of appropriate task property.
     * @param content New required content of appropriate task property.
     */
    public void setContent(Object content) {
        this.content = content;
    }
    
    /** Returns localized name of filter.
     * @return Localized name of filter.
     */
    public String getFilterName() {
        return filterName;
    }
    
    /** Returns content rule of filter as localized string.
     * @param contentRule Content rule of filter.
     * @return Content rule of filter as localized string.
     */
    public static String getContentRule(int contentRule) {
        return Translator.getTranslation("FILTER.CONTENT_RULE_" + contentRule);
    }
    
    /** Returns content rule of filter.
     * @return Content rule of filter.
     */
    public int getContentRule() {
        return contentRule;
    }
    
    /** Returns required content of appropriate task property.
     * @return Required content of appropriate task property.
     */
    public Object getContent() {
        return content;
    }
    
    /** Applies filter on given tasks and returns those tasks
     * that satisfied filter criterion.
     * @param tasks Vector of tasks to be filtered.
     * @return Filtered tasks.
     */
    public Vector filterTasks(Vector tasks) {
        return tasks;
    }
}