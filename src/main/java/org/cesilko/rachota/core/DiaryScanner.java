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
 * Created on May 18, 2005  8:17 AM
 * DiaryScanner.java - generated from Diary.dtd
 */

package org.cesilko.rachota.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.cesilko.rachota.gui.Tools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** This is a DOM tree scanner of diary XML files.
 */
public class DiaryScanner {
    
    /**
     * XML document that is being loaded.
     */
    Document document;
    
    /**
     * Creates new diary scanner.
     * @param document XML diary document to be loaded.
     */
    public DiaryScanner(Document document) {
	this.document = document;
    }
    
    /**
     * Loads XML diary document given in the scanner constructor.
     */
    public void loadDocument() {
	Element weekElement = document.getDocumentElement();
	loadWeek(weekElement);
    }
    
    /**
     * Loads week from given XML element.
     * @param element XML element representing a week.
     */
    private void loadWeek(Element element) {
	int week_id = Integer.parseInt(element.getAttributeNode("id").getValue());
	int week_year = Integer.parseInt(element.getAttributeNode("year").getValue());
	
	NodeList nodes = element.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element dayElement = (Element) node;
		loadDay(dayElement, week_id, week_year);
	    }
	}
    }
    
    /**
     * Loads day from given XML element.
     * @param dayElement XML element representing a week.
     * @param week_id Number of week to which this day belongs.
     * @param week_year Number of year to which this day belongs.
     */
    private void loadDay(Element dayElement, int week_id, int week_year) {
	int day_id = Integer.parseInt(dayElement.getAttributeNode("id").getValue());
	String start = dayElement.getAttributeNode("start").getValue();
	String finish = dayElement.getAttributeNode("finish").getValue();
	Date start_time = null;
        Date finish_time = null;
        if (!finish.equals("00:00")) {
            finish_time = new Date(Tools.getTime(finish));
            start_time = new Date(Tools.getTime(start));
        }
	Calendar calendar = Calendar.getInstance();
	calendar.set(Calendar.YEAR, week_year);
	calendar.set(Calendar.WEEK_OF_YEAR, week_id);
	calendar.set(Calendar.DAY_OF_WEEK, day_id);
	Vector tasks = new Vector();
	
	NodeList nodes = dayElement.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element taskElement = (Element) node;
		Task task = loadTask(taskElement);
		tasks.add(task);
	    }
	}
	Day day = new Day(tasks, calendar.getTime(), start_time, finish_time);
	Plan.getDefault().addDay(day);
    }
    
    /**
     * Loads task from given XML element.
     * @param taskElement XML element representing a task.
     * @return Task loaded from given XML element.
     */
    private Task loadTask(Element taskElement) {
	long duration = Tools.getTime(taskElement.getAttributeNode("duration").getValue());
	int state = Integer.parseInt(taskElement.getAttributeNode("state").getValue());
	Task task = null;
	int priority = 0;
	String description = "";
	String keyword = "";
	String notes = "";
	long notification = -1;
	boolean automaticStart = false;
	boolean privateTask = false;
	int repetition = -1;
        boolean idle = false;
	
	NodeList nodes = taskElement.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element nodeElement = (Element) node;
		if (nodeElement.getTagName().equals("priority"))
		    priority = loadPriority(nodeElement);
		if (nodeElement.getTagName().equals("description"))
		    description = loadDescription(nodeElement);
		if (nodeElement.getTagName().equals("keyword"))
		    keyword = loadKeyword(nodeElement);
		if (nodeElement.getTagName().equals("notes"))
		    notes = loadNotes(nodeElement);
		if (nodeElement.getTagName().equals("notification")) {
		    notification = loadNotification(nodeElement);
		    automaticStart = loadAutomaticStart(nodeElement);
		}
		if (nodeElement.getTagName().equals("private"))
		    privateTask = true;
		if (nodeElement.getTagName().equals("repetition"))
		    repetition = loadRepetition(nodeElement);
		if (nodeElement.getTagName().equals("idle"))
		    idle = true;
	    }
	}
	Date notificationTime = (notification == -1 ? null : new Date(notification));
        task = new Task(description, keyword, notes, priority, state, duration, notificationTime, automaticStart, privateTask);
	if (repetition != -1)
	    task = new RegularTask(description, keyword, notes, priority, state, duration, notificationTime, automaticStart, privateTask, repetition);
	if (idle) {
            task = new IdleTask();
            task.addDuration(duration);
            task.setState(state);
        }
	return task;
    }
    
    /**
     *  Loads priority from given XML element.
     * @param priorityElement XML element representing a priority.
     * @return Priority loaded from given XML element.
     */
    private int loadPriority(Element priorityElement) {
	NodeList nodes = priorityElement.getChildNodes();
	Node node = nodes.item(0);
	return Integer.parseInt(((Text) node).getData());
    }
    
    /**
     *  Loads description from given XML element.
     * @param descriptionElement XML element representing a description.
     * @return Description loaded from given XML element.
     */
    private String loadDescription(Element descriptionElement) {
	NodeList nodes = descriptionElement.getChildNodes();
	Node node = nodes.item(0);
	return ((Text) node).getData();
    }
    
    /**
     *  Loads keyword from given XML element.
     * @param keywordElement XML element representing a keyword.
     * @return Keyword loaded from given XML element.
     */
    private String loadKeyword(Element keywordElement) {
	NodeList nodes = keywordElement.getChildNodes();
	Node node = nodes.item(0);
	return ((Text) node).getData();
    }
    
    /**
     *  Loads notes from given XML element.
     * @param notesElement XML element representing a notes.
     * @return Notes loaded from given XML element.
     */
    private String loadNotes(Element notesElement) {
	NodeList nodes = notesElement.getChildNodes();
	Node node = nodes.item(0);
	return ((Text) node).getData();
    }
    
    /**
     *  Loads notification time from given XML element.
     * @param notificationElement XML element representing a notification time.
     * @return Notification time loaded from given XML element.
     */
    private long loadNotification(Element notificationElement) {
	long notificationTime = Tools.getTime(notificationElement.getAttributeNode("time").getValue());
	return notificationTime;
    }
    
    /**
     *  Loads automatic start option from given XML element.
     * @param notificationElement XML element representing a notification time.
     * @return Automatic start option loaded from given XML element.
     */
    private boolean loadAutomaticStart(Element notificationElement) {
	boolean automaticStart = Boolean.parseBoolean(notificationElement.getAttributeNode("switch").getValue());
	return automaticStart;
    }
    
    /**
     *  Loads repetition from given XML element.
     * @param repetitionElement XML element representing a repetition.
     * @return Repetition loaded from given XML element.
     */
    private int loadRepetition(Element repetitionElement) {
	int repetition = Integer.parseInt(repetitionElement.getAttributeNode("frequency").getValue());
	return repetition;
    }
    
    /**
     * Creates diary.dtd file that is used for automatic XML validation of diary files.
     */
    public static void createDTD() {
	String userDir = (String) Settings.getDefault().getSetting("userDir");
	String dtdFileName = userDir + File.separator + "diary.dtd";
	File dtdFile = new File(dtdFileName);
	try {
	    if (dtdFile.exists()) return;
	    BufferedWriter writer = new BufferedWriter(new FileWriter(dtdFile));
	    StringTokenizer data = new StringTokenizer(dtd, "&", true);
	    while (data.hasMoreTokens()) {
		String token = data.nextToken();
		if (token.equals("&")) writer.newLine();
		else writer.write(token);
	    }
	    writer.close();
	} catch (IOException e) {
	    JOptionPane.showMessageDialog(null, Translator.getTranslation("ERROR.WRITE_ERROR", new String[] {dtdFileName}), Translator.getTranslation("ERROR.ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
	}
    }
    
    /**
     * String holding whole content of diary.dtd validation file.
     */
    private static String dtd = "<?xml version='1.1' encoding='" + (String) Settings.getDefault().getSetting("systemEncoding") + "'?>&&<!--- One week of working plan.&(id - number of week in year,&os - identification of operating system e.g. Linux 3.0.0-12-generic,&year - calendar year)&-->&<!ELEMENT week (day)*>&<!ATTLIST week&    id CDATA #REQUIRED&    year CDATA #REQUIRED&    os CDATA #IMPLIED&  >&&<!--- Day of week.&(finish - time when user finished to work,&date - textual identification of day in format MM-dd-yyyy e.g. 12-31-2013,&start - time when user started to work,&id - number of day in week)&-->&<!ELEMENT day (task)*>&<!ATTLIST day&    finish CDATA #IMPLIED&    date CDATA #IMPLIED&    start CDATA #IMPLIED&    id CDATA #REQUIRED&  >&&<!--- Task planned for a day.&(duration - total time how long user worked on task so far,&state - information about progress on task)&-->&<!ELEMENT task (repetition|notification|notes|keyword|description|priority|private|idle)*>&<!ATTLIST task&    duration CDATA #REQUIRED&    state CDATA #IMPLIED&  >&&<!--- Priority of a task. -->&<!ELEMENT priority (#PCDATA)>&&<!--- Short description of a task. -->&<!ELEMENT description (#PCDATA)>&&<!--- Category of a task. -->&<!ELEMENT keyword (#PCDATA)>&&<!--- Long description of a task. -->&<!ELEMENT notes (#PCDATA)>&&<!--- Identification of an idle task. -->&<!ELEMENT idle EMPTY>&&<!--- Notification about a task at specified time.&(switch - whether task should start automatically,&time - when user should start working on a task)&-->&<!ELEMENT notification EMPTY>&<!ATTLIST notification&    switch CDATA #IMPLIED&    time CDATA #IMPLIED&  >&&<!--- Identification of private task. -->&<!ELEMENT private EMPTY>&&<!--- Identification of regular task.&(frequency - type of repetition)&-->&<!ELEMENT repetition EMPTY>&<!ATTLIST repetition&    frequency CDATA #IMPLIED&  >&";
}