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
 * Created on February 16, 2005  8:56 PM
 * MainWindow.java
 */

package org.cesilko.rachota.gui;
import java.awt.AWTException;
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
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.cesilko.rachota.core.Clock;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Plan;
import org.cesilko.rachota.core.Settings;
import org.cesilko.rachota.core.Task;
import org.cesilko.rachota.core.Translator;

/** Main window of the Rachota application.
 * @author Jiri Kovalsky
 */
public class MainWindow extends javax.swing.JFrame implements PropertyChangeListener {
    
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
        System.out.println("»» " + title + " «« (build " + build + ") - " + Translator.getTranslation("INFORMATION.PROGRAM"));
        System.out.println("   http://rachota.sourceforge.net");
        System.out.println("   " + Translator.getTranslation("INFORMATION.SESSION") + ": " + System.getProperty("os.name") + ", JDK " + System.getProperty("java.version") + ", " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
        System.out.println("   " + Translator.getTranslation("INFORMATION.LOCALIZATION") + ": " + Settings.getDefault().getSetting("dictionary"));
        System.out.println("   " + Translator.getTranslation("INFORMATION.USERDIR") + ": " + userDir);
        if (printHelp) {
            System.out.println("\nHelp: java [-Duser.language=<language_id> -Duser.country=<country_id>] -jar Rachota.jar [-userdir=<diary_folder>] where:");
            System.out.println("      <diary_folder> is directory with settings and diary files e.g. C:\\Rachota");
            System.out.println("      <language_id> is Java language code e.g. cs, de, en, es, pt or ru");
            System.out.println("      <country_id> is Java country code e.g. BR, CZ, DE, MX, RU or US");
            System.out.println("      java -Duser.language=cs -Duser.country=CZ -jar Rachota.jar -userdir=/home/jkovalsky/diaries");
        }
        Settings.loadSettings();
        new MainWindow().setVisible(true);
    }
    
    /** Creates new application main window.
     * @throws java.lang.Exception Exception thrown when some I/O problems occur while loading settings or diary files.
     */
    public MainWindow() throws Exception {
        Plan.loadPlan();
        Plan.loadRegularTasks();
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
        pack();
        setTitle(title + " " + dayView.getTitleSuffix());
        String size = (String) Settings.getDefault().getSetting("size");
        String location = (String) Settings.getDefault().getSetting("location");
        if (size != null) {
            try {
                int width = Integer.parseInt(size.substring(1, size.indexOf(",")));
                int height = Integer.parseInt(size.substring(size.indexOf(",") + 1, size.length() - 1));
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
                setLocation(x, y);
            } catch (Exception e) {
                System.out.println("Error: Unable to load location of main window: " + location);
                e.printStackTrace();
                setLocationRelativeTo(null);
            }
        } else setLocationRelativeTo(null);
        Clock.getDefault().start();
    }
    
