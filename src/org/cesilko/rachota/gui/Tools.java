/*
 * Tools.java
 *
 * Created on April 9, 2005, 9:18 PM
 */

package org.cesilko.rachota.gui;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.cesilko.rachota.core.Translator;

/** Helper class providing support for time conversion between
 * long, Date and String formats.
 * @author Jiri Kovalsky
 */
public class Tools {
    
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
}