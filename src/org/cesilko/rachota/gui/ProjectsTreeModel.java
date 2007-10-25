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
 * Created on 8 September 2007  13:39
 * ProjectsTreeModel.java
 */

package org.cesilko.rachota.gui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.cesilko.rachota.core.Day;
import org.cesilko.rachota.core.Task;

/** Model representing tree view on Rachota projects i.e. groups of
 * tasks with same category.
 * @author Jiri Kovalsky
 */
class ProjectsTreeModel extends DefaultTreeModel {
    
    public static final int NODE_TYPE_ROOT = 1;
    public static final int NODE_TYPE_CATEGORY = 2;
    public static final int NODE_TYPE_TASK = 3;

    static class ProjectsTreeListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent event) {}
    }

    /** Days whose tasks are being displayed. */
    private Vector days;

    public ProjectsTreeModel(Vector days) {
        super(new DefaultMutableTreeNode("All projects"));
        setDays(days);
    }
    
    public void setDays(Vector days) {
        this.days = days;
        initializeProjectTree();
    }

    public Object getSelectedNode(TreePath treePath) {
        DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        return lastNode.getUserObject();
    }

    int getSelectedNodeType(TreePath treePath) {
        return treePath.getPathCount();
    }

    private ProjectsTreeModel.CategoryNode getCategoryNode(Vector categories, String name) {
        Iterator iterator = categories.iterator();
        while (iterator.hasNext()) {
            CategoryNode categoryNode = (CategoryNode) iterator.next();
            if (categoryNode.getName().equals(name)) {
                return categoryNode;
            }
        }
        return null;
    }

    private Vector getCategoryNodes() {
        CategoryNode categoryNode = new CategoryNode("Uncategorized");
        Vector categories = new Vector();
        categories.add(categoryNode);
        Iterator iterator = days.iterator();
        while (iterator.hasNext()) {
            Day day = (Day) iterator.next();
            Iterator tasks = day.getTasks().iterator();
            while (tasks.hasNext()) {
                Task task = (Task) tasks.next();
                if (task.getDuration() == 0) {
                    continue;
                }
                String keywords = task.getKeyword();
                if (!keywords.equals("")) {
                    StringTokenizer tokenizer = new StringTokenizer(keywords, " ");
                    while (tokenizer.hasMoreElements()) {
                        String category = (String) tokenizer.nextElement();
                        categoryNode = getCategoryNode(categories, category);
                        if (categoryNode != null) {
                            categoryNode.addTask(task);
                        } else {
                            categoryNode = new CategoryNode(category);
                            categoryNode.addTask(task);
                            categories.add(categoryNode);
                        }
                    }
                } else {
                    categoryNode = getCategoryNode(categories, "Uncategorized");
                    if (task.getDuration() != 0) {
                        categoryNode.addTask(task);
                    }
                }
            }
        }
        return categories;
    }

    private void initializeProjectTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        Iterator iterator = sortCategoryNodes(getCategoryNodes()).iterator();
        while (iterator.hasNext()) {
            CategoryNode categoryNode = (CategoryNode) iterator.next();
            DefaultMutableTreeNode category = new DefaultMutableTreeNode(categoryNode);
            Iterator tasks = categoryNode.getTaskNodes().iterator();
            while (tasks.hasNext()) {
                TaskNode taskNode = (TaskNode) tasks.next();
                DefaultMutableTreeNode task = new DefaultMutableTreeNode(taskNode);
                category.add(task);
            }
            root.add(category);
        } setRoot(root);
    }

    private Vector sortCategoryNodes(Vector categoryNodesVector) {
        int categoryNodesCount = categoryNodesVector.size();
        CategoryNode[] categoryNodesArray = new CategoryNode[categoryNodesCount];
        for (int i = 0; i < categoryNodesCount; i++)
            categoryNodesArray[i] = (ProjectsTreeModel.CategoryNode) categoryNodesVector.get(i);
        Arrays.sort(categoryNodesArray);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.removeAllChildren();
        categoryNodesVector.removeAllElements();
        for (int i = 0; i < categoryNodesCount; i++) {
            categoryNodesArray[i].sortTaskNodes();
            categoryNodesVector.add(categoryNodesArray[i]);
        }
        return categoryNodesVector;
    }

    class CategoryNode implements Comparable {

        private String name;
        private Vector taskNodes;
        public static final int PROPERTY_PRIORITY = 0;
        public static final int PROPERTY_STATE = 1;

        CategoryNode(String name) {
            this.name = name;
            taskNodes = new Vector();
        }

        private void addTask(Task task) {
            Iterator iterator = taskNodes.iterator();
            while (iterator.hasNext()) {
                TaskNode taskNode = (TaskNode) iterator.next();
                if (taskNode.getDescription().equals(task.getDescription())) {
                    taskNode.addTask(task);
                    return;
                }
            }
            TaskNode taskNode = new TaskNode(task);
            taskNodes.add(taskNode);
        }

        public String getName() {
            return name;
        }

        public String toString() {
            String text = name;
            text = text + " [" + Tools.getTimeShort(getTotalTime()) + "]";
            return text;
        }

        public Vector getTaskNodes() {
            return taskNodes;
        }

        public long getTotalTime() {
            Iterator iterator = taskNodes.iterator();
            long duration = 0;
            while (iterator.hasNext()) {
                TaskNode taskNode = (TaskNode) iterator.next();
                duration = duration + taskNode.getTotalTime();
            }
            return duration;
        }
        
        public int getAverageValue(int property) {
            Iterator taskNodesIterator = taskNodes.iterator();
            int totalProperty = 0;
            int numberOfTasks = 0;
            while (taskNodesIterator.hasNext()) {
                TaskNode taskNode = (TaskNode) taskNodesIterator.next();
                Iterator tasksIterator = taskNode.getTasks().iterator();
                while (tasksIterator.hasNext()) {
                    Task task = (Task) tasksIterator.next();
                    totalProperty = totalProperty + (property == PROPERTY_PRIORITY ? task.getPriority() : task.getState());
                    numberOfTasks++;
                }
            }
            float averageProperty = (float) totalProperty / (float) numberOfTasks;
            if (averageProperty < (Task.PRIORITY_MEDIUM - 0.66)) return Task.PRIORITY_HIGH;
            if (averageProperty > (Task.PRIORITY_MEDIUM + 0.66)) return Task.PRIORITY_LOW;
            return Task.PRIORITY_MEDIUM;
        }

        public int compareTo(Object object) {
            if (object == null) return 0;
            CategoryNode categoryNode = (ProjectsTreeModel.CategoryNode) object;
            if (categoryNode.getTotalTime() > getTotalTime()) return 1;
            return -1;
        }
        
        public void sortTaskNodes() {
            int taskNodesCount = taskNodes.size();
            TaskNode[] taskNodesArray = new TaskNode[taskNodesCount];
            for (int i = 0; i < taskNodesCount; i++)
                taskNodesArray[i] = (ProjectsTreeModel.TaskNode) taskNodes.get(i);
            Arrays.sort(taskNodesArray);
            taskNodes.removeAllElements();
            for (int i = 0; i < taskNodesArray.length; i++) {
                taskNodes.add(taskNodesArray[i]);
            }
        }
    }

    class TaskNode implements Comparable {

        private String description;
        private Vector tasks;
        public static final int PROPERTY_PRIORITY = 0;
        public static final int PROPERTY_STATE = 1;

        TaskNode(Task task) {
            description = task.getDescription();
            tasks = new Vector();
            tasks.add(task);
        }

        private void addTask(Task task) {
            getTasks().add(task);
        }

        public String toString() {
            return description + " [" + Tools.getTimeShort(getTotalTime()) + "]";
        }

        public long getTotalTime() {
            Iterator iterator = getTasks().iterator();
            long duration = 0;
            while (iterator.hasNext()) {
                Task task = (Task) iterator.next();
                duration = duration + task.getDuration();
            }
            return duration;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo(Object object) {
            if (object == null) return 0;
            TaskNode taskNode = (ProjectsTreeModel.TaskNode) object;
            if (taskNode.getTotalTime() > getTotalTime()) return 1;
            return -1;
        }

        public Vector getTasks() {
            return tasks;
        }

        public int getAverageValue(int property) {
            int totalProperty = 0;
            int numberOfTasks = 0;
            Iterator tasksIterator = tasks.iterator();
            while (tasksIterator.hasNext()) {
                Task task = (Task) tasksIterator.next();
                totalProperty = totalProperty + (property == PROPERTY_PRIORITY ? task.getPriority() : task.getState());
                numberOfTasks++;
            }
            float averageProperty = (float) totalProperty / (float) numberOfTasks;
            if (averageProperty < (Task.PRIORITY_MEDIUM - 0.66)) return Task.PRIORITY_HIGH;
            if (averageProperty > (Task.PRIORITY_MEDIUM + 0.66)) return Task.PRIORITY_LOW;
            return Task.PRIORITY_MEDIUM;
        }
    }
}