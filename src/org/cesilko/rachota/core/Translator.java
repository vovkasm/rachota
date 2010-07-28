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
 * Created on July 30, 2004  9:19 PM
 * Translator.java
 */
/*
 * Translator.java
 *
 * Created on July 30, 2004, 9:19 PM
 */
package org.cesilko.rachota.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.cesilko.rachota.gui.Tools;

/** Translator class providing localization.
 *
 * @author  Jiri Kovalsky
 */
public class Translator {

    /** Resource bundle representing appropriate dictionary of translations. */
    private static PropertyResourceBundle dictionary = null;
    /** Resource bundle representing fallback/default dictionary of translations. */
    private static PropertyResourceBundle fallbackDictionary = null;
    /** Logger used by the Translator class to log errors. */
    private static final Logger log = Logger.getLogger(Translator.class.getName());
    /** The path of the dictionary as used by Rachota for storing dictionaries. */
    private static final String DICTIONARY_PATH_FORMAT = "/org/cesilko/rachota/core/%s";
    /** The default dictionary to use with Rachota if none is found for your current locale. */
    private static final String FALLBACK_DICTIONARY = "Dictionary_en_US.properties";
    /** Flag to indicate whether or not the dictionary has been looked up. */
    private static boolean dictionaryLookupDone = false;

    /** Finds appropriate dictionary based on set country and language locales.
     */
    private static void findDictionary() {
        setupFallback();

        String dictionaryName = (String) Settings.getDefault().getSetting("dictionary");

        InputStream inputStream = null;
        if (!dictionaryName.equals(FALLBACK_DICTIONARY)) {
            // If the dictionary name is not equal to the fallback dictionary, try to fetch it.
            inputStream = Translator.class.getResourceAsStream(String.format(DICTIONARY_PATH_FORMAT, dictionaryName));
            if (null != inputStream) {
                try {
                    // Try to read the dictionary.
                    dictionary = new PropertyResourceBundle(inputStream);
                } catch (IOException ex) {
                    // Failed to read the dictionary due to I/O error...
                    log.log(Level.WARNING, "Unable to read dictionary file.", ex);
                }
            } else {
                log.log(Level.WARNING, String.format("Dictionary \"%s\" not found.", dictionaryName));
            }
        }
        if (null == dictionary && null == fallbackDictionary) {
            // This will generally not happen, but if it does, we should be able to handle it.
            log.severe("No dictionaries found, unable to continue running...");
            // Although we are trying to provide translations for everything, in this instance it doesn't matter, since we didn't find any dictionary...
            JOptionPane.showMessageDialog(null, "No dictionaries found, unable to continue running...", "ERROR: No dictionaries found.", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        dictionaryLookupDone = true;
    }

    /**
     * Sets up the fallback dictionary.
     */
    private static void setupFallback() {
        InputStream stream = Translator.class.getResourceAsStream(String.format(DICTIONARY_PATH_FORMAT, FALLBACK_DICTIONARY));
        if (null != stream) {
            try {
                fallbackDictionary = new PropertyResourceBundle(stream);
            } catch (IOException ex) {
                log.log(Level.WARNING, "Unable to read fallback dictionary.", ex);
            }
        }
        if (null == fallbackDictionary) {
            log.log(Level.WARNING, String.format("Fallback dictionary \"%s\" not found.", FALLBACK_DICTIONARY));
        }
    }

    /** Returns the translated string associated with the provided key either in
     * the active dictionary, or the fallback dictionary. If the key is not associated
     * with a translated string, the key is returned.
     * @param key the key to find the associated translated string for.
     * @return the translated string associated with the provided key either in
     * the active dictionary, or the fallback dictionary.
     */
    private static String lookupWord(String key) {
        if (!dictionaryLookupDone) {
            // Set up the dictionaries, we need something to look up words in :-)
            findDictionary();
        }
        String result = null;
        if (null != dictionary) {
            try {
                result = dictionary.getString(key);
            } catch (MissingResourceException ex) {
                // This means the current dictionary didn't have the string we wanted, check the fallback.
                log.log(Level.INFO, String.format("Unable to find the translation bound to key %s in current dictionary.", key), ex);
            }
        }
        if (null == result) {
            try {
                result = fallbackDictionary.getString(key);
            } catch (MissingResourceException inEx) {
                log.log(Level.WARNING, String.format("Unable to find the translation bound to key %s.", key), inEx);
            }
        }
        // This is not the best we can do if the word does not exist, but atleast we will see it in the application...
        return result != null ? result : key;
    }

    /** Returns translation of given word in default language. If required
     * removes all & chars in addition.
     * @param word Word to be translated.
     * @param removeAnds Whether & chars should be removed.
     * @return Translation of given word in default language.
     */
    private static String getTranslation(String word, boolean removeAnds) {
            String translation = lookupWord(word);
            if (removeAnds) {
                translation = removeAnds(translation);
            }
            return translation;
    }

    /** Removes the & characters in the provided string.
     * @param word The string to remove & characters in.
     * @return the provided string without any & characters.
     */
    private static String removeAnds(String word){
        return Tools.replaceAll(word, "&", "");
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
        for (int i = 0; i < count; i++) {
            translation = Tools.replaceAll(translation, "${" + i + "}", substitutions[i]);
        }
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
