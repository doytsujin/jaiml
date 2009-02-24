/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package aiml.text;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;

public class SentenceSplitter {
  BreakIterator sentenceIterator;

  public SentenceSplitter() {
    sentenceIterator = BreakIterator.getSentenceInstance();
  }

  public SentenceSplitter(String rules) {
    sentenceIterator = new RuleBasedBreakIterator(rules);
  }

  public List<String> split(String text) {
    ArrayList<String> result = new ArrayList<String>();
    sentenceIterator.setText(text);
    int start = sentenceIterator.first();
    for (int end = sentenceIterator.next(); end != BreakIterator.DONE; start = end, end = sentenceIterator.next()) {
      result.add(text.substring(start, end));
    }

    return result;
  }
}
