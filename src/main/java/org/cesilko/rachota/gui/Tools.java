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
 * Created on April 9, 2005  9:18 PM
 * Tools.java
 */

package org.cesilko.rachota.gui;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Helper class providing support for time conversion between
 * long, Date and String formats and other static functions.
 * @author Jiri Kovalsky
 */
public class Tools {
    
    /** Name and version of application. */
    public static final String title = "Rachota 2.4";
    /** Build number. */
    public static final String build = "#190211";
    /** Warning type of beep. */
    public static final int BEEP_WARNING = 0;
    /** Notification type of beep. */
    public static final int BEEP_NOTIFICATION = 1;
    /** Font that should be used for all UI elements. */
    private static Font font = null;
    
    /** Transforms time in milliseconds to text string.
     * @param time Time in milliseconds.
     * @return Textual representation of time in format hh:mm:ss.
     */
    public static String getTime(double time) {
        long hours = (long) time/(1000*60*60);
        String text = ((hours > 9) ? "" : "0") + hours;
        time = time - hours * (1000*60*60);
        long minutes = (long) time/(1000*60);
        text = text + ":" + ((minutes > 9) ? "" : "0") + minutes;
        time = time - minutes*(1000*60);
        long seconds = (long) time/1000;
        text = text + ":" + ((seconds > 9) ? "" : "0") + seconds;
        return text;
    }
    
    /** Transforms time in milliseconds to text string.
     * @param time Time in milliseconds.
     * @return Textual representation of time in format hh:mm.
     */
    public static String getTimeShort(double time) {
        long hours = (long) time/(1000*60*60);
        String text = ((hours > 9) ? "" : "0") + hours;
        time = time - hours * (1000*60*60);
        long minutes = (long) time/(1000*60);
        text = text + ":" + ((minutes > 9) ? "" : "0") + minutes;
        time = time - minutes*(1000*60);
        return text;
    }
    
    /** Transforms time of Date to text string.
     * @param time Time in Date object.
     * @return Textual representation of time in format hh:mm.
     */
    public static String getTime(Date time) {
        if (time == null) return "00:00";
        SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        String timeFormat = Translator.getTranslation("FORMAT.TIME");
        df.applyPattern(timeFormat);
        return df.format(time);
    }
    
    /** Transforms text string to time in milliseconds.
     * @return Time in milliseconds.
     * @param text Textual representation of time in format hh:mm or hh:mm:ss.
     * @throws NumberFormatException in case format of time does not comply with hh:mm:ss format.
     */
    public static long getTime(String text) throws NumberFormatException {
        long time = 0;
        if (text.length() == 5) {
            SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            String timeFormat = Translator.getTranslation("FORMAT.TIME");
            df.applyPattern(timeFormat);
            try {
                time = df.parse(text).getTime();
            } catch (ParseException ex) {
                throw new NumberFormatException("Error: Time does not comply with hh:mm format: " + text);
            }
        } else {
            int firstColon = text.indexOf(":");
            int secondColon = text.lastIndexOf(":");
            int hours = Integer.parseInt(text.substring(0, firstColon));
            int minutes = Integer.parseInt(text.substring(firstColon + 1, secondColon));
            int seconds = Integer.parseInt(text.substring(secondColon + 1, text.length()));
            time = seconds * 1000;
            time = time + minutes * 1000 * 60;
            time = time + hours * 1000 * 60 * 60;
        }
        return time;
    }
    
