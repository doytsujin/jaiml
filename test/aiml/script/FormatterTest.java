package aiml.script;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {

  public void testFormal() {
    assertEquals("Abc Def", Formatter.formal("abc def"));
    assertEquals("Abc Def.ghi", Formatter.formal("abc def.ghi"));
  }

  public void testSentence() {
    assertEquals("This is a sentence.",
        Formatter.sentence("this is a sentence."));
  }

  public void testSingleSentenceWithoutPunctuation() {
    assertEquals("This is another sentence",
        Formatter.sentence("this is another sentence"));
  }

  public void testTwoSentencesWithPunctuation() {
    assertEquals("First sentence. Second sentence.",
        Formatter.sentence("first sentence. second sentence."));
  }

  public void testTwoSentencesWithoutPunctuation() {
    assertEquals("First sentence. Second sentence",
        Formatter.sentence("first sentence. second sentence"));
  }

}
