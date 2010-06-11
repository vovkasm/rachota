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
 * Created on February 16, 2005  8:56 PM
 * MainWindow.java
 */

package org.cesilko.rachota.gui;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.cesilko.rachota.core.Clock;
import org.cesilko.rachota.core.ClockListener;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Main window of the Rachota application.
 * @author Jiri Kovalsky
 */
public class MainWindow extends javax.swing.JFrame implements PropertyChangeListener, ClockListener {
    
    /**
     * Main method called when application is started.
     * @param args Command line arguments passed from operating system.
     * @throws java.lang.Exception Exception thrown when some I/O problems occur while loading settings or diary files.
     */
    public static void main(String[] args) throws Exception {
        int length = args.length;
        boolean printHelp = false;
        for (int i=0; i<length; i++) {
            String argument = args[i];
            if (argument.startsWith("-userdir=")) {
                File userdir = new File(argument.substring(9));
                userdir.mkdirs();
                if (userdir.isDirectory())
                    Settings.getDefault().setSetting("userDir", userdir.getCanonicalPath());
                else JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.USERDIR_ERROR", new String[] {(String) Settings.getDefault().getSetting("userDir")}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.WARNING_MESSAGE);
            } else printHelp = true;
        }
        String userDir = (String) Settings.getDefault().getSetting("userDir");
        if (userDir.indexOf("rachota.sourceforge.net") != -1) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle(Translator.getTranslation("QUESTION.DIARY_LOCATION"));
            fileChooser.setApproveButtonText(Translator.getTranslation("HISTORYVIEW.BT_SELECT"));
            fileChooser.setApproveButtonMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_SELECT"));
            int decision = fileChooser.showOpenDialog(null);
            if (decision != JFileChooser.APPROVE_OPTION) return;
            Settings.getDefault().setSetting("userDir", fileChooser.getSelectedFile().getAbsolutePath());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("�� " + Tools.title + " �� (build " + Tools.build + ") - " + Translator.getTranslation("INFORMATION.PROGRAM"));
        System.out.println("   http://rachota.sourceforge.net");
        System.out.println("   " + Translator.getTranslation("INFORMATION.SESSION") + ": " + System.getProperty("os.name") + ", JDK " + System.getProperty("java.version") + ", " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
        System.out.println("   " + Translator.getTranslation("INFORMATION.LOCALIZATION") + ": " + Settings.getDefault().getSetting("dictionary"));
        System.out.println("   " + Translator.getTranslation("INFORMATION.USERDIR") + ": " + userDir);
        if (printHelp) {
            System.out.println("\nHelp: java [-Duser.language=<language_id> -Duser.country=<country_id>] -jar rachota_22.jar [-userdir=<diary_folder>] where:");
            System.out.println("      <diary_folder> is directory with settings and diary files e.g. C:\\rachota\\diaries");
            System.out.println("      <language_id> is Java language code e.g. cs, de, en, es, hu, it, ja, pt, ro or ru");
            System.out.println("      <country_id> is Java country code e.g. BR, CZ, DE, ES, HU, IT, JP, MX, RO, RU or US");
            System.out.println("      java -Duser.language=cs -Duser.country=CZ -jar Rachota.jar -userdir=/home/jkovalsky/diaries");
        }
        checkAnotherInstance();
        StartupWindow startupWindow = StartupWindow.getInstance();
        Settings.loadSettings();
        MainWindow mainWindow = new MainWindow();
        boolean minimizeOnStart = ((Boolean) Settings.getDefault().getSetting("minimizeOnStart")).booleanValue();
        if (!minimizeOnStart) mainWindow.setVisible(true);
        startupWindow.hideWindow();
        Tools.recordActivity();
    }
    
    /** Creates new application main window.
     * @throws java.lang.Exception Exception thrown when some I/O problems occur while loading settings or diary files.
     */
    public MainWindow() throws Exception {
        Plan.loadPlan();
        StartupWindow startupWindow = StartupWindow.getInstance();
        startupWindow.setProgressMessage("regular_tasks.xml");
        Plan.loadRegularTasks();
        startupWindow.setProgressMessage(Translator.getTranslation("INFORMATION.OPEN_MAIN_WINDOW"));
        Boolean moveUnfinishedTasks = (Boolean) Settings.getDefault().getSetting("moveUnfinished");
        if (moveUnfinishedTasks.booleanValue()) Plan.getDefault().copyUnfinishedTasks();
        initComponents();
        DayView dayView = new DayView();
        tpViews.add(dayView, TAB_DAY_VIEW);
        tpViews.setFont(getFont());
        dayView.addPropertyChangeListener(this);
        HistoryView historyView = new HistoryView();
        dayView.addPropertyChangeListener(historyView);
        historyView.addPropertyChangeListener(dayView);
        tpViews.add(historyView, TAB_HISTORY_VIEW);
        AnalyticsView analyticsView = new AnalyticsView();
        tpViews.add(analyticsView, TAB_ANALYTICS_VIEW);
        setSize(511, 646);
        setTitle(Tools.title + " " + dayView.getTitleSuffix());
        String size = (String) Settings.getDefault().getSetting("size");
        String location = (String) Settings.getDefault().getSetting("location");
        if (size != null) {
            try {
                int width = Integer.parseInt(size.substring(1, size.indexOf(",")));
                int height = Integer.parseInt(size.substring(size.indexOf(",") + 1, size.length() - 1));
                if ((width < 0) || (height < 0)) {
                    width = 511;
                    height = 646;
                }
                setSize(width, height);
            } catch (Exception e) {
                System.out.println("Error: Unable to load size of main window: " + size);
                e.printStackTrace();
            }
        }
        if (location != null) {
            try {
                int x = Integer.parseInt(location.substring(1, location.indexOf(",")));
                int y = Integer.parseInt(location.substring(location.indexOf(",") + 1, location.length() - 1));
                if ((x < 0) || (y < 0)) setLocationRelativeTo(null);
                else {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    int screenWidth = toolkit.getScreenSize().width;
                    int screenHeight = toolkit.getScreenSize().height;
                    if ((x + getWidth() > screenWidth) || (y + getHeight() > screenHeight))
                        setLocationRelativeTo(null);
                    else setLocation(x, y);
                }
            } catch (Exception e) {
                System.out.println("Error: Unable to load location of main window: " + location);
                e.printStackTrace();
                setLocationRelativeTo(null);
            }
        } else setLocationRelativeTo(null);
        Clock.getDefault().start();
        // htietgens: start IdleTask if configured
        boolean automaticStart = ((Boolean) Settings.getDefault().getSetting("automaticStart")).booleanValue();
        if (automaticStart) {
            Task task = dayView.getTask();
            if ((task == null) || (!task.isRunning()))
                dayView.setTask(dayView.getDay().getIdleTask(), true);
        }
        createSystemTray();
        Clock.getDefault().addListener(this);
    }
    
    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return Tools.getFont();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tpViews = new javax.swing.JTabbedPane();
        mbMenu = new javax.swing.JMenuBar();
        mnSystem = new javax.swing.JMenu();
        mnAbout = new javax.swing.JMenuItem();
        mnSettings = new javax.swing.JMenuItem();
        separator = new javax.swing.JSeparator();
        mnExit = new javax.swing.JMenuItem();
        mnTask = new javax.swing.JMenu();
        mnCopyTask = new javax.swing.JMenuItem();
        mnMoveTime = new javax.swing.JMenuItem();
        mnCorrectDuration = new javax.swing.JMenuItem();
        mnAddNote = new javax.swing.JMenuItem();
        mnTools = new javax.swing.JMenu();
        mnSwitchDate = new javax.swing.JMenuItem();
        mnAdjustStart = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_small.png")).getImage());
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tpViews.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tpViewsMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        getContentPane().add(tpViews, gridBagConstraints);

        mbMenu.setFont(getFont());

        mnSystem.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_SYSTEM"));
        mnSystem.setText(Translator.getTranslation("MAINWINDOW.MN_SYSTEM"));
        mnSystem.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_SYSTEM_TOOLTIP"));
        mnSystem.setFont(getFont());

        mnAbout.setFont(getFont());
        mnAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/info.png"))); // NOI18N
        mnAbout.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_ABOUT"));
        mnAbout.setText(Translator.getTranslation("MAINWINDOW.MN_ABOUT"));
        mnAbout.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_ABOUT_TOOLTIP"));
        mnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnAboutActionPerformed(evt);
            }
        });
        mnSystem.add(mnAbout);

        mnSettings.setFont(getFont());
        mnSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/service.png"))); // NOI18N
        mnSettings.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_SETTINGS"));
        mnSettings.setText(Translator.getTranslation("MAINWINDOW.MN_SETTINGS"));
        mnSettings.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_SETTINGS_TOOLTIP"));
        mnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSettingsActionPerformed(evt);
            }
        });
        mnSystem.add(mnSettings);
        mnSystem.add(separator);

        mnExit.setFont(getFont());
        mnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/exit.png"))); // NOI18N
        mnExit.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_EXIT"));
        mnExit.setText(Translator.getTranslation("MAINWINDOW.MN_EXIT"));
        mnExit.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_EXIT_TOOLTIP"));
        mnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExitActionPerformed(evt);
            }
        });
        mnSystem.add(mnExit);

        mbMenu.add(mnSystem);

        mnTask.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_TASK"));
        mnTask.setText(Translator.getTranslation("MAINWINDOW.MN_TASK"));
        mnTask.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_TASK_TOOLTIP"));
        mnTask.setFont(getFont());

        mnCopyTask.setFont(getFont());
        mnCopyTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/move_task.png"))); // NOI18N
        mnCopyTask.setMnemonic(Translator.getMnemonic("MAINWINDOW.MOVE_TASK"));
        mnCopyTask.setText(Translator.getTranslation("MAINWINDOW.MOVE_TASK"));
        mnCopyTask.setToolTipText(Translator.getTranslation("MAINWINDOW.MOVE_TASK_TOOLTIP"));
        mnCopyTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCopyTaskActionPerformed(evt);
            }
        });
        mnTask.add(mnCopyTask);

        mnMoveTime.setFont(getFont());
        mnMoveTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/move_time.png"))); // NOI18N
        mnMoveTime.setMnemonic(Translator.getMnemonic("MAINWINDOW.MOVE_TIME"));
        mnMoveTime.setText(Translator.getTranslation("MAINWINDOW.MOVE_TIME"));
        mnMoveTime.setToolTipText(Translator.getTranslation("MAINWINDOW.MOVE_TIME_TOOLTIP"));
        mnMoveTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnMoveTimeActionPerformed(evt);
            }
        });
        mnTask.add(mnMoveTime);

        mnCorrectDuration.setFont(getFont());
        mnCorrectDuration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/fix_time.png"))); // NOI18N
        mnCorrectDuration.setMnemonic(Translator.getMnemonic("MAINWINDOW.CORRECT_DURATION"));
        mnCorrectDuration.setText(Translator.getTranslation("MAINWINDOW.CORRECT_DURATION"));
        mnCorrectDuration.setToolTipText(Translator.getTranslation("MAINWINDOW.CORRECT_DURATION_TOOLTIP"));
        mnCorrectDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCorrectDurationActionPerformed(evt);
            }
        });
        mnTask.add(mnCorrectDuration);

        mnAddNote.setFont(getFont());
        mnAddNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/note.png"))); // NOI18N
        mnAddNote.setMnemonic(Translator.getMnemonic("MAINWINDOW.ADD_NOTE"));
        mnAddNote.setText(Translator.getTranslation("MAINWINDOW.ADD_NOTE"));
        mnAddNote.setToolTipText(Translator.getTranslation("MAINWINDOW.ADD_NOTE_TOOLTIP"));
        mnAddNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnAddNoteActionPerformed(evt);
            }
        });
        mnTask.add(mnAddNote);

        mbMenu.add(mnTask);

        mnTools.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_TOOLS"));
        mnTools.setText(Translator.getTranslation("MAINWINDOW.MN_TOOLS"));
        mnTools.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_TOOLS_TOOLTIP"));
        mnTools.setFont(getFont());

        mnSwitchDate.setFont(getFont());
        mnSwitchDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/calendar.png"))); // NOI18N
        mnSwitchDate.setMnemonic(Translator.getMnemonic("MAINWINDOW.SWITCH_DATE"));
        mnSwitchDate.setText(Translator.getTranslation("MAINWINDOW.SWITCH_DATE"));
        mnSwitchDate.setToolTipText(Translator.getTranslation("MAINWINDOW.SWITCH_DATE_TOOLTIP"));
        mnSwitchDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSwitchDateActionPerformed(evt);
            }
        });
        mnTools.add(mnSwitchDate);

        mnAdjustStart.setFont(getFont());
        mnAdjustStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/clock.png"))); // NOI18N
        mnAdjustStart.setMnemonic(Translator.getMnemonic("MAINWINDOW.ADJUST_START"));
        mnAdjustStart.setText(Translator.getTranslation("MAINWINDOW.ADJUST_START"));
        mnAdjustStart.setToolTipText(Translator.getTranslation("MAINWINDOW.ADJUST_START_TOOLTIP"));
        mnAdjustStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnAdjustStartActionPerformed(evt);
            }
        });
        mnTools.add(mnAdjustStart);

        mbMenu.add(mnTools);

        setJMenuBar(mbMenu);
    }// </editor-fold>//GEN-END:initComponents

