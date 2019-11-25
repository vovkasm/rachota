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
 * Created on August 27, 2004  8:11 PM
 * Settings.java
 */

package org.cesilko.rachota.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.cesilko.rachota.gui.Tools;

/** Persistent settings of the system.
 *
 * @author  Jiri Kovalsky
 */
public class Settings {

    /** Logger for recording important or erroneous events. */
    private static final Logger logger = Logger.getLogger(Settings.class.getName());
    /** The only instance of Settings object in the system. */
    private static Settings settings;
    /** Map containing all settings. Key is setting name e.g. "displayFinishedTasks"
     * and value holds the setting value e.g. "true".
     */
    private HashMap settingsMap;
    /** Class containing all registered listeners interested in settings. */
    private PropertyChangeSupport propertyChangeSupport;
    /** Flag to ignore downtime if hibernation was detected. */
    public static String ON_HIBERNATION_IGNORE = "0";
    /** Flag to include downtime if hibernation was detected. */
    public static String ON_HIBERNATION_INCLUDE = "1";
    /** Flag to ask user if hibernation was detected. */
    public static String ON_HIBERNATION_ASK = "2";
    /** On exit, ask for measuring downtime. */
    public static final String ON_EXIT_ASK = "0";
    /** On exit, stop measuring downtime. */
    public static final String ON_EXIT_STOP = "1";
    /** On inactivity, only notify user. */
    public static final String ON_INACTIVITY_NOTIFY = "0";
    /** On inactivity, ask user what to do next. */
    public static final String ON_INACTIVITY_ASK = "1";
    /** On inactivity, stop measuring current task. */
    public static final String ON_INACTIVITY_STOP = "2";
    /** Tried to report activity but failed. */
    public static final String ACTIVITY_REPORT_FAILED = "-1";
    /** Activity not yet reported. */
    public static final String ACTIVITY_NOT_REPORTED = "-2";
    /** Upload of diary file turned off. */
    public static String UPLOAD_OFF = "0";
    /** Upload of diary file turned on. */
    public static String UPLOAD_ON = "1";
    /** Upload of diary file suspended until next Rachota session. */
    public static String UPLOAD_SUSPENDED = "2";
    
    /** Creates private instance of Settings object. */
    private Settings() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        settingsMap = new HashMap();
        // This is done to make sure that the default value is correctly formatted.
        settingsMap.put("dayWorkHours", "8.0");
        settingsMap.put("warnHoursNotReached", new Boolean(true));
        settingsMap.put("warnHoursExceeded", new Boolean(false));
        settingsMap.put("moveUnfinished", new Boolean(true));
        settingsMap.put("archiveNotStarted", new Boolean(false));
        settingsMap.put("checkPriority", new Boolean(true));
        settingsMap.put("displayFinishedTasks", new Boolean(false));
        settingsMap.put("countPrivateTasks", new Boolean(false));
        settingsMap.put("countIdleTasks", new Boolean(false));
        settingsMap.put("reportActivity", new Boolean(true));
        settingsMap.put("rachota.reported.week", ACTIVITY_NOT_REPORTED);
        settingsMap.put("detectInactivity", new Boolean(true));
        settingsMap.put("proxyHost", "");
        settingsMap.put("proxyPort", "");
        settingsMap.put("inactivityTime", "10");
        settingsMap.put("inactivityAction", ON_INACTIVITY_ASK);
        settingsMap.put("onExitAction", ON_EXIT_ASK);
        settingsMap.put("runningTask", null);
        settingsMap.put("savingPeriod", "30");
        settingsMap.put("dictionary", "Dictionary_" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry() + ".properties");
        settingsMap.put("size", null);
        settingsMap.put("location", null);
        // htietgens: new param for automatic starting of IdleTask on startup of program
        settingsMap.put("automaticStart", new Boolean(true));
        settingsMap.put("recordStartStopTimeOnIdle", new Boolean(false)); // Do not record start/finish time when not working
        settingsMap.put("showTime", "both");
        settingsMap.put("backupAge", "10");
        settingsMap.put("startTask", new Boolean(false));
        settingsMap.put("logTaskEvents", new Boolean(false));
        settingsMap.put("hibernationTime", "10");
        settingsMap.put("hibernationAction", ON_HIBERNATION_ASK);
        System.setProperty("backupCreated", "" + new Date().getTime());
        settingsMap.put("minimizeOnStart", new Boolean(false));
        settingsMap.put("popupGroupByKeyword", new Boolean(false));
        // This decides wheter or not to display a estimate of working time remainding when displaying the current day in the day view.
        settingsMap.put("dayViewUseEstimate", true);
        settingsMap.put("uploadDiary", UPLOAD_OFF);
        settingsMap.put("uploadDiaryUsername", "");
        settingsMap.put("uploadDiaryURL", "");
        