    /** Returns font that should be used for all widgets in this component
     * based on the language preferences specified by user.
     * @return Font to be used in this component.
     */
    public java.awt.Font getFont() {
        return new java.awt.Font((String) Settings.getDefault().getSetting("fontName"), java.awt.Font.PLAIN, Integer.parseInt((String) Settings.getDefault().getSetting("fontSize")));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
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
        mnTools = new javax.swing.JMenu();
        mnSwitchDate = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_small.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());
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
        mnAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/info.png")));
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
        mnSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/service.png")));
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
        mnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/exit.png")));
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
        mnCopyTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/move_task.png")));
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
        mnMoveTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/move_time.png")));
        mnMoveTime.setMnemonic(Translator.getMnemonic("MAINWINDOW.MOVE_TIME"));
        mnMoveTime.setText(Translator.getTranslation("MAINWINDOW.MOVE_TIME"));
        mnMoveTime.setToolTipText(Translator.getTranslation("MAINWINDOW.MOVE_TIME_TOOLTIP"));
        mnMoveTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnMoveTimeActionPerformed(evt);
            }
        });
        mnTask.add(mnMoveTime);

        mbMenu.add(mnTask);

        mnTools.setMnemonic(Translator.getMnemonic("MAINWINDOW.MN_TOOLS"));
        mnTools.setText(Translator.getTranslation("MAINWINDOW.MN_TOOLS"));
        mnTools.setToolTipText(Translator.getTranslation("MAINWINDOW.MN_TOOLS_TOOLTIP"));
        mnTools.setFont(getFont());

        mnSwitchDate.setFont(getFont());
        mnSwitchDate.setMnemonic(Translator.getMnemonic("MAINWINDOW.SWITCH_DATE"));
        mnSwitchDate.setText(Translator.getTranslation("MAINWINDOW.SWITCH_DATE"));
        mnSwitchDate.setToolTipText(Translator.getTranslation("MAINWINDOW.SWITCH_DATE_TOOLTIP"));
        mnSwitchDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSwitchDateActionPerformed(evt);
            }
        });
        mnTools.add(mnSwitchDate);

        mbMenu.add(mnTools);

        setJMenuBar(mbMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
    if (!System.getProperty("java.version").startsWith("1.6")) return;
    SystemTray systemTray = SystemTray.getSystemTray();
    TrayIcon[] trayIcons = systemTray.getTrayIcons();
    for (int i = 0; i < trayIcons.length; i++) {
        TrayIcon trayIcon = trayIcons[i];
        if (trayIcon.getToolTip().equals(title)) {
            systemTray.remove(trayIcon);
            break;
        }
    }
}//GEN-LAST:event_formWindowDeiconified