private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        if (!enableSystemTray()) return;
        try { SystemTray.getSystemTray().getTrayIcons(); }
        catch (UnsupportedOperationException e) { return; }
        try {
            long now = new Date().getTime();
            long rachotaShownTime = Long.parseLong(System.getProperty("rachota.shownTime"));
            if (now - rachotaShownTime < 1000)
                return; // Unix systems call this method twice sometimes -> ignore
        } catch (NumberFormatException e) { e.printStackTrace(); return; }
        setVisible(false); // User called this method -> hide window
}//GEN-LAST:event_formWindowIconified
    
    private void mnSwitchDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSwitchDateActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.switchDate(this);
    }//GEN-LAST:event_mnSwitchDateActionPerformed
    
    /** Method called when move time action is required.
     * @param evt Event that invoked the action.
     */
    private void mnMoveTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnMoveTimeActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.moveTime(this);
    }//GEN-LAST:event_mnMoveTimeActionPerformed
    
    /** Method called when copy task action is required.
     * @param evt Event that invoked the action.
     */
    private void mnCopyTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCopyTaskActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.copyTask(this);
    }//GEN-LAST:event_mnCopyTaskActionPerformed
    
    /** Method called when change settings action is required.
     * @param evt Event that invoked the action.
     */
    private void mnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSettingsActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        AnalyticsView analyticsView = (AnalyticsView) tpViews.getComponentAt(TAB_ANALYTICS_VIEW);
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.addPropertyChangeListener(dayView);
        dialog.addPropertyChangeListener(analyticsView);
        dialog.addPropertyChangeListener(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnSettingsActionPerformed
    
    /** Method called when exit application action was invoked.
     * @param evt Event that invoked the action.
     */
    private void mnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnExitActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_mnExitActionPerformed
    
    /** Method called when information about the application should be displayed.
     * @param evt Event that invoked the action.
     */
    private void mnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnAboutActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_mnAboutActionPerformed
    
    /** Method called when application should be exited.
     * @param evt Event that invoked the action.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        boolean warnHoursNotReaced = ((Boolean) Settings.getDefault().getSetting("warnHoursNotReached")).booleanValue();
        if (warnHoursNotReaced) {
            double dayWorkHours = Settings.getDefault().getWorkingHours();
            Day today = Plan.getDefault().getDay(new Date());
            double totalTime = (double) today.getTotalTime(((Boolean) Settings.getDefault().getSetting("countPrivateTasks")).booleanValue())/(60 * 60 * 1000);
            if (totalTime < dayWorkHours) {
                String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
                int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("WARNING.HOURS_NOT_REACHED"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
                if (decision != JOptionPane.YES_OPTION) return;
            }
        }
        String reportedWeek = (String) Settings.getDefault().getSetting("rachota.reported.week");
        if (reportedWeek.equals(Settings.ACTIVITY_REPORT_FAILED)) Settings.getDefault().setSetting("rachota.reported.week", Settings.ACTIVITY_NOT_REPORTED);
        Settings.getDefault().setSetting("size", "[" + (int) getBounds().getWidth() + "," + (int) getBounds().getHeight() + "]");
        Settings.getDefault().setSetting("location", "[" + (int) getBounds().getLocation().getX() + "," + (int) getBounds().getLocation().getY() + "]");
        HistoryView historyView = (HistoryView) tpViews.getComponentAt(TAB_HISTORY_VIEW);
        historyView.saveSetup();
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.saveSetup();
        Settings.saveSettings();
        String task = (String) Settings.getDefault().getSetting("runningTask");
        if ((task != null) && !task.equals("null")) {
            
            String onExitAction = (String) Settings.getDefault().getSetting("onExitAction");
            if (Settings.ON_EXIT_STOP.equals(onExitAction)) {
                // Stop measuring current task
                Settings.getDefault().setSetting("runningTask", null);
            } else {
                // ask user about measuring downtime
                task = task.substring(0, task.indexOf("["));
                String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO"), Translator.getTranslation("MOVETIMEDIALOG.BT_CANCEL")};
                int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.COUNT_RUNNING_TASK", new String[] {task}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
                if ((decision == JOptionPane.CANCEL_OPTION) || (decision == -1)) return;
                if (decision == JOptionPane.NO_OPTION) Settings.getDefault().setSetting("runningTask", null);
            }            
            Settings.saveSettings();
        }
        int attempts = 0;
        while (true) {
            boolean planSaved = Plan.savePlan();
            attempts++;
            if (planSaved) break;
            if (attempts == 10) {
                String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
                int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.SELECT_LOCATION"), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
                if (decision == JOptionPane.NO_OPTION) break;
                String location = (String) Settings.getDefault().getSetting("userdir");
                JFileChooser fileChooser = new JFileChooser(location);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setApproveButtonText(Translator.getTranslation("HISTORYVIEW.BT_SELECT"));
                fileChooser.setApproveButtonMnemonic(Translator.getMnemonic("HISTORYVIEW.BT_SELECT"));
                decision = fileChooser.showOpenDialog(this);
                attempts = 0;
                if (decision == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Settings.getDefault().setSetting("userdir", file.getAbsolutePath());
                }
            }
        }
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        System.setProperty("rachota.shownTime", "" + new Date().getTime());
    }//GEN-LAST:event_formComponentShown

    private void mnAddNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnAddNoteActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.addNote(this);
}//GEN-LAST:event_mnAddNoteActionPerformed

    private void mnAdjustStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnAdjustStartActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.adjustStartTime(this);
}//GEN-LAST:event_mnAdjustStartActionPerformed

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        Tools.recordActivity();
    }//GEN-LAST:event_formMouseEntered

    private void tpViewsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tpViewsMouseEntered
        Tools.recordActivity();
    }//GEN-LAST:event_tpViewsMouseEntered

    private void mnCorrectDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCorrectDurationActionPerformed
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.correctTaskDuration(this);
    }//GEN-LAST:event_mnCorrectDurationActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar mbMenu;
    private javax.swing.JMenuItem mnAbout;
    private javax.swing.JMenuItem mnAddNote;
    private javax.swing.JMenuItem mnAdjustStart;
    private javax.swing.JMenuItem mnCopyTask;
    private javax.swing.JMenuItem mnCorrectDuration;
    private javax.swing.JMenuItem mnExit;
    private javax.swing.JMenuItem mnMoveTime;
    private javax.swing.JMenuItem mnSettings;
    private javax.swing.JMenuItem mnSwitchDate;
    private javax.swing.JMenu mnSystem;
    private javax.swing.JMenu mnTask;
    private javax.swing.JMenu mnTools;
    private javax.swing.JSeparator separator;
    private javax.swing.JTabbedPane tpViews;
    // End of variables declaration//GEN-END:variables
    
    /** Flag to prevent multiple reporting of activity. */
    private boolean reportingActivity;
    /** Index of day view tab. */
    private static final int TAB_DAY_VIEW = 0;
    /** Index of history view tab. */
    private static final int TAB_HISTORY_VIEW = 1;
    /** Index of analytics view tab. */
    private static final int TAB_ANALYTICS_VIEW = 2;
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        setTitle(Tools.title + " " + dayView.getTitleSuffix());
        if (evt.getPropertyName().equals("day"))
            tpViews.setSelectedIndex(0);
        if (evt.getPropertyName().equals("enable_menu")) {
            getMenuItem((String) evt.getOldValue()).setEnabled(Boolean.parseBoolean((String) evt.getNewValue()));
        }
        updateSystemTray(dayView);
    }
    
    private void updateSystemTray(DayView dayView) {
        if (!enableSystemTray()) return;
        TrayIcon[] trayIcons;
        try { trayIcons = SystemTray.getSystemTray().getTrayIcons(); } catch (UnsupportedOperationException e) { return; }
        for (int i = 0; i < trayIcons.length; i++) {
            TrayIcon trayIcon = trayIcons[i];
            if (trayIcon.getToolTip().startsWith(Tools.title)) {
                Task task = dayView.getTask();
                String currentRachotaTrayColor = System.getProperty("rachotaTrayColor");
                String neededRachotaTrayColor = "red";
                if ((task != null) && task.isRunning() && !task.isIdleTask()) neededRachotaTrayColor = "blue";
                if (!neededRachotaTrayColor.equals(currentRachotaTrayColor)) { // To prevent flashing icon in system tray
                    if (neededRachotaTrayColor.equals("red")) trayIcon.setImage(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage());
                    else trayIcon.setImage(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage());
                    System.setProperty("rachotaTrayColor", neededRachotaTrayColor);
                }
                trayIcon.setPopupMenu(getTrayPopupMenu());
                trayIcon.setToolTip(Tools.title + " " + dayView.getTitleSuffix());
                break;
            }
        }
    }
    
    private PopupMenu getTrayPopupMenu() {
        final SystemTray systemTray = SystemTray.getSystemTray();
        ActionListener maximizeListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    setVisible(true);
                    try { Thread.sleep(100); }
                    catch(InterruptedException exception) {}
                    setExtendedState(java.awt.Frame.NORMAL);
                    try { Thread.sleep(100); }
                    catch(InterruptedException exception) {}
                    requestFocus();
                    Tools.recordActivity();
            }
        };
        PopupMenu popup = new PopupMenu();
        MenuItem menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.OPEN"));
        menuItem.addActionListener(maximizeListener);
        menuItem.setFont(getFont().deriveFont(Font.BOLD));
        popup.add(menuItem);
        ActionListener newTaskListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                TaskDialog dialog = new TaskDialog(dayView.getDay());
                dialog.addPropertyChangeListener(dayView);
                dialog.setVisible(true);
                Tools.recordActivity();
            }
        };
        menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.NEW"));
        menuItem.addActionListener(newTaskListener);
        popup.add(menuItem);
        ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Tools.recordActivity();
                formWindowClosing(null);
            }
        };
        menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.MN_EXIT"));
        menuItem.addActionListener(exitListener);
        popup.add(menuItem);
        Iterator tasks = Plan.getDefault().getDay(new Date()).getTasks().iterator();
        Task selectedTask = null;
        while(tasks.hasNext()) {
            final Task task = (Task) tasks.next();
            if (task.getState() == Task.STATE_DONE) continue;
            if (task.isIdleTask()) continue;
            if (popup.getItemCount() == 3) popup.addSeparator();
            menuItem = new MenuItem(task.getDescription());
            DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
            if (task == dayView.getTask()) {
                menuItem.setFont(getFont().deriveFont(Font.BOLD));
                selectedTask = task;
            }
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                    dayView.selectTask(task);
                    Tools.recordActivity();
                    Image image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage();
                    TrayIcon[] trayIcons = systemTray.getTrayIcons();
                    for (int i = 0; i < trayIcons.length; i++) {
                        TrayIcon trayIcon = trayIcons[i];
                        if (trayIcon.getToolTip().startsWith(Tools.title)) {
                            trayIcon.setImage(image);
                            break;
                        }
                    }
                }
            };
            menuItem.addActionListener(actionListener);
            popup.add(menuItem);
        }
        ActionListener startStopTaskListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                Task task = dayView.getTask();
                Image image;
                if (task.isRunning()) {
                    dayView.pauseTask();
                    image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
                } else {
                    dayView.startTask();
                    image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage();
                }
                Tools.recordActivity();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(Tools.title)) {
                        trayIcon.setImage(image);
                        break;
                    }
                }
            }
        };
        ActionListener finishTaskListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                dayView.finishTask();
                Image image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
                Tools.recordActivity();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(Tools.title)) {
                        trayIcon.setImage(image);
                        break;
                    }
                }
            }
        };
        if (selectedTask != null) {
            popup.addSeparator();
            if (selectedTask.isRunning()) menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.RELAX"));
            else menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.WORK"));
            menuItem.addActionListener(startStopTaskListener);
            popup.add(menuItem);
            menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.DONE"));
            menuItem.addActionListener(finishTaskListener);
            popup.add(menuItem);
        }
        return popup;
    }

    private void createSystemTray() {
        if (!enableSystemTray()) return;
        if (SystemTray.isSupported()) {
            final SystemTray systemTray = SystemTray.getSystemTray();
            DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
            Image image;
            Task task = dayView.getTask();
            if (task == null) {
                image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
                System.setProperty("rachotaTrayColor", "red");
            }
            else
                if (task.isRunning() && !task.isIdleTask()) {
                    image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage();
                    System.setProperty("rachotaTrayColor", "blue");
                } else {
                    image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
                    System.setProperty("rachotaTrayColor", "red");
                }
            final TrayIcon trayIcon = new TrayIcon(image, Tools.title, getTrayPopupMenu());
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isVisible()) formWindowIconified(null);
                else {
                    setVisible(true);
                    try { Thread.sleep(100); }
                    catch(InterruptedException exception) {}
                    setExtendedState(java.awt.Frame.NORMAL);
                    try { Thread.sleep(100); }
                    catch(InterruptedException exception) {}
                    requestFocus();
                }
            }
        });

            try { systemTray.add(trayIcon); }
            catch (AWTException ex) { System.out.println("Error: Can't create Rachota system tray icon."); };
        }
    }

    public void tick() {
        Boolean detectInactivity = (Boolean) Settings.getDefault().getSetting("detectInactivity");
        if (detectInactivity.booleanValue()) {
            int inactivityTime = Integer.parseInt((String) Settings.getDefault().getSetting("inactivityTime"));
            if ((Tools.getInactivity() > inactivityTime * 60 * 1000) && (System.getProperty("inactivityReminderOpen")==null)) {
                Tools.recordActivity();
                SystemTray systemTray = SystemTray.getSystemTray();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(Tools.title))
                        trayIcon.displayMessage(Translator.getTranslation("WARNING.WARNING_TITLE"), Translator.getTranslation("INACTIVITYDIALOG.LBL_INACTIVITY_MESSAGE", new String[] {(String) Settings.getDefault().getSetting("inactivityTime")}), TrayIcon.MessageType.WARNING);
                }
                String inactivityAction = (String) Settings.getDefault().getSetting("inactivityAction");
                if (!inactivityAction.equals(Settings.ON_INACTIVITY_NOTIFY)) {
                    EventQueue.invokeLater(new Runnable() {
                        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                        public void run() {
                            new InactivityReminderDialog(dayView).setVisible(true);
                        }
                    });
                }
            }
        }
        Boolean reportActivity = (Boolean) Settings.getDefault().getSetting("reportActivity");
        if (!reportActivity.booleanValue()) return;
        Calendar calendar = Calendar.getInstance();
        final int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        String reportedWeek = (String) Settings.getDefault().getSetting("rachota.reported.week");
        if (reportedWeek.equals(Settings.ACTIVITY_REPORT_FAILED)) return;
        if (Integer.parseInt(reportedWeek)  == currentWeek) return;
        String RID = Tools.getRID();
        Plan plan = Plan.getDefault();
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek - 1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Day day = plan.getDay(calendar.getTime());
        long totalTime = 0;
        long idleTime = 0;
        long privateTime = 0;
        for (int i=0; i<7; i++) {
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.isIdleTask()) idleTime = idleTime + task.getDuration();
                else if (task.privateTask()) privateTime = privateTime + task.getDuration();
                else totalTime = totalTime + task.getDuration();
            }
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            day = plan.getDay(calendar.getTime());
        }
        String userDir = (String) Settings.getDefault().getSetting("userDir");
        File[] diaries = new File(userDir).listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                return (name.startsWith("diary_") && (name.endsWith(".xml")));
            }
        });
        final AnalyticsView analyticsView = (AnalyticsView) tpViews.getComponentAt(TAB_ANALYTICS_VIEW);
        String WUT = "" + totalTime + "|" + idleTime + "|" + privateTime + "|" + diaries.length + "|" + analyticsView.getWeeklyAnalysis(); // Week Usage Times
        try {
            RID = URLEncoder.encode(RID, "UTF-8");
            WUT = URLEncoder.encode(WUT, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("Error: Can't build URL to Rachota Analytics server.");
            e.printStackTrace();
        }
        final String url_string = "http://rachota.sourceforge.net/reportActivity.php?rid=" + RID + "&wut=" + WUT;
        final Thread connectionThread = new Thread("Rachota Analytics Reporter") {
            public void run() {
                try {
                    if (reportingActivity) return;
                    reportingActivity = true;
                    URL url = new URL(url_string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.getResponseMessage();
                    connection.disconnect();
                    Settings.getDefault().setSetting("rachota.reported.week", "" + currentWeek);
                    analyticsView.updateChart();
                    reportingActivity = false;
                }
                catch (Exception e) {
                    System.out.println("Error: Can't connect to Rachota Analytics server.");
                    Settings.getDefault().setSetting("rachota.reported.week", Settings.ACTIVITY_REPORT_FAILED);
                    reportingActivity = false;
                }
            }};
        connectionThread.start();
        new Thread("Rachota Analytics Reporter killer") {
            public void run() {
                try { sleep(30000); } catch (InterruptedException e) {}
                if (connectionThread.isAlive()) {
                    System.out.println("Error: Giving up...");
                    connectionThread.interrupt();
                }
            }
        }.start();
        //Clock.getDefault().addListener(this);
    }
    
    /** Returns whether system tray icon should be created or not.
     * @return False if Rachota is not running on Java 6 or 7. True otherwise.
     */
    private boolean enableSystemTray() {
        return System.getProperty("java.version").startsWith("1.6") || System.getProperty("java.version").startsWith("1.7");
    }
    
    /** Checks whether another instance of Rachota is running or Rachota was not
     * exited normally i.e. if this instance could be launched. If there is not
     * a lock file in userdir, startup is approved. If there is the lock file,
     * user is asked to confirm if he really wants to share selected userdir with
     * another instance of Rachota. If s/he agrees, the startup goes on.
     */
    private static void checkAnotherInstance() {
        String userdir = (String) Settings.getDefault().getSetting("userDir");
        File lockFile = new File(userdir + File.separator + "lock.tmp");
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
                lockFile.deleteOnExit();
                return;
            } catch (IOException e) {
                System.out.println("Error: Can't create new " + lockFile + " file.");
            }
        }
        String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
        int decision = JOptionPane.showOptionDialog(null, Translator.getTranslation("QUESTION.ANOTHER_INSTANCE"), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (decision == JOptionPane.NO_OPTION) System.exit(-1);
        lockFile.deleteOnExit();
    }

    private JMenuItem getMenuItem(String menuName) {
        JMenuItem menuItem = null;
        if (menuName.equals("mnCorrectDuration")) menuItem = mnCorrectDuration;
        if (menuName.equals("mnCopyTask")) menuItem = mnCopyTask;
        if (menuName.equals("mnAddNote")) menuItem = mnAddNote;
        if (menuName.equals("mnMoveTime")) menuItem = mnMoveTime;
        if (menuName.equals("mnAdjustStart")) menuItem = mnAdjustStart;
        return menuItem;
    }
}