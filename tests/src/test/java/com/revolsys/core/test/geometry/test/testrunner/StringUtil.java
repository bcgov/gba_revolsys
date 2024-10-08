/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.revolsys.core.test.geometry.test.testrunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jeometry.common.exception.Exceptions;

/**
 *  Useful string utilities
 *
 *@author     jaquino
 *@created    June 22, 2001
 *
 * @version 1.7
 */
public class StringUtil {
  public final static String newLine = System.getProperty("line.separator");

  /**
   * Capitalizes the given string.
   *
   * @param s the string to capitalize
   * @return the capitalized string
   */
  public static String capitalize(final String s) {
    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  /**
   *  Returns true if substring is indeed a substring of string.
   */
  public static boolean contains(final String string, final String substring) {
    return string.indexOf(substring) > -1;
  }

  /**
   *  Returns true if substring is indeed a substring of string.
   *  Case-insensitive.
   */
  public static boolean containsIgnoreCase(final String string, final String substring) {
    return contains(string.toLowerCase(), substring.toLowerCase());
  }

  // Based on code from
  // http://developer.java.sun.com/developer/qow/archive/104/index.html
  public static String currentMethodName() {
    final StringWriter sw = new StringWriter();
    new Throwable().printStackTrace(new PrintWriter(sw));
    final String callStack = sw.toString();
    int atPos = callStack.indexOf("at");
    atPos = callStack.indexOf("at", atPos + 1);
    final int parenthesisPos = callStack.indexOf("(", atPos);
    return callStack.substring(atPos + 3, parenthesisPos);
  }

  /**
   *  Decodes strings returned by #encodeStartingVowels
   */
  private static String decodeStartingVowels(final String s) {
    String result = s;
    result = replaceAll(s, "!~b", " a");
    result = replaceAll(s, "!~f", " e");
    result = replaceAll(s, "!~j", " i");
    result = replaceAll(s, "!~p", " o");
    result = replaceAll(s, "!~v", " u");
    result = replaceAll(s, "!~B", " A");
    result = replaceAll(s, "!~F", " E");
    result = replaceAll(s, "!~J", " I");
    result = replaceAll(s, "!~P", " O");
    result = replaceAll(s, "!~V", " U");
    return result;
  }

  /**
   *  Replaces vowels that start words with a special code
   */
  private static String encodeStartingVowels(final String s) {
    String result = s;
    result = replaceAll(s, " a", "!~b");
    result = replaceAll(s, " e", "!~f");
    result = replaceAll(s, " i", "!~j");
    result = replaceAll(s, " o", "!~p");
    result = replaceAll(s, " u", "!~v");
    result = replaceAll(s, " A", "!~B");
    result = replaceAll(s, " E", "!~F");
    result = replaceAll(s, " I", "!~J");
    result = replaceAll(s, " O", "!~P");
    result = replaceAll(s, " U", "!~V");
    return result;
  }

  // From: Phil Hanna (pehanna@my-deja.com)
  // Subject: Re: special html characters and java???
  // Newsgroups: comp.lang.java.help
  // Date: 2000/09/16
  public static String escapeHTML(final String s) {
    replace(s, "\r\n", "\n", true);
    replace(s, "\n\r", "\n", true);
    replace(s, "\r", "\n", true);
    final StringBuilder sb = new StringBuilder();
    final int n = s.length();
    for (int i = 0; i < n; i++) {
      final char c = s.charAt(i);
      switch (c) {
        case '<':
          sb.append("&lt;");
        break;
        case '>':
          sb.append("&gt;");
        break;
        case '&':
          sb.append("&amp;");
        break;
        case '"':
          sb.append("&quot;");
        break;
        case '\n':
          sb.append("<BR>");
        break;
        default:
          sb.append(c);
        break;
      }
    }
    return sb.toString();
  }

  /**
   *  Returns d as a string truncated to the specified number of decimal places
   */
  public static String format(final double d, final int decimals) {
    final double factor = Math.pow(10, decimals);
    final double digits = Math.round(factor * d);
    return (int)Math.floor(digits / factor) + "." + (int)(digits % factor);
  }

  /**
   *  Converts the comma-delimited string into a List of trimmed strings.
   */
  public static List fromCommaDelimitedString(final String s) {
    final ArrayList result = new ArrayList();
    final StringTokenizer tokenizer = new StringTokenizer(s, ",");
    while (tokenizer.hasMoreTokens()) {
      result.add(tokenizer.nextToken().toString().trim());
    }
    return result;
  }

  /**
   *  Returns an throwable's stack trace
   */
  public static String getStackTrace(final Throwable t) {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final PrintStream ps = new PrintStream(os);
    t.printStackTrace(ps);
    return os.toString();
  }

  public static String getStackTrace(final Throwable t, final int depth) {
    String stackTrace = "";
    final StringReader stringReader = new StringReader(getStackTrace(t));
    final LineNumberReader lineNumberReader = new LineNumberReader(stringReader);
    for (int i = 0; i < depth; i++) {
      try {
        stackTrace += lineNumberReader.readLine() + newLine;
      } catch (final IOException e) {
        return Exceptions.throwUncheckedException(e);
      }
    }
    return stackTrace;
  }

  /**
   *  Converts the milliseconds value into a String of the form "9d 22h 15m 8s".
   */
  public static String getTimeString(final long milliseconds) {
    long remainder = milliseconds;
    final long days = remainder / 86400000;
    remainder = remainder % 86400000;
    final long hours = remainder / 3600000;
    remainder = remainder % 3600000;
    final long minutes = remainder / 60000;
    remainder = remainder % 60000;
    final long seconds = remainder / 1000;
    return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
  }

  public static String indent(final String original, final int spaces) {
    final String indent = stringOfChar(' ', spaces);
    String indented = indent + original;

    indented = replaceAll(indented, "\r\n", "<<<<.CRLF.>>>>");
    indented = replaceAll(indented, "\r", "<<<<.CR.>>>>");
    indented = replaceAll(indented, "\n", "<<<<.LF.>>>>");

    indented = replaceAll(indented, "<<<<.CRLF.>>>>", "\r\n" + indent);
    indented = replaceAll(indented, "<<<<.CR.>>>>", "\r" + indent);
    indented = replaceAll(indented, "<<<<.LF.>>>>", "\n" + indent);
    return indented;
  }

  /**
   *  Returns the position of the first occurrence of the given character found
   *  in s starting at start. Ignores text within pairs of parentheses. Returns
   *  -1 if no occurrence is found.
   */
  public static int indexOfIgnoreParentheses(final char c, final String s, final int start) {
    int level = 0;
    for (int i = start; i < s.length(); i++) {
      final char other = s.charAt(i);
      if (other == '(') {
        level++;
      } else if (other == ')') {
        level--;
      } else if (other == c && level == 0) {
        return i;
      }
    }
    return -1;
  }

  /**
   *  Returns true if s can be converted to an int.
   */
  public static boolean isInteger(final String s) {
    try {
      Integer.valueOf(s);
      return true;
    } catch (final NumberFormatException e) {
      return false;
    }
  }

  /**
   *  Pads the String with the given character until it has the given length. If
   *  original is longer than the given length, returns original.
   */
  public static String leftPad(final String original, final int length, final char padChar) {
    if (original.length() >= length) {
      return original;
    }
    return stringOfChar(padChar, length - original.length()) + original;
  }

  /**
   *  Replaces consecutive instances of characters with single instances.
   *  Case-insensitive.
   */
  public static String removeConsecutiveDuplicates(final String s) {
    String previous = "??";
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      final String c = s.charAt(i) + "";
      if (!previous.equalsIgnoreCase(c)) {
        result.append(c);
      }
      previous = c;
    }
    return result.toString();
  }

