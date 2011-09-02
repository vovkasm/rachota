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
 * Created on Sept 9, 2010, 8:40:10 PM
 * InvoiceGenerator.java
 */

package org.cesilko.rachota.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.AbstractTaskFilter;

/** Helper class for generation of HTML/TXT invoices. */
class InvoiceGenerator {

    private File file;
    private String title;
    private String userDetails;
    private String customerDetails;
    private String paymentDetails;
    private Integer dueDays;
    private Double price;
    private String currency;
    private Double tax;
    private String rowsRepresent;
    private Vector days;
    private Vector selectFilters;

    InvoiceGenerator(File outputFile, String title, String userDetails, String customerDetails, String paymentDetails, Integer dueDays, Double price, String currency, Double tax, String rowsRepresent, Vector days, Vector selectFilters) {
        this.file = outputFile;
        this.title = title;
        this.userDetails = userDetails;
        this.customerDetails = customerDetails;
        this.paymentDetails = paymentDetails;
        this.dueDays = dueDays;
        this.price = price;
        this.currency = currency;
        this.tax = tax;
        this.rowsRepresent = rowsRepresent;
        this.days = days;
        this.selectFilters = selectFilters;
    }

    /** Generates invoice based on data provided in constructor of InvoiceGenerator class. */
    void generateInvoice() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), (String) Settings.getDefault().getSetting("systemEncoding"));
            writeHeader(writer);
            writeSubjects(writer);
            if (rowsRepresent.equals(ReportWizard.INVOICE_TASKS_PROJECTS)) writeProjectsTasks(writer);
            else writeTasks(writer);
            writePaymentDetails(writer);
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
        else writeTXTHeader(writer);
    }

    private void writeSubjects(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLSubjects(writer);
        else writeTXTSubjects(writer);
    }

    private void writeProjectsTasks(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLProjectsTasks(writer);
        else writeTXTProjectsTasks(writer);
    }

    private void writeTasks(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLTasks(writer);
        else writeTXTTasks(writer);
    }

    private void writePaymentDetails(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLPaymentDetails(writer);
        else writeTXTPaymentDetails(writer);
    }

    private void writeFooter(OutputStreamWriter writer) throws IOException {
        if (file.getAbsolutePath().endsWith(ReportWizard.OUTPUT_HTML)) writeHTMLFooter(writer);
        else writeTXTFooter(writer);
    }

    private void writeHTMLHeader(OutputStreamWriter writer) throws IOException {
        writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        writer.write("<!--\n");
        writer.write("    " + Tools.title + " report file\n");
        writer.write("    Generated: " + new Date() + "\n");
        writer.write("-->\n");
        writer.write("<html lang=\"" + Locale.getDefault().getLanguage() + "\">\n");
        writer.write("  <head>\n");
        String invoiceNumber = new SimpleDateFormat("yyMMdd-").format(new Date()) + Tools.getRandomID();
        writer.write("    <title>" + Translator.getTranslation("INVOICE.TITLE") + "</title>\n");
        writer.write("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=" + (String) Settings.getDefault().getSetting("systemEncoding") + "\">\n");
        writer.write("  </head>\n");
        writer.write("  <body>\n");
        writer.write("    <table>\n");
        writer.write("      <tr>\n");
        writer.write("        <td>");
        String logoLocation = (String) Settings.getDefault().getSetting("invoice.logo");
        if (logoLocation != null)
            writer.write("<img src=\"" + logoLocation + "\">");
        writer.write("</td>\n");
        writer.write("        <td width=\"20\">&nbsp;</td>\n");
        writer.write("        <td>\n");
        writer.write("          <h2>" + Translator.getTranslation("INVOICE.TITLE") + "</h2>\n");
        writer.write("          <h3>" + title + "</h3>\n");
        writer.write("          <table>\n");
        writer.write("            <tr><td>" + Translator.getTranslation("INVOICE.NUMBER") + "</td>&nbsp;<td></td><td>" + invoiceNumber + "</td></tr>\n");
        writer.write("            <tr><td>" + Translator.getTranslation("INVOICE.DATE") + "</td>&nbsp;<td></td><td>" + new SimpleDateFormat("MMMM dd, yyyy").format(new Date()) + "</td></tr>\n");
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.add(Calendar.DAY_OF_YEAR, dueDays.intValue());
        writer.write("            <tr><td>" + Translator.getTranslation("INVOICE.DUE") + "</td><td>&nbsp;</td><td>" + new SimpleDateFormat("MMMM dd, yyyy").format(dueDateCalendar.getTime()) + "</td></tr>\n");
        writer.write("          </table>\n");
        writer.write("        </td>\n");
        writer.write("      </tr>\n");
        writer.write("    </table><br>\n\n");
    }

    private void writeHTMLSubjects(OutputStreamWriter writer) throws IOException {
        writer.write("    <table>\n");
        writer.write("      <tr>\n");
        if (!userDetails.isEmpty())
            writer.write("        <td bgcolor=\"BBBBFF\" align=\"center\"><b>" + Translator.getTranslation("INVOICE.SUPPLIER") + "</b></td>\n");
        writer.write("        <td width=\"50\">&nbsp;</td>\n");
        if (!customerDetails.isEmpty())
            writer.write("        <td bgcolor=\"BBBBFF\" align=\"center\"><b>" + Translator.getTranslation("INVOICE.CUSTOMER") + "</b></td>\n");
        writer.write("      </tr>\n");
        writer.write("      <tr>\n");
        if (!userDetails.isEmpty())
            writer.write("        <td>" + Tools.replaceAll(userDetails, "\n", "<br>") + "</td>\n");
        writer.write("        <td>&nbsp;</td>\n");
        if (!customerDetails.isEmpty())
            writer.write("        <td>" + Tools.replaceAll(customerDetails, "\n", "<br>") + "</td>\n");
        writer.write("      </tr>\n");
        writer.write("    </table><br>\n\n");
    }

    private void writeHTMLProjectsTasks(OutputStreamWriter writer) throws IOException {
        writer.write("    <table>\n");
        writer.write("      <tr bgcolor=\"CCCCCC\">\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.PROJECT") + "</b></td>\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("INVOICE.HOURS") + "</b></td>\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("INVOICE.COST") + "</b></td>\n");
        writer.write("      </tr>\n");
        ProjectRow[] projectRows = getProjectRows();
        for (int i = 0; i < projectRows.length; i++) {
            writer.write("      <tr><td><b>" + projectRows[i].getProject() + "</b></td>");
            writer.write("<td>" + Tools.getTimeShort(projectRows[i].getDuration()) + "</td>");
            double cost = price * projectRows[i].getDuration() / (double) (1000*60*60);
            writer.write("<td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(cost) + " " + currency + "</td></tr>\n");
            writer.write("      <tr>\n");
            writer.write("        <td colspan=\"2\">\n");
            Iterator iterator = projectRows[i].getTasks().iterator();
            while (iterator.hasNext()) {
                String task = (String) iterator.next();
                writer.write("          <li>" + task + "</li>\n");
            }
            writer.write("        </td>\n");
            writer.write("        <td>&nbsp;</td>\n");
            writer.write("      </tr>\n");
            writer.write("      <tr><td colspan=\"3\">&nbsp;</td></tr>\n");
        }
        double totalCost = price * Tools.getTotalTime(false, true, days) / (double) (1000*60*60);
        double totalTax = tax * totalCost / (double) 100;
        double total = totalCost + totalTax;
        writer.write("      <tr><td colspan=\"2\">" + Translator.getTranslation("INVOICE.TOTAL_COST") + "</td><td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(totalCost) + " " + currency + "</td></tr>\n");
        writer.write("      <tr><td colspan=\"2\">" + Translator.getTranslation("INVOICE.TOTAL_TAX", new String[] {Double.toString(tax)}) + "</td><td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(totalTax) + " " + currency + "</td></tr>\n");
        writer.write("      <tr><td colspan=\"2\"><b>" + Translator.getTranslation("INVOICE.TOTAL") + "</b></td><td align=\"right\"><b>" + DecimalFormat.getCurrencyInstance().format(total) + " " + currency + "</b></td></tr>\n");
        writer.write("    </table><br>\n\n");
    }

    private void writeHTMLTasks(OutputStreamWriter writer) throws IOException {
        writer.write("    <table>\n");
        writer.write("      <tr bgcolor=\"CCCCCC\">\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("TASKS.DESCRIPTION") + "</b></td>\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("INVOICE.HOURS") + "</b></td>\n");
        writer.write("        <td align=\"center\"><b>" + Translator.getTranslation("INVOICE.COST") + "</b></td>\n");
        writer.write("      </tr>\n");
        TaskRow[] taskRows = getTaskRows();
        for (int i = 0; i < taskRows.length; i++) {
            writer.write("      <tr><td><i>" + taskRows[i].getTask() + "</i></td>");
            writer.write("<td>" + Tools.getTimeShort(taskRows[i].getDuration()) + "</td>");
            double cost = price * taskRows[i].getDuration() / (double) (1000*60*60);
            writer.write("<td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(cost) + " " + currency + "</td></tr>\n");
        }
        double totalCost = price * Tools.getTotalTime(false, true, days) / (double) (1000*60*60);
        double totalTax = tax * totalCost / (double) 100;
        double total = totalCost + totalTax;
        writer.write("      <tr><td colspan=\"3\">&nbsp;</td></tr>\n");
        writer.write("      <tr><td colspan=\"2\">" + Translator.getTranslation("INVOICE.TOTAL_COST") + "</td><td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(totalCost) + " " + currency + "</td></tr>\n");
        writer.write("      <tr><td colspan=\"2\">" + Translator.getTranslation("INVOICE.TOTAL_TAX", new String[] {Double.toString(tax)}) + "</td><td align=\"right\">" + DecimalFormat.getCurrencyInstance().format(totalTax) + " " + currency + "</td></tr>\n");
        writer.write("      <tr><td colspan=\"2\"><b>" + Translator.getTranslation("INVOICE.TOTAL") + "</b></td><td align=\"right\"><b>" + DecimalFormat.getCurrencyInstance().format(total) + " " + currency + "</b></td></tr>\n");
        writer.write("    </table><br>\n\n");
    }

    private void writeHTMLPaymentDetails(OutputStreamWriter writer) throws IOException {
        if (paymentDetails.isEmpty()) return;
        writer.write("    <table>\n");
        writer.write("      <tr>\n");
        writer.write("        <td bgcolor=\"BBBBFF\"><b>" + Translator.getTranslation("INVOICE.PAYMENT_DETAILS") + "</b></td>\n");
        writer.write("      </tr>\n");
        writer.write("      <tr>\n");
        writer.write("        <td>" + Tools.replaceAll(paymentDetails, "\n", "<br>") + "</td>\n");
        writer.write("      </tr>\n");
        writer.write("    </table>\n");
    }

    private void writeHTMLFooter(OutputStreamWriter writer) throws IOException {
        writer.write("\n");
        writer.write("    <hr/><u>" + Translator.getTranslation("REPORT.GENERATED_BY") + "</u> <a href=\"http://rachota.sourceforge.net/\">" + Tools.title + "</a> " + "(build " + Tools.build + ")<br/>\n");
        writer.write("    " + new Date() + "\n");
        writer.write("  </body>\n");
        writer.write("</html>");
    }

    private void writeTXTHeader(OutputStreamWriter writer) throws IOException {
        writer.write(Translator.getTranslation("INVOICE.TITLE") + "\n\n");
        writer.write(title + "\n");
        for (int i = 0; i < title.length(); i++) writer.write("=");
        writer.write("\n\n");
        String invoiceNumber = new SimpleDateFormat("yyMMdd-").format(new Date()) + Tools.getRandomID();
        writer.write(Translator.getTranslation("INVOICE.NUMBER") + " " + invoiceNumber + "\n");
        writer.write(Translator.getTranslation("INVOICE.DATE") + " " + new SimpleDateFormat("MMMM dd, yyyy").format(new Date()) + "\n");
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.add(Calendar.DAY_OF_YEAR, dueDays.intValue());
        writer.write(Translator.getTranslation("INVOICE.DUE") + " " + new SimpleDateFormat("MMMM dd, yyyy").format(dueDateCalendar.getTime()) + "\n\n");
    }

    private void writeTXTSubjects(OutputStreamWriter writer) throws IOException {
        if (!userDetails.isEmpty()) {
            writer.write(Translator.getTranslation("INVOICE.SUPPLIER") + "\n");
            for (int i = 0; i < Translator.getTranslation("INVOICE.SUPPLIER").length(); i++) writer.write("=");
            writer.write("\n" + userDetails + "\n\n");
        }
        if (!customerDetails.isEmpty()) {
            writer.write(Translator.getTranslation("INVOICE.CUSTOMER") + "\n");
            for (int i = 0; i < Translator.getTranslation("INVOICE.CUSTOMER").length(); i++) writer.write("=");
            writer.write("\n" + customerDetails + "\n\n");
        }
    }

    private void writeTXTProjectsTasks(OutputStreamWriter writer) throws IOException {
        writer.write(Translator.getTranslation("REPORT.PROJECTS") + "\n");
        for (int i = 0; i < Translator.getTranslation("REPORT.PROJECTS").length(); i++) writer.write("=");
        writer.write("\n");
        ProjectRow[] projectRows = getProjectRows();
        for (int i = 0; i < projectRows.length; i++) {
            writer.write(projectRows[i].getProject() + "   ");
            writer.write(Tools.getTimeShort(projectRows[i].getDuration()) + "   ");
            double cost = price * projectRows[i].getDuration() / (double) (1000*60*60);
            writer.write(DecimalFormat.getCurrencyInstance().format(cost) + " " + currency + "\n");
            Iterator iterator = projectRows[i].getTasks().iterator();
            while (iterator.hasNext()) {
                String task = (String) iterator.next();
                writer.write("* " + task + "\n");
            }
            writer.write("\n");
        }
        double totalCost = price * Tools.getTotalTime(false, true, days) / (double) (1000*60*60);
        double totalTax = tax * totalCost / (double) 100;
        double total = totalCost + totalTax;
        writer.write("\n" + Translator.getTranslation("INVOICE.TOTAL_COST") + " " + DecimalFormat.getCurrencyInstance().format(totalCost) + " " + currency + "\n");
        writer.write(Translator.getTranslation("INVOICE.TOTAL_TAX", new String[] {Double.toString(tax)}) + " " + DecimalFormat.getCurrencyInstance().format(totalTax) + " " + currency + "\n");
        writer.write(Translator.getTranslation("INVOICE.TOTAL") + " " + DecimalFormat.getCurrencyInstance().format(total) + " " + currency + "\n\n");
    }

    private void writeTXTPaymentDetails(OutputStreamWriter writer) throws IOException {
        if (paymentDetails.isEmpty()) return;
        writer.write(Translator.getTranslation("INVOICE.PAYMENT_DETAILS") + "\n");
        for (int i = 0; i < Translator.getTranslation("INVOICE.PAYMENT_DETAILS").length(); i++) writer.write("=");
        writer.write("\n" + paymentDetails + "\n");
    }

    private void writeTXTFooter(OutputStreamWriter writer) throws IOException {
        writer.write("---------------------------------------------------------------\n");
        writer.write(Translator.getTranslation("REPORT.GENERATED_BY") + " " + Tools.title + " (build " + Tools.build + ")\n");
        writer.write("http://rachota.sourceforge.net\n");
        writer.write("" + new Date());
    }

    private void writeTXTTasks(OutputStreamWriter writer) throws IOException {
        writer.write(Translator.getTranslation("REPORT.TASKS") + "\n");
        for (int i = 0; i < Translator.getTranslation("REPORT.TASKS").length(); i++) writer.write("=");
        writer.write("\n");
        TaskRow[] taskRows = getTaskRows();
        for (int i = 0; i < taskRows.length; i++) {
            writer.write(taskRows[i].getTask() + "   ");
            writer.write(Tools.getTimeShort(taskRows[i].getDuration()) + "   ");
            double cost = price * taskRows[i].getDuration() / (double) (1000*60*60);
            writer.write(DecimalFormat.getCurrencyInstance().format(cost) + " " + currency + "\n");
        }
        double totalCost = price * Tools.getTotalTime(false, true, days) / (double) (1000*60*60);
        double totalTax = tax * totalCost / (double) 100;
        double total = totalCost + totalTax;
        writer.write("\n" + Translator.getTranslation("INVOICE.TOTAL_COST") + "   " + DecimalFormat.getCurrencyInstance().format(totalCost) + " " + currency + "\n");
        writer.write(Translator.getTranslation("INVOICE.TOTAL_TAX", new String[] {Double.toString(tax)}) + "   " + DecimalFormat.getCurrencyInstance().format(totalTax) + " " + currency + "\n");
        writer.write(Translator.getTranslation("INVOICE.TOTAL") + "   " + DecimalFormat.getCurrencyInstance().format(total) + " " + currency + "\n\n");
    }

    /** Processes all days in selected period and their tasks and for each non-idle,
     * non-private (unless required) task calculates projects statistic (@see ProjectRow).
     * Finally, sorts all entries according to user's preference.
     * @return Sorted array of project rows to be included on the invoice.
     */
    private ProjectRow[] getProjectRows() {
        Hashtable projectRowsTable = new Hashtable();
        Iterator daysIterator = days.iterator();
        while (daysIterator.hasNext()) { // Process all days in selected period
            Day day = (Day) daysIterator.next();
            Vector filteredTasks = filterTasks(day.getTasks()); // Filter all tasks in a day
            Iterator tasksIterator = filteredTasks.iterator();
            while (tasksIterator.hasNext()) { // Process all filtered tasks in every day
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
                            ProjectRow projectRow = new ProjectRow(project, task, ReportWizard.SORTBY_DURATION);
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

    /** Processes all days in selected period and their tasks and for each non-idle,
     * non-private (unless required) task calculates its statistic (@see TaskRow).
     * Finally, sorts all entries according to user's preference.
     * @return Sorted array of task rows to be included on the invoice.
     */
    private TaskRow[] getTaskRows() {
        Hashtable taskRowsTable = new Hashtable();
        Iterator daysIterator = days.iterator();
        while (daysIterator.hasNext()) { // Process all days in selected period
            Day day = (Day) daysIterator.next();
            Vector filteredTasks = filterTasks(day.getTasks()); // Filter all tasks in a day
            Iterator tasksIterator = filteredTasks.iterator();
            while (tasksIterator.hasNext()) { // Process all filtered tasks in every day
                Task task = (Task) tasksIterator.next();
                if (task.isIdleTask()) continue;
                boolean includePrivateTasks = ((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue();
                if (task.privateTask() & !includePrivateTasks) continue;
                if (taskRowsTable.containsKey(task.getDescription())) { // If task with same name was already found
                    TaskRow taskRow = (TaskRow) taskRowsTable.get(task.getDescription());
                    taskRow.includeTask(task);
                } else { // If this is the first occurence of a task
                    TaskRow taskRow = new TaskRow(task, ReportWizard.SORTBY_DURATION);
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

    /** Comparable object representing one project in the summary on invoice. It has several properties
     * like name, tasks that belonged to the project and total duration of all owned tasks.
     */
    class ProjectRow implements Comparable {

        String project;
        Vector tasks;
        long duration;
        String sortBy;

        ProjectRow(String project, Task task, String sortBy) {
            this.project = project;
            tasks = new Vector();
            tasks.add(task.getDescription());
            duration = task.getDuration();
            this.sortBy = sortBy;
        }

        void includeTask(Task task) {
            String taskDescription = task.getDescription();
            if (!tasks.contains(taskDescription))
                tasks.add(taskDescription);
            duration = duration + task.getDuration();
        }

        String getProject() {
            return project;
        }

        Vector getTasks() {
            return tasks;
        }

        long getDuration() {
            return duration;
        }

        @Override
        public int compareTo(Object object) {
            if (object == null) return 0;
            ProjectRow projectRow = (ProjectRow) object;
            if (sortBy.equals(ReportWizard.SORTBY_PROJECTS_TASKS))
                return project.compareTo(projectRow.getProject());
            if (duration < projectRow.getDuration()) return 1;
            return -1;
        }
    }

    /** Comparable object representing one task in the summary on invoice. It has
     *  its name and total duration of all tasks with the same name.
     */
    class TaskRow implements Comparable {

        String task;
        long duration;
        String sortBy;

        TaskRow(Task task, String sortBy) {
            this.task = task.getDescription();
            duration = task.getDuration();
            this.sortBy = sortBy;
        }

        void includeTask(Task task) {
            duration = duration + task.getDuration();
        }

        String getTask() {
            return task;
        }

        long getDuration() {
            return duration;
        }

        @Override
        public int compareTo(Object object) {
            if (object == null) return 0;
            TaskRow taskRow = (TaskRow) object;
            if (sortBy.equals(ReportWizard.SORTBY_PROJECTS_TASKS))
                return task.compareTo(taskRow.getTask());
            if (duration < taskRow.getDuration()) return 1;
            return -1;
        }
    }
}