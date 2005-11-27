/*
 * Settings.java
 *
 * Created on August 27, 2004, 8:11 PM
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
        settingsMap.put("runningTask", null);
        settingsMap.put("savingPeriod", "30");
        settingsMap.put("dictionary", "Dictionary_" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry() + ".properties");
        
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
     * @return True if given setting was set, false if invalid setting was provided.
     */
    public boolean setSetting(String setting, Object value) {
        if (settingsMap.containsKey(setting)) {
            settingsMap.put(setting, value);
            propertyChangeSupport.firePropertyChange("settings", null, setting);
            return true;
        }
        return false;
    }
    
    /** Returns value of given setting.
     * @param setting Setting whose value will be returned.
     * @return Value of given setting or null if it does not exist.
     */
    public Object getSetting(String setting) {
        return settingsMap.get(setting);
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