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
 * Created on August 27, 2004  7:46 PM
 * RegularTask.java
 */

package org.cesilko.rachota.core;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;

/** Task that repeats every day or once a week.
 *
 * @author  Jiri Kovalsky
 */
public class RegularTask extends Task {
    
    /** Type of repetition this regular task represents. */
    private int frequency;
    /** Regular task that repeats every day. */
    public static int FREQUENCY_DAILY = 0;
    /** Regular task that repeats every Monday. */
    public static int FREQUENCY_MONDAY = 1;
    /** Regular task that repeats every Tuesday. */
    public static int FREQUENCY_TUESDAY = 2;
    /** Regular task that repeats every Wednesday. */
    public static int FREQUENCY_WEDNESDAY = 3;
    /** Regular task that repeats every Thursday. */
    public static int FREQUENCY_THURSDAY = 4;
    /** Regular task that repeats every Friday. */
    public static int FREQUENCY_FRIDAY = 5;
    /** Regular task that repeats every Saturday. */
    public static int FREQUENCY_SATURDAY = 6;
    /** Regular task that repeats every Sunday. */
    public static int FREQUENCY_SUNDAY = 7;
    /** Regular task that repeats every working day. */
    public static int FREQUENCY_WORKDAY = 8;
    /** Regular task that repeats every weekend day. */
    public static int FREQUENCY_WEEKEND = 9;
    
    /** Creates a new instance of regular task.
     * @param description Description of task.
     * @param keyword Arbitrary text for grouping tasks etc.
     * @param notes Notes of task.
     * @param priority Priority of task.
     * @param state State of progress of task.
     * @param duration Time in ms that was already spent on task.
     * @param notificationTime Time when system should warn about task.
     * @param automaticStart Should system switch to task automatically ?
     * @param privateTask Is this task private ?
     * @param frequency Frequency of task's repetitions.
     */
    public RegularTask(String description, String keyword, String notes, int priority, int state, long duration, Date notificationTime, boolean automaticStart, boolean privateTask, int frequency) {
        super(description, keyword, notes, priority, state, duration, notificationTime, automaticStart,  privateTask);
        setFrequency(frequency);
    }
    
    /** Sets how often task is repeated.
     * @param frequency Frequency of task's repetitions.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    
    /** Returns how often task is repeated.
     * @return Frequency of task's repetitions.
     */
    public int getFrequency() {
        return frequency;
    }
    
    /** Returns how often task is repeated.
     * @param frequency Frequency of task's repetitions as number.
     * @return Frequency of task's repetitions as text.
     */
    public static String getFrequency(int frequency) {
        return Translator.getTranslation("TASK_FREQUENCY_" + frequency);
    }
    
    /** Writes information about repetition of task.
     * @param stream Print stream where repetition info will be written.
     * @throws java.io.IOException Input/output exception thrown when some error during writing repetition information occurs.
     */
    public void writeRepetition(PrintStream stream) throws IOException {
        stream.println("            <repetition frequency=\"" + frequency + "\"/>");
    }
    
    /** Verifies if task is planned for given day.
     * @param day Day to be checked.
     * @return True if task suits for given day, false otherwise.
     */
    public boolean isPlannedFor(Day day) {
        if (frequency == FREQUENCY_DAILY) return true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) dayOfWeek = 7;
        if (frequency <= FREQUENCY_SUNDAY) return frequency == dayOfWeek;
        boolean weekend = dayOfWeek == FREQUENCY_SUNDAY | dayOfWeek == FREQUENCY_SATURDAY;
        if (frequency == FREQUENCY_WEEKEND) return weekend;
        return !weekend;
    }
    
    /** Returns clone of itself with time set to 0 and state set to STATE_NEW.
     * @return Regular task similar to this one except time and state.
     */
    public Task cloneTask() {
        return new RegularTask(getDescription(), getKeyword(), getNotes(), getPriority(), STATE_NEW, 0, getNotificationTime(), automaticStart(), privateTask(), frequency);
    }
}
