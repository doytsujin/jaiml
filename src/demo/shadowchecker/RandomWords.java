/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package demo.shadowchecker;

import java.util.Random;

/**
 * Small utility class to generate random "words".
 * 
 * @author Kim Sullivan
 * 
 */
public class RandomWords {
  private Random rng = new Random();
  private int wordLength;
  private int minWordLength;
  private int wordCount;
  private int minWordCount;

  public RandomWords() {
    wordLength = 5;
    minWordLength = 1;
    wordCount = 5;
    minWordCount = 1;
  }

  /**
   * Appends the random letter '$' to the string builder
   * 
   * @param sb
   */
  private void appendRandomLetter(StringBuilder sb) {
    sb.append('$');
  }

  /**
   * Appends a random word to the string builder
   * 
   * @param sb
   */
  public void appendRandomWord(StringBuilder sb) {
    int length = (int) Math.abs(rng.nextGaussian() * wordLength - minWordLength) +
        minWordLength;
    for (int i = 0; i < length; i++) {
      appendRandomLetter(sb);
    }
  }

  /**
   * Appends a sequence of random words to the string builder
   * 
   * @param sb
   */
  public void appendRandomWords(StringBuilder sb) {
    int count = (int) Math.abs(rng.nextGaussian() * wordCount) + minWordCount;
    for (int i = 0; i < count; i++) {
      appendRandomWord(sb);
      sb.append(' ');
    }
    sb.deleteCharAt(sb.length() - minWordCount);

  }
}