        java.net.URL url = Settings.class.getResource("Settings.class");
        String userDir = url.getFile();
        int index = System.getProperty("os.name").indexOf("Windows") != -1 ? 1 : 0;
        if (index == 1) userDir = Tools.replaceAll(userDir, "/", "\\");
        if (userDir.indexOf(".jar!") == -1) {
            userDir = userDir.substring(index, userDir.indexOf("Settings.class"));
        } else { //  file:/home/jk110465/Projects/Rachota/dist/Rachota.jar!/org/cesilko/rachota/core/
            userDir = userDir.substring(userDir.indexOf(":") + 1, userDir.indexOf(".jar!"));
            userDir = userDir.substring(index, userDir.lastIndexOf((index == 1) ? "\\" : "/"));
        }
        userDir = Tools.replaceAll(userDir, "%20", " "); // Space in path is replaced by %20 and this needs to be changed
        if (!new File(userDir + File.separator + "diary.dtd").exists()) { // User upgraded to newer version of Rachota without any diaries
            userDir = System.getProperty("user.home") + File.separator + ".rachota"; // Default userdir since 2.4 is $HOME/.rachota
            new File(userDir).mkdir();
        }
        settingsMap.put("userDir", userDir);
    }
    
    /** Returns the only instance of persistent system settings.
     * @return Persistent system settings.
     */
    public static Settings getDefault() {
        if (settings == null) settings = new Settings();
        return settings;
    }
    
    /**
     * Sets given setting to given value.
     * @param setting Setting to be set.
     * @param value New value of setting.
     */
    public void setSetting(String setting, Object value) {
        settingsMap.put(setting, value);
        propertyChangeSupport.firePropertyChange("settings", null, setting);
    }
    
    /** Returns value of given setting.
     * @param setting Setting whose value will be returned.
     * @return Value of given setting or null if it does not exist.
     */
    public Object getSetting(String setting) {
        if (!setting.startsWith("font") && !setting.startsWith("system"))
            return settingsMap.get(setting);
        if (settingsMap.containsKey(setting))
            return settingsMap.get(setting);
        String bundleValue;
        if (setting.startsWith("font")) {
            bundleValue = Translator.getTranslation("FONT." + setting.substring(4).toUpperCase());
        } else bundleValue = Translator.getTranslation("SYSTEM." + setting.substring(6).toUpperCase());
        setSetting(setting, bundleValue);
        return bundleValue;
    }

    /** Adds new listener to set of objects interested in this settings.
     * @param listener Object interested in this settings.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /** Adds new listener to set of objects interested in this settings.
     * @param listener Object interested in this settings.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /** Saves all settings into settings.cfg file.
     */
    public static void saveSettings() {
        Settings settings = Settings.getDefault();
        String userDir = (String) settings.getSetting("userDir");
        String location = userDir + File.separator + "settings.cfg";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(location));
            Set keys = settings.settingsMap.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (key.equals("userDir")) continue;
                writer.write(key + " = " + settings.getSetting(key));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** Loads all settings from settings.cfg file.
     */
    public static void loadSettings() {
        Settings settings = Settings.getDefault();
        String userDir = (String) settings.getSetting("userDir");
        String location = userDir + File.separator + "settings.cfg";
        File settingsFile = new File(location);
        if (!settingsFile.exists()) {
            saveSettings();
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            while (reader.ready()) {
                String data = reader.readLine();
                int delimiterPosition = data.indexOf("=");
                String key = data.substring(0, delimiterPosition - 1);
                String value = data.substring(delimiterPosition + 1).trim();
                if (value.equals("true") | value.equals("false"))
                    settings.setSetting(key, new Boolean(value));
                else settings.setSetting(key, value);
            }
            String proxyHost = (String) settings.getSetting("proxyHost");
            String proxyPort = (String) settings.getSetting("proxyPort");
            if ((proxyHost != null) && (proxyHost.length() > 0)) System.setProperty("http.proxyHost", proxyHost);
            else System.clearProperty("http.proxyHost");
            if ((proxyPort != null) && (proxyPort.length() > 0)) System.setProperty("http.proxyPort", proxyPort);
            else System.clearProperty("http.proxyPort");
            String uploadDiary = (String) settings.getSetting("uploadDiary"); // Turn on upload of settings if it was temporarily suspended.
            if (uploadDiary.equals(UPLOAD_SUSPENDED)) Settings.getDefault().setSetting("uploadDiary", UPLOAD_ON);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** Static String used to store the daily working hours field. */
    public static final String DAY_WORKING_HOURS = "dayWorkHours";

    /** Returns the daily working hours.
     * @return The daily working hours.
     */
    public double getWorkingHours() {
        double returnValue = 8;
        try {
            // Convert the String to a double.
            returnValue = Double.parseDouble(getSetting(DAY_WORKING_HOURS).toString());
        } catch (NumberFormatException ex) {
            logger.log(Level.WARNING, "Unable to read setting: " + "\"dayWorkHours\"" + ".", ex);
        }
        return returnValue;
    }

    /** Sets the daily working hours.
     * @param value The new value for the daily working hours.
     */
    public void setWorkingHours(double value) {
        // Convert the double to a String.
        setSetting(DAY_WORKING_HOURS, Double.toString(value));
    }

    /** Return whether or not to count private tasks towards the daily
     * working hours.
     * @return {@code true} if the private tasks should count towards the daily
     * working hours, {@code false} if not.
     * @see #setCountPrivateTasks(boolean)
     */
    public boolean getCountPrivateTasks(){
        return Boolean.valueOf(getSetting("countPrivateTasks").toString());
    }

    /** Sets whether or not to count private tasks towards the daily
     * working hours.
     * @param countPrivateTasks whether or not to count tasks towards the
     * daily working hours.
     * @see #getCountPrivateTasks() 
     */
    public void setCountPrivateTasks(boolean countPrivateTasks){
        setSetting("countPrivateTasks", countPrivateTasks);
    }

    /** Returns whether or not to use estimated work day in the day view when
     * viewing current working day.
     * @return {@code true} if the day view should show estimated end of work day,
     * {@code false} otherwise.
     */
    public boolean getUseEstimatedWorkDay(){
        return Boolean.valueOf(getSetting("dayViewUseEstimate").toString());
    }

    /** Sets whether or not to use estimated work day in the day view.
     * @param useEstimate whether or not to use estimated work day in the day view.
     */
    public void setUseEstimatedWorkDay(boolean useEstimate){
        setSetting("dayViewUseEstimate", useEstimate);
    }
}