private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
    if (!System.getProperty("java.version").startsWith("1.6")) return;
    if (SystemTray.isSupported()) {
        final SystemTray systemTray = SystemTray.getSystemTray();
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        Image image;
        Task task = dayView.getTask();
        if (task == null) image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
        else
            if (task.isRunning()) image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage();
            else image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
        final TrayIcon trayIcon = new TrayIcon(image, title, getTrayPopupMenu());
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
                setState(java.awt.Frame.NORMAL);
                requestFocus();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(title)) {
                        systemTray.remove(trayIcon);
                        break;
                    }
                }
            }
        };
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(actionListener);
        try {
            systemTray.add(trayIcon);
            setVisible(false);
        } catch (AWTException ex) { System.out.println("Error: Can't create Rachota system tray icon."); };
    }
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
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.addPropertyChangeListener(dayView);
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
        String text = title + " (build " + build + ")\n";
        text = text + Translator.getTranslation("INFORMATION.PROGRAM");
        text = text + "\n<html><body><a href=\"http://rachota.sourceforge.net\">http://rachota.sourceforge.net</a></body";
        text = text + "\n\njiri.kovalsky@centrum.cz\n2006 (c)";
        JOptionPane.showMessageDialog(this, text, Translator.getTranslation("INFORMATION.INFORMATION_TITLE"), JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_name_48.png")));
    }//GEN-LAST:event_mnAboutActionPerformed
    
    /** Method called when application should be exited.
     * @param evt Event that invoked the action.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        boolean warnHoursNotReaced = ((Boolean) Settings.getDefault().getSetting("warnHoursNotReached")).booleanValue();
        if (warnHoursNotReaced) {
            double dayWorkHours = Double.parseDouble((String) Settings.getDefault().getSetting("dayWorkHours"));
            Day today = Plan.getDefault().getDay(new Date());
            double totalTime = (double) today.getTotalTime()/(60 * 60 * 1000);
            if (totalTime < dayWorkHours) {
                String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
                int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("WARNING.HOURS_NOT_REACHED"), Translator.getTranslation("WARNING.WARNING_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
                if (decision != JOptionPane.YES_OPTION) return;
            }
        }
        Settings.getDefault().setSetting("size", "[" + (int) getBounds().getWidth() + "," + (int) getBounds().getHeight() + "]");
        Settings.getDefault().setSetting("location", "[" + (int) getBounds().getLocation().getX() + "," + (int) getBounds().getLocation().getY() + "]");
        HistoryView historyView = (HistoryView) tpViews.getComponentAt(TAB_HISTORY_VIEW);
        historyView.saveSetup();
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        dayView.saveSetup();
        Settings.saveSettings();
        String task = (String) Settings.getDefault().getSetting("runningTask");
        if ((task != null) && !task.equals("null")) {
            task = task.substring(0, task.indexOf("["));
            String[] buttons = {Translator.getTranslation("QUESTION.BT_YES"), Translator.getTranslation("QUESTION.BT_NO")};
            int decision = JOptionPane.showOptionDialog(this, Translator.getTranslation("QUESTION.COUNT_RUNNING_TASK", new String[] {task}), Translator.getTranslation("QUESTION.QUESTION_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
            if (decision != JOptionPane.YES_OPTION) Settings.getDefault().setSetting("runningTask", null);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar mbMenu;
    private javax.swing.JMenuItem mnAbout;
    private javax.swing.JMenuItem mnCopyTask;
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
    
    /** Name and version of application. */
    protected static final String title = "Rachota 2.1 RC";
    /** Build number. */
    protected static final String build = "#061208";
    /** Index of day view tab. */
    private static final int TAB_DAY_VIEW = 0;
    /** Index of history view tab. */
    private static final int TAB_HISTORY_VIEW = 1;
    
    /** Method called when some property of task was changed.
     * @param evt Event describing what was changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
        setTitle(title + " " + dayView.getTitleSuffix());
        if (evt.getPropertyName().equals("day"))
            tpViews.setSelectedIndex(0);
        if (!isVisible()) {
            if (!System.getProperty("java.version").startsWith("1.6")) return;
            TrayIcon[] trayIcons = SystemTray.getSystemTray().getTrayIcons();
            for (int i = 0; i < trayIcons.length; i++) {
                TrayIcon trayIcon = trayIcons[i];
                if (trayIcon.getToolTip().startsWith(title)) {
                    trayIcon.setPopupMenu(getTrayPopupMenu());
                    trayIcon.setToolTip(title + " " + dayView.getTitleSuffix());
                    break;
                }
            }
        }
    }
    
    private PopupMenu getTrayPopupMenu() {
        final SystemTray systemTray = SystemTray.getSystemTray();
        ActionListener maximizeListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(true);
                setState(java.awt.Frame.NORMAL);
                requestFocus();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(title)) {
                        systemTray.remove(trayIcon);
                        break;
                    }
                }
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
                new TaskDialog(dayView.getDay()).setVisible(true);
            }
        };
        menuItem = new MenuItem(Translator.getTranslation("MAINWINDOW.NEW"));
        menuItem.addActionListener(newTaskListener);
        popup.add(menuItem);
        Iterator tasks = Plan.getDefault().getDay(new Date()).getTasks().iterator();
        Task selectedTask = null;
        while(tasks.hasNext()) {
            final Task task = (Task) tasks.next();
            if (task.getState() == Task.STATE_DONE) continue;
            if (popup.getItemCount() == 2) popup.addSeparator();
            menuItem = new MenuItem(task.getDescription());
            DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
            if (task == dayView.getTask()) {
                menuItem.setFont(getFont().deriveFont(Font.BOLD));
                selectedTask = task;
            }
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                    dayView.setTask(task, true);
                    Image image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_48.png")).getImage();
                    TrayIcon[] trayIcons = systemTray.getTrayIcons();
                    for (int i = 0; i < trayIcons.length; i++) {
                        TrayIcon trayIcon = trayIcons[i];
                        if (trayIcon.getToolTip().startsWith(title)) {
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
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(title)) {
                        trayIcon.setImage(image);
                        break;
                    }
                }
            }
        };
        ActionListener finishTaskListener = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DayView dayView = (DayView) tpViews.getComponentAt(TAB_DAY_VIEW);
                Task task = dayView.getTask();
                dayView.finishTask();
                Image image = new javax.swing.ImageIcon(getClass().getResource("/org/cesilko/rachota/gui/images/logo_red_48.png")).getImage();
                TrayIcon[] trayIcons = systemTray.getTrayIcons();
                for (int i = 0; i < trayIcons.length; i++) {
                    TrayIcon trayIcon = trayIcons[i];
                    if (trayIcon.getToolTip().startsWith(title)) {
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
}
