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
import javax.swing.JOptionPane;
import org.cesilko.rachota.gui.Tools;

/** Persistent settings of the system.
 *
 * @author  Jiri Kovalsky
 */
public class Settings {
    
    /** The only instance of Settings object in the system. */
    private static Settings settings;
    /** Map containing all settings. Key is setting name e.g. "displayFinishedTasks"
     * and value holds the setting value e.g. "true".
     */
    private HashMap settingsMap;
    /** Class containing all registered listeners interested in settings. */
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Creates private instance of Settings object. */
    private Settings() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        settingsMap = new HashMap();
        settingsMap.put("dayWorkHours", "8.0");
        settingsMap.put("warnHoursNotReached", new Boolean(true));
        settingsMap.put("warnHoursExceeded", new Boolean(false));
        settingsMap.put("moveUnfinished", new Boolean(true));
        settingsMap.put("archiveNotStarted", new Boolean(false));
        settingsMap.put("checkPriority", new Boolean(true));
        settingsMap.put("displayFinishedTasks", new Boolean(false));
        settingsMap.put("countPrivateTasks", new Boolean(false));
        settingsMap.put("reportActivity", new Boolean(true));
        settingsMap.put("runningTask", null);
        settingsMap.put("savingPeriod", "30");
        settingsMap.put("dictionary", "Dictionary_" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry() + ".properties");
        settingsMap.put("size", null);
        settingsMap.put("location", null);
        // htietgens: new param for automatic starting of IdleTask on startup of program
        settingsMap.put("automaticStart", new Boolean(true));
        settingsMap.put("showTime", "both");
        settingsMap.put("backupAge", "10");
        System.setProperty("backupCreated", "" + new Date().getTime());
        
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
                String value = data.substring(delimiterPosition + 2);
                if (value.equals("true") | value.equals("false"))
                    settings.setSetting(key, new Boolean(value));
                else settings.setSetting(key, value);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {location}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }
}