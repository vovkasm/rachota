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
 * The Original Software is NetBeans.
 * The Initial Developer of the Original Software is Jiri Kovalsky
 * Portions created by Jiri Kovalsky are Copyright (C) 2006
 * All Rights Reserved.
 *
 * Contributor(s): Jiri Kovalsky
 * Created on June 23, 2005  8:21 AM
 * RegularTasksScanner.java
 */

package org.cesilko.rachota.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import org.cesilko.rachota.gui.Tools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** This is a DOM tree scanner of regular tasks plan XML files.
 */
public class RegularTasksScanner {
    
    /**
     * XML document that is being loaded.
     */
    Document document;
    
    /**
     * Creates new regular tasks scanner.
     * @param document XML regular tasks document to be loaded.
     */
    public RegularTasksScanner(Document document) {
	this.document = document;
    }
    
    /**
     * Loads XML regular tasks document given in the scanner constructor.
     */
    public void loadDocument() {
	Element planElement = document.getDocumentElement();
	loadPlan(planElement);
    }
    
    /**
     * Loads regular tasks plan from given XML element.
     * @param element XML element representing a plan of regular tasks.
     */
    private void loadPlan(Element element) {
	NodeList nodes = element.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element regularTaskElement = (Element) node;
		loadRegularTask(regularTaskElement);
	    }
	}
    }
    
    /**
     * Loads regular task from given XML element.
     * @param regularTaskElement XML element representing a regular task.
     */
    private void loadRegularTask(Element regularTaskElement) {
	long duration = Tools.getTime(regularTaskElement.getAttributeNode("duration").getValue());
	int state = Integer.parseInt(regularTaskElement.getAttributeNode("state").getValue());
	RegularTask regularTask = null;
	int priority = 0;
	String description = "";
	String keyword = "";
	String notes = "";
	long notification = -1;
	boolean automaticStart = false;
	boolean privateTask = false;
	int repetition = -1;
	
	NodeList nodes = regularTaskElement.getChildNodes();
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
	    }
	}
	Date notificationTime = (notification == -1 ? null : new Date(notification));
	regularTask = new RegularTask(description, keyword, notes, priority, state, duration, notificationTime, automaticStart, privateTask, repetition);
	Plan.getDefault().addRegularTask(regularTask);
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
     * Creates regular_tasks.dtd file that is used for automatic XML validation of regular tasks plan files.
     */
    public static void createDTD() {
	String userDir = (String) Settings.getDefault().getSetting("userDir");
	String dtdFileName = userDir + File.separator + "regular_tasks.dtd";
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
     * String holding whole content of regular_tasks.dtd validation file.
     */
    private static String dtd = "<?xml version='1.0' encoding='" + (String) Settings.getDefault().getSetting("systemEncoding") + "'?>&&<!--- Plan of regular tasks. -->&<!ELEMENT regular_tasks (task)*>&&<!--- Regular task of the plan.&(duration - total time how long user worked on task so far,&state - information about progress on task)&-->&<!ELEMENT task (repetition|notification|notes|keyword|description|priority)*>&<!ATTLIST task&    duration CDATA #IMPLIED&    state CDATA #IMPLIED&  >&&<!--- Priority of a task. -->&<!ELEMENT priority (#PCDATA)>&&<!--- Short description of a task. -->&<!ELEMENT description (#PCDATA)>&&<!--- Category of a task. -->&<!ELEMENT keyword (#PCDATA)>&&<!--- Long description of a task. -->&<!ELEMENT notes (#PCDATA)>&&<!--- Notification about a task at specified time.&(switch - whether task should start automatically,&time - when user should start working on a task)&-->&<!ELEMENT notification EMPTY>&<!ATTLIST notification&    switch CDATA #IMPLIED&    time CDATA #IMPLIED&  >&&<!--- Identification of regular task.&(frequency - type of repetition)&-->&<!ELEMENT repetition EMPTY>&<!ATTLIST repetition&    frequency CDATA #IMPLIED&  >&";
}