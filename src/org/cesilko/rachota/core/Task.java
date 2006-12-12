/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * The Original Software is Rachota.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on July 30, 2004  8:41 PM
 * Task.java
 */

package org.cesilko.rachota.core;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import org.cesilko.rachota.gui.DayTableModel;
import org.cesilko.rachota.gui.Tools;

/** Single task planned for particular day.
 *
 * @author  Jiri Kovalsky
 */
public class Task implements ClockListener {
    
    /** Short description of task. */
    private String description;
    /** Categories of task. */
    private String keyword;
    /** Long description of task. */
    private String notes;
    /** Priority of task i.e. either High or Medium or Low. */
    private int priority;
    /** State of task i.e. either New or Started or Done. */
    private int state;
    /** Total time in ms that was already spent on task. */
    private long duration;
    /** Time when system should warn about task. */
    private Date notificationTime;
    /** Whether system should switch to task automatically. */
    private boolean automaticStart;
    /** Time checkpoint used for exact calculation of time to be added to duration. */
    private Date timeStamp;
    /** Whether this task is private i.e. its duration should not be counted to whole day duration. */
    private boolean privateTask;
    /** Class containing all registered listeners interested in task. */
    private PropertyChangeSupport propertyChangeSupport;
    /** Time in miliseconds from previous saving of diary and settings files. */
    private long lastSaving;
    
    /** State representing not started task. */
    public static int STATE_NEW = 0;
    /** State representing already started task. */
    public static int STATE_STARTED = 1;
    /** State representing already finished task. */
    public static int STATE_DONE = 2;
    
    /** High level of task priority. */
    public static int PRIORITY_HIGH = 0;
    /** Medium level of task priority. */
    public static int PRIORITY_MEDIUM = 1;
    /** Low level of task priority. */
    public static int PRIORITY_LOW = 2;
    
