/*
 * Tools.java
 *
 * Created on April 9, 2005, 9:18 PM
 */

package org.cesilko.rachota.gui;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class providing support for time conversion between long, Date and String formats.
 * @author Jiri Kovalsky
 */
public class Tools {
    
    /** Transforms time in milliseconds to text string.
     * @param time Time in milliseconds.
     * @return Textual representation of time in format hh:mm:ss.
     */
    public static String getTime(long time) {
        int hours = (int) time/(1000*60*60);
        String text = ((hours > 9) ? "" : "0") + hours;
        time = time - hours * (1000*60*60);
        int minutes = (int) time/(1000*60);
        text = text + ":" + ((minutes > 9) ? "" : "0") + minutes;
        time = time - minutes*(1000*60);
        int seconds = (int) time/1000;
        text = text + ":" + ((seconds > 9) ? "" : "0") + seconds;
        return text;
    }
    
    /** Transforms time of Date to text string.
     * @param time Time in Date object.
     * @return Textual representation of time in format hh:mm.
     */
    public static String getTime(Date time) {
        if (time == null) return "00:00";
        SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        df.applyPattern("HH:mm");
        return df.format(time);
    }
    
    /**
     * Transforms text string to time in milliseconds.
     * @param text Textual representation of time in format hh:mm:ss.
     * @return Time in milliseconds.
     */
    public static long getTime(String text) {
        long time = 0;
        if (text.length() == 5) {
            SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            df.applyPattern("HH:mm");
            try { time = df.parse(text).getTime(); } catch (ParseException e) { e.printStackTrace(); }
        } else {
            int hours = Integer.parseInt(text.substring(0, 2));
            int minutes = Integer.parseInt(text.substring(3, 5));
            int seconds = Integer.parseInt(text.substring(6, 8));
            time = seconds * 1000;
            time = time + minutes * 1000 * 60;
            time = time + hours * 1000 * 60 * 60;
        }
        return time;
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
            index = text.indexOf(oldText);
        }
        return text;
    }
}
