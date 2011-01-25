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
 * Created on August 27, 2004  8:36 PM
 * Day.java
 */

package org.cesilko.rachota.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.cesilko.rachota.gui.Tools;


/** Day with plan of tasks.
 *
 * @author  Jiri Kovalsky
 */
public class Day implements PropertyChangeListener {
    
    /** Set of all (including regular kind) tasks planned for day. */
    private Vector tasks;
    /** Calendar date representing day. */
    private Date date;
    /** Time when the very first task was started. */
    private Date startTime;
    /** Time when the last task was worked on. */
    private Date finishTime;
    /** Identification whether day was modified compared to its saved state.
     * Day gets modified when irregular task is added to its plan or any
     * task is removed or its start or finish times are changed.
     */
    private boolean modified;
    /** Class containing all registered listeners interested in day. */
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Creates a new instance of day.
     * @param tasks Vector of tasks planned for day.
     * @param date Identification of day.
     * @param startTime Time when first task was started.
     * @param finishTime Last time when some task was worked on.
     */
    public Day(Vector tasks, Date date, Date startTime, Date finishTime) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        setTasks(tasks);
        setDate(date);
        setStartTime(startTime);
        setFinishTime(finishTime);
        modified = false;
    }
    
    /** Sets tasks of day.
     * @param tasks Tasks of day.
     */
    public void setTasks(Vector tasks) {
        this.tasks = new Vector();
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            addTask(task);
        }
        if (getIdleTask() == null) addTask(new IdleTask());
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "tasks", null, tasks));
    }
    
    /** Returns tasks planned for day.
     * @return Tasks planned for day.
     */
    public Vector getTasks() {
        return tasks;
    }
    
    /** Adds new listener to set of objects interested in this day.
     * @param listener Object interested in this day.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /** Adds new listener to set of objects interested in this day.
     * @param listener Object interested in this day.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /** Adds new task to plan of day.
     * @param task New task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
        if (!(task instanceof RegularTask)) modified = true;
        task.addPropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "tasks", null, tasks));
    }
    
    /** Removes given task from plan of day.
     * @param task Task to be removed from plan.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        modified = true;
        task.removePropertyChangeListener(this);
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "tasks", null, tasks));
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
    
    /** Returns idle task of the day. */
    public Task getIdleTask() {
        Iterator iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.isIdleTask())
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
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "date", null, tasks));
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
        boolean recordStartStopTimeOnIdle = ((Boolean) Settings.getDefault().getSetting("recordStartStopTimeOnIdle")).booleanValue();
        if (!recordStartStopTimeOnIdle) // User does not want to record start time when not working
            if (getTotalTime(true) == 0) return;
        this.startTime = startTime;
        modified = true;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "startTime", null, null));
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
        boolean recordStartStopTimeOnIdle = ((Boolean) Settings.getDefault().getSetting("recordStartStopTimeOnIdle")).booleanValue();
        if (!recordStartStopTimeOnIdle) // User does not want to record finish time if not working
            if (getIdleTask().isRunning()) return;
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
    
    /** Get total time spent on tasks without idle time.
     * @param includePrivateTasks If true, also time spent on private tasks will be calculated.
     * @return Total time spent on tasks in milliseconds.
     */
    public long getTotalTime(boolean includePrivateTasks) {
        Iterator iterator = tasks.iterator();
        long totalTime = 0;
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.isIdleTask()) continue;
            if (!task.privateTask() || includePrivateTasks) totalTime = totalTime + task.getDuration();
        }
        return totalTime;
    }

    /** Get total time spent on tasks without idle time. This will use the preference
     * stored in the settings determine whether or not to count private tasks.
     * @return Total time spent on tasks in milliseconds.
     * @see #getTotalTime(boolean)
     * @see Settings#getCountPrivateTasks()
     */
    public long getTotalTime(){
        return getTotalTime(Settings.getDefault().getCountPrivateTasks());
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
    
    /** Write day to given writer.
     * @param stream Print stream where day will be written.
     * @throws java.io.IOException Input/Output exception thrown whenever any problem while writing day occurs.
     */
    public void write(PrintStream stream) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int id = calendar.get(Calendar.DAY_OF_WEEK);
        stream.println("    <day id=\"" + id + "\" start=\"" + Tools.getTime(startTime) + "\" finish=\"" + Tools.getTime(finishTime) + "\">");
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
            if (writeTask) task.write(stream);
        }
        stream.println("    </day>");
    }
    
    /** Returns textual representation of day's date e.g. 11/25 or 25.11.
     * @return Tex representation of days' date.
     */
    public String toString() {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        String dayFormat = Translator.getTranslation("FORMAT.DAY_SHORT");
        sdf.applyPattern(dayFormat);
        return sdf.format(date);
    }

    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        boolean today = Plan.getDefault().isToday(this);
        if (today & (evt.getPropertyName().equals("duration"))) {
            modified = true;
            Plan.getDefault().addDay(this);
            if (getStartTime() == null)
                setStartTime(new Date());
            setFinishTime(new Date());
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "duration", null, null));
        } else propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "generic", null, tasks));
    }

    /** Returns the remaining work time for this day.
     * @return the remaining work time for this day in milli seconds.
     */
    public long getRemainingWorkingTime() {
        Double workingHoursInMinutes = Settings.getDefault().getWorkingHours() * 60;
        long remaining = TimeUnit.MILLISECONDS.convert(workingHoursInMinutes.longValue(), TimeUnit.MINUTES);
        remaining -= getTotalTime();
        return remaining >= 0 ? remaining : 0;
    }
}
