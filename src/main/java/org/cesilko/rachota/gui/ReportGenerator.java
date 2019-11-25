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
 * Portions created by Jiri Kovalsky are Copyright (C) 2010
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on Jul 1, 2010, 7:14:40 PM
 * ReportGenerator.java
 */

package org.cesilko.rachota.gui;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.AbstractTaskFilter;

/** Helper class for generation of HTML/TXT/CSV reports. */
public class ReportGenerator {

    private File file;
    private String title;
    private boolean showChart;
    private boolean showFilters;
    private String rowsRepresent;
    private boolean includeDuration;
    private boolean includeProjectsTasks;
    private boolean includeOccurrences;
    private boolean includeNotes;
    private String sortBy;
    private Vector days;
    private HistoryChart chart;
    private AbstractTaskFilter highlightFilter;
    private Vector selectFilters;
    private static int OUTPUT_TXT = 0;
    private static int OUTPUT_CSV = 1;

    public ReportGenerator(
            File file,
            String title,
            boolean showChart,
            boolean showFilters,
            String rowsRepresent,
            boolean includeDuration,
            boolean includeProjectsTasks,
            boolean includeOccurrences,
            boolean includeNotes,
            String sortBy,
            Vector days,
            HistoryChart chart,
            AbstractTaskFilter highlightFilter,
            Vector selectFilters) {

        this.file = file;
        this.title = title;
        this.showChart = showChart;
        this.showFilters = showFilters;
        this.rowsRepresent =rowsRepresent;
        this.includeDuration =includeDuration;
        this.includeProjectsTasks = includeProjectsTasks;
        this.includeOccurrences = includeOccurrences;
        this.includeNotes = includeNotes;
        this.sortBy = sortBy;
        this.days = days;
        this.chart = chart;
        this.highlightFilter = highlightFilter;
        this.selectFilters = selectFilters;
    }

