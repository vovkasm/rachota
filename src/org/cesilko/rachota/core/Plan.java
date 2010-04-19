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
 * Created on August 27, 2004  9:02 PM
 * Plan.java
 */

package org.cesilko.rachota.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.cesilko.rachota.gui.AnalyticsView;
import org.cesilko.rachota.gui.StartupWindow;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Plan containing all days that have some tasks planned. Plan also contains
 * all regular tasks.
 * @author Jiri Kovalsky
 */
public class Plan {
    
    /** All days planned for the future or when any worked happened in the past. */
    private Hashtable days;
    /** Set of all regular tasks planned for the future. */
    private Vector regularTasks;
    /** The only instance of Plan object in the system. */
    private static Plan plan;
    
    /** Creates a new instance of plan */
    private Plan() {
        days = new Hashtable();
        regularTasks = new Vector();
    }
    
    /** Returns the only available instance of plan.
     * @return The only instance of Plan object in system.
     */
    public static Plan getDefault() {
        if (plan == null) plan = new Plan();
        return plan;
    }
    
    /** Adds new day to plan.
     * @param day New day to be added to plan.
     */
    public void addDay(Day day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        String ID = getDayID(calendar);
        days.put(ID, day);
    }
    
    /** Returns day identified by given date.
     * @param date Date that will be used to find the day.
     * @return Day with given date.
     **/
    public Day getDay(Date date) {
        return getDay(date, 0);
    }
    
    /** Returns one day before given day.
     * @param day Day whose predecessor should be found.
     * @return Day before given day.
     **/
    public Day getDayBefore(Day day) {
        return getDay(day.getDate(), -1);
    }
    
    /** Returns one day after given day.
     * @param day Day whose ancessor should be found.
     * @return Day after given day.
     **/
    public Day getDayAfter(Day day) {
        return getDay(day.getDate(), 1);
    }

