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
 * Created on December 8, 2006  7:31 PM
 * RegularTask.java
 */

package org.cesilko.rachota.core;

import java.io.IOException;
import java.io.PrintStream;

/** Task that repeats every day or once a week.
 *
 * @author Jiri Kovalsky
 */
public class IdleTask extends Task {
    
    /** Creates a new instance of idle task. */
    public IdleTask() {
        super(Translator.getTranslation("TASK.IDLE_DESCRIPTION"), Translator.getTranslation("TASK.IDLE_KEYWORD"), Translator.getTranslation("TASK.IDLE_NOTES"), Task.PRIORITY_LOW, Task.STATE_NEW, 0, null, false, false);
    }
    
    /** Returns true meaning that the task measure idle time. */
    public boolean isIdleTask() {
        return true;
    }

    /** Writes information that this is an idle task.
     * @param stream Print stream where info about this idle task will be written.
     * @throws java.io.IOException Input/output exception thrown when some error during writing information occurs.
     */
    public void writeRepetition(PrintStream stream) throws IOException {
        stream.println("            <idle/>");
    }
}