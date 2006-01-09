package aiml.parser;

/**
 * <p>Title: AIML Pull Parser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author Kim Sullivan
 * @version 1.0
 */
import java.io.*;
import java.util.*;
import org.xmlpull.v1.*;

public class AIMLPullParser {
  InputStream is; //in case the requested encoding doesn't match the detected encoding, and we need to re-open the stream
  Reader in;
  String encoding;
  char ch; //the current character in the input
  HashMap<String,String> entityReplacementText=new HashMap<String,String>();

  class Attribute {
    String name;
    String value;
    boolean isdefault=false;
    String type="CDATA";
    Attribute(String name,String value) {
      assert (name!=null && !name.equals("")) : "Name must not be empty";
      assert (value!=null) : "Value must not be null";
      this.name=name;
      this.value=value;
    }
    String getNamespace() {
      return "";
    }
    String getName() {
      return name;
    }
    String getPrefix() {
      return null;
    }
    String getType() {
      return type;
    }
    boolean isDefault() {
      return isdefault;
    }
    String getValue() {
      return value;
    }
  }

  HashMap<String,Attribute> attributeMap = new HashMap<String,Attribute>();
  ArrayList<Attribute> attributeList=new ArrayList<Attribute>();

  private boolean readCR;
  private int lineNumber;
  private int colNumber;

  public static final char EOF='\uFFFF';
  public static final char CR='\r';
  public static final char LF='\n';
  public static final char QUOT='"';
  public static final char APOS='\'';
  public static final char AMP='&';
  public static final char HASH='#';
  public static final char X='x';
  public static final char LT='<';
  public static final char GT='>';
  public static final char EXCL='!';
  public static final char QUES='?';
  public static final char EQ='=';
  public static final char SEMICOLON=';';

  public static final int PI_START      = 0;      // '<?'PITarget
  public static final int XMLDECL_START = 1;      // '<?xml'
  public static final int PI_END        = 2;      // '?>'
  public static final int STAG_START    = 3;      // '<'Name
  public static final int TAG_END       = 4;      // '>'
  public static final int EMPTY_TAG_END = 5;      // '/>'
  public static final int ETAG_START    = 6;      // '</'Name
  public static final int REF_START     = 7;      // '&'
  public static final int CDATA_START   = 8;      // '<![CDATA['
  public static final int CDATA_END     = 9;      // ']]>'
  public static final int COMMENT_START =10;      // '<!--'
  public static final int COMMENT_END   =11;      // '--' (actually, it's '-->' but since the string '--' isn't allowed to appear except as part of the end marker we can conveniently use this
  public static final int DOCTYPE_START =12;      // '<!doctype'

  public AIMLPullParser() {
  }

  private void resetState() {
    lineNumber=colNumber=0;
    readCR=false;
    setDefaultEntityReplacementText();
    attributeMap.clear();
    attributeList.clear();
    is=null;
    in=null;
    encoding=null;
  }
  private void setDefaultEntityReplacementText() {
    entityReplacementText.clear();
    try {
      defineEntityReplacementText("amp", "&");
      defineEntityReplacementText("lt", "<");
      defineEntityReplacementText("gt", ">");
      defineEntityReplacementText("quot", "'");
      defineEntityReplacementText("apos", "\"");
    } catch (XmlPullParserException e) {};
  }
  public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException{
    if (entityReplacementText.containsKey(entityName))
      throw new XmlPullParserException("Cannot redefine entity replacement text");
    entityReplacementText.put(entityName,replacementText);
  }
  public void setInput(java.io.InputStream inputStream,java.lang.String inputEncoding) throws XmlPullParserException{
    resetState();
    try {
      is=inputStream;
      InputStreamReader isr;
      if (inputEncoding!=null) {
        isr = new InputStreamReader(inputStream, inputEncoding);
        encoding=isr.getEncoding();
      }
      else {
        isr = new InputStreamReader(inputStream);
      }

      in = new BufferedReader(isr);
    }
    catch (UnsupportedEncodingException e) {
      throw new XmlPullParserException("Unsupported encoding",null,e);
    };
  }
  public void setInput(java.io.Reader in) {
    resetState();
    this.in=in;
  }
  public String getInputEncoding() {
    return encoding;
  }

  public char nextChar() throws XmlPullParserException{
    try {
      ch = (char) in.read();
      colNumber++;
      switch (ch) {
        case LF:
          if (readCR) { // Processing CRLF, so silently skip the LF
            ch = (char) in.read();
          }
          else {
            lineNumber++;
            colNumber = 0;
          }
          readCR = false;
          break;
        case CR:
          ch = LF;
          lineNumber++;
          colNumber = 0;
          readCR = true;
          break;
        default:
          readCR = false;
      }

      return ch;
    } catch (IOException e) {
      throw new XmlPullParserException("IO Exception",null,e);
    }
  }
  public char getChar() {
    return ch;
  }
  public boolean isS() {
    //[3]   	S	   ::=   	(#x20 | #x9 | #xD | #xA)+
    switch (ch) {
      case '\u0020':
      case '\u0009':
      case '\r':
      case '\n': return true;
      default: return false;

    }
  }
  public void skipS() throws XmlPullParserException {
    while (isS()) nextChar();
  }
  public void nextS() throws XmlPullParserException {
    if (!isS()) throw new XmlPullParserException("Syntax error, expecting production\n[3]   	S	   ::=   	(#x20 | #x9 | #xD | #xA)+");
    skipS();
  }

