package aiml.parser.script;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TextElement implements ScriptElement {
  private String text = null;
  public ScriptElement parse(XmlPullParser parser) throws XmlPullParserException, IOException {
    text=parser.getText();
    parser.next();
    return this;
  }

  public String evaluate() {
    return printable(text);
  }
  protected String printable(char ch) {
    if(ch == '\n') {
        return "\\n";
    } else if(ch == '\r') {
        return "\\r";
    } else if(ch == '\t') {
        return "\\t";
    } if(ch > 127 || ch < 32) {
        StringBuffer buf = new StringBuffer("\\u");
        String hex = Integer.toHexString(ch);
        for (int i = 0; i < 4-hex.length(); i++)
        {
            buf.append('0');
        }
        buf.append(hex);
        return buf.toString();
    }
    return ""+ch;
  }

  protected String printable(String s) {
      if(s == null) return null;
      StringBuffer buf = new StringBuffer();
      for(int i = 0; i < s.length(); ++i) {
          buf.append(printable(s.charAt(i)));
      }
      s = buf.toString();
      return s;
  }
}
