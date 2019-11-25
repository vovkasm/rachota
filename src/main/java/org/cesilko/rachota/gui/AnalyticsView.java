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
        countUserTimes(SCALE_PAST_WEEK);
        updateChart();
        updateAnalysis(SCALE_PAST_WEEK);
        updateSuggestions();
    }

    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return Tools.getFont();
    }

    /** Counts how much user is effective in using his/her working hours in given
     * period. More idle time means less effectivity.
     * @param scale One of three possible time scales.
     * @return Computed effectivity factor.
     */
    private float countEffectivity(int scale) {
        countUserTimes(scale);
        float totalTime = getTotalTimeUser();
        float privateTime = getPrivateTimeUser();
        float idleTime = getIdleTimeUser();
        if (totalTime != 0) effectivity = totalTime * 5 / (idleTime + privateTime + totalTime);
        else effectivity = 0;
        return Math.round(effectivity*10)/10f;
    }
    
    /** Counts effectivity factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updateEffectivity(int scale) {
        countEffectivity(scale);
        lbEffectivityResult.setIcon(getIcon(Math.round(effectivity)));
        lbEffectivityResult.setToolTipText("" + effectivity);
    }

    /** Counts usage of categories across tasks in given period.
     * Does user specify categories for tasks enough?
     * @param scale One of three possible time scales.
     * @return Computed categorization factor.
     */
    private float countCategorization(int scale) {
        Iterator days = Plan.getDefault().getDays(scale);
        float numberOfTasks = 0;
        float numberOfCategories = 0;
        while (days.hasNext()) {
            Day day = (Day) days.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) continue;
                numberOfTasks++;
                String keyword = task.getKeyword();
                if (keyword != null)
                    if (!keyword.equals(""))
                        numberOfCategories++;
            }
        }
        if (numberOfTasks != 0) {
            float categorizationRatio = numberOfCategories / numberOfTasks;
            categorization = categorizationRatio * 10 / 2;
        } else categorization = 0;
        return Math.round(categorization*10)/10f;
    }

    /** Counts categorization factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updateCategorization(int scale) {
        countCategorization(scale);
        lbCategorizationResult.setIcon(getIcon(Math.round(categorization)));
        lbCategorizationResult.setToolTipText("" + categorization);
    }
    
    /** In given period counts distribution of tasks durations in selected shares
     * intervals and compares it to normal distribution. Does user create too many
     * or too little tasks?
     * @param scale One of three possible time scales.
     * @return Computed granularity factor.
     */
    private float countGranularity(int scale) {
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
            long totalDayTime = day.getTotalTime(true);
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
        if (allTasks != 0) {
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
            if (totalError > 100) totalError = 100;

            granularity = (100f - totalError) / 20;
        } else granularity = 0;
        return Math.round(granularity*10)/10f;
    }
        
    /** Counts granularity factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updateGranularity(int scale) {
        countGranularity(scale);
        lbGranularityResult.setToolTipText("" + granularity);
        lbGranularityResult.setIcon(getIcon(Math.round(granularity)));
    }

    /** Counts distribution of priorities across tasks in given period. Does user
     * utilize task priorities enough?
     * @param scale One of three possible time scales.
     * @return Computed prioritization factor.
     */
    private float countPrioritization(int scale) {
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
        if (allTasks != 0) {
            highPriority = highPriority * 100 / allTasks;
            middlePriority = middlePriority * 100 / allTasks;
            lowPriority = lowPriority * 100 / allTasks;

            int totalError = Math.abs(highPriority - 30);
            totalError = totalError + Math.abs(middlePriority - 50);
            totalError = totalError + Math.abs(lowPriority - 20);

            prioritization = (100f - totalError) / 20;
        } else prioritization = 0;
        return Math.round(prioritization*10)/10f;
    }
    
    /** Counts prioritization factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updatePrioritization(int scale) {
        countPrioritization(scale);
        lbPrioritizationResult.setToolTipText("" + prioritization);
        lbPrioritizationResult.setIcon(getIcon(Math.round(prioritization)));
    }

    /** Verifies clever usage of regular tasks in given period. How many irregular
     * tasks do actually repeat often? And how many regular tasks are useless?
     * @param scale One of three possible time scales.
     * @return Computed repetition factor.
     */
    private float countRepetition(int scale) {
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
        int uselessRegularTasks = 0;
        int totalError = 0;
        Vector regularTasks = Plan.getDefault().getRegularTasks();
        if (allDays != 0) {
            Enumeration enumeration = taskCounts.keys();
            while (enumeration.hasMoreElements()) {
                String description = (String) enumeration.nextElement();
                Integer count = (Integer) taskCounts.get(description);
                int taskRepetition = count.intValue() * 100 / allDays;
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
            Iterator iterator = regularTasks.iterator();
            while (iterator.hasNext()) {
                RegularTask regularTask = (RegularTask) iterator.next();
                if (!taskCounts.containsKey(regularTask.getDescription())) uselessRegularTasks++;
                else {
                    Integer count = (Integer) taskCounts.get(regularTask.getDescription());
                    int taskRepetition = count.intValue() * 100 / allDays;
                    if (regularTask.getFrequency() == RegularTask.FREQUENCY_DAILY)
                        if (taskRepetition < 60) uselessRegularTasks++;
                    if (regularTask.getFrequency() == RegularTask.FREQUENCY_WORKDAY)
                        if (taskRepetition < 40) uselessRegularTasks++;
                }
            }
            if (allTasks != 0) totalError = missingRegularTasks * 100 / (allTasks / allDays);
        }
        int allRegularTasks = regularTasks.size();
        if (allRegularTasks != 0) totalError = (totalError + uselessRegularTasks * 100 / allRegularTasks) / 2;
        if (totalError != 0) repetition = (100f - totalError) / 20;
        else repetition = 0;
        return Math.round(repetition*10)/10f;
    }
    
    /** Counts repetition factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updateRepetition(int scale) {
        countRepetition(scale);
        lbRepetitionResult.setToolTipText("" + repetition);
        lbRepetitionResult.setIcon(getIcon(Math.round(repetition)));
    }

    /** Counts usage of DONE status in given period. Does user close tasks or
     * leaves them open forever?
     * @param scale One of three possible time scales.
     * @return Computed statusing factor.
     */
    private float countStatusing(int scale) {
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
        if (allTasks != 0) {
            doneStatus = doneStatus * 100 / allTasks;
            startedStatus = startedStatus * 100 / allTasks;
            int totalError = Math.abs(doneStatus - 40);
            totalError = totalError + Math.abs(startedStatus - 60);

            statusing = (100f - totalError) / 20;
        } else statusing = 0;
        return Math.round(statusing*10)/10f;
    }
        
    /** Counts statusing factor in given period and updates users star ranking.
     * @param scale One of three possible time scales.
     */
    private void updateStatusing(int scale) {
        countStatusing(scale);
        lbStatusingResult.setToolTipText("" + statusing);
        lbStatusingResult.setIcon(getIcon(Math.round(statusing)));
    }

    /** Returns idle time of all Rachota users worldwide in milliseconds.
     * @return Time of all Rachota users worldwide spent in idle mode in milliseconds.
     */
    private long getIdleTimeAll() {
        if (usageTimesAll == null) return 0;
        if (usageTimesAll.equals("")) return 0;
        int firstIndex = usageTimesAll.indexOf("|");
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(firstIndex + 1, lastIndex));
    }

    /** Returns private time of all Rachota users worldwide in milliseconds.
     * @return Time spent on private tasks of all Rachota users worldwide in milliseconds.
     */
    private long getPrivateTimeAll() {
        if (usageTimesAll == null) return 0;
        if (usageTimesAll.equals("")) return 0;
        int lastIndex = usageTimesAll.lastIndexOf("|");
        return Long.parseLong(usageTimesAll.substring(lastIndex + 1));
    }

    /** Returns total working time of all Rachota users worldwide in milliseconds.
     * @return Time spent on non-private and non-idle tasks of all Rachota users worldwide in milliseconds.
     */
    private long getTotalTimeAll() {
        if (usageTimesAll == null) return 0;
        if (usageTimesAll.equals("")) return 0;
        int index = usageTimesAll.indexOf("|");
        return Long.parseLong(usageTimesAll.substring(0, index));
    }

    /** Returns idle time of current Rachota user in milliseconds.
     * @return Time of current Rachota user spent in idle mode in milliseconds.
     */
    private long getIdleTimeUser() {
        int firstIndex = usageTimesUser.indexOf("|");
        int lastIndex = usageTimesUser.lastIndexOf("|");
        return Long.parseLong(usageTimesUser.substring(firstIndex + 1, lastIndex));
    }

    /** Returns private time of current Rachota user in milliseconds.
     * @return Time of current Rachota user spent on private tasks in milliseconds.
     */
    private long getPrivateTimeUser() {
        int lastIndex = usageTimesUser.lastIndexOf("|");
        return Long.parseLong(usageTimesUser.substring(lastIndex + 1));
    }

    /** Returns total working time of current Rachota user in milliseconds.
     * @return Time of current Rachota user spent on working in milliseconds.
     */
    private long getTotalTimeUser() {
        int index = usageTimesUser.indexOf("|");
        return Long.parseLong(usageTimesUser.substring(0, index));
    }

    /** Tries to download usage times from Rachota Analytics server and reports
     * success or failure.
     * @return Returns true if usage times were successfully downloaded. If server
     * didn't provide the number due to missing activity report or it couldn't be
     * even contacted false is returned.
     */
    private boolean downloadTimesAll() {
        String RID = Tools.getRID();
        try { RID = URLEncoder.encode(RID, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            System.out.println("Error: Can't build URL to Rachota Analytics server.");
            e.printStackTrace();
        }
        final String url_string = "http://rachota.sourceforge.net/getUsageTimes.php?rid=" + RID;
        usageTimesAll = "";
        final Thread connectionThread = new Thread("Rachota Analytics Download Times") {
            public void run() {
                try {
                    URL url = new URL(url_string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
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
                    System.out.println("Error: Can't download weekly data from Rachota Analytics server.");
                    usageTimesAll = null;
                    // usageTimesAll = "80903403|59269685|2210448"; // Based on 250 usage reports
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

    /** Computes total, idle and private times of current user in given time scale.
     * @param scale One of three possible time scales: past week, past month or whole time.
     */
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
        if (scale != SCALE_PAST_WEEK) {
            float numberOfWeeks = numberOfDays / 7f;
            if (numberOfWeeks > 1) {
                totalTimeUser = (int) (totalTimeUser / numberOfWeeks);
                idleTimeUser = (int) (idleTimeUser / numberOfWeeks);
                privateTimeUser = (int) (privateTimeUser / numberOfWeeks);
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
        rbMonth = new javax.swing.JRadioButton();
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
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        pnTimeUsage.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Translator.getTranslation("ANALYTICSVIEW.PN_COMPARISON"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, getFont(), new java.awt.Color(0, 0, 255)));
        pnTimeUsage.setLayout(new java.awt.GridBagLayout());

        pnChart.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
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

        rbMonth.setFont(getFont());
        rbMonth.setMnemonic(Translator.getMnemonic("ANALYTICSVIEW.RB_MONTH"));
        rbMonth.setText(Translator.getTranslation("ANALYTICSVIEW.RB_MONTH"));
        rbMonth.setToolTipText(Translator.getTranslation("ANALYTICSVIEW.RB_MONTH_TOOLTIP"));
        rbMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonthActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnTimeUsage.add(rbMonth, gridBagConstraints);

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
        tpSuggestions.setFont(getFont());
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

    /** Method called when All time radio button was clicked.
     * @param evt Event that invoked the action.
     */
    private void rbAllTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAllTimeActionPerformed
        rbAllTime.setSelected(true);
        rbWeek.setSelected(false);
        rbMonth.setSelected(false);
        countUserTimes(SCALE_WHOLE_TIME);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
        updateAnalysis(SCALE_WHOLE_TIME);
        updateSuggestions();
    }//GEN-LAST:event_rbAllTimeActionPerformed

    /** Method called when Last week radio button was clicked.
     * @param evt Event that invoked the action.
     */
    private void rbWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbWeekActionPerformed
        rbAllTime.setSelected(false);
        rbWeek.setSelected(true);
        rbMonth.setSelected(false);
        countUserTimes(SCALE_PAST_WEEK);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
        updateAnalysis(SCALE_PAST_WEEK);
        updateSuggestions();
    }//GEN-LAST:event_rbWeekActionPerformed

    /** Method called when Last month radio button was clicked.
     * @param evt Event that invoked the action.
     */
    private void rbMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonthActionPerformed
        rbAllTime.setSelected(false);
        rbWeek.setSelected(false);
        rbMonth.setSelected(true);
        countUserTimes(SCALE_PAST_MONTH);
        comparisonChart.setTimes(getTotalTimeUser(), getTotalTimeAll(), getIdleTimeUser(), getIdleTimeAll(), getPrivateTimeUser(), getPrivateTimeAll());
        updateAnalysis(SCALE_PAST_MONTH);
        updateSuggestions();
}//GEN-LAST:event_rbMonthActionPerformed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        Tools.recordActivity();
    }//GEN-LAST:event_formMouseMoved

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
    private javax.swing.JRadioButton rbMonth;
    private javax.swing.JRadioButton rbWeek;
    private javax.swing.JScrollPane spSuggestions;
    private javax.swing.JTextPane tpSuggestions;
    // End of variables declaration//GEN-END:variables

    /** Index of past week scale */
    public static final int SCALE_PAST_WEEK = 0;
    /** Index of past month scale */
    public static final int SCALE_PAST_MONTH = 1;
    /** Index of whole time scale */
    public static final int SCALE_WHOLE_TIME = 2;
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
    
    /** Downloads working times of other users and updates comparison chart. */
    public void updateChart() {
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

    /** Method called when some setting has changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("settings")) updateChart();
        repaint();
    }

    /** Updates all efficiency factors in selected time scale.
     * @param scale One of three possible time scales: past week, past month or whole time.
     */
    private void updateAnalysis(int scale) {
        updateCategorization(scale);
        updateEffectivity(scale);
        updateGranularity(scale);
        updatePrioritization(scale);
        updateRepetition(scale);
        updateStatusing(scale);
    }

    /** Returns all efficiency factors as a text separated by | character.
     * @return All efficiency factors as a text separated by | character.
     */
    public String getWeeklyAnalysis() {
        return "" +
            countCategorization(SCALE_PAST_WEEK) + "|" +
            countEffectivity(SCALE_PAST_WEEK) + "|" +
            countGranularity(SCALE_PAST_WEEK) + "|" +
            countPrioritization(SCALE_PAST_WEEK) + "|" +
            countRepetition(SCALE_PAST_WEEK) + "|" +
            countStatusing(SCALE_PAST_WEEK);
    }
    
    /** Prepares suggestions and displays them. */
    private void updateSuggestions() {
        String suggestions = getSuggestions();
        tpSuggestions.setText(suggestions);
    }
    
    /** Returns image with appropriate number of highlighted stars based on given ranking.
     * @param ranking Number (0-5) specifying required ranking.
     * @return Image with appropriate number of highlighted stars.
     */
    private javax.swing.ImageIcon getIcon(int ranking) {
        if (ranking > 5) ranking = 5;
        if (ranking < 0) ranking = 0;
        return new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/ranking_" + ranking + ".png"));
    }

    /** Returns compiled summary of all suitable suggestions.
     * @return Summary of all suitable suggestions based on effectivity of user.
     */
    private String getSuggestions() {
        String suggestions = "";
        if (categorization + effectivity + granularity + prioritization + repetition + statusing == 0) return Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_NO_DATA");
        if (categorization < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_CATEGORIZATION") + "\n";
        if (effectivity < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_EFFECTIVITY") + "\n";
        if (granularity < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_GRANULARITY") + "\n";
        if (prioritization < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_PRIORITIZATION") + "\n";
        if (repetition < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_REPETITION") + "\n";
        if (statusing < 3) suggestions = suggestions + "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_STATUSING");
        if (suggestions.equals("")) suggestions = "* " + Translator.getTranslation("ANALYTICSVIEW.SUGGESTION_NONE");
        return suggestions;
    }
}