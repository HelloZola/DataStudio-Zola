/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * Title: SQLWordRule
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class SQLWordRule implements IRule {

    private SQLDelimiterRule delimRule;
    private IToken defaultToken;
    private Map<String, IToken> fWords = new HashMap<>();
    private StringBuilder fBuffer = new StringBuilder();
    private char[][] delimiters;

    /**
     * Instantiates a new SQL word rule.
     *
     * @param delimRule the delim rule
     * @param defaultToken the default token
     */
    public SQLWordRule(SQLDelimiterRule delimRule, IToken defaultToken) {
        this.delimRule = delimRule;
        this.defaultToken = defaultToken;
    }

    /**
     * Checks for word.
     *
     * @param word the word
     * @return true, if successful
     */
    public boolean hasWord(String word) {
        return fWords.containsKey(word.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Adds the word.
     *
     * @param word the word
     * @param token the token
     */
    public void addWord(String word, IToken token) {
        fWords.put(word.toLowerCase(Locale.ENGLISH), token);
    }

    /**
     * Evaluate.
     *
     * @param scanner the scanner
     * @return the i token
     */
    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        int chr = scanner.read();
        if (chr != ICharacterScanner.EOF && Character.isUnicodeIdentifierStart(chr)) {
            fBuffer.setLength(0);
            delimiters = delimRule.getDelimiters();
            do {
                fBuffer.append((char) chr);
                chr = scanner.read();
            } while (chr != ICharacterScanner.EOF && isWordPart((char) chr, scanner));
            scanner.unread();

            String buffer = fBuffer.toString().toLowerCase(Locale.ENGLISH);
            IToken token = fWords.get(buffer);

            if (token != null) {
                return token;
            }

            if (defaultToken.isUndefined()) {
                unreadBuffer(scanner);
            }

            return defaultToken;
        }

        scanner.unread();
        return Token.UNDEFINED;
    }

    private boolean isWordPart(char c, ICharacterScanner scanner) {
        if (!Character.isUnicodeIdentifierPart(c) && c != '$') {
            return false;
        }

        if (null == delimiters) {
            return false;
        }

        // Check for delimiter
        for (char[] wordDelimiter : delimiters) {
            if (!Character.isLetter(c) && c == wordDelimiter[0]) {
                if (wordDelimiter.length == 1) {
                    return false;
                }
                int charsRead = 0;
                boolean matches = true;
                for (int i = 1; i < wordDelimiter.length; i++) {
                    int c2 = scanner.read();
                    charsRead++;
                    if (c2 == ICharacterScanner.EOF) {
                        break;
                    }
                    if (c2 != wordDelimiter[i]) {
                        matches = false;
                        break;
                    }
                }
                for (int i = 0; i < charsRead; i++) {
                    scanner.unread();
                }
                if (matches) {
                    return false;
                }
            }
        }

        return true;
    }

    private void unreadBuffer(ICharacterScanner scanner) {
        for (int i = fBuffer.length() - 1; i >= 0; i--) {
            scanner.unread();
        }
    }

}