  public static String removeFromEnd(final String s, final String strToRemove) {
    if (s == null || strToRemove == null) {
      return s;
    }
    if (s.length() < strToRemove.length()) {
      return s;
    }
    final int subLoc = s.length() - strToRemove.length();
    if (s.substring(subLoc).equalsIgnoreCase(strToRemove)) {
      return s.substring(0, subLoc);
    }
    return s;
  }

  /**
   *  Removes vowels from the string. Case-insensitive.
   */
  public static String removeVowels(final String s) {
    String result = s;
    result = replaceAll(s, "a", "");
    result = replaceAll(s, "e", "");
    result = replaceAll(s, "i", "");
    result = replaceAll(s, "o", "");
    result = replaceAll(s, "u", "");
    result = replaceAll(s, "A", "");
    result = replaceAll(s, "E", "");
    result = replaceAll(s, "I", "");
    result = replaceAll(s, "O", "");
    result = replaceAll(s, "U", "");
    return result;
  }

  /**
   *  Removes vowels from the string except those that start words.
   *  Case-insensitive.
   */
  public static String removeVowelsSkipStarts(final String s) {
    String result = s;
    if (!s.startsWith(" ")) {
      result = result.substring(1);
    }
    result = encodeStartingVowels(result);
    result = removeVowels(result);
    result = decodeStartingVowels(result);
    if (!s.startsWith(" ")) {
      result = s.charAt(0) + result;
    }
    return result;
  }