    /** Generates report based on data provided in constructor of ReportGenerator class. */
    public void generateReport() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), (String) Settings.getDefault().getSetting("systemEncoding"));
            writeHeader(writer);
            if (showChart) writeChart(writer);
            if (showFilters) writeFilters(writer);
            if (rowsRepresent.equals(ReportWizard.REPORT_ROWS_TASKS)) writeTasks(writer);
            else writeProjects(writer);
            writeFooter(writer);
            writer.flush();
            writer.close();
            JOptionPane.showMessageDialog(null, Translator.getTranslation("INFORMATION.REPORT_CREATED"), Translator.getTranslation("INFORMATION.INFORMATION_TITLE"), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {file.getAbsolutePath()}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeHeader(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLHeader(writer);
        else if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_TXT)) writeTXTCSVHeader(writer, "");
        else writeTXTCSVHeader(writer, "# ");
    }

    private void writeHTMLHeader(OutputStreamWriter writer) throws IOException {
        writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        writer.write("<!--\n");
        writer.write("    " + Tools.title + " report file\n");
        writer.write("    Generated: " + new Date() + "\n");
        writer.write("-->\n");
        writer.write("<html lang=\"" + Locale.getDefault().getLanguage() + "\">\n");
        writer.write("  <head>\n");
        writer.write("    <title>" + Translator.getTranslation("REPORT.TITLE") + "</title>\n");
        writer.write("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=" + (String) Settings.getDefault().getSetting("systemEncoding") + "\">\n");
        writer.write("  </head>\n");
        writer.write("  <body>\n");
        writer.write("    <h1>" + Translator.getTranslation("REPORT.TITLE") + "</h1>\n");
        writer.write("    <table>\n");
        writer.write("      <tr><td><u>" + Translator.getTranslation("QUESTION.REPORT_DESCRIPTION") + "</u></td><td width=\"20\"/><td>" + title + "</td></tr>\n");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Day firstDay = (Day) days.get(0);
        Day lastDay = (Day) days.get(days.size() - 1);
        int numberOfWorkingDays = 0;
        Iterator iterator = days.iterator();
        while(iterator.hasNext()) {
            Day day = (Day) iterator.next();
            if (day.getTotalTime(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue()) != 0) numberOfWorkingDays++;
        }
        writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.PERIOD") + "</u></td><td width=\"20\"/><td>" + df.format(firstDay.getDate()) + " - " + df.format(lastDay.getDate()) + "</td></tr>\n");
        writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.NUMBER_OF_DAYS") + "</u></td><td width=\"20\"/><td>" + days.size() + "</td></tr>\n");
        writer.write("      <tr><td><u>" + Translator.getTranslation("REPORT.NUMBER_OF_WORK_DAYS") + "</u></td><td width=\"20\"/><td>" + numberOfWorkingDays + "</td></tr>\n");
        writer.write("    </table><br/>\n");
    }

    private void writeTXTCSVHeader(OutputStreamWriter writer, String prefix) throws IOException {
        writer.write(prefix + title + "\n" + prefix);
        for (int i = 0; i < title.length(); i++) {
            writer.write("=");
        }
        writer.write("\n");
        writer.write(prefix + Tools.title + " " + Translator.getTranslation("REPORT.TITLE") + "\n");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Day firstDay = (Day) days.get(0);
        Day lastDay = (Day) days.get(days.size() - 1);
        int numberOfWorkingDays = 0;
        Iterator iterator = days.iterator();
        while(iterator.hasNext()) {
            Day day = (Day) iterator.next();
            if (day.getTotalTime(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue()) != 0) numberOfWorkingDays++;
        }
        writer.write(prefix + Translator.getTranslation("REPORT.PERIOD") + " " + df.format(firstDay.getDate()) + " - " + df.format(lastDay.getDate()) + "\n");
        writer.write(prefix + Translator.getTranslation("REPORT.NUMBER_OF_DAYS") + " " + days.size() + "\n");
        writer.write(prefix + Translator.getTranslation("REPORT.NUMBER_OF_WORK_DAYS") + " " + numberOfWorkingDays + "\n" + prefix + "\n");
    }

    private void writeFooter(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLFooter(writer);
        else if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_TXT)) writeTXTCSVFooter(writer, "");
        else writeTXTCSVFooter(writer, "# ");
    }

    private void writeHTMLFooter(OutputStreamWriter writer) throws IOException {
        writer.write("\n");
        writer.write("    <hr/><u>" + Translator.getTranslation("REPORT.GENERATED_BY") + "</u> <a href=\"http://rachota.sourceforge.net/\">" + Tools.title + "</a> " + "(build " + Tools.build + ")<br/>\n");
        writer.write("    " + new Date() + "\n");
        writer.write("  </body>\n");
        writer.write("</html>");
    }

    private void writeTXTCSVFooter(OutputStreamWriter writer, String prefix) throws IOException {
        writer.write(prefix + "\n" + prefix + "---------------------------------------------------------------\n");
        writer.write(prefix + Translator.getTranslation("REPORT.GENERATED_BY") + " " + Tools.title + " (build " + Tools.build + ")\n");
        writer.write(prefix + "http://rachota.sourceforge.net\n");
        writer.write(prefix + new Date());
    }

    private void writeChart(OutputStreamWriter writer) throws FileNotFoundException, IOException {
        String filename = file.getAbsolutePath();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename.substring(0, filename.lastIndexOf(".")) + "_chart.png"));
        PNGImageWriter imageWriter = new PNGImageWriter();
        BufferedImage image = new BufferedImage(chart.getBounds().width, chart.getBounds().height, BufferedImage.TYPE_INT_RGB);
        chart.paint(image.getGraphics());
        imageWriter.write(image, out);
        writer.write("    <img src=\"" + file.getName().substring(0, file.getName().indexOf(".")) + "_chart.png\" title=\"Times chart of selected period.\" border=\"1\"/><br/><br/>\n");
        if (highlightFilter != null) {
            writer.write("    <u>" + Translator.getTranslation("HISTORYVIEW.LBL_HIGHLIGHT_TASKS") + "</u>\n");
            writer.write("    <ul><li>" + highlightFilter.toString() + " " + highlightFilter.getContentRules().get(highlightFilter.getContentRule()) + " <b>" + highlightFilter.getContent() + "</b></li></ul>\n");
        }
    }

    private void writeFilters(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLFilters(writer);
        else if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_TXT)) writeTXTCSVFilters(writer, "", " ");
        else writeTXTCSVFilters(writer, "# ", ";");
    }

    private void writeHTMLFilters(OutputStreamWriter writer) throws IOException {
        writer.write("    <u>" + Translator.getTranslation("REPORT.APPLIED_FILTERS") + "</u>\n");
        writer.write("    <ul>\n");
        int count = selectFilters.size();
        for (int i=0; i<count; i++) {
            AbstractTaskFilter selectFilter = (AbstractTaskFilter) selectFilters.get(i);
            writer.write("      <li>" + selectFilter.toString() + " ");
            writer.write(selectFilter.getContentRules().get(selectFilter.getContentRule()) + " ");
            writer.write("<b>" + selectFilter.getContent() + "</b></li>\n");
        }
        writer.write("    </ul>\n");
    }

    private void writeTXTCSVFilters(OutputStreamWriter writer, String prefix, String delimiter) throws IOException {
        writer.write(prefix + Translator.getTranslation("REPORT.APPLIED_FILTERS") + "\n");
        int count = selectFilters.size();
        for (int i=0; i<count; i++) {
            AbstractTaskFilter selectFilter = (AbstractTaskFilter) selectFilters.get(i);
            writer.write("" + selectFilter.toString() + delimiter);
            writer.write(selectFilter.getContentRules().get(selectFilter.getContentRule()) + delimiter);
            writer.write(selectFilter.getContent() + "\n");
        }
        writer.write(prefix + "\n");
    }

    private void writeTasks(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLTasks(writer);
        else if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_TXT)) writeTXTCSVTasks(writer, OUTPUT_TXT);
        else writeTXTCSVTasks(writer, OUTPUT_CSV);
    }

    private void writeHTMLTasks(OutputStreamWriter writer) throws IOException {
        writer.write("    <u>" + Translator.getTranslation("REPORT.TASKS") + "</u><br/><br/>\n");
        writer.write("    <table border=\"1\">\n");
        writer.write("      <tr bgcolor=\"CCCCCC\">\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DESCRIPTION") + "</b></td>\n");
        if (includeNotes)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.NOTES") + "</b></td>\n");
        if (includeProjectsTasks)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.PROJECT") + "</b></td>\n");
        if (includeDuration)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DURATION_TIME") + "</b></td>\n");
        if (includeOccurrences)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.OCCURRENCES") + "</b></td>\n");
        writer.write("      </tr>\n");
        long totalTime = 0;
        TaskRow[] taskRows = getTaskRows();
        for (int i = 0; i < taskRows.length; i++) {
            totalTime = totalTime + taskRows[i].getDuration();
            writer.write("      <tr>\n");
            writer.write("        <td>" + taskRows[i].getTaskDescription() + "</td>\n");
            if (includeNotes) {
                String notes = taskRows[i].getNotes();
                if (notes == null | notes.isEmpty()) writer.write("        <td>&nbsp;</td>\n");
                else {
                    writer.write("        <td align=\"left\">\n");
                    Iterator notesIterator = taskRows[i].getNotesIterator();
                    while(notesIterator.hasNext()) {
                        String taskNotes = (String) notesIterator.next();
                        writer.write("          <li>" + taskNotes + "</li>\n");
                    }
                    writer.write("        </td>\n");
                }
            }
            if (includeProjectsTasks) {
                String projects = taskRows[i].getProjects();
                if (projects == null | projects.isEmpty()) writer.write("        <td>&nbsp;</td>\n");
                else {
                    writer.write("        <td align=\"left\">\n");
                    Iterator projectsIterator = taskRows[i].getProjectsIterator();
                    while(projectsIterator.hasNext()) {
                        String projectDescription = (String) projectsIterator.next();
                        writer.write("          <li>" + projectDescription + "</li>\n");
                    }
                    writer.write("        </td>\n");
                }
            }
            if (includeDuration)
                writer.write("        <td align=\"right\">" + Tools.getTime(taskRows[i].getDuration()) + "</td>\n");
            if (includeOccurrences)
                writer.write("        <td align=\"right\">" + taskRows[i].getOccurrences() + "</td>\n");
            writer.write("      </tr>\n");
        }
        writer.write("    </table><br/>\n");
        writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_FILTERED_TIME") + "</u> " + Tools.getTime(totalTime) + "<br/>\n");
        boolean includePrivateTime = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
        writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_TIME") + "</u><b> " + Tools.getTime(Tools.getTotalTime(false, includePrivateTime, days)) + "</b><br/><br/>\n");
    }

    private void writeTXTCSVTasks(OutputStreamWriter writer, int output) throws IOException {
        String prefix = "";
        String delimiter = " | ";
        if (output == OUTPUT_CSV) {
            prefix = "#";
            delimiter = ";";
        }
        writer.write(prefix + Translator.getTranslation("REPORT.TASKS") + "\n" + prefix + "\n");
        writer.write("ID" + delimiter + Translator.getTranslation("TASKS.DESCRIPTION"));
        if (includeNotes)
            writer.write(delimiter + Translator.getTranslation("TASKS.NOTES"));
        if (includeProjectsTasks)
            writer.write(delimiter + Translator.getTranslation("TASKS.PROJECT"));
        if (includeDuration)
            writer.write(delimiter + Translator.getTranslation("TASKS.DURATION_TIME"));
        if (includeOccurrences)
            writer.write(delimiter + Translator.getTranslation("TASKS.OCCURRENCES"));
        writer.write("\n");
        long totalTime = 0;
        TaskRow[] taskRows = getTaskRows();
        for (int i = 0; i < taskRows.length; i++) {
            totalTime = totalTime + taskRows[i].getDuration();
            writer.write("" + (i+1) + "." + delimiter + taskRows[i].getTaskDescription());
            if (includeNotes) {
                writer.write(delimiter);
                String notes = taskRows[i].getNotes();
                if (notes != null & !notes.isEmpty()) {
                    if (output == OUTPUT_CSV) writer.write("\"");
                    Iterator notesIterator = taskRows[i].getNotesIterator();
                    while(notesIterator.hasNext()) {
                        String taskNotes = (String) notesIterator.next();
                        writer.write(" * " + taskNotes );
                    }
                    if (output == OUTPUT_CSV) writer.write("\"");
                }
            }
            if (includeProjectsTasks) {
                writer.write(delimiter);
                String projects = taskRows[i].getProjects();
                if (projects != null & !projects.isEmpty()) {
                    if (output == OUTPUT_CSV) writer.write("\"");
                    Iterator projectsIterator = taskRows[i].getProjectsIterator();
                    while(projectsIterator.hasNext()) {
                        String projectDescription = (String) projectsIterator.next();
                        writer.write(" * " + projectDescription);
                    }
                    if (output == OUTPUT_CSV) writer.write("\"");
                }
            }
            if (includeDuration)
                writer.write(delimiter + Tools.getTime(taskRows[i].getDuration()));
            if (includeOccurrences)
                writer.write(delimiter + taskRows[i].getOccurrences());
            writer.write("\n");
        }
        writer.write(prefix + "\n");
        writer.write(prefix + Translator.getTranslation("REPORT.TOTAL_FILTERED_TIME") + " " + Tools.getTime(totalTime) + "\n");
        boolean includePrivateTime = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
        writer.write(prefix + Translator.getTranslation("REPORT.TOTAL_TIME") + " " + Tools.getTime(Tools.getTotalTime(false, includePrivateTime, days)) + "\n");
    }

    private void writeProjects(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLProjects(writer);
        else if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_TXT)) writeTXTCSVProjects(writer, OUTPUT_TXT);
        else writeTXTCSVProjects(writer, OUTPUT_CSV);
    }

    private void writeHTMLProjects(OutputStreamWriter writer) throws IOException {
        writer.write("    <u>" + Translator.getTranslation("REPORT.PROJECTS") + "</u><br/><br/>\n");
        writer.write("    <table border=\"1\">\n");
        writer.write("      <tr bgcolor=\"CCCCCC\">\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.PROJECT") + "</b></td>\n");
        if (includeProjectsTasks)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DESCRIPTION") + "</b></td>\n");
        if (includeNotes)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.NOTES") + "</b></td>\n");
        if (includeDuration)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DURATION_TIME") + "</b></td>\n");
        if (includeOccurrences)
            writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.OCCURRENCES") + "</b></td>\n");
        writer.write("      </tr>\n");
        long totalTime = 0;
        ProjectRow[] projectRows = getProjectRows();
        for (int i = 0; i < projectRows.length; i++) {
            totalTime = totalTime + projectRows[i].getDuration();
            writer.write("      <tr>\n");
            writer.write("        <td>" + projectRows[i].getProjectDescription() + "</td>\n");
            if (includeProjectsTasks) {
                writer.write("        <td align=\"left\">\n");
                Iterator tasksIterator = projectRows[i].getTasksIterator();
                while(tasksIterator.hasNext()) {
                    String taskDescription = (String) tasksIterator.next();
                    writer.write("          <li>" + taskDescription + "</li>\n");
                }
                writer.write("        </td>\n");
            }
            if (includeNotes) {
                String notes = projectRows[i].getNotes();
                if (notes == null | notes.isEmpty()) writer.write("        <td>&nbsp;</td>\n");
                else {
                    writer.write("        <td align=\"left\">\n");
                    Iterator notesIterator = projectRows[i].getNotesIterator();
                    while(notesIterator.hasNext()) {
                        String taskNotes = (String) notesIterator.next();
                        writer.write("          <li>" + taskNotes + "</li>\n");
                    }
                    writer.write("        </td>\n");
                }
            }
            if (includeDuration)
                writer.write("        <td align=\"right\">" + Tools.getTime(projectRows[i].getDuration()) + "</td>\n");
            if (includeOccurrences)
                writer.write("        <td align=\"right\">" + projectRows[i].getOccurrences() + "</td>\n");
            writer.write("      </tr>\n");
        }
        writer.write("    </table><br/>\n");
        writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_FILTERED_TIME") + "</u> " + Tools.getTime(totalTime) + "<br/>\n");
        boolean includePrivateTime = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
        writer.write("    <u>" + Translator.getTranslation("REPORT.TOTAL_TIME") + "</u><b> " + Tools.getTime(Tools.getTotalTime(false, includePrivateTime, days)) + "</b><br/><br/>\n");
    }

    private void writeTXTCSVProjects(OutputStreamWriter writer, int output) throws IOException {
        String prefix = "";
        String delimiter = " | ";
        if (output == OUTPUT_CSV) {
            prefix = "#";
            delimiter = ";";
        }
        writer.write(prefix + Translator.getTranslation("REPORT.PROJECTS") + "\n" + prefix + "\n");
        writer.write("ID" + delimiter + Translator.getTranslation("TASKS.PROJECT"));
        if (includeProjectsTasks)
            writer.write(delimiter + Translator.getTranslation("TASKS.DESCRIPTION"));
        if (includeNotes)
            writer.write(delimiter + Translator.getTranslation("TASKS.NOTES"));
        if (includeDuration)
            writer.write(delimiter + Translator.getTranslation("TASKS.DURATION_TIME"));
        if (includeOccurrences)
            writer.write(delimiter + Translator.getTranslation("TASKS.OCCURRENCES"));
        writer.write("\n");
        long totalTime = 0;
        ProjectRow[] projectRows = getProjectRows();
        for (int i = 0; i < projectRows.length; i++) {
            totalTime = totalTime + projectRows[i].getDuration();
            writer.write("" + (i+1) + "." + delimiter + projectRows[i].getProjectDescription());
            if (includeProjectsTasks) {
                writer.write(delimiter);
                if (output == OUTPUT_CSV) writer.write("\"");
                Iterator tasksIterator = projectRows[i].getTasksIterator();
                while(tasksIterator.hasNext()) {
                    String taskDescription = (String) tasksIterator.next();
                    writer.write(" * " + taskDescription);
                }
                if (output == OUTPUT_CSV) writer.write("\"");
            }
            if (includeNotes) {
                writer.write(delimiter);
                String notes = projectRows[i].getNotes();
                if (notes != null & !notes.isEmpty()) {
                    if (output == OUTPUT_CSV) writer.write("\"");
                    Iterator notesIterator = projectRows[i].getNotesIterator();
                    while(notesIterator.hasNext()) {
                        String taskNotes = (String) notesIterator.next();
                        writer.write(" * " + taskNotes);
                    }
                    if (output == OUTPUT_CSV) writer.write("\"");
                }
            }
            if (includeDuration)
                writer.write(delimiter + Tools.getTime(projectRows[i].getDuration()));
            if (includeOccurrences)
                writer.write(delimiter + projectRows[i].getOccurrences());
            writer.write("\n");
        }
        writer.write(prefix + "\n");
        writer.write(prefix + Translator.getTranslation("REPORT.TOTAL_FILTERED_TIME") + " " + Tools.getTime(totalTime) + "\n");
        boolean includePrivateTime = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
        writer.write(prefix + Translator.getTranslation("REPORT.TOTAL_TIME") + " " + Tools.getTime(Tools.getTotalTime(false, includePrivateTime, days)) + "\n");
    }

    /** Processes all days in selected period and their tasks and for each non-idle,
     * non-private (unless required) task calculates its statistic (@see TaskRow).
     * Finally, sorts all entries according to user's preference.
     * @return Sorted array of task rows to be included on the report.
     */
    private TaskRow[] getTaskRows() {
        Hashtable taskRowsTable = new Hashtable();
        Iterator daysIterator = days.iterator();
        while (daysIterator.hasNext()) { // Process all days in selected period
            Day day = (Day) daysIterator.next();
            Vector filteredTasks = filterTasks(day.getTasks()); // Filter all tasks in a day
            Iterator tasksIterator = filteredTasks.iterator();
            while (tasksIterator.hasNext()) { // Process all filtered tasks in a day
                Task task = (Task) tasksIterator.next();
                if (task.isIdleTask()) continue;
                boolean includePrivateTasks = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
                if (task.privateTask() & !includePrivateTasks) continue;
                if (taskRowsTable.containsKey(task.getDescription())) { // If same task was already found
                    TaskRow taskRow = (TaskRow) taskRowsTable.get(task.getDescription());
                    taskRow.includeTask(task);
                } else { // If this is the first occurence of a task
                    TaskRow taskRow = new TaskRow(task, sortBy);
                    taskRowsTable.put(task.getDescription(), taskRow);
                }
            }
        }
        int size = taskRowsTable.size();
        TaskRow[] taskRows = new TaskRow[size];
        Iterator iterator = taskRowsTable.values().iterator();
        for (int i = 0; i < size; i++)
            taskRows[i] = (TaskRow) iterator.next();
        Arrays.sort(taskRows);
        return taskRows;
    }

    /** Processes all days in selected period and their tasks and for each non-idle,
     * non-private (unless required) task calculates projects statistic (@see ProjectRow).
     * Finally, sorts all entries according to user's preference.
     * @return Sorted array of project rows to be included on the report.
     */
    private ProjectRow[] getProjectRows() {
        Hashtable projectRowsTable = new Hashtable();
        Iterator daysIterator = days.iterator();
        while (daysIterator.hasNext()) { // Process all days in selected period
            Day day = (Day) daysIterator.next();
            Vector filteredTasks = filterTasks(day.getTasks()); // Filter all tasks in a day
            Iterator tasksIterator = filteredTasks.iterator();
            while (tasksIterator.hasNext()) { // Process all filtered tasks in a day
                Task task = (Task) tasksIterator.next();
                if (task.isIdleTask()) continue;
                boolean includePrivateTasks = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
                if (task.privateTask() & !includePrivateTasks) continue;
                Iterator keywordIterator = task.getKeywordIterator();
                if (keywordIterator.hasNext()) {
                    while (keywordIterator.hasNext()) { // Process all projects of a task
                        String project = (String) keywordIterator.next();
                        if (projectRowsTable.containsKey(project)) { // If same project was already found
                            ProjectRow projectRow = (ProjectRow) projectRowsTable.get(project);
                            projectRow.includeTask(task);
                        } else { // If this is the first occurence of a project
                            ProjectRow projectRow = new ProjectRow(project, task, sortBy);
                            projectRowsTable.put(project, projectRow);
                        }
                    }
                }
            }
        }
        int size = projectRowsTable.size();
        ProjectRow[] projectRows = new ProjectRow[size];
        Iterator iterator = projectRowsTable.values().iterator();
        for (int i = 0; i < size; i++)
            projectRows[i] = (ProjectRow) iterator.next();
        Arrays.sort(projectRows);
        return projectRows;
    }

    /** Filters given vector of tasks trough all used filters and returns vector
     * of tasks satisfying all filters.
     * @param tasks Vector of tasks to be filtered.
     * @return Vector of tasks that satisfied all filters.
     */
    private Vector filterTasks(Vector tasks) {
        Vector filteredTasks = tasks;
        Iterator iterator = selectFilters.iterator();
        while (iterator.hasNext()) {
            AbstractTaskFilter filter = (AbstractTaskFilter) iterator.next();
            filteredTasks = filter.filterTasks(filteredTasks);
        }
        return filteredTasks;
    }

    /** Comparable object representing one task in the summary on report. It has several properties
     * like name, number of occurrences in the selected period, projects the task belongs to, total
     * duration of the task (tasks with same name) and compilation of notes. */
    class TaskRow implements Comparable {
        String taskDescription;
        int occurrences;
        String projects;
        long duration;
        String notes;
        String sortBy;

        TaskRow(Task task, String sortBy) {
            taskDescription = task.getDescription();
            occurrences = 1;
            projects = task.getKeyword();
            duration = task.getDuration();
            notes = task.getNotes();
            this.sortBy = sortBy;
        }

        void includeTask(Task task) {
            occurrences++;
            String keyword = task.getKeyword();
            if (keyword != null)
                if (!keyword.equals(""))
                    if (!projects.contains(keyword))
                        projects = projects + "," + keyword;
            duration = duration + task.getDuration();
            String notes = task.getNotes();
            if (notes != null)
                if (!notes.equals(""))
                    if (!this.notes.contains(notes))
                        this.notes = this.notes + "," + notes;
        }

        String getTaskDescription() {
            return taskDescription;
        }

        int getOccurrences() {
            return occurrences;
        }

        Iterator getProjectsIterator() {
            StringTokenizer tokenizer = new StringTokenizer(Tools.replaceAll(projects, " ", ","), ","); // If task has two keywords divided by space
            ArrayList tokens = new ArrayList();
            while (tokenizer.hasMoreTokens()) tokens.add(tokenizer.nextToken());
            return tokens.iterator();
        }

        String getProjects() {
            return projects;
        }

        long getDuration() {
            return duration;
        }

        Iterator getNotesIterator() {
            StringTokenizer tokenizer = new StringTokenizer(notes, ",");
            ArrayList tokens = new ArrayList();
            while (tokenizer.hasMoreTokens()) tokens.add(tokenizer.nextToken());
            return tokens.iterator();
        }

        String getNotes() {
            return notes;
        }

        @Override
        public int compareTo(Object object) {
            if (object == null) return 0;
            TaskRow taskRow = (TaskRow) object;
            if (sortBy == null) return taskDescription.compareTo(taskRow.getTaskDescription());
            if (sortBy.equals(ReportWizard.SORTBY_NOTES))
                return notes.compareTo(taskRow.getNotes());
            if (sortBy.equals(ReportWizard.SORTBY_DURATION)) {
                if (duration < taskRow.getDuration()) return 1;
                return -1;
            }
            if (sortBy.equals(ReportWizard.SORTBY_OCCURRENCES)) {
                if (occurrences < taskRow.getOccurrences()) return 1;
                return -1;
            }
            if (sortBy.equals(ReportWizard.SORTBY_PROJECTS_TASKS))
                return projects.compareTo(taskRow.getProjects());
            return 0;
        }
    }

    /** Comparable object representing one project in the summary on report. It has several properties
     * like name, number of occurrences in the selected period, tasks that belonged to the project,
     * total duration of all owned tasks and compilation of notes. */
    class ProjectRow implements Comparable {
        String projectDescription;
        int occurrences;
        String tasks;
        long duration;
        String notes;
        String sortBy;

        ProjectRow(String projectDescription, Task task, String sortBy) {
            this.projectDescription = projectDescription;
            occurrences = 1;
            tasks = task.getDescription();
            duration = task.getDuration();
            notes = task.getNotes();
            this.sortBy = sortBy;
        }

        void includeTask(Task task) {
            occurrences++;
            String taskDescription = task.getDescription();
            if (!tasks.contains(taskDescription))
                tasks = tasks + "," + taskDescription;
            duration = duration + task.getDuration();
            String notes = task.getNotes();
            if (notes != null)
                if (!notes.equals(""))
                    if (!this.notes.contains(notes))
                        this.notes = this.notes + "," + notes;
        }

        String getProjectDescription() {
            return projectDescription;
        }

        int getOccurrences() {
            return occurrences;
        }

        Iterator getTasksIterator() {
            StringTokenizer tokenizer = new StringTokenizer(tasks, ",");
            ArrayList tokens = new ArrayList();
            while (tokenizer.hasMoreTokens()) tokens.add(tokenizer.nextToken());
            return tokens.iterator();
        }

        String getTasks() {
            return tasks;
        }

        long getDuration() {
            return duration;
        }

        Iterator getNotesIterator() {
            StringTokenizer tokenizer = new StringTokenizer(notes, ",");
            ArrayList tokens = new ArrayList();
            while (tokenizer.hasMoreTokens()) tokens.add(tokenizer.nextToken());
            return tokens.iterator();
        }

        String getNotes() {
            return notes;
        }

        @Override
        public int compareTo(Object object) {
            if (object == null) return 0;
            ProjectRow projectRow = (ProjectRow) object;
            if (sortBy == null) return projectDescription.compareTo(projectRow.getProjectDescription());
            if (sortBy.equals(ReportWizard.SORTBY_NOTES))
                return notes.compareTo(projectRow.getNotes());
            if (sortBy.equals(ReportWizard.SORTBY_DURATION)) {
                if (duration < projectRow.getDuration()) return 1;
                return -1;
            }
            if (sortBy.equals(ReportWizard.SORTBY_OCCURRENCES)) {
                if (occurrences < projectRow.getOccurrences()) return 1;
                return -1;
            }
            if (sortBy.equals(ReportWizard.SORTBY_PROJECTS_TASKS))
                return tasks.compareTo(projectRow.getTasks());
            return 0;
        }
    }
}