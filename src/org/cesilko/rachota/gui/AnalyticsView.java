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
 * Portions created by Jiri Kovalsky are Copyright (C) 2007
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.RegularTask;
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

    /** Counts how much user is effective in using his working hours.
     * More idle time means less effectivity.
     */
    private void updateEffectivity() {
        float totalTime = getTotalTimeUser();
        float privateTime = getPrivateTimeUser();
        float idleTime = getIdleTimeUser();
        effectivity = totalTime * 5 / (idleTime + privateTime + totalTime);
        lbEffectivityResult.setIcon(getIcon(Math.round(effectivity)));
        lbEffectivityResult.setToolTipText("" + effectivity);
    }

    /** Counts usage of categories across tasks.
     * Does user specify categories for tasks enough?
     */
    private void updateCategorization() {
        Iterator days = Plan.getDefault().getDays(scale);
        float numberOfTasks = 0;
        float numberOfCategories = 0;
        while (days.hasNext()) {
            Day day = (Day) days.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                numberOfTasks++;
                String keyword = task.getKeyword();
                if (keyword != null)
                    if (!keyword.equals(""))
                        numberOfCategories++;
            }
        }
        float categorizationRatio = numberOfCategories / numberOfTasks;
        categorization = categorizationRatio * 10 / 2;
        lbCategorizationResult.setIcon(getIcon(Math.round(categorization)));
        lbCategorizationResult.setToolTipText("" + categorization);
    }

    /** Counts distribution of tasks durations in selected shares intervals.
     * Does user create too many or too little tasks?
     */
    private void updateGranularity() {
        int tasks_80 = 0;
        int tasks_40 = 0;
        int tasks_20 = 0;
        int tasks_10 = 0;
        int tasks_4 = 0;
        int tasks_2 = 0;
        int tasks_1 = 0;
        int allTasks = 0;
        Iterator days = Plan.getDefault().getDays(scale);
        while (days.hasNext()) {
            Day day = (Day) days.next();
            long totalDayTime = day.getTotalTime(false);
            if (totalDayTime == 0) continue;
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) continue;
                int timeShare = (int) (task.getDuration() * 100 / totalDayTime);
                if (timeShare >= 80) tasks_80++;
                else if (timeShare >= 40) tasks_40++;
                else if (timeShare >= 20) tasks_20++;
                else if (timeShare >= 10) tasks_10++;
                else if (timeShare >= 4) tasks_4++;
                else if (timeShare >= 2) tasks_2++;
                else tasks_1++;
                allTasks++;
            }
        }
        tasks_80 = tasks_80 * 100 / allTasks;
        tasks_40 = tasks_40 * 100 / allTasks;
        tasks_20 = tasks_20 * 100 / allTasks;
        tasks_10 = tasks_10 * 100 / allTasks;
        tasks_4 = tasks_4 * 100 / allTasks;
        tasks_2 = tasks_2 * 100 / allTasks;
        tasks_1 = tasks_1 * 100 / allTasks;
        
        int totalError = Math.abs(tasks_80 - 1);
        totalError = totalError + Math.abs(tasks_40 - 5);
        totalError = totalError + Math.abs(tasks_20 - 16);
        totalError = totalError + Math.abs(tasks_10 - 26);
        totalError = totalError + Math.abs(tasks_4 - 30);
        totalError = totalError + Math.abs(tasks_2 - 17);
        totalError = totalError + Math.abs(tasks_1 - 5);
        
        granularity = (100f - totalError) / 20;
        lbGranularityResult.setToolTipText("" + granularity);
        lbGranularityResult.setIcon(getIcon(Math.round(granularity)));
    }

    private void updatePrioritization() {
        int highPriority = 0;
        int middlePriority = 0;
        int lowPriority = 0;
        int allTasks = 0;
        Iterator days = Plan.getDefault().getDays(scale);
        while (days.hasNext()) {
            Day day = (Day) days.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) continue;
                int taskPriority = task.getPriority();
                if (taskPriority == Task.PRIORITY_HIGH) highPriority++;
                else if (taskPriority == Task.PRIORITY_MEDIUM) middlePriority++;
                else lowPriority++;
                allTasks++;
            }
        }
        highPriority = highPriority * 100 / allTasks;
        middlePriority = middlePriority * 100 / allTasks;
        lowPriority = lowPriority * 100 / allTasks;
        
        int totalError = Math.abs(highPriority - 30);
        totalError = totalError + Math.abs(middlePriority - 50);
        totalError = totalError + Math.abs(lowPriority - 20);
        
        prioritization = (100f - totalError) / 20;
        lbPrioritizationResult.setToolTipText("" + prioritization);
        lbPrioritizationResult.setIcon(getIcon(Math.round(prioritization)));
    }

    private void updateRepetition() {
        int allDays = 0;
        int allTasks = 0;
        Hashtable taskCounts = new Hashtable();
        Iterator days = Plan.getDefault().getDays(scale);
        while (days.hasNext()) {
            Day day = (Day) days.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) continue;
                allTasks++;
                String description = task.getDescription();
                if (taskCounts.containsKey(description)) {
                    Integer count = (Integer) taskCounts.get(description);
                    taskCounts.put(description, new Integer(count.intValue() + 1));
                } else taskCounts.put(description, new Integer(1));
            }
            if (day.getTotalTime(true) != 0) allDays++;
        }
        int missingRegularTasks = 0;
        Enumeration enumeration = taskCounts.keys();
        Vector regularTasks = Plan.getDefault().getRegularTasks();
        while (enumeration.hasMoreElements()) {
            String description = (String) enumeration.nextElement();
            Integer count = (Integer) taskCounts.get(description);
            int taskRepetition = count * 100 / allDays;
            if (taskRepetition > 50) {
                boolean isRegular = false;
                Iterator iterator = regularTasks.iterator();
                while (iterator.hasNext()) {
                    RegularTask regularTask = (RegularTask) iterator.next();
                    if (regularTask.getDescription().equals(description)) {
                        isRegular = true;
                        break;
                    }
                }
                if (!isRegular) missingRegularTasks++;
            }
        }
        int uselessRegularTasks = 0;
        Iterator iterator = regularTasks.iterator();
        while (iterator.hasNext()) {
            RegularTask regularTask = (RegularTask) iterator.next();
            if (!taskCounts.containsKey(regularTask.getDescription())) uselessRegularTasks++;
            else {
                Integer count = (Integer) taskCounts.get(regularTask.getDescription());
                int taskRepetition = count * 100 / allDays;
                if (regularTask.getFrequency() == RegularTask.FREQUENCY_DAILY)
                    if (taskRepetition < 60) uselessRegularTasks++;
                if (regularTask.getFrequency() == RegularTask.FREQUENCY_WORKDAY)
                    if (taskRepetition < 40) uselessRegularTasks++;
            }
        }
        int totalError = (missingRegularTasks * 100 / (allTasks / allDays) + (uselessRegularTasks * 100 / regularTasks.size())) / 2;
        repetition = (100f - totalError) / 20;
        lbRepetitionResult.setToolTipText("" + repetition);
        lbRepetitionResult.setIcon(getIcon(Math.round(repetition)));
    }

    private void updateStatusing() {
        int doneStatus = 0;
        int startedStatus = 0;
        int allTasks = 0;
        Iterator days = Plan.getDefault().getDays(scale);
        while (days.hasNext()) {
            Day day = (Day) days.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) continue;
                int taskState = task.getState();
                if (taskState == Task.STATE_DONE) doneStatus++;
                else startedStatus++;
                allTasks++;
            }
        }
        doneStatus = doneStatus * 100 / allTasks;
        startedStatus = startedStatus * 100 / allTasks;
        int totalError = Math.abs(doneStatus - 40);
        totalError = totalError + Math.abs(startedStatus - 60);
        
        statusing = (100f - totalError) / 20;
        lbStatusingResult.setToolTipText("" + statusing);
        lbStatusingResult.setIcon(getIcon(Math.round(statusing)));
    }

    private long getIdleTimeAll() {
        if (usageTimesAll == null) return 0;
        int firstIndex = usageTimesAll.indexOf("|");
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(firstIndex + 1, lastIndex));
    }

    private long getPrivateTimeAll() {
        if (usageTimesAll == null) return 0;
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(lastIndex + 1));
    }

    private long getTotalTimeAll() {
        if (usageTimesAll == null) return 0;
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
                        usageTimesAll = "";
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
                    // usageTimesAll = "81670875|51336579|2496951"; // Based on 141 usage reports
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

    private void countUserTimes() {
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
        scale = SCALE_WHOLE_TIME;
        countUserTimes();
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
        updateAnalysis();
    }//GEN-LAST:event_rbAllTimeActionPerformed

    private void rbWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWeekActionPerformed
        rbAllTime.setSelected(false);
        rbWeek.setSelected(true);
        scale = SCALE_PAST_WEEK;
        countUserTimes();
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
        updateAnalysis();
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
    /** Selected time scale */
    private int scale = SCALE_PAST_WEEK;
    /** Usage times downloaded from Rachota Analytics server in format: totalTime|idleTime|privateTime */
    private String usageTimesAll;
    /** Usage times calculated for user in format: totalTime|idleTime|privateTime */
    private String usageTimesUser;
    /** Comparison chart showing average weekly usage times */
    private ComparisonChart comparisonChart;
    /** How much user is effective in using his working hours. More idle time means less effectivity. */
    private float effectivity;
    /** How much time an average task take. Does user create too many or too little tasks? */
    private float granularity;
    /** Distribution of priorities across tasks. Does user utilize task priorities enough? */
    private float prioritization;
    /** Usage of categories across tasks. Does user specify categories for tasks enough? */
    private float categorization;
    /** Usage of all statuses. Does user close tasks or leaves them open forever? */
    private float statusing;
    /** Clever usage of regular tasks. How many irregular tasks do actually repeat often? */
    private float repetition;
    
    public void updateChart() {
        countUserTimes();
        comparisonChart.setTimes(getTotalTimeUser(), 0, getIdleTimeUser(), 0, getPrivateTimeUser(), 0);
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
        if (usageTimesAll.equals("")) return;
        int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        String reportedWeek = (String) Settings.getDefault().getSetting("rachota.reported.week");
        if (reportedWeek == null) return;
        int week = Integer.parseInt(reportedWeek);
        if (week != currentWeek) return;
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("settings")) updateChart();
        repaint();
    }

    private void updateAnalysis() {
        updateCategorization();
        updateEffectivity();
        updateGranularity();
        updatePrioritization();
        updateRepetition();
        updateStatusing();
    }
    
    private javax.swing.ImageIcon getIcon(int ranking) {
        return new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/ranking_" + ranking + ".png"));
    }
}