  /**
   *  Returns a string with all occurrences of oldChar replaced by newStr
   */
  public static String replace(final String str, final char oldChar, final String newStr) {
    final StringBuilder buf = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      final char ch = str.charAt(i);
      if (ch == oldChar) {
        buf.append(newStr);
      } else {
        buf.append(ch);
      }
    }
    return buf.toString();
  }

  /**
   *  Returns original with occurrences of oldSubstring replaced by
   *  newSubstring. Set all to true to replace all occurrences, or false to
   *  replace the first occurrence only.
   */
  public static String replace(final String original, final String oldSubstring,
    final String newSubstring, final boolean all) {
    final StringBuilder b = new StringBuilder(original);
    replace(b, oldSubstring, newSubstring, all);
    return b.toString();
  }

  /**
   *  Replaces all instances of the String o with the String n in the
   *  StringBuilder orig if all is true, or only the first instance if all is
   *  false. Posted by Steve Chapel <schapel@breakthr.com> on UseNet
   */
  public static void replace(final StringBuilder orig, final String o, final String n,
    final boolean all) {
    if (orig == null || o == null || o.length() == 0 || n == null) {
      throw new IllegalArgumentException("Null or zero-length String");
    }
    int i = 0;
    while (i + o.length() <= orig.length()) {
      if (orig.substring(i, i + o.length()).equals(o)) {
        orig.replace(i, i + o.length(), n);
        if (!all) {
          break;
        } else {
          i += n.length();
        }
      } else {
        i++;
      }
    }
  }

  /**
   *  Returns original with all occurrences of oldSubstring replaced by
   *  newSubstring
   */
  public static String replaceAll(final String original, final String oldSubstring,
    final String newSubstring) {
    return replace(original, oldSubstring, newSubstring, true);
  }

  /**
   *  Returns original with the first occurrenc of oldSubstring replaced by
   *  newSubstring
   */
  public static String replaceFirst(final String original, final String oldSubstring,
    final String newSubstring) {
    return replace(original, oldSubstring, newSubstring, false);
  }

  /**
   *  Pads the String with the given character until it has the given length. If
   *  original is longer than the given length, returns original.
   */
  public static String rightPad(final String original, final int length, final char padChar) {
    if (original.length() >= length) {
      return original;
    }
    return original + stringOfChar(padChar, length - original.length());
  }

  /**
   *  Returns a String of the given length consisting entirely of the given
   *  character
   */
  public static String stringOfChar(final char ch, final int count) {
    final StringBuilder buf = new StringBuilder();
    for (int i = 0; i < count; i++) {
      buf.append(ch);
    }
    return buf.toString();
  }

  /**
   *  Removes the HTML tags from the given String, inserting line breaks at
   *  appropriate places. Needs a little work.
   */
  public static String stripHTMLTags(final String original) {
    // Strip the tags from the HTML description
    boolean skipping = false;
    boolean writing = false;
    final StringBuilder buffer = new StringBuilder();
    final StringTokenizer tokenizer = new StringTokenizer(original, "<>", true);
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
      if (token.equalsIgnoreCase("<")) {
        skipping = true;
        writing = false;
        continue;
      }
      if (token.equalsIgnoreCase(">")) {
        skipping = false;
        continue;
      }
      if (!skipping) {
        if (token.trim().length() == 0) {
          continue;
        }
        if (!writing) {
          buffer.append("\n");
        }
        writing = true;
        buffer.append(token.trim());
      }
    }
    return buffer.toString();
  }

  /**
   *  Returns the elements of c separated by commas. c must not be empty.
   */
  public static String toCommaDelimitedString(final Collection c) {
    if (c.isEmpty()) {
      throw new IllegalArgumentException();
    }
    final StringBuilder result = new StringBuilder();
    for (final Iterator i = c.iterator(); i.hasNext();) {
      final Object o = i.next();
      result.append(", " + o.toString());
    }
    return result.substring(1);
  }

  /**
   *  Returns the elements of c separated by commas and enclosed in
   *  single-quotes
   */
  public static String toCommaDelimitedStringInQuotes(final Collection c) {
    final StringBuilder result = new StringBuilder();
    for (final Iterator i = c.iterator(); i.hasNext();) {
      final Object o = i.next();
      result.append(",'" + o.toString() + "'");
    }
    return result.substring(1);
  }

  /**
   *  Returns the elements of v in lowercase
   */
  public static Vector toLowerCase(final List v) {
    final Vector result = new Vector();
    for (final Iterator i = v.iterator(); i.hasNext();) {
      final String s = i.next().toString();
      result.add(s.toLowerCase());
    }
    return result;
  }

  /**
   *  If s is null, returns "null"; otherwise, returns s.
   */
  public static String toStringNeverNull(final Object o) {
    return o == null ? "null" : o.toString();
  }

  /**
   *  Returns the elements of v in uppercase
   */
  public static Vector toUpperCase(final Vector v) {
    final Vector result = new Vector();
    for (final Enumeration e = v.elements(); e.hasMoreElements();) {
      final String s = e.nextElement().toString();
      result.add(s.toUpperCase());
    }
    return result;
  }

  /**
   *  Line-wraps a string s by inserting CR-LF instead of the first space after the nth
   *  columns.
   */
  public static String wrap(final String s, final int n) {
    final StringBuilder b = new StringBuilder();
    boolean wrapPending = false;
    for (int i = 0; i < s.length(); i++) {
      if (i % n == 0 && i > 0) {
        wrapPending = true;
      }
      final char c = s.charAt(i);
      if (wrapPending && c == ' ') {
        b.append("\n");
        wrapPending = false;
      } else {
        b.append(c);
      }
    }
    return b.toString();
  }

}
