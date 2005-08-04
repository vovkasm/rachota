/*
 * Day.java
 *
 * Created on August 27, 2004, 8:36 PM
 */

package org.cesilko.rachota.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.gui.Tools;


/** Day with plan of tasks.
 *
 * @author  Jiri Kovalsky
 */
public class Day implements ChangeListener {
    
    /**
     * Set of all (including regular kind) tasks planned for day.
     */
    private Vector tasks;
    /**
     * Calendar date representing day.
     */
    private Date date;
    /**
     * Time when the very first task was started.
     */
    private Date startTime;
    /**
     * Time when the last task was worked on.
     */
    private Date finishTime;
    /**
     * Identification whether day was modified compared to its saved state.
     * Day gets modified when irregular task is added to its plan or any
     * task is removed or its start or finish times are changed.
     */
    private boolean modified;
    
    /** Creates a new instance of day.
     * @param tasks Vector of tasks planned for day.
     * @param date Identification of day.
     * @param startTime Time when first task was started.
     * @param finishTime Last time when some task was worked on.
     */
    public Day(Vector tasks, Date date, Date startTime, Date finishTime) {
        setTasks(tasks);
        setDate(date);
        setStartTime(startTime);
        setFinishTime(finishTime);
        modified = false;
    }
    
    /**
     * Sets tasks of day.
     * @param tasks Tasks of day.
     */
    public void setTasks(Vector tasks) {
        this.tasks = tasks;
        ChangeHandler.getDefault().fireEvent(this, ChangeListener.GENERIC_CHANGE);
    }
    
    /** Returns tasks planned for day.
     * @return Tasks planned for day.
     */
    public Vector getTasks() {
        return tasks;
    }
    
    /** Adds new task to plan of day.
     * @param task New task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
        if (!(task instanceof RegularTask)) modified = true;
        ChangeHandler.getDefault().fireEvent(this, TASK_CREATED);
    }
    
    /** Removes given task from plan of day.
     * @param task Task to be removed from plan.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        modified = true;
        ChangeHandler.getDefault().fireEvent(this, ChangeListener.GENERIC_CHANGE);
    }
    
    /** Removes all not started regular tasks from plan of day.
     */
    public void removeNotStartedRegularTasks() {
        Vector oldTasks = new Vector();
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if ((task instanceof RegularTask) && (task.getState() == Task.STATE_NEW))
                oldTasks.add(task);
        }
        iterator = oldTasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            removeTask(task);
        }
    }
    
    /** Returns task with given description or null if such does not exist.
     * @param description Description of task to be searched for.
     * @return Task with given description or null otherwise.
     **/
    public Task getTask(String description) {
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.getDescription().equals(description))
                return task;
        }
        return null;
    }
    
    /** Checks if there is some unfinished task in plan with higher priority.
     * @param priority Priority to be compared with.
     * @return True if plan contains at least one task with higher priority than given one, else false.
     */
    public boolean existsMorePriorityTask(int priority) {
        if (priority == Task.PRIORITY_HIGH) return false;
        int count = tasks.size();
        for (int i=0; i<count; i++) {
            Task task = (Task) tasks.get(i);
            if ((task.getState() != Task.STATE_DONE) & (task.getPriority() < priority)) return true;
        }
        return false;
    }
    
    /** Sets identification of day.
     * @param date Identification of day.
     */
    public void setDate(Date date) {
        this.date = date;
        ChangeHandler.getDefault().fireEvent(this, ChangeListener.GENERIC_CHANGE);
    }
    
    /** Returns identification of day.
     * @return Identification of day.
     */
    public Date getDate() {
        return date;
    }
    
    /** Sets start time of day.
     * @param startTime Start time of day.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        modified = true;
    }
    
    /** Returns start time of day.
     * @return Start time of day.
     */
    public Date getStartTime() {
        return startTime;
    }
    
    /** Sets finish time of day.
     * @param finishTime Finish time of day.
     */
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
        modified = true;
    }
    
    /** Returns finish time of day.
     * @return Finish time of day.
     */
    public Date getFinishTime() {
        return finishTime;
    }
    
    /** Returns if day was modified or not since it was created.
     * @return True if some task was added, removed or start or finish times were changed.
     */
    public boolean isModified() {
        return modified;
    }
    
    /**
     * Given object fired a change event of given type.
     * @param changeType Type of change event.
     * @param object Object that was changed.
     */
    public void eventFired(Object object, int changeType) {
        boolean today = Plan.getDefault().isToday(this);
        if (today & (changeType == ChangeListener.TASK_DURATION_CHANGED)) {
            if (startTime == null)
                startTime = new Date();
            setFinishTime(new Date());
        } else ChangeHandler.getDefault().fireEvent(this, ChangeListener.GENERIC_CHANGE);
    }
    
    /** Get total time spent on tasks.
     * @return Total time spent on tasks in milliseconds.
     */
    public long getTotalTime() {
        Iterator iterator = tasks.iterator();
        long totalTime = 0;
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (!task.privateTask()) totalTime = totalTime + task.getDuration();
        }
        return totalTime;
    }
    
    /** Sort tasks by given attribute and sorting order.
     * @param attribute Attribute used for sorting tasks e.g. DayTableModel.TASK_PRIORITY
     * @param ascendingOrder Sorting order. If true, tasks will be sorted in ascending order.
     */
    public void sortTasks(int attribute, boolean ascendingOrder) {
        Vector sortedTasks = new Vector();
        while (tasks.size() > 0) {
            Task selectedTask = (Task) tasks.get(0);
            Iterator iterator = tasks.iterator();
            while (iterator.hasNext()) {
                Task task = (Task) iterator.next();
                selectedTask = task.compare(selectedTask, attribute, ascendingOrder);
            }
            sortedTasks.add(selectedTask);
            tasks.remove(selectedTask);
        }
        tasks = sortedTasks;
    }
    
    /**
     * Write day to given writer.
     * @param writer File writer where day will be written.
     * @throws java.io.IOException Input/Output exception thrown whenever any problem while writing day occurs.
     */
    public void write(BufferedWriter writer) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int id = calendar.get(Calendar.DAY_OF_WEEK);
        writer.write("    <day id=\"" + id + "\" start=\"" + Tools.getTime(startTime) + "\" finish=\"" + Tools.getTime(finishTime) + "\">");
        writer.newLine();
        Boolean archiveNotStarted = (Boolean) Settings.getDefault().getSetting("archiveNotStarted");
        boolean pastDay = !(Plan.getDefault().isFuture(this) | Plan.getDefault().isToday(this));
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            boolean writeTask = true;
            if ((!archiveNotStarted.booleanValue()) && (pastDay))
                writeTask = task.getState() != Task.STATE_NEW;
            if ((task instanceof RegularTask) && (task.getDuration() == 0))
                writeTask = false;
            if (writeTask) task.write(writer);
        }
        writer.write("    </day>");
        writer.newLine();
    }
}
