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
 * Created on July 30, 2004  9:19 PM
 * Translator.java
 */
/*
 * Translator.java
 *
 * Created on July 30, 2004, 9:19 PM
 */

package org.cesilko.rachota.core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.cesilko.rachota.gui.Tools;

/** Translator class providing localization.
 *
 * @author  Jiri Kovalsky
 */
public class Translator {
    
    /** Resource bundle representing appropriate dictionary of translations. */
    private static PropertyResourceBundle dictionary;
    
    /** Finds appropriate dictionary based on set country and language locales.
     */
    private static void findDictionary() {
        java.net.URL url = Translator.class.getResource("Translator.class");
        String location = url.getFile();
        String dictionaryName = (String) Settings.getDefault().getSetting("dictionary");
        
        InputStream inputStream;
        try {
            if (location.indexOf(".jar!") == -1)
                inputStream = new FileInputStream(location.substring(0, location.indexOf("Translator.class")) + dictionaryName);
            else { // http://rachota.sourceforge.net/rachota_21.jar!/org/cesilko/rachota/core/Translator.class
                JarFile jarFile;
                if (location.indexOf("rachota.sourceforge.net") != -1) {
                    String fileName = location.substring(0, location.indexOf("!/") + 2);
                    url = new URL("jar:http://rachota.sourceforge.net/rachota_21.jar!/");
                    JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                    jarFile = jarConnection.getJarFile();
                } else {
                    String fileName = System.getProperty("os.name").indexOf("Windows") == -1 ? "/" : "";
                    fileName = fileName + location.substring(6, location.indexOf(".jar") + 4);
                    fileName = Tools.replaceAll(fileName, "%20", " "); // Space in path is replaced by %20 and this needs to be changed
                    jarFile = new JarFile(fileName);
                }
                ZipEntry entry = jarFile.getEntry("org/cesilko/rachota/core/" + dictionaryName);
                if (entry == null) {
                    entry = jarFile.getEntry("org/cesilko/rachota/core/Dictionary_en_US.properties");
                    Settings.getDefault().setSetting("dictionary", "Dictionary_en_US.properties");
                }
                inputStream = jarFile.getInputStream(entry);
            }
            dictionary = new PropertyResourceBundle(inputStream);
        } catch (Exception e) {
            System.out.println("Error: Reading from " + dictionaryName + " dictionary failed.");
            e.printStackTrace();
        }
        
    }
    
    /** Returns translation of given word in default language. If required
     * removes all & chars in addition.
     * @param word Word to be translated.
     * @param removeAnds Whether & chars should be removed.
     * @return Translation of given word in default language.
     */
    private static String getTranslation(String word, boolean removeAnds) {
        if (dictionary == null) findDictionary();
        try {
            String translation = dictionary.getString(word);
            if (removeAnds) translation = Tools.replaceAll(translation, "&", "");
            return translation;
        } catch (Exception e) {
            System.out.println("Error: Reading translation of " + word + " word failed.");
            e.printStackTrace();
            return null;
        }
    }
    
    /** Returns translation of given word in default language.
     * @param word Word to be translated.
     * @return Translation of given word in default language.
     */
    public static String getTranslation(String word) {
        return getTranslation(word, true);
    }
    
    /** Returns translation of given word in default language and replaces arguments.
     * @param word Word to be translated containing ${0}, ${1} etc. elements.
     * @param substitutions Words that will be used to replace ${0}, ${1} etc. elements.
     * @return Translation of given word in default language with substitutions.
     */
    public static String getTranslation(String word, String[] substitutions) {
        String translation = getTranslation(word, true);
        int count = substitutions.length;
        for (int i=0; i<count; i++)
            translation = Tools.replaceAll(translation, "${" + i + "}", substitutions[i]);
        return translation;
    }
    
    /** Returns mnemonic char found in translated word. As the mnemonic is considered
     * the character after & char. If & is not found in the translation, invisible
     * character is returned.
     * @param word Word to be translated and where mnemonic char will be searched for.
     * @return Mnemonic i.e. character right after first occurence of & character in
     * the translated word.
     */
    public static char getMnemonic(String word) {
        String translation = getTranslation(word, false);
        int index = translation.indexOf("&");
        return (index == -1) ? 31 : translation.charAt(index + 1);
    }
}