    /**
     * Creates a new instance of Task
     * @param description Description of task.
     * @param keyword Arbitrary text for grouping tasks etc.
     * @param notes Any kind of additional information about task.
     * @param priority Priority of task.
     * @param state State of progress of task.
     * @param duration Time in ms that was already spent on task.
     * @param notificationTime Time when system should warn about task.
     * @param automaticStart Should system switch to task automatically ?
     * @param privateTask Is this task private ?
     */
    public Task(String description, String keyword, String notes, int priority, int state, long duration, Date notificationTime, boolean automaticStart, boolean privateTask) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        setDescription(description);
        setKeyword(keyword);
        setNotes(notes);
        setPriority(priority);
        setState(state);
        setDuration(duration);
        setNotificationTime(notificationTime);
        setAutomaticStart(automaticStart);
        setPrivateTask(privateTask);
        timeStamp = null;
        lastSaving = 0;
    }
    
    /** Sets description of this task.
     * @param description Description of task.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /** Returns description of this task.
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }
    
    /** Sets keyword of this task.
     * @param keyword Keyword of task.
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "keyword", null, keyword));
    }
    
    /** Returns keyword of this task.
     * @return Keyword of this task.
     */
    public String getKeyword() {
        return keyword;
    }
    
    /**
     * Sets notes of this task.
     * @param notes Notes of task.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /** Returns notes of this task.
     * @return Notes of this task.
     */
    public String getNotes() {
        return notes;
    }
    
    /** Sets priority of this task.
     * @param priority Priority of task.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /** Returns priority of this task.
     * @return Priority of this task as number.
     */
    public int getPriority() {
        return priority;
    }
    
    /** Returns priority of this task.
     * @param priority Priority of task as number.
     * @return Priority of this task as text.
     */
    public static String getPriority(int priority) {
        return Translator.getTranslation("TASK_PRIORITY_" + priority);
    }
    
    /** Sets state of this task.
     * @param state State of task.
     */
    public void setState(int state) {
        this.state = state;
    }
    
    /** Returns state of this task.
     * @return State of this task as number.
     */
    public int getState() {
        return state;
    }
    
    /** Returns state of this task.
     * @param state State of task as number.
     * @return State of this task as text.
     */
    public static String getState(int state) {
        return Translator.getTranslation("TASK_STATE_" + state);
    }
    
    /** Sets time in ms that was already spent on task.
     * @param duration Time in ms that was already spent on task.
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    /** Adds time in ms that was additionally spent on task.
     * @param duration Time in ms that was additionally spent on task.
     */
    public void addDuration(long duration) {
        this.duration = this.duration + duration;
    }
    
    /** Returns time in ms that was already spent on task.
     * @return Time in ms that was already spent on task.
     */
    public long getDuration() {
        return duration;
    }
    
    /** Sets notification time when system should warn about task.
     * @param notificationTime Time when system should warn about task.
     */
    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "notificationTime", null, notificationTime));
    }
    
    /** Returns time when system should warn about task or null if no warning is required.
     * @return Time when system should warn about task or null if no warning is required.
     */
    public Date getNotificationTime() {
        return notificationTime;
    }
    
    /** Sets whether system should switch to task automatically.
     * @param automaticStart Should system switch to task automatically ?
     */
    public void setAutomaticStart(boolean automaticStart) {
        this.automaticStart = automaticStart;
    }
    
    /** Returns whether system should switch to task automatically.
     * @return Should system switch to task automatically ?
     */
    public boolean automaticStart() {
        return automaticStart;
    }
    
    /**
     * Sets whether this task is private or not.
     * @param privateTask Is this private task or not ?
     */
    public void setPrivateTask(boolean privateTask) {
        this.privateTask = privateTask;
    }
    
    /** Returns whether this task is private.
     * @return True if this task is private and false otherwise.
     */
    public boolean privateTask() {
        return privateTask;
    }
    
    /** Adds new listener to set of objects interested in this task.
     * @param listener Object interested in this task.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /** Adds new listener to set of objects interested in this task.
     * @param listener Object interested in this task.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Writes task to given writer.
     * @param stream Print stream where task will be written.
     * @throws java.io.IOException Input/output exception thrown when some error during writing basic task information occurs.
     */
    public void write(PrintStream stream) throws IOException {
        stream.print("        <task state=\"" + state + "\"");
        stream.println(" duration=\"" + Tools.getTime(duration) + "\">");
        stream.println("            <priority>" + priority + "</priority>");
        String data = Tools.replaceAll(description, "&", "&amp;");
        data = Tools.replaceAll(data, "<", "&lt;");
        data = Tools.replaceAll(data, ">", "&gt;");
        data = Tools.replaceAll(data, "\"", "&quot;");
        stream.println("            <description>" + data + "</description>");
        if ((keyword != null) && (!keyword.equals(""))) {
            data = Tools.replaceAll(keyword, "&", "&amp;");
            data = Tools.replaceAll(data, "<", "&lt;");
            data = Tools.replaceAll(data, ">", "&gt;");
            data = Tools.replaceAll(data, "\"", "&quot;");
            stream.println("            <keyword>" + data + "</keyword>");
        }
        if ((notes != null) && (!notes.equals(""))) {
            data = Tools.replaceAll(notes, "&", "&amp;");
            data = Tools.replaceAll(data, "<", "&lt;");
            data = Tools.replaceAll(data, ">", "&gt;");
            data = Tools.replaceAll(data, "\"", "&quot;");
            stream.println("            <notes>" + data + "</notes>");
        }
        if (notificationTime != null) {
            stream.println("            <notification time=\"" + Tools.getTime(notificationTime) + "\" switch=\"" + automaticStart + "\"/>");
        }
        if (privateTask) {
            stream.println("            <private/>");
        }
        writeRepetition(stream);
        stream.println("        </task>");
    }
    
    /**
     * Writes information about repetition of task.
     * @param stream Print stream where task will be written.
     * @throws java.io.IOException Input/output exception thrown when some error during writing repetition information occurs.
     */
    public void writeRepetition(PrintStream stream) throws IOException {
        // Does nothing by default, only instances of RegularTask objects save the information.
    }
    
    /** Method called when user starts to work on task.
     */
    public void startWork() {
        Clock.getDefault().addListener(this);
        timeStamp = new Date();
        setState(STATE_STARTED);
    }
    
    /** Method called when user temporarily stops to work on task.
     */
    public void suspendWork() {
        Clock.getDefault().removeListener(this);
        Date now = new Date();
        addDuration(now.getTime() - timeStamp.getTime());
        timeStamp = null;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "duration", null, new Long(duration)));
    }
    
    /** Method called when user finished to work on task.
     */
    public void workDone() {
        if (timeStamp != null) {
            Clock.getDefault().removeListener(this);
            Date now = new Date();
            addDuration(now.getTime() - timeStamp.getTime());
            timeStamp = null;
        }
        setState(STATE_DONE);
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "duration", null, new Long(duration)));
    }
    
    /** Returns whether task is being worked on or not.
     * @return True if task is currently being worked on. False otherwise.
     */
    public boolean isRunning() {
        return (timeStamp != null);
    }
    
    /** Method called by clock after one tick. Task has to increase its working time.
     */
    public void tick() {
        Date now = new Date();
        long timeSinceLastTick = now.getTime() - timeStamp.getTime();
        addDuration(timeSinceLastTick);
        if (!isIdleTask()) Settings.getDefault().setSetting("runningTask", getDescription() + "[" + timeStamp.getTime() + "]");
        timeStamp = new Date();
        lastSaving = lastSaving + timeSinceLastTick;
        int savingPeriod = Integer.parseInt((String) Settings.getDefault().getSetting("savingPeriod")) * 1000;
        if (lastSaving > savingPeriod) {
            Plan.savePlan();
            Settings.saveSettings();
            lastSaving = 0;
        }
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "duration", null, new Long(duration)));
    }
    
    /**
     * Compare attributes with given task and return the one which is greater/smaller according to sorting order.
     * @param task Task to be compared with.
     * @param attribute Property of task to be compared e.g. DayTableModel.TASK_PRIORITY.
     * @param ascendingOrder If true smaller task will be returned.
     * @return True or false depending on actual and given task resulting from their comparison
     * based on given attribute and sorting order.
     */
    public Task compare(Task task, int attribute, boolean ascendingOrder) {
        switch (attribute) {
            case DayTableModel.TASK_PRIORITY:
                if (ascendingOrder) {
                    if (task.getPriority() < priority)
                        task = this;
                } else
                    if (task.getPriority() > priority)
                        task = this;
                break;
            case DayTableModel.TASK_DESCRIPTION:
                if (ascendingOrder) {
                    if (task.getDescription().toLowerCase().compareTo(description.toLowerCase()) > 0)
                        task = this;
                } else
                    if (task.getDescription().toLowerCase().compareTo(description.toLowerCase()) < 0)
                        task = this;
                break;
            case DayTableModel.TASK_DURATION:
                if (ascendingOrder) {
                    if (task.getDuration() > duration)
                        task = this;
                } else
                    if (task.getDuration() < duration)
                        task = this;
                break;
            case DayTableModel.TASK_STATE:
                if (ascendingOrder) {
                    if (task.getState() > state)
                        task = this;
                } else
                    if (task.getState() < state)
                        task = this;
                break;
            case DayTableModel.TASK_REGULAR:
                boolean taskRegular = task instanceof RegularTask;
                boolean thisRegular = this instanceof RegularTask;
                if (ascendingOrder) {
                    if (thisRegular & !taskRegular)
                        task = this;
                } else
                    if (taskRegular & !thisRegular)
                        task = this;
                break;
            case DayTableModel.TASK_KEYWORD:
                if (ascendingOrder) {
                    if (task.getKeyword().toLowerCase().compareTo(keyword.toLowerCase()) > 0)
                        task = this;
                } else
                    if (task.getKeyword().toLowerCase().compareTo(keyword.toLowerCase()) < 0)
                        task = this;
                break;
            case DayTableModel.TASK_PRIVATE:
                boolean taskPrivate = task.privateTask();
                boolean thisPrivate = privateTask();
                if (ascendingOrder) {
                    if (thisPrivate & !taskPrivate)
                        task = this;
                } else
                    if (taskPrivate & !thisPrivate)
                        task = this;
                break;
        }
        return task;
    }
    
    /** Returns clone of itself with time set to 0 and state set to STATE_NEW.
     * @return Task similar to this one except time and state.
     */
    public Task cloneTask() {
        return new Task(description, keyword, notes, priority, STATE_NEW, 0, notificationTime, automaticStart, privateTask);
    }
    
    /** Return textual representation of task.
     * @return Description of task.
     */
    public String toString() {
        return getDescription();
    }

    /** Returns false meaning that the task does not measure idle time. */
    public boolean isIdleTask() {
        return false;
    }
}