  public boolean isChar() {
    //[2]   	Char	   ::=   	#x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    return ( ch=='\u0009' ||
             ch=='\n' ||
             ch=='\r' ||
            (ch>='\u0020' && ch<='\uD7FF') ||
            (ch>='\uE000' && ch<='\uFFFD'));
  }
  public boolean isExtender() {
    //[89]   	Extender	   ::=   	#x00B7 | #x02D0 | #x02D1 | #x0387 | #x0640 | #x0E46 | #x0EC6 | #x3005 | [#x3031-#x3035] | [#x309D-#x309E] | [#x30FC-#x30FE]
    return ( ch=='\u00B7' ||
             ch=='\u02D0' ||
             ch=='\u02D1' ||
             ch=='\u0387' ||
             ch=='\u0640' ||
             ch=='\u0E46' ||
             ch=='\u0EC6' ||
             ch=='\u3005' ||
            (ch>='\u3031' && ch<='\u3035') ||
            (ch>='\u309D' && ch<='\u309E') ||
            (ch>='\u30FC' && ch<='\u30FF'));
  }
  public boolean isDigit() {
    //[88]   	Digit	   ::=   	[#x0030-#x0039] | [#x0660-#x0669] | [#x06F0-#x06F9] | [#x0966-#x096F] | [#x09E6-#x09EF] | [#x0A66-#x0A6F] | [#x0AE6-#x0AEF] | [#x0B66-#x0B6F] | [#x0BE7-#x0BEF] | [#x0C66-#x0C6F] | [#x0CE6-#x0CEF] | [#x0D66-#x0D6F] | [#x0E50-#x0E59] | [#x0ED0-#x0ED9] | [#x0F20-#x0F29]
    return ((ch>='\u0030' && ch<='\u0039') ||
            (ch>='\u0660' && ch<='\u0669') ||
            (ch>='\u06F0' && ch<='\u06F9') ||
            (ch>='\u0966' && ch<='\u096F') ||
            (ch>='\u09E6' && ch<='\u09EF') ||
            (ch>='\u0A66' && ch<='\u0A6F') ||
            (ch>='\u0AE6' && ch<='\u0AEF') ||
            (ch>='\u0B66' && ch<='\u0B6F') ||
            (ch>='\u0BE7' && ch<='\u0BEF') ||
            (ch>='\u0C66' && ch<='\u0C6F') ||
            (ch>='\u0CE6' && ch<='\u0CEF') ||
            (ch>='\u0D66' && ch<='\u0D6F') ||
            (ch>='\u0E50' && ch<='\u0E59') ||
            (ch>='\u0ED0' && ch<='\u0ED9') ||
            (ch>='\u0F20' && ch<='\u0F29'));
  }
  public boolean isCombiningChar(){
    //[87]   	CombiningChar	   ::=   	[#x0300-#x0345] | [#x0360-#x0361] | [#x0483-#x0486] | [#x0591-#x05A1] | [#x05A3-#x05B9] | [#x05BB-#x05BD] | #x05BF | [#x05C1-#x05C2] | #x05C4 | [#x064B-#x0652] | #x0670 | [#x06D6-#x06DC] | [#x06DD-#x06DF] | [#x06E0-#x06E4] | [#x06E7-#x06E8] | [#x06EA-#x06ED] | [#x0901-#x0903] | #x093C | [#x093E-#x094C] | #x094D | [#x0951-#x0954] | [#x0962-#x0963] | [#x0981-#x0983] | #x09BC | #x09BE | #x09BF | [#x09C0-#x09C4] | [#x09C7-#x09C8] | [#x09CB-#x09CD] | #x09D7 | [#x09E2-#x09E3] | #x0A02 | #x0A3C | #x0A3E | #x0A3F | [#x0A40-#x0A42] | [#x0A47-#x0A48] | [#x0A4B-#x0A4D] | [#x0A70-#x0A71] | [#x0A81-#x0A83] | #x0ABC | [#x0ABE-#x0AC5] | [#x0AC7-#x0AC9] | [#x0ACB-#x0ACD] | [#x0B01-#x0B03] | #x0B3C | [#x0B3E-#x0B43] | [#x0B47-#x0B48] | [#x0B4B-#x0B4D] | [#x0B56-#x0B57] | [#x0B82-#x0B83] | [#x0BBE-#x0BC2] | [#x0BC6-#x0BC8] | [#x0BCA-#x0BCD] | #x0BD7 | [#x0C01-#x0C03] | [#x0C3E-#x0C44] | [#x0C46-#x0C48] | [#x0C4A-#x0C4D] | [#x0C55-#x0C56] | [#x0C82-#x0C83] | [#x0CBE-#x0CC4] | [#x0CC6-#x0CC8] | [#x0CCA-#x0CCD] | [#x0CD5-#x0CD6] | [#x0D02-#x0D03] | [#x0D3E-#x0D43] | [#x0D46-#x0D48] | [#x0D4A-#x0D4D] | #x0D57 | #x0E31 | [#x0E34-#x0E3A] | [#x0E47-#x0E4E] | #x0EB1 | [#x0EB4-#x0EB9] | [#x0EBB-#x0EBC] | [#x0EC8-#x0ECD] | [#x0F18-#x0F19] | #x0F35 | #x0F37 | #x0F39 | #x0F3E | #x0F3F | [#x0F71-#x0F84] | [#x0F86-#x0F8B] | [#x0F90-#x0F95] | #x0F97 | [#x0F99-#x0FAD] | [#x0FB1-#x0FB7] | #x0FB9 | [#x20D0-#x20DC] | #x20E1 | [#x302A-#x302F] | #x3099 | #x309A
    return ((ch>='\u0300' && ch<='\u0345') ||
            (ch>='\u0360' && ch<='\u0361') ||
            (ch>='\u0483' && ch<='\u0486') ||
            (ch>='\u0591' && ch<='\u05A1') ||
            (ch>='\u05A3' && ch<='\u05B9') ||
            (ch>='\u05BB' && ch<='\u05BD') ||
             ch=='\u05BF' ||
            (ch>='\u05C1' && ch<='\u05C2') ||
             ch=='\u05C4' ||
            (ch>='\u064B' && ch<='\u0652') ||
             ch=='\u0670' ||
            (ch>='\u06D6' && ch<='\u06DC') ||
            (ch>='\u06DD' && ch<='\u06DF') ||
            (ch>='\u06E0' && ch<='\u06E4') ||
            (ch>='\u06E7' && ch<='\u06E8') ||
            (ch>='\u06EA' && ch<='\u06ED') ||
            (ch>='\u0901' && ch<='\u0903') ||
             ch=='\u093C' ||
            (ch>='\u093E' && ch<='\u094C') ||
             ch=='\u094D' ||
            (ch>='\u0951' && ch<='\u0954') ||
            (ch>='\u0962' && ch<='\u0963') ||
            (ch>='\u0981' && ch<='\u0983') ||
             ch=='\u09BC' ||
             ch=='\u09BE' ||
             ch=='\u09BF' ||
            (ch>='\u09C0' && ch<='\u09C4') ||
            (ch>='\u09C7' && ch<='\u09C8') ||
            (ch>='\u09CB' && ch<='\u09CD') ||
             ch=='\u09D7' ||
            (ch>='\u09E2' && ch<='\u09E3') ||
             ch=='\u0A02' ||
             ch=='\u0A3C' ||
             ch=='\u0A3E' ||
             ch=='\u0A3F' ||
            (ch>='\u0A40' && ch<='\u0A42') ||
            (ch>='\u0A47' && ch<='\u0A48') ||
            (ch>='\u0A4B' && ch<='\u0A4D') ||
            (ch>='\u0A70' && ch<='\u0A71') ||
            (ch>='\u0A81' && ch<='\u0A83') ||
             ch=='\u0ABC' ||
            (ch>='\u0ABE' && ch<='\u0AC5') ||
            (ch>='\u0AC7' && ch<='\u0AC9') ||
            (ch>='\u0ACB' && ch<='\u0ACD') ||
            (ch>='\u0B01' && ch<='\u0B03') ||
             ch=='\u0B3C' ||
            (ch>='\u0B3E' && ch<='\u0B43') ||
            (ch>='\u0B47' && ch<='\u0B48') ||
            (ch>='\u0B4B' && ch<='\u0B4D') ||
            (ch>='\u0B56' && ch<='\u0B57') ||
            (ch>='\u0B82' && ch<='\u0B83') ||
            (ch>='\u0BBE' && ch<='\u0BC2') ||
            (ch>='\u0BC6' && ch<='\u0BC8') ||
            (ch>='\u0BCA' && ch<='\u0BCD') ||
             ch=='\u0BD7' ||
            (ch>='\u0C01' && ch<='\u0C03') ||
            (ch>='\u0C3E' && ch<='\u0C44') ||
            (ch>='\u0C46' && ch<='\u0C48') ||
            (ch>='\u0C4A' && ch<='\u0C4D') ||
            (ch>='\u0C55' && ch<='\u0C56') ||
            (ch>='\u0C82' && ch<='\u0C83') ||
            (ch>='\u0CBE' && ch<='\u0CC4') ||
            (ch>='\u0CC6' && ch<='\u0CC8') ||
            (ch>='\u0CCA' && ch<='\u0CCD') ||
            (ch>='\u0CD5' && ch<='\u0CD6') ||
            (ch>='\u0D02' && ch<='\u0D03') ||
            (ch>='\u0D3E' && ch<='\u0D43') ||
            (ch>='\u0D46' && ch<='\u0D48') ||
            (ch>='\u0D4A' && ch<='\u0D4D') ||
             ch=='\u0D57' ||
             ch=='\u0E31' ||
            (ch>='\u0E34' && ch<='\u0E3A') ||
            (ch>='\u0E47' && ch<='\u0E4E') ||
             ch=='\u0EB1' ||
            (ch>='\u0EB4' && ch<='\u0EB9') ||
            (ch>='\u0EBB' && ch<='\u0EBC') ||
            (ch>='\u0EC8' && ch<='\u0ECD') ||
            (ch>='\u0F18' && ch<='\u0F19') ||
             ch=='\u0F35' ||
             ch=='\u0F37' ||
             ch=='\u0F39' ||
             ch=='\u0F3E' ||
             ch=='\u0F3F' ||
            (ch>='\u0F71' && ch<='\u0F84') ||
            (ch>='\u0F86' && ch<='\u0F8B') ||
            (ch>='\u0F90' && ch<='\u0F95') ||
             ch=='\u0F97' ||
            (ch>='\u0F99' && ch<='\u0FAD') ||
            (ch>='\u0FB1' && ch<='\u0FB7') ||
             ch=='\u0FB9' ||
            (ch>='\u20D0' && ch<='\u20DC') ||
             ch=='\u20E1' ||
            (ch>='\u302A' && ch<='\u302F') ||
             ch=='\u3099' ||
             ch=='\u309A');
  }
  public boolean isIdeographic() {
    //[86]   	Ideographic	   ::=   	[#x4E00-#x9FA5] | #x3007 | [#x3021-#x3029]
    return ((ch>='\u4E00' && ch<='\u9FA5') ||
             ch=='\u3007' ||
            (ch>='\u3021' && ch<='\u3029'));
  }
  public boolean isBaseChar() {
    //[85]   	BaseChar	   ::=   	[#x0041-#x005A] | [#x0061-#x007A] | [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x00FF] | [#x0100-#x0131] | [#x0134-#x013E] | [#x0141-#x0148] | [#x014A-#x017E] | [#x0180-#x01C3] | [#x01CD-#x01F0] | [#x01F4-#x01F5] | [#x01FA-#x0217] | [#x0250-#x02A8] | [#x02BB-#x02C1] | #x0386 | [#x0388-#x038A] | #x038C | [#x038E-#x03A1] | [#x03A3-#x03CE] | [#x03D0-#x03D6] | #x03DA | #x03DC | #x03DE | #x03E0 | [#x03E2-#x03F3] | [#x0401-#x040C] | [#x040E-#x044F] | [#x0451-#x045C] | [#x045E-#x0481] | [#x0490-#x04C4] | [#x04C7-#x04C8] | [#x04CB-#x04CC] | [#x04D0-#x04EB] | [#x04EE-#x04F5] | [#x04F8-#x04F9] | [#x0531-#x0556] | #x0559 | [#x0561-#x0586] | [#x05D0-#x05EA] | [#x05F0-#x05F2] | [#x0621-#x063A] | [#x0641-#x064A] | [#x0671-#x06B7] | [#x06BA-#x06BE] | [#x06C0-#x06CE] | [#x06D0-#x06D3] | #x06D5 | [#x06E5-#x06E6] | [#x0905-#x0939] | #x093D | [#x0958-#x0961] | [#x0985-#x098C] | [#x098F-#x0990] | [#x0993-#x09A8] | [#x09AA-#x09B0] | #x09B2 | [#x09B6-#x09B9] | [#x09DC-#x09DD] | [#x09DF-#x09E1] | [#x09F0-#x09F1] | [#x0A05-#x0A0A] | [#x0A0F-#x0A10] | [#x0A13-#x0A28] | [#x0A2A-#x0A30] | [#x0A32-#x0A33] | [#x0A35-#x0A36] | [#x0A38-#x0A39] | [#x0A59-#x0A5C] | #x0A5E | [#x0A72-#x0A74] | [#x0A85-#x0A8B] | #x0A8D | [#x0A8F-#x0A91] | [#x0A93-#x0AA8] | [#x0AAA-#x0AB0] | [#x0AB2-#x0AB3] | [#x0AB5-#x0AB9] | #x0ABD | #x0AE0 | [#x0B05-#x0B0C] | [#x0B0F-#x0B10] | [#x0B13-#x0B28] | [#x0B2A-#x0B30] | [#x0B32-#x0B33] | [#x0B36-#x0B39] | #x0B3D | [#x0B5C-#x0B5D] | [#x0B5F-#x0B61] | [#x0B85-#x0B8A] | [#x0B8E-#x0B90] | [#x0B92-#x0B95] | [#x0B99-#x0B9A] | #x0B9C | [#x0B9E-#x0B9F] | [#x0BA3-#x0BA4] | [#x0BA8-#x0BAA] | [#x0BAE-#x0BB5] | [#x0BB7-#x0BB9] | [#x0C05-#x0C0C] | [#x0C0E-#x0C10] | [#x0C12-#x0C28] | [#x0C2A-#x0C33] | [#x0C35-#x0C39] | [#x0C60-#x0C61] | [#x0C85-#x0C8C] | [#x0C8E-#x0C90] | [#x0C92-#x0CA8] | [#x0CAA-#x0CB3] | [#x0CB5-#x0CB9] | #x0CDE | [#x0CE0-#x0CE1] | [#x0D05-#x0D0C] | [#x0D0E-#x0D10] | [#x0D12-#x0D28] | [#x0D2A-#x0D39] | [#x0D60-#x0D61] | [#x0E01-#x0E2E] | #x0E30 | [#x0E32-#x0E33] | [#x0E40-#x0E45] | [#x0E81-#x0E82] | #x0E84 | [#x0E87-#x0E88] | #x0E8A | #x0E8D | [#x0E94-#x0E97] | [#x0E99-#x0E9F] | [#x0EA1-#x0EA3] | #x0EA5 | #x0EA7 | [#x0EAA-#x0EAB] | [#x0EAD-#x0EAE] | #x0EB0 | [#x0EB2-#x0EB3] | #x0EBD | [#x0EC0-#x0EC4] | [#x0F40-#x0F47] | [#x0F49-#x0F69] | [#x10A0-#x10C5] | [#x10D0-#x10F6] | #x1100 | [#x1102-#x1103] | [#x1105-#x1107] | #x1109 | [#x110B-#x110C] | [#x110E-#x1112] | #x113C | #x113E | #x1140 | #x114C | #x114E | #x1150 | [#x1154-#x1155] | #x1159 | [#x115F-#x1161] | #x1163 | #x1165 | #x1167 | #x1169 | [#x116D-#x116E] | [#x1172-#x1173] | #x1175 | #x119E | #x11A8 | #x11AB | [#x11AE-#x11AF] | [#x11B7-#x11B8] | #x11BA | [#x11BC-#x11C2] | #x11EB | #x11F0 | #x11F9 | [#x1E00-#x1E9B] | [#x1EA0-#x1EF9] | [#x1F00-#x1F15] | [#x1F18-#x1F1D] | [#x1F20-#x1F45] | [#x1F48-#x1F4D] | [#x1F50-#x1F57] | #x1F59 | #x1F5B | #x1F5D | [#x1F5F-#x1F7D] | [#x1F80-#x1FB4] | [#x1FB6-#x1FBC] | #x1FBE | [#x1FC2-#x1FC4] | [#x1FC6-#x1FCC] | [#x1FD0-#x1FD3] | [#x1FD6-#x1FDB] | [#x1FE0-#x1FEC] | [#x1FF2-#x1FF4] | [#x1FF6-#x1FFC] | #x2126 | [#x212A-#x212B] | #x212E | [#x2180-#x2182] | [#x3041-#x3094] | [#x30A1-#x30FA] | [#x3105-#x312C] | [#xAC00-#xD7A3]
    return ((ch>='\u0041' && ch<='\u005A') ||
            (ch>='\u0061' && ch<='\u007A') ||
            (ch>='\u00C0' && ch<='\u00D6') ||
            (ch>='\u00D8' && ch<='\u00F6') ||
            (ch>='\u00F8' && ch<='\u00FF') ||
            (ch>='\u0100' && ch<='\u0131') ||
            (ch>='\u0134' && ch<='\u013E') ||
            (ch>='\u0141' && ch<='\u0148') ||
            (ch>='\u014A' && ch<='\u017E') ||
            (ch>='\u0180' && ch<='\u01C3') ||
            (ch>='\u01CD' && ch<='\u01F0') ||
            (ch>='\u01F4' && ch<='\u01F5') ||
            (ch>='\u01FA' && ch<='\u0217') ||
            (ch>='\u0250' && ch<='\u02A8') ||
            (ch>='\u02BB' && ch<='\u02C1') ||
             ch=='\u0386' ||
            (ch>='\u0388' && ch<='\u038A') ||
             ch=='\u038C' ||
            (ch>='\u038E' && ch<='\u03A1') ||
            (ch>='\u03A3' && ch<='\u03CE') ||
            (ch>='\u03D0' && ch<='\u03D6') ||
             ch=='\u03DA' ||
             ch=='\u03DC' ||
             ch=='\u03DE' ||
             ch=='\u03E0' ||
            (ch>='\u03E2' && ch<='\u03F3') ||
            (ch>='\u0401' && ch<='\u040C') ||
            (ch>='\u040E' && ch<='\u044F') ||
            (ch>='\u0451' && ch<='\u045C') ||
            (ch>='\u045E' && ch<='\u0481') ||
            (ch>='\u0490' && ch<='\u04C4') ||
            (ch>='\u04C7' && ch<='\u04C8') ||
            (ch>='\u04CB' && ch<='\u04CC') ||
            (ch>='\u04D0' && ch<='\u04EB') ||
            (ch>='\u04EE' && ch<='\u04F5') ||
            (ch>='\u04F8' && ch<='\u04F9') ||
            (ch>='\u0531' && ch<='\u0556') ||
             ch=='\u0559' ||
            (ch>='\u0561' && ch<='\u0586') ||
            (ch>='\u05D0' && ch<='\u05EA') ||
            (ch>='\u05F0' && ch<='\u05F2') ||
            (ch>='\u0621' && ch<='\u063A') ||
            (ch>='\u0641' && ch<='\u064A') ||
            (ch>='\u0671' && ch<='\u06B7') ||
            (ch>='\u06BA' && ch<='\u06BE') ||
            (ch>='\u06C0' && ch<='\u06CE') ||
            (ch>='\u06D0' && ch<='\u06D3') ||
             ch=='\u06D5' ||
            (ch>='\u06E5' && ch<='\u06E6') ||
            (ch>='\u0905' && ch<='\u0939') ||
             ch=='\u093D' ||
            (ch>='\u0958' && ch<='\u0961') ||
            (ch>='\u0985' && ch<='\u098C') ||
            (ch>='\u098F' && ch<='\u0990') ||
            (ch>='\u0993' && ch<='\u09A8') ||
            (ch>='\u09AA' && ch<='\u09B0') ||
             ch=='\u09B2' ||
            (ch>='\u09B6' && ch<='\u09B9') ||
            (ch>='\u09DC' && ch<='\u09DD') ||
            (ch>='\u09DF' && ch<='\u09E1') ||
            (ch>='\u09F0' && ch<='\u09F1') ||
            (ch>='\u0A05' && ch<='\u0A0A') ||
            (ch>='\u0A0F' && ch<='\u0A10') ||
            (ch>='\u0A13' && ch<='\u0A28') ||
            (ch>='\u0A2A' && ch<='\u0A30') ||
            (ch>='\u0A32' && ch<='\u0A33') ||
            (ch>='\u0A35' && ch<='\u0A36') ||
            (ch>='\u0A38' && ch<='\u0A39') ||
            (ch>='\u0A59' && ch<='\u0A5C') ||
             ch=='\u0A5E' ||
            (ch>='\u0A72' && ch<='\u0A74') ||
            (ch>='\u0A85' && ch<='\u0A8B') ||
             ch=='\u0A8D' ||
            (ch>='\u0A8F' && ch<='\u0A91') ||
            (ch>='\u0A93' && ch<='\u0AA8') ||
            (ch>='\u0AAA' && ch<='\u0AB0') ||
            (ch>='\u0AB2' && ch<='\u0AB3') ||
            (ch>='\u0AB5' && ch<='\u0AB9') ||
             ch=='\u0ABD' ||
             ch=='\u0AE0' ||
            (ch>='\u0B05' && ch<='\u0B0C') ||
            (ch>='\u0B0F' && ch<='\u0B10') ||
            (ch>='\u0B13' && ch<='\u0B28') ||
            (ch>='\u0B2A' && ch<='\u0B30') ||
            (ch>='\u0B32' && ch<='\u0B33') ||
            (ch>='\u0B36' && ch<='\u0B39') ||
             ch=='\u0B3D' ||
            (ch>='\u0B5C' && ch<='\u0B5D') ||
            (ch>='\u0B5F' && ch<='\u0B61') ||
            (ch>='\u0B85' && ch<='\u0B8A') ||
            (ch>='\u0B8E' && ch<='\u0B90') ||
            (ch>='\u0B92' && ch<='\u0B95') ||
            (ch>='\u0B99' && ch<='\u0B9A') ||
             ch=='\u0B9C' ||
            (ch>='\u0B9E' && ch<='\u0B9F') ||
            (ch>='\u0BA3' && ch<='\u0BA4') ||
            (ch>='\u0BA8' && ch<='\u0BAA') ||
            (ch>='\u0BAE' && ch<='\u0BB5') ||
            (ch>='\u0BB7' && ch<='\u0BB9') ||
            (ch>='\u0C05' && ch<='\u0C0C') ||
            (ch>='\u0C0E' && ch<='\u0C10') ||
            (ch>='\u0C12' && ch<='\u0C28') ||
            (ch>='\u0C2A' && ch<='\u0C33') ||
            (ch>='\u0C35' && ch<='\u0C39') ||
            (ch>='\u0C60' && ch<='\u0C61') ||
            (ch>='\u0C85' && ch<='\u0C8C') ||
            (ch>='\u0C8E' && ch<='\u0C90') ||
            (ch>='\u0C92' && ch<='\u0CA8') ||
            (ch>='\u0CAA' && ch<='\u0CB3') ||
            (ch>='\u0CB5' && ch<='\u0CB9') ||
             ch=='\u0CDE' ||
            (ch>='\u0CE0' && ch<='\u0CE1') ||
            (ch>='\u0D05' && ch<='\u0D0C') ||
            (ch>='\u0D0E' && ch<='\u0D10') ||
            (ch>='\u0D12' && ch<='\u0D28') ||
            (ch>='\u0D2A' && ch<='\u0D39') ||
            (ch>='\u0D60' && ch<='\u0D61') ||
            (ch>='\u0E01' && ch<='\u0E2E') ||
             ch=='\u0E30' ||
            (ch>='\u0E32' && ch<='\u0E33') ||
            (ch>='\u0E40' && ch<='\u0E45') ||
            (ch>='\u0E81' && ch<='\u0E82') ||
             ch=='\u0E84' ||
            (ch>='\u0E87' && ch<='\u0E88') ||
             ch=='\u0E8A' ||
             ch=='\u0E8D' ||
            (ch>='\u0E94' && ch<='\u0E97') ||
            (ch>='\u0E99' && ch<='\u0E9F') ||
            (ch>='\u0EA1' && ch<='\u0EA3') ||
             ch=='\u0EA5' ||
             ch=='\u0EA7' ||
            (ch>='\u0EAA' && ch<='\u0EAB') ||
            (ch>='\u0EAD' && ch<='\u0EAE') ||
             ch=='\u0EB0' ||
            (ch>='\u0EB2' && ch<='\u0EB3') ||
             ch=='\u0EBD' ||
            (ch>='\u0EC0' && ch<='\u0EC4') ||
            (ch>='\u0F40' && ch<='\u0F47') ||
            (ch>='\u0F49' && ch<='\u0F69') ||
            (ch>='\u10A0' && ch<='\u10C5') ||
            (ch>='\u10D0' && ch<='\u10F6') ||
             ch=='\u1100' ||
            (ch>='\u1102' && ch<='\u1103') ||
            (ch>='\u1105' && ch<='\u1107') ||
             ch=='\u1109' ||
            (ch>='\u110B' && ch<='\u110C') ||
            (ch>='\u110E' && ch<='\u1112') ||
             ch=='\u113C' ||
             ch=='\u113E' ||
             ch=='\u1140' ||
             ch=='\u114C' ||
             ch=='\u114E' ||
             ch=='\u1150' ||
            (ch>='\u1154' && ch<='\u1155') ||
             ch=='\u1159' ||
            (ch>='\u115F' && ch<='\u1161') ||
             ch=='\u1163' ||
             ch=='\u1165' ||
             ch=='\u1167' ||
             ch=='\u1169' ||
            (ch>='\u116D' && ch<='\u116E') ||
            (ch>='\u1172' && ch<='\u1173') ||
             ch=='\u1175' ||
             ch=='\u119E' ||
             ch=='\u11A8' ||
             ch=='\u11AB' ||
            (ch>='\u11AE' && ch<='\u11AF') ||
            (ch>='\u11B7' && ch<='\u11B8') ||
             ch=='\u11BA' ||
            (ch>='\u11BC' && ch<='\u11C2') ||
             ch=='\u11EB' ||
             ch=='\u11F0' ||
             ch=='\u11F9' ||
            (ch>='\u1E00' && ch<='\u1E9B') ||
            (ch>='\u1EA0' && ch<='\u1EF9') ||
            (ch>='\u1F00' && ch<='\u1F15') ||
            (ch>='\u1F18' && ch<='\u1F1D') ||
            (ch>='\u1F20' && ch<='\u1F45') ||
            (ch>='\u1F48' && ch<='\u1F4D') ||
            (ch>='\u1F50' && ch<='\u1F57') ||
             ch=='\u1F59' ||
             ch=='\u1F5B' ||
             ch=='\u1F5D' ||
            (ch>='\u1F5F' && ch<='\u1F7D') ||
            (ch>='\u1F80' && ch<='\u1FB4') ||
            (ch>='\u1FB6' && ch<='\u1FBC') ||
             ch=='\u1FBE' ||
            (ch>='\u1FC2' && ch<='\u1FC4') ||
            (ch>='\u1FC6' && ch<='\u1FCC') ||
            (ch>='\u1FD0' && ch<='\u1FD3') ||
            (ch>='\u1FD6' && ch<='\u1FDB') ||
            (ch>='\u1FE0' && ch<='\u1FEC') ||
            (ch>='\u1FF2' && ch<='\u1FF4') ||
            (ch>='\u1FF6' && ch<='\u1FFC') ||
             ch=='\u2126' ||
            (ch>='\u212A' && ch<='\u212B') ||
             ch=='\u212E' ||
            (ch>='\u2180' && ch<='\u2182') ||
            (ch>='\u3041' && ch<='\u3094') ||
            (ch>='\u30A1' && ch<='\u30FA') ||
            (ch>='\u3105' && ch<='\u312C') ||
            (ch>='\uAC00' && ch<='\uD7A3'));
  }
  public boolean isLetter() {
    //[84]   	Letter	   ::=   	BaseChar | Ideographic
    return isBaseChar() || isIdeographic();
  }
  public boolean isNameChar() {
    //[4]   	NameChar	   ::=   	Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar | Extender
    return (isLetter() || isDigit() || ch=='.' || ch=='-' || ch=='_' || ch==':' || isCombiningChar() || isExtender());
  }
  public boolean isNameFirst() {
   //[5]   	Name	   ::=   	(Letter | '_' | ':') (NameChar)*
   return (isLetter() || ch=='_' || ch==':');
  }

  public String nextName() throws XmlPullParserException {
    //[5]   	Name	   ::=   	(Letter | '_' | ':') (NameChar)*
    if (!isNameFirst()) throw new XmlPullParserException("Syntax error, expecting production\n[5]   	Name	   ::=   	(Letter | '_' | ':') (NameChar)*");
    StringBuffer result=new StringBuffer();
    result.append(ch);
    while ((nextChar()!=EOF)&& isNameChar())
      result.append(ch);
    return result.toString();
  }
  public void nextEq() throws XmlPullParserException {
    // [25]   	Eq	   ::=   	S? '=' S?
    if (!isS()&&ch!=EQ)
      throw new XmlPullParserException("Syntax error, expecting production\n[25]   	Eq	   ::=   	S? '=' S?");
    skipS();
    if (ch!=EQ)
      throw new XmlPullParserException("Syntax error, expecting production\n[25]   	Eq	   ::=   	S? '=' S?");
    nextChar();
    skipS();
  }
  public int getLineNumber() {
    return lineNumber;
  }
  public int getColumnNumber() {
    return colNumber;
  }

  public boolean isDecDigit() {
    // [0-9]
    return (ch>='0' && ch <='9');
  }
  public boolean isHexDigit() {
    // [0-9a-fA-F]
    return ((ch>='0' && ch <='9') || (ch>='a' && ch<='f') || (ch>='A' && ch<='F'));
  }
  public String nextReference() throws XmlPullParserException{
    //[67]   	Reference	   ::=   	EntityRef | CharRef
    if (ch!=AMP) throw new XmlPullParserException("Syntax error, production [67] Referencee must start with &");
    nextChar();
    StringBuffer result= new StringBuffer();
    if (isNameFirst()) { //[68]   	EntityRef	   ::=   	'&' Name ';'
      String name=nextName();
      if (entityReplacementText.containsKey(name))
        result.append(entityReplacementText.get(name));
      else
        result.append(AMP).append(name).append(SEMICOLON);
    } else if (ch==HASH) {//[66]   	CharRef	   ::=   	'&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
      nextChar();
      int radix;
      StringBuffer codepointBuffer=new StringBuffer();
      if (ch==X) {//[66]   	CharRef	   ::=   	'&#x' [0-9a-fA-F]+ ';'
        radix=16;
        nextChar();
        do {
          if (isHexDigit())
            codepointBuffer.append(ch);
          else
            throw new XmlPullParserException("Syntax error, invalid hexadecimal digit '"+ch+"' in character reference");
          nextChar();
        } while (isHexDigit());
      } else {//[66]   	CharRef	   ::=   	'&#' [0-9]+ ';'
	radix=10;
        do {
          if (isDecDigit())
            codepointBuffer.append(ch);
          else
            throw new XmlPullParserException("Syntax error, invalid decimal digit '"+ch+"' in character reference");
          nextChar();
        } while (isDecDigit());
      }
      int codepoint;
      try {
        codepoint = Integer.parseInt(codepointBuffer.toString(),radix);
      } catch (NumberFormatException e) {
        throw new XmlPullParserException("Syntax error, bad character reference '"+codepointBuffer +"'");
      }
      result.appendCodePoint(codepoint);
    } else {
      throw new XmlPullParserException("Syntax error, bad entity reference");
    }
    if (ch==';') {
      nextChar();
      return result.toString();
    } else {
        throw new XmlPullParserException("Syntax error, production [67] Reference must end with ';'");
    }

  }

  public String nextAttValue() throws XmlPullParserException{
    //[10]   	AttValue	   ::=   	'"' ([^<&"] | Reference)* '"' |  "'" ([^<&'] | Reference)* "'"
    if ((ch!=QUOT) && (ch!=APOS)) {
      System.out.println("((["+ch+"]!=["+QUOT+"]) || ([["+ch+"]!=["+APOS+"]))");
      throw new XmlPullParserException("Syntax error, attribute value must begin with quote or apostrophe");
    }
    char delim=ch;
    StringBuffer result=new StringBuffer();
    nextChar();
    do {

      if (ch==delim) {
        nextChar();
        return result.toString();
      }
      if (isS()) {
        result.append('\u0020');
        nextChar();
        continue;
      }
      switch(ch) {
        case LT: throw new XmlPullParserException("Syntax error, character '<' not allowed in attribute value");
        case AMP: String replacement=nextReference();
                  if (replacement.contains("<"))
                    throw new XmlPullParserException("Syntax error, character '<' not allowed in attribute value");
                  result.append(replacement);
                  continue;
        default: result.append(ch);
      }
      nextChar();
    } while (true);

  }

  public void nextAttribute() throws XmlPullParserException{
    String name=nextName();
    nextEq();
    String value=nextAttValue();
    if (attributeMap.containsKey(name))
      throw new XmlPullParserException("Violation of WFC: Unique Att Spec (An attribute name MUST NOT appear more than once in the same start-tag or empty-element tag.)");
    Attribute a = new Attribute(name,value);
    attributeMap.put(name,a);
    attributeList.add(a);
  }
  public int getAttributeCount() {
    return attributeList.size();
  }
  public String getAttributeName(int index) {
    return attributeList.get(index).getName();
  }
  public String getAttributeNamespace(int index) {
    return attributeList.get(index).getNamespace();
  }

  public String getAttributePrefix(int index) {
    return attributeList.get(index).getPrefix();
  }

  public String getAttributeType(int index) {
    return attributeList.get(index).getType();
  }

  public boolean isAttributeDefault(int index) {
    return attributeList.get(index).isDefault();
  }

  public String getAttributeValue(int index) {
    return attributeList.get(index).getValue();
  }
  public String getAttributeValue(String namespace, String name) {
    assert (namespace==null): "Namespaces not supported";
    return attributeMap.get(name).getValue();
  }

  public static void main(String[] args) throws Exception{
    AIMLPullParser pp = new AIMLPullParser();
    System.out.println("\nj00 fail\n");
    if ((char)-1=='\uFFFF') System.out.println("(char)-1=='\\uFFFF'\n");
    /*
    pp.setInput(new FileInputStream("forget.aiml"),System.getProperty("file.encoding"));
    //System.out.println(((BufferedReader)pp.in).getEncoding());

    while (pp.nextChar()!=EOF) {
      if (pp.isS()) {
        pp.skipS();
        System.out.print("\n");
      }
      String chcl;
      if (pp.isLetter()) chcl="[L]";
      else if (pp.isDigit()) chcl="[D]";
      else chcl="[?]";
      System.out.print(pp.getChar()+chcl+"["+pp.lineNumber+":"+pp.colNumber+"]");
    }
    */
    pp.setInput(new StringReader("&fooBar;&#64;&lt;&amp;ap:kf  =   \n \r\n \"foo\r\n\n\r&amp;'xxx\"foofoo='wtf'"));
    pp.nextChar();
    System.out.print(pp.nextReference());
    System.out.print(pp.nextReference());
    System.out.print(pp.nextReference());
    System.out.print(pp.nextReference());
    //System.out.print(pp.nextAttValue());
    //System.out.println(pp.nextName());
    System.out.println();
    pp.nextAttribute();
    pp.nextAttribute();
    System.out.println("Number of attributes: "+pp.getAttributeCount());
    for (int i=0;i<pp.getAttributeCount();i++) {
      System.out.println("Attribute ["+i+"]:"+pp.getAttributeName(i)+"="+pp.getAttributeValue(i));
    }
  }

}