    /** Returns iterator of days according to given period scale.
     * @param scale Period whose days to return. Either week or all time.
     * @return Iterator of days from previous week or all days.
     */
    public Iterator getDays(int scale) {
        Vector requiredDays = new Vector();
        if (scale == AnalyticsView.SCALE_PAST_WEEK) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            Day day = getDay(calendar.getTime());
            for (int i=0; i<7; i++) {
                requiredDays.add(day);
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                day = getDay(calendar.getTime());
            }
            return requiredDays.iterator();
        }
        if (scale == AnalyticsView.SCALE_PAST_MONTH) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Day day = getDay(calendar.getTime());
            for (int i=0; i<numberOfDays; i++) {
                requiredDays.add(day);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                day = getDay(calendar.getTime());
            }
            return requiredDays.iterator();
        }
        return days.values().iterator();
    }
    
    /** Returns day by given date and offset.
     * @param date Date whose day should be returned.
     * @param offset Relative difference in days from specified date e.g. 0 if date exactly should be returned.
     * @return Day based on date and offset.
     **/
    private Day getDay(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        String ID = getDayID(calendar);
        Day day = (Day) days.get(ID);
        if (day == null) day = new Day(new Vector(), calendar.getTime(), null, null);
        return day;
    }
    
    /** Returns identification of day set in given calendar.
     * @param calendar Calendar preset to some day.
     * @return Identification of day e.g. "2005_2_17"
     **/
    public String getDayID(Calendar calendar) {
        String ID = "" + calendar.get(Calendar.YEAR);
        ID = ID + "_" + calendar.get(Calendar.MONTH);
        ID = ID + "_" + calendar.get(Calendar.DATE);
        return ID;
    }
    
    /** Checks if given day represents today.
     * @param day Day that should be checked.
     * @return True if given day represents today otherwise false.
     **/
    public boolean isToday(Day day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getDate());
        String thisID = getDayID(calendar);
        calendar.setTime(new Date());
        String todayID = getDayID(calendar);
        return todayID.equals(thisID);
    }
    
    /** Checks if given day will be after today.
     * @param day Day that should be checked.
     * @return True if given day represents some day after today otherwise false.
     **/
    public boolean isFuture(Day day) {
        return day.getDate().after(new Date());
    }
    
    /** Adds new regular task to plan.
     * @param regularTask New regular task to be added to plan.
     */
    public void addRegularTask(RegularTask regularTask) {
        regularTasks.add(regularTask);
    }
    
    /** Removes existing regular task from plan.
     * @param regularTask Regular task to be removed from plan.
     */
    public void removeRegularTask(RegularTask regularTask) {
        regularTasks.remove(regularTask);
    }
    
    /** Returns vector of all regular tasks.
     * @return Vector of all regular tasks.
     */
    public Vector getRegularTasks() {
        return regularTasks;
    }
    
    /** Creates a backup copy of current diary. */
    public static void createBackup() {
        org.cesilko.rachota.core.Plan plan = org.cesilko.rachota.core.Plan.getDefault();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int thisWeek = calendar.get(java.util.Calendar.WEEK_OF_YEAR);
        plan.saveWeek(thisWeek, true);
        File backupFile = new File((String) Settings.getDefault().getSetting("userDir") + File.separator + "backup_diary.xml");
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.parse(backupFile);
        } catch (Exception e) {
            System.out.println("Error: Can't create backup_diary.xml file. Another try in " + Settings.getDefault().getSetting("backupAge") + " minute(s).");
            e.printStackTrace();
            backupFile.delete();
        }
        
    }
    
    /** Saves all days to XML files.
     * @return True if plan was saved successfully, false otherwise
     */
    public static boolean savePlan() {
        Plan plan = Plan.getDefault();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int thisWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int weekToSave = plan.getNextWeekToSave(thisWeek - 2);
        boolean planSaved = true;
        while (weekToSave != -1) {
            planSaved = planSaved & plan.saveWeek(weekToSave, false);
            weekToSave = plan.getNextWeekToSave(weekToSave);
        }
        return planSaved;
    }
    
    /** Returns index of next week to be saved after given week.
     * @param savedWeek Index of week that has already been saved.
     * @return Index of next week to save or -1 if there is no more day in plan after given week.
     */
    private int getNextWeekToSave(int savedWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, savedWeek);
        Date savedDate = calendar.getTime();
        int maxWeek = calendar.getMaximum(Calendar.WEEK_OF_YEAR);
        Iterator iterator = days.values().iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            Date date = day.getDate();
            if (date.after(savedDate)) {
                calendar.setTime(date);
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                if ((week > savedWeek) & (week < maxWeek))
                    maxWeek = week;
            }
        }
        if (maxWeek == calendar.getMaximum(Calendar.WEEK_OF_YEAR)) maxWeek = -1;
        return maxWeek;
    }
    
    /** Saves week with given index of year.
     * @param week Index of year to be saved.
     * @param isBackup If true backup will be created otherwise common diary file.
     * @return True if week was successfully saved, false otherwise.
     */
    private boolean saveWeek(int week, boolean isBackup) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        int year = calendar.get(Calendar.YEAR);
        String location = (String) Settings.getDefault().getSetting("userDir");
        if (isBackup) location = location + File.separator + "backup_diary.xml";
        else location = location + File.separator + "diary_" + year + "_" + week + ".xml";
        try {
            String encoding = (String) Settings.getDefault().getSetting("systemEncoding");
            PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(location)), true, encoding);
            stream.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            stream.println("<!--");
            stream.println("    Rachota 2.1 diary file - editing not recommended");
            stream.println("    " + new Date());
            stream.println("-->");
            stream.println("<!DOCTYPE week SYSTEM \"diary.dtd\">");
            stream.println();
            stream.println("<week year=\"" + year + "\" id=\"" + week + "\">");
            calendar.set(Calendar.WEEK_OF_YEAR, week);
            for (int i=0; i<7; i++) {
                calendar.set(Calendar.DAY_OF_WEEK, i);
                Day day = (Day) days.get(getDayID(calendar));
                if (day != null) day.write(stream);
            }
            stream.println("</week>");
            stream.flush();
            stream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
        File diaryFile = new File(location);
        return (diaryFile.length() != 0);
    }
    
    /** Loads all planned days and history.
     * @throws java.lang.Exception Exception thrown whenever any problem while loading plan occurs.
     */
    public static void loadPlan() throws Exception {
        String userDir = (String) Settings.getDefault().getSetting("userDir");
        File[] diaries = new File(userDir).listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                return (name.startsWith("diary_") && (name.endsWith(".xml")));
            }
        });
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        DiaryScanner.createDTD();
        if (diaries == null) return;
        StartupWindow startupWindow = StartupWindow.getInstance();
        startupWindow.setNumberOfDiaries(diaries.length);
        for (int i=0; i<diaries.length; i++) {
            try {
                DiaryScanner scanner = scanner = new DiaryScanner(builder.parse(diaries[i]));
                startupWindow.setProgressMessage(diaries[i].getName());
                scanner.loadDocument();
                startupWindow.setProgress(i);
            } catch (SAXParseException e) {
                e.printStackTrace();
                File backupFile = new File(userDir + File.separator + "backup_diary.xml");
                int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (backupFile.exists() & diaries[i].getName().equals("diary_" + currentYear + "_" + currentWeek + ".xml")) {
                    String message = Translator.getTranslation("ERROR.DIARY_CORRUPTED", new String[] {diaries[i].getName()});
                    String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO"), Translator.getTranslation("DATEDIALOG.BT_CANCEL")};
                    int decision = JOptionPane.showOptionDialog(null, message, Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, buttons[0]);
                    if (decision == JOptionPane.CANCEL_OPTION) System.exit(-1);
                    if (decision == JOptionPane.YES_OPTION) {
                        DiaryScanner scanner = scanner = new DiaryScanner(builder.parse(backupFile));
                        startupWindow.setProgressMessage("backup_diary.xml");
                        scanner.loadDocument();
                        startupWindow.setProgress(diaries.length-1);
                    }
                } else JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.READ_ERROR", new String[] {diaries[i].getName()}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Saves all regular tasks.
     */
    public static void saveRegularTasks() {
        String location = (String) Settings.getDefault().getSetting("userDir");
        location = location + File.separator + "regular_tasks.xml";
        try {
            String encoding = (String) Settings.getDefault().getSetting("systemEncoding");
            PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(location)), true, encoding);
            stream.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            stream.println("<!--");
            stream.println("    Rachota 2.1 regular tasks - editing not recommended");
            stream.println("    " + new Date());
            stream.println("-->");
            stream.println("<!DOCTYPE week SYSTEM \"regular_tasks.dtd\">");
            stream.println();
            stream.println("    <regular_tasks>");
            Iterator iterator = Plan.getDefault().getRegularTasks().iterator();
            while (iterator.hasNext()) {
                RegularTask regularTask = (RegularTask) iterator.next();
                regularTask.write(stream);
            }
            stream.println("    </regular_tasks>");
            stream.flush();
            stream.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** Loads plan of regular tasks.
     * @throws java.lang.Exception Exception thrown whenever any problem while loading regular tasks occurs.
     */
    public static void loadRegularTasks() throws Exception {
        String userDir = (String) Settings.getDefault().getSetting("userDir");
        String fileName = userDir + File.separator + "regular_tasks.xml";
        File regularTasksFile = new File(fileName);
        if (!regularTasksFile.exists()) return;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        RegularTasksScanner.createDTD();
        RegularTasksScanner scanner;
        try{
            scanner = new RegularTasksScanner(builder.parse(regularTasksFile));
        }catch(SAXException ex){
            java.util.logging.Logger.getLogger(Plan.class.getName()).log(Level.WARNING, "Error occured while parsing existing regular task XML file.", ex);
            return;
        }
        scanner.loadDocument();
    }
    
    /** Copies all unfinished tasks from previous working day to today.
     */
    public void copyUnfinishedTasks() {
        Day today = getDay(new Date());
        if (!existsDayBefore(today)) return;
        Day previousWorkingDay = getDayBefore(today);
        while (previousWorkingDay.getTotalTime(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue()) == 0) {
            if (!existsDayBefore(previousWorkingDay)) return;
            previousWorkingDay = getDayBefore(previousWorkingDay);
        }
        Iterator iterator = previousWorkingDay.getTasks().iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();
            if (task.getState() != Task.STATE_DONE)
                if (today.getTask(task.getDescription()) == null)
                    today.addTask(task.cloneTask());
        }
        if (today.isModified()) addDay(today);
    }
    
    /** Adds regular tasks to given day.
     * @param day Day to be extended for regular tasks.
     */
    public void addRegularTasks(Day day) {
        boolean pastDay = !(Plan.getDefault().isFuture(day) | Plan.getDefault().isToday(day));
        if (pastDay) return;
        day.removeNotStartedRegularTasks();
        Iterator iterator = regularTasks.iterator();
        while (iterator.hasNext()) {
            RegularTask regularTask = (RegularTask) iterator.next();
            if (regularTask.isPlannedFor(day) && (day.getTask(regularTask.getDescription()) == null)) {
                RegularTask clone = (RegularTask) regularTask.cloneTask();
                day.addTask(clone);
            }
        }
    }
    
    /** Finds out if there is any day in plan before specified day.
     * @param day Day whose predecessor existence should be verified.
     * @return True if any day before given day exists in plan. False
     * otherwise.
     */
    private boolean existsDayBefore(Day day) {
        Iterator iterator = days.values().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Day indexedDay = (Day) iterator.next();
            if (day.equals(indexedDay)) break;
            if (indexedDay.getDate().before(day.getDate())) index++;
        }
        return (index != 0);
    }
    
    /** Returns all categories that were used to date.
     * @return Vector of categories that were used to date.
     */
    public Vector getCategories() {
        Vector allCategories = new Vector();
        Iterator dayIterator = days.values().iterator();
        while (dayIterator.hasNext()) {
            Day day = (Day) dayIterator.next();
            Vector tasks = day.getTasks();
            Iterator taskIterator = tasks.iterator();
            while (taskIterator.hasNext()) {
                Task task = (Task) taskIterator.next();
                String keywords = task.getKeyword();
                if (keywords != null) {
                    StringTokenizer tokenizer = new StringTokenizer(keywords, " ");
                    while(tokenizer.hasMoreElements()) {
                        String category = (String) tokenizer.nextElement();
                        if (!allCategories.contains(category)) allCategories.add(category);
                    }
                }
            }
        }
        String[] defaults = new String[] {
            Translator.getTranslation("CATEGORY.MEETING"),
            Translator.getTranslation("CATEGORY.DISCUSSION"),
            Translator.getTranslation("CATEGORY.EMAIL"),
            Translator.getTranslation("CATEGORY.INTERNET")
        };
        for (int i = 0; i < defaults.length; i++) {
            if (!allCategories.contains(defaults[i]))
                allCategories.add(defaults[i]);
        }
        return allCategories;
    }
}