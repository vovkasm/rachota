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
 * Created on 05 October 2007  20:30
 * AnalyticsView.java
 */
package org.cesilko.rachota.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;
import org.cesilko.rachota.core.filters.*;

/** Panel providing comparison of current user's weekly times with
 * other Rachota users and analytics summary and recommendations.
 * @author Jiri Kovalsky
 */
public class AnalyticsView extends javax.swing.JPanel  implements PropertyChangeListener {

    /** Creates new HistoryView panel charts and table. */
    public AnalyticsView() {
        initComponents();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        comparisonChart = new ComparisonChart();
        pnChart.add(comparisonChart, gridBagConstraints);
        updateChart();
        updateAnalysis();
    }

    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return new java.awt.Font((String) Settings.getDefault().getSetting("fontName"), java.awt.Font.PLAIN, Integer.parseInt((String) Settings.getDefault().getSetting("fontSize")));
    }

    private int getCategorization() {
        return 0;
    }

    private int getEffectivity() {
        return 0;
    }

    private int getGranularity() {
        return 0;
    }

    private int getPrioritization() {
        return 0;
    }

    private int getRepetition() {
        return 0;
    }

    private int getStatusing() {
        return 0;
    }

    private long getIdleTimeAll() {
        int firstIndex = usageTimesAll.indexOf("|");
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(firstIndex + 1, lastIndex));
    }

    private long getPrivateTimeAll() {
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(lastIndex + 1));
    }

    private long getTotalTimeAll() {
        int index = usageTimesAll.indexOf("|");
        return Long.parseLong(usageTimesAll.substring(0, index));
    }

    private long getIdleTimeUser() {
        int firstIndex = usageTimesUser.indexOf("|");
        int lastIndex = usageTimesUser.lastIndexOf("|");
        return Long.parseLong(usageTimesUser.substring(firstIndex + 1, lastIndex));
    }

    private long getPrivateTimeUser() {
        int lastIndex = usageTimesUser.lastIndexOf("|");
        return Long.parseLong(usageTimesUser.substring(lastIndex + 1));
    }

    private long getTotalTimeUser() {
        int index = usageTimesUser.indexOf("|");
        return Long.parseLong(usageTimesUser.substring(0, index));
    }

    private boolean downloadTimesAll() {
        int scale = SCALE_PAST_WEEK;
        if (rbAllTime.isSelected()) scale = SCALE_WHOLE_TIME;
        String RID = Tools.getRID();
        try { RID = URLEncoder.encode(RID, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            System.out.println("Error: Can't build URL to Rachota Analytics server.");
            e.printStackTrace();
        }
        final String url_string = "http://rachota.sourceforge.net/getUsageTimes.php?scale=" + scale + "&rid=" + RID;
        usageTimesAll = "";
        final Thread connectionThread = new Thread("Rachota Analytics Download Times") {
            public void run() {
                try {
                    URL url = new URL(url_string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    while (true) {
                        int character = inputStream.read();
                        if (character == -1) break;
                        usageTimesAll = usageTimesAll + (char) character;
                    }
                    if (usageTimesAll.indexOf("Access denied.") != -1) {
                        usageTimesAll = null;
                    } else {
                        String start = "data: <b>";
                        String end = "</b><br>Records: ";
                        int indexStart = usageTimesAll.indexOf(start) + start.length();
                        int indexEnd = usageTimesAll.indexOf(end);
                        usageTimesAll = usageTimesAll.substring(indexStart, indexEnd);
                    }
                    connection.disconnect();
                }
                catch (Exception e) {
                    System.out.println("Error: Can't connect to Rachota Analytics server.");
                    usageTimesAll = null;
                    // usageTimesAll = "72443470|31442794|5487976";
                }
            }};
        connectionThread.start();
        new Thread("Rachota Analytics Download Times killer") {
            public void run() {
                try { sleep(30000); } catch (InterruptedException e) {}
                if (connectionThread.isAlive()) {
                    System.out.println("Error: Giving up...");
                    connectionThread.interrupt();
                }
            }
        }.start();
        while (connectionThread.isAlive()) {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
        return (usageTimesAll != null);
    }

    private void countUserTimes(int scale) {
        long totalTimeUser = 0;
        long idleTimeUser = 0;
        long privateTimeUser = 0;
        int numberOfDays = 0;
        Iterator days = Plan.getDefault().getDays(scale);
        while (days.hasNext()) {
            Day day = (Day) days.next();
            numberOfDays++;
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) idleTimeUser = idleTimeUser + task.getDuration();
                else if (task.privateTask()) privateTimeUser = privateTimeUser + task.getDuration();
                else totalTimeUser = totalTimeUser + task.getDuration();
            }
        }
        if (scale == SCALE_WHOLE_TIME) {
            int numberOfWeeks = numberOfDays / 7;
            if (numberOfWeeks != 0) {
                totalTimeUser = totalTimeUser / numberOfWeeks;
                idleTimeUser = idleTimeUser / numberOfWeeks;
                privateTimeUser = privateTimeUser / numberOfWeeks;
            }
        }
        usageTimesUser = "" + totalTimeUser + "|" + idleTimeUser + "|" + privateTimeUser;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnTimeUsage = new javax.swing.JPanel();
        pnChart = new javax.swing.JPanel();
        rbWeek = new javax.swing.JRadioButton();
        rbAllTime = new javax.swing.JRadioButton();
        pnAnalysis = new javax.swing.JPanel();
        lbEffectivity = new javax.swing.JLabel();
        lbEffectivityResult = new javax.swing.JLabel();
        lbGranularity = new javax.swing.JLabel();
        lbGranularityResult = new javax.swing.JLabel();
        lbPrioritization = new javax.swing.JLabel();
        lbPrioritizationResult = new javax.swing.JLabel();
        lbCategorization = new javax.swing.JLabel();
        lbCategorizationResult = new javax.swing.JLabel();
        lbStatusing = new javax.swing.JLabel();
        lbStatusingResult = new javax.swing.JLabel();
        lbRepetition = new javax.swing.JLabel();
        lbRepetitionResult = new javax.swing.JLabel();
        pnSuggestions = new javax.swing.JPanel();
        spSuggestions = new javax.swing.JScrollPane();
        tpSuggestions = new javax.swing.JTextPane();

        setName(Translator.getTranslation("ANALYTICSVIEW.TB_NAME"));
        setLayout(new java.awt.GridBagLayout());

        pnTimeUsage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Translator.getTranslation("ANALYTICSVIEW.PN_COMPARISON"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnTimeUsage.setLayout(new java.awt.GridBagLayout());

        pnChart.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimeUsage.add(pnChart, gridBagConstraints);

        rbWeek.setFont(getFont());
        rbWeek.setMnemonic(Translator.getMnemonic("ANALYTICSVIEW.RB_WEEK"));
        rbWeek.setSelected(true);
        rbWeek.setText(Translator.getTranslation("ANALYTICSVIEW.RB_WEEK"));
        rbWeek.setToolTipText(Translator.getTranslation("ANALYTICSVIEW.RB_WEEK_TOOLTIP"));
        rbWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbWeekActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimeUsage.add(rbWeek, gridBagConstraints);

        rbAllTime.setFont(getFont());
        rbAllTime.setMnemonic(Translator.getMnemonic("ANALYTICSVIEW.RB_ALLTIME"));
        rbAllTime.setText(Translator.getTranslation("ANALYTICSVIEW.RB_ALLTIME"));
        rbAllTime.setToolTipText(Translator.getTranslation("ANALYTICSVIEW.RB_ALLTIME_TOOLTIP"));
        rbAllTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAllTimeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimeUsage.add(rbAllTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnTimeUsage, gridBagConstraints);

        pnAnalysis.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Translator.getTranslation("ANALYTICSVIEW.PN_ANALYSIS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnAnalysis.setLayout(new java.awt.GridBagLayout());

        lbEffectivity.setFont(getFont());
        lbEffectivity.setText(Translator.getTranslation("ANALYTICSVIEW.LB_EFFECTIVITY"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbEffectivity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbEffectivityResult, gridBagConstraints);

        lbGranularity.setFont(getFont());
        lbGranularity.setText(Translator.getTranslation("ANALYTICSVIEW.LB_GRANULARITY"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbGranularity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbGranularityResult, gridBagConstraints);

        lbPrioritization.setFont(getFont());
        lbPrioritization.setText(Translator.getTranslation("ANALYTICSVIEW.LB_PRIORITIZATION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbPrioritization, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbPrioritizationResult, gridBagConstraints);

        lbCategorization.setFont(getFont());
        lbCategorization.setText(Translator.getTranslation("ANALYTICSVIEW.LB_CATEGORIZATION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbCategorization, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbCategorizationResult, gridBagConstraints);

        lbStatusing.setFont(getFont());
        lbStatusing.setText(Translator.getTranslation("ANALYTICSVIEW.LB_STATUSING"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbStatusing, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbStatusingResult, gridBagConstraints);

        lbRepetition.setFont(getFont());
        lbRepetition.setText(Translator.getTranslation("ANALYTICSVIEW.LB_REPETITION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbRepetition, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnAnalysis.add(lbRepetitionResult, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnAnalysis, gridBagConstraints);

        pnSuggestions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Translator.getTranslation("ANALYTICSVIEW.PN_SUGGESTIONS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnSuggestions.setLayout(new java.awt.GridBagLayout());

        tpSuggestions.setEditable(false);
        spSuggestions.setViewportView(tpSuggestions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnSuggestions.add(spSuggestions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnSuggestions, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void rbAllTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAllTimeActionPerformed
        rbAllTime.setSelected(true);
        rbWeek.setSelected(false);
        countUserTimes(SCALE_WHOLE_TIME);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
    }//GEN-LAST:event_rbAllTimeActionPerformed

    private void rbWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWeekActionPerformed
        rbAllTime.setSelected(false);
        rbWeek.setSelected(true);
        countUserTimes(SCALE_PAST_WEEK);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
    }//GEN-LAST:event_rbWeekActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbCategorization;
    private javax.swing.JLabel lbCategorizationResult;
    private javax.swing.JLabel lbEffectivity;
    private javax.swing.JLabel lbEffectivityResult;
    private javax.swing.JLabel lbGranularity;
    private javax.swing.JLabel lbGranularityResult;
    private javax.swing.JLabel lbPrioritization;
    private javax.swing.JLabel lbPrioritizationResult;
    private javax.swing.JLabel lbRepetition;
    private javax.swing.JLabel lbRepetitionResult;
    private javax.swing.JLabel lbStatusing;
    private javax.swing.JLabel lbStatusingResult;
    private javax.swing.JPanel pnAnalysis;
    private javax.swing.JPanel pnChart;
    private javax.swing.JPanel pnSuggestions;
    private javax.swing.JPanel pnTimeUsage;
    private javax.swing.JRadioButton rbAllTime;
    private javax.swing.JRadioButton rbWeek;
    private javax.swing.JScrollPane spSuggestions;
    private javax.swing.JTextPane tpSuggestions;
    // End of variables declaration//GEN-END:variables

    /** Index of past week scale */
    public static final int SCALE_PAST_WEEK = 0;
    /** Index of whole time scale */
    public static final int SCALE_WHOLE_TIME = 1;
    /** Usage times downloaded from Rachota Analytics server in format: totalTime|idleTime|privateTime */
    private String usageTimesAll;
    /** Usage times calculated for user in format: totalTime|idleTime|privateTime */
    private String usageTimesUser;
    /** Comparison chart showing average weekly usage times */
    private ComparisonChart comparisonChart;
    /** How much user is effective in using his working hours. More idle time means less effectivity. */
    private int effectivity;
    /** How much time an average task take. Does user create too many or too little tasks? */
    private int granularity;
    /** Distribution of priorities across tasks. Does user utilize task priorities enough? */
    private int prioritization;
    /** Usage of categories across tasks. Does user specify categories for tasks enough? */
    private int categorization;
    /** Usage of all statuses. Does user close tasks or leaves them open forever? */
    private int statusing;
    /** Clever usage of regular tasks. How many irregular tasks do actually repeat often? */
    private int repetition;
    
    public void updateChart() {
        Boolean reportActivity = (Boolean) Settings.getDefault().getSetting("reportActivity");
        if (!reportActivity.booleanValue()) {
            comparisonChart.setMessage(Translator.getTranslation("ANALYTICSVIEW.NO_REPORT"), Translator.getTranslation("ANALYTICSVIEW.NO_REPORT_HINT"));
            return;
        }
        boolean timesDownloaded = downloadTimesAll();
        if (!timesDownloaded) {
            comparisonChart.setMessage(Translator.getTranslation("ANALYTICSVIEW.NO_CONNECTION"), Translator.getTranslation("ANALYTICSVIEW.NO_CONNECTION_HINT"));
            return;
        }
        comparisonChart.setMessage(Translator.getTranslation("ANALYTICSVIEW.NO_REPORT_THIS_WEEK"), Translator.getTranslation("ANALYTICSVIEW.NO_REPORT_THIS_WEEK_HINT"));
        int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        String reportedWeek = (String) Settings.getDefault().getSetting("rachota.reported.week");
        if (reportedWeek == null) return;
        int week = Integer.parseInt(reportedWeek);
        if (week != currentWeek) return;
        countUserTimes(SCALE_PAST_WEEK);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("settings")) updateChart();
        repaint();
    }

    private void updateAnalysis() {
        categorization = getCategorization();
        effectivity = getEffectivity();
        granularity = getGranularity();
        prioritization = getPrioritization();
        repetition = getRepetition();
        statusing = getStatusing();
        lbCategorizationResult.setIcon(getIcon(categorization));
        lbEffectivityResult.setIcon(getIcon(effectivity));
        lbGranularityResult.setIcon(getIcon(granularity));
        lbPrioritizationResult.setIcon(getIcon(prioritization));
        lbRepetitionResult.setIcon(getIcon(repetition));
        lbStatusingResult.setIcon(getIcon(statusing));
    }
    
    private javax.swing.ImageIcon getIcon(int ranking) {
        return new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/ranking_" + ranking + ".png"));
    }
}