/*
 * Plan.java
 *
 * Created on August 27, 2004, 9:02 PM
 */

package org.cesilko.rachota.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
            planSaved = planSaved & plan.saveWeek(weekToSave);
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
     * @return True if week was successfully saved, false otherwise.
     */
    private boolean saveWeek(int week) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String location = (String) Settings.getDefault().getSetting("userDir");
        location = location + File.separator + "diary_" + year + "_" + week + ".xml";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<!--");
            writer.newLine();
            writer.write("    Rachota 2.0 diary file - editing not recommended");
            writer.newLine();
            writer.write("    " + new Date());
            writer.newLine();
            writer.write("-->");
            writer.newLine();
            writer.write("<!DOCTYPE week SYSTEM \"diary.dtd\">");
            writer.newLine();
            writer.newLine();
            writer.write("<week year=\"" + year + "\" id=\"" + week + "\">");
            writer.newLine();
            calendar.set(Calendar.WEEK_OF_YEAR, week);
            for (int i=0; i<7; i++) {
                calendar.set(Calendar.DAY_OF_WEEK, i);
                Day day = (Day) days.get(getDayID(calendar));
                if (day != null) day.write(writer);
            }
            writer.write("</week>");
            writer.flush();
            writer.close();
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
        ProgressMonitor pm = new ProgressMonitor(null, Translator.getTranslation("MESSAGE.PROGRESS_LOADING"), null, 0,  diaries.length - 1);
        for (int i=0; i<diaries.length; i++) {
            try {
                DiaryScanner scanner = scanner = new DiaryScanner(builder.parse(diaries[i]));
                pm.setNote(diaries[i].getName());
                scanner.loadDocument();
                pm.setProgress(i);
            } catch (SAXParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.READ_ERROR", new String[] {diaries[i].getName()}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /** Saves all regular tasks.
     */
    public static void saveRegularTasks() {
        String location = (String) Settings.getDefault().getSetting("userDir");
        location = location + File.separator + "regular_tasks.xml";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<!--");
            writer.newLine();
            writer.write("    Rachota 2.0 regular tasks - editing not recommended");
            writer.newLine();
            writer.write("    " + new Date());
            writer.newLine();
            writer.write("-->");
            writer.newLine();
            writer.write("<!DOCTYPE week SYSTEM \"regular_tasks.dtd\">");
            writer.newLine();
            writer.newLine();
            writer.write("    <regular_tasks>");
            writer.newLine();
            Iterator iterator = Plan.getDefault().getRegularTasks().iterator();
            while (iterator.hasNext()) {
                RegularTask regularTask = (RegularTask) iterator.next();
                regularTask.write(writer);
            }
            writer.write("    </regular_tasks>");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** Loads plan of regular tasks.
     * @throws java.lang.Exception Exception thrown whenever any problem while loading regular tasks occurs.
     */
    public static void loadRegularTasks() throws Exception {
        String userDir = (String) Settings.getDefault().getSetting("userDir");
        File regularTasksFile = new File(userDir + File.separator + "regular_tasks.xml");
        if (!regularTasksFile.exists()) return;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        RegularTasksScanner.createDTD();
        RegularTasksScanner scanner = new RegularTasksScanner(builder.parse(regularTasksFile));
        scanner.loadDocument();
    }
    
    /** Copies all unfinished tasks from previous working day to today.
     */
    public void copyUnfinishedTasks() {
        Day today = getDay(new Date());
        if (!existsDayBefore(today)) return;
        Day previousWorkingDay = getDayBefore(today);
        while (previousWorkingDay.getTotalTime() == 0) {
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
}