    /** Returns total time measured in selected period.
     * @param includeIdleTime Should idle time be included in the total time?
     * @param includePrivateTime Should private time be included in the total time?
     * @param days Vector of days whose total time should be counted.
     * @return Total time measured in selected period including idle time and private time if desired.
     */
    public static long getTotalTime(boolean includeIdleTime, boolean includePrivateTime, Vector days) {
        long totalTime = 0;
        Iterator iterator = days.iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            totalTime = totalTime + day.getTotalTime(includePrivateTime);
            if (includeIdleTime) {
                Task idleTask = day.getIdleTask();
                if (idleTask != null) totalTime = totalTime + idleTask.getDuration();
            }
        }
        return totalTime;
    }

    /** Returns text string that has all occurences of oldText strings replaced by newText string.
     * @param text String where all occurences of oldText should be replaced.
     * @param oldText Substring to be searched for in text string.
     * @param newText New replacement string for all occurences of oldText string.
     * @return Text string with all occurences of oldText replaced by newText strings.
     */
    public static String replaceAll(String text, String oldText, String newText) {
        int index = text.indexOf(oldText);
        while (index != -1) {
            text = text.substring(0, index) + newText + text.substring(index + oldText.length());
            index = text.indexOf(oldText, index + 1);
        }
        return text;
    }
    
    /** Produce couple of warning beeps at user when necessary.
     * @param type Type of beep i.e. BEEP_NOTIFICATION or BEEP_WARNING.
     */
    public static void beep(int type) {
        int[] notify = {100, 100, 100, 200, 200, 100, 100, 100, 100};
        int[] delays = {200, 200, 200, 100, 100, 200, 200, 200};
        switch (type) {
            case BEEP_NOTIFICATION:
                delays = notify;
                break;
        }
        for (int i = 0; i < delays.length; i++) {
            Toolkit.getDefaultToolkit().beep();
            try { Thread.sleep(delays[i]); } catch (InterruptedException ex) {}
        }
    }

    /** Returns unique Rachota identification string.
     * @return Rachota identification string.
     */
    public static String getRID() {
        return title + "|" + build + "|" +
              System.getProperty("os.name") + "|" +
              System.getProperty("os.arch") + "|" +
              System.getProperty("os.version") + "|" +
              System.getProperty("java.version") + "|" +
              Locale.getDefault().getDisplayCountry(Locale.US) + "|" +
              System.getProperty("user.name") + "|" +
              System.getProperty("user.dir");
    }
    
    /** Returns font that should be used for all UI components
     * based on the language preferences or specified by user.
     * @return Font to be used across Rachota UI components.
     */
    public static Font getFont() {
        if (font == null)
            font = new Font((String) Settings.getDefault().getSetting("fontName"), java.awt.Font.PLAIN, Integer.parseInt((String) Settings.getDefault().getSetting("fontSize")));
        return font;
    }

    public static void recordActivity() {
        System.setProperty("rachota.lastInteraction", "" + new Date().getTime());
    }

    public static long getInactivity() {
        String lastActivity = System.getProperty("rachota.lastInteraction");
        if (lastActivity == null) return 0;
        return new Date().getTime() - Long.parseLong(lastActivity);
    }

    /** Sets up a focus gained listener to the {@code JSpinner} that selects all text
     * currently in the text field of the supplied {@code JSpinner}.
     * @param spinner The {@code JSpinner} to add a select all listener to.
     */
    public static void setupSelectAllListener(JSpinner spinner) {
        FocusAdapter spinnerFocus = new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent e) {
                // Make sure that the event is from a JTextField
                if (e.getSource() instanceof JTextField) {
                    Runnable runnable = new Runnable() {

                        public void run() {
                            ((JTextField) e.getSource()).selectAll();
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            }
        };
        // Make sure that the editor is a instance of JSpinner.DefaultEditor.
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
            // Get the text field from the JSpinner, and add the focus listener to it.
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addFocusListener(spinnerFocus);
        }
    }

    /** Generates random three digits identifier.
     * @return Returns random three digits as a string. e.g. "537"
     */
    static String getRandomID() {
        Double randomID = new Double(Math.random());
        return randomID.toString().substring(2, 5);
    }

    static void showURL(String webPage) {
        if (System.getProperty("java.version").startsWith("1.6")) {
            try { 
                URI url = new URI(webPage);
                Desktop.getDesktop().browse(url);
                return;
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        String[] commands = null;
        String os = System.getProperty("os.name");
        if (os.indexOf("Unix") != -1) commands = new String[] {"firefox ", "konqueror ", "mozilla ", "opera "};
        if (os.indexOf("Linux") != -1) commands = new String[] {"firefox ", "konqueror ", "mozilla ", "opera "};
        if (os.indexOf("Windows") != -1) commands = new String[] {"cmd.exe /c start "};
        if (os.indexOf("Macintosh") != -1) commands = new String[] {"open "};
        if (commands == null) return;
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i] + webPage;
            System.out.println("Executing \"" + command + "\" command.");
            try {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                int exit = process.exitValue();
                if (exit == 0) return;
            } catch(Exception e) { e.printStackTrace(); }
        }
    }
}