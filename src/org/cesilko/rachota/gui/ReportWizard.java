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
 * Created on Mar 18, 2010, 9:21:50 PM
 * ReportWizard.java
 */
package org.cesilko.rachota.gui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Vector;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.AbstractTaskFilter;

/** Wizard for generation of task reports or invoices. */
public class ReportWizard extends GenericWizard {

    /** Wizard is generating report. */
    static final String TYPE_REPORT = "report";
    /** Wizard is generating invoice. */
    static final String TYPE_INVOICE = "invoice";
    /** Report table rows represent tasks. */
    static final String REPORT_ROWS_TASKS = "tasks";
    /** Report table rows represent projects. */
    static final String REPORT_ROWS_PROJECTS = "projects";
    /** HTML output format. */
    static final String OUTPUT_HTML = ".html";
    /** TXT output format. */
    static final String OUTPUT_TXT = ".txt";
    /** CSV output format. */
    static final String OUTPUT_CSV = ".csv";
    /** Invoice will list tasks grouped in projects. */
    static final String INVOICE_TASKS_PROJECTS = "projects_tasks";
    /** Invoice will only list tasks. */
    static final String INVOICE_TASKS = "tasks";
    /** Sort rows by duration. */
    static final String SORTBY_DURATION = "duration";
    /** Sort rows by projects/tasks. */
    static final String SORTBY_PROJECTS_TASKS = "projects/tasks";
    /** Sort rows by occurrences. */
    static final String SORTBY_OCCURRENCES = "occurrences";
    /** Sort rows by notes. */
    static final String SORTBY_NOTES = "notes";
    /** Vector of days with tasks to generate report from. */
    private Vector days;
    /** History chart to be included in case user selects HTML type and wants it. */
    private HistoryChart chart;
    /** Filter used to highlight tasks in times chart. */
    private AbstractTaskFilter highlightFilter;
    /** List of filters used to sort out some tasks. */
    private Vector selectFilters;

    /** Constructor or report generation wizard. */
    public ReportWizard(Vector days, HistoryChart chart, AbstractTaskFilter highlightFilter, Vector selectFilters) {
        super(Translator.getTranslation("REPORTWIZARD.TITLE"));
        this.days = days;
        this.chart = chart;
        this.highlightFilter = highlightFilter;
        this.selectFilters = selectFilters;
        ReportOutputWizardStep reportOutputWizardStep = new ReportOutputWizardStep(this);
        ReportContentWizardStep reportContentWizardStep = new ReportContentWizardStep(this);

        reportOutputWizardStep.addPropertyChangeListener(reportContentWizardStep);

        setPreview(TYPE_REPORT);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    /** Method called when property of some wizard step changed.
     * @param evt Event that generated this call.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object property = evt.getNewValue();
        setWizardProperty(propertyName, property);
        if (propertyName.equals("report.output.file_selected")) checkButtons();
        if (propertyName.equals("invoice.currency")) checkButtons();
        if (propertyName.equals("invoice.tax")) checkButtons();
        if (propertyName.equals("invoice.price")) checkButtons();
        if (propertyName.equals("report.type")) setPreview((String) property);
        if (propertyName.equals("status.error")) setStatus((String) property);
        if (propertyName.equals("wizard.step.next")) {
            if (property instanceof ReportOutputWizardStep) goNextStep();
            else finishWizard();
        }
        if (propertyName.equals("wizard.cancel")) cancelWizard();
    }

    /** Sets wizard preview to the correct image.
     * @param previewType Type of preview to be shown.
     */
    private void setPreview(String previewType) {
        if (TYPE_REPORT.equals(previewType)) setPreview(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/report_preview.png")));
        else setPreview(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/invoice_preview.png")));
    }

    /** Method called when Finish button is pressed. */
    @Override
    public void finishWizard() {
        setVisible(false);
        File outputFile = (File) getWizardProperty("report.output.file_selected");
        String reportTitle = (String) getWizardProperty("report.title");
        Boolean showChart = (Boolean) getWizardProperty("report.chart");
        Boolean showFilters = (Boolean) getWizardProperty("report.filters");
        String rowsRepresent = (String) getWizardProperty("report.rows");
        Boolean includeDuration = (Boolean) getWizardProperty("report.content.duration");
        Boolean includeProjectsTasks = (Boolean) getWizardProperty("report.content.projects_tasks");
        Boolean includeOccurrences = (Boolean) getWizardProperty("report.content.occurrences");
        Boolean includeNotes = (Boolean) getWizardProperty("report.content.notes");
        String sortBy = (String) getWizardProperty("report.sortby");
        ReportGenerator reportGenerator = new ReportGenerator(outputFile, reportTitle, showChart, showFilters, rowsRepresent, includeDuration, includeProjectsTasks, includeOccurrences, includeNotes, sortBy, days, chart, highlightFilter, selectFilters);
        reportGenerator.generateReport();
    }
}