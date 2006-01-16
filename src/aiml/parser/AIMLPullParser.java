package aiml.parser;

/**
 * <p>Title: AIML Pull Parser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author Kim Sullivan
 * @version 1.0
 */
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import junit.framework.*;
import junit.textui.TestRunner;

import org.xmlpull.v1.*;

public class AIMLPullParser implements XmlPullParser{
  private Reader in;
  private String encoding;
  char ch; //the current character in the input
  private HashMap<String,String> entityReplacementText=new HashMap<String,String>();

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

  private HashMap<String,Attribute> attributeMap = new HashMap<String,Attribute>();
  private ArrayList<Attribute> attributeList=new ArrayList<Attribute>();

  private boolean readCR;
  private int lineNumber;
  private int colNumber;
  private int depth;
  private int eventType;
  private boolean isEmptyElemTag;
  private String name;
  private StringBuilder text;
  

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
  public static final char DASH='-';
  public static final char RAB=']';
  public static final char LAB = '[';
  public static final char SLASH='/';


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
    resetState();
  }

  public void setProperty(String name, Object value) throws XmlPullParserException {
    if (name==null) throw new IllegalArgumentException("Property name cannot be null");
    throw new XmlPullParserException("Property "+name+" not supported");
  }

  public Object getProperty(String name) {
    return null;
  }

  public void setFeature(String name, boolean state) throws XmlPullParserException {
    if (eventType!=START_DOCUMENT) throw new XmlPullParserException("Features can only be set before the first call to next or nextToken");
    if (state) throw new XmlPullParserException("This feature can't be activated");
  }

  public boolean getFeature(java.lang.String name) {
    return false;
  }

  public int getNamespaceCount(int depth) throws XmlPullParserException{
    return 0;
  }

  public String getNamespacePrefix(int pos) throws XmlPullParserException {
    return null;
  }

  public String getNamespaceUri(int pos)  throws XmlPullParserException {
    return null;
  }

  public String getNamespace(String prefix) {
    switch (eventType) {
      case START_TAG:
      case END_TAG: return NO_NAMESPACE;
      default: return null;
    } 
  }

  public int getDepth() {
    return depth;
  }

  public String getPositionDescription() {
    return "@"+getLineNumber()+":"+getColumnNumber();
  }

  public String getNamespace() {
    return "";
  }

  public String getPrefix() {
    return null;
  }

  private void resetState() {
    lineNumber=1;
    colNumber=0;
    depth=0;
    readCR=false;
    setDefaultEntityReplacementText();
    attributeMap.clear();
    attributeList.clear();
    in=null;
    encoding=null;
    eventType=START_DOCUMENT;
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

  private char nextChar() throws IOException{
    ch = (char) in.read();
    colNumber++;
    switch (ch) { //normalize end of line markers and count the position
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
  }
  private char getChar() {
    return ch;
  }
  private void skipS() throws XmlPullParserException, IOException {
    while (CharacterClasses.isS(ch)) nextChar();
  }
  private void nextS() throws XmlPullParserException, IOException {
    //[3]   	S	   ::=   	(#x20 | #x9 | #xD | #xA)+
    if (!CharacterClasses.isS(ch)) throw new XmlPullParserException("Syntax error, expecting production\n[3]   	S	   ::=   	(#x20 | #x9 | #xD | #xA)+",this,null);
    skipS();
  }

  private String nextName() throws XmlPullParserException, IOException {
    //[5]   	Name	   ::=   	(Letter | '_' | ':') (NameChar)*
    if (!CharacterClasses.isNameFirst(ch)) throw new XmlPullParserException("Syntax error, expecting production\n[5]   	Name	   ::=   	(Letter | '_' | ':') (NameChar)*",this,null);
    StringBuffer result=new StringBuffer();
    result.append(ch);
    while ((nextChar()!=EOF)&& CharacterClasses.isNameChar(ch))
      result.append(ch);
    return result.toString();
  }
  private void nextEq() throws XmlPullParserException, IOException {
    // [25]   	Eq	   ::=   	S? '=' S?
    if (!CharacterClasses.isS(ch)&&ch!=EQ)
      throw new XmlPullParserException("Syntax error, expecting production\n[25]   	Eq	   ::=   	S? '=' S?",this,null);
    skipS();
    requireChar(EQ,"Syntax error, expecting production\n[25]   	Eq	   ::=   	S? '=' S?");
    skipS();
  }
  public int getLineNumber() {
    return lineNumber;
  }
  public int getColumnNumber() {
    return colNumber;
  }

  private String nextReference() throws XmlPullParserException, IOException{
    //[67]   	Reference	   ::=   	EntityRef | CharRef
    requireChar(AMP,"Syntax error, production [67] Referencee must start with &");
    StringBuffer result= new StringBuffer();
    if (CharacterClasses.isNameFirst(ch)) { //[68]   	EntityRef	   ::=   	'&' Name ';'
      name=nextName();
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
        name="#x";
        nextChar();
        do {
          if (CharacterClasses.isHexDigit(ch))
            codepointBuffer.append(ch);
          else
            throw new XmlPullParserException("Syntax error, invalid hexadecimal digit '"+ch+"' in character reference",this,null);
          nextChar();
        } while (CharacterClasses.isHexDigit(ch));
      } else {//[66]   	CharRef	   ::=   	'&#' [0-9]+ ';'
        radix=10;
        name="#";
        do {
          if (CharacterClasses.isDecDigit(ch))
            codepointBuffer.append(ch);
          else
            throw new XmlPullParserException("Syntax error, invalid decimal digit '"+ch+"' in character reference",this,null);
          nextChar();
        } while (CharacterClasses.isDecDigit(ch));
      }
      int codepoint;
      try {
        name=name+codepointBuffer.toString();
        codepoint = Integer.parseInt(codepointBuffer.toString(),radix);
      } catch (NumberFormatException e) {
        throw new XmlPullParserException("Syntax error, bad character reference '"+codepointBuffer +"'",this,null);
      }
      result.appendCodePoint(codepoint);
    } else {
      throw new XmlPullParserException("Syntax error, bad entity reference",this,null);
    }

    requireChar(SEMICOLON,"Syntax error, production [67] Reference must end with ';'");
    return result.toString();
  }

  private String nextAttValue() throws XmlPullParserException, IOException{
    //[10]   	AttValue	   ::=   	'"' ([^<&"] | Reference)* '"' |  "'" ([^<&'] | Reference)* "'"
    if ((ch!=QUOT) && (ch!=APOS)) {
      //System.out.println("((["+ch+"]!=["+QUOT+"]) || ([["+ch+"]!=["+APOS+"]))");
      throw new XmlPullParserException("Syntax error, attribute value must begin with quote or apostrophe",this,null);
    }
    char delim=ch;
    StringBuffer result=new StringBuffer();
    nextChar();
    do {

      if (ch==delim) {
        nextChar();
        return result.toString();
      }
      if (CharacterClasses.isS(ch)) {
        result.append('\u0020');
        nextChar();
        continue;
      }
      switch(ch) {
        case LT: throw new XmlPullParserException("Syntax error, character '<' not allowed in attribute value",this,null);
        case AMP: String replacement=nextReference();
                  if (replacement.contains("<"))
                    throw new XmlPullParserException("Syntax error, character '<' not allowed in attribute value",this,null);
                  result.append(replacement);
                  continue;
        default: result.append(ch);
      }
      nextChar();
    } while (true);

  }

  private void nextAttribute() throws XmlPullParserException, IOException{
    String name=nextName();
    nextEq();
    String value=nextAttValue();
    if (attributeMap.containsKey(name))
      throw new XmlPullParserException("Violation of WFC: Unique Att Spec (An attribute name MUST NOT appear more than once in the same start-tag or empty-element tag.)",this,null);
    Attribute a = new Attribute(name,value);
    attributeMap.put(name,a);
    attributeList.add(a);
  }
  public int getAttributeCount() {
    if (eventType==START_TAG)
      return attributeList.size();
    else
      return -1;
  }
  public String getAttributeName(int index) {
    if (eventType!=START_TAG)
      throw new IndexOutOfBoundsException();
    return attributeList.get(index).getName();
  }
  public String getAttributeNamespace(int index) {
    if (eventType!=START_TAG)
      throw new IndexOutOfBoundsException();
    return attributeList.get(index).getNamespace();
  }

  public String getAttributePrefix(int index) {
    if (eventType!=START_TAG)
      throw new IndexOutOfBoundsException();
    return attributeList.get(index).getPrefix();
  }

  public String getAttributeType(int index) {
    return attributeList.get(index).getType();
  }

  public boolean isAttributeDefault(int index) {
    return attributeList.get(index).isDefault();
  }

  public String getAttributeValue(int index) {
    if (eventType!=START_TAG)
      throw new IndexOutOfBoundsException();
    return attributeList.get(index).getValue();
  }
  public String getAttributeValue(String namespace, String name) {
    assert (namespace==null): "Namespaces not supported";
    if (eventType!=START_TAG)
      throw new IndexOutOfBoundsException();
    return attributeMap.get(name).getValue();
  }
  private void requireChar(char what, String failMessage) throws XmlPullParserException, IOException{
    if (ch!=what) throw new XmlPullParserException(failMessage,this,null);
    nextChar();
  }
  private void requireString(String what,String failMessage) throws XmlPullParserException, IOException {
    for (int i=0;i<what.length();i++) {
      if (ch!= what.charAt(i))
        throw new XmlPullParserException(failMessage,this,null);
      nextChar();
    }
  }
  
  private String nextPIContent() throws XmlPullParserException, IOException{
    //[16]   	PI	   ::=   	'<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
    //assumes we already have parsed '<?'PITarget and are on the character after that
    if (CharacterClasses.isS(ch)){
      //(S (Char* - (Char* '?>' Char*)))
      /*This is a bit tricky. The notation says we're looking for strings that begin with
       * whitespace, and DON'T contain the '?>' marker. What we have to do is the opposite:
       * actually LOOK for the marker. The translational grammar for it looks like this:
       * S ::= '?' A | '>' S {out('>')} | C S {out(C)}
       * A ::= '?' A {out('?')} | '>' {break} | C S {out('?') out (C)}
       * C ::= Char - ('>' | '?')
      */
      StringBuffer result=new StringBuffer();
      boolean seenQ=false;
PIContent:
      do {
        if (!CharacterClasses.isChar(ch)) {
          if (ch==EOF) throw new EOFException("Unexpected end of input while parsing PI");
          else throw new XmlPullParserException("Syntax error, invalid character while parsing PI",this,null);
        }
        if (!seenQ) {
          //S ::= '?' A | '>' S {out('>')} | C S {out(C)}
          if (ch==QUES)
            seenQ=true;
          else
            result.append(ch);
        } else {
          //A ::= '?' A {out('?')} | '>' {break} | C S {out('?') out (C)}
          switch (ch) {
            case QUES:result.append('?');break;      //what we're outputting here is not this '?' but the one before that
            case GT:break PIContent;   //a simple break would just terminate the switch, not the do {} while block.
            default :result.append('?').append(ch);
                     seenQ=false;
          }
        }
        nextChar();
      } while (true);
      nextChar();
      return result.toString();
    } else {
      //'?>'
      requireChar(QUES, "Syntax error, in production [16] PI: PITarget must be followed by whitespace, or immediately terminated with '?>'");
      requireChar(GT, "Syntax error, in production [16] PI: PITarget must be followed by whitespace, or immediately terminated with '?>'");
      return "";
    }

  }

  private String nextCommentContent() throws IOException,XmlPullParserException {
    //[15]   	Comment	   ::=   	'<!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
    //Assumes we already read '<!--'
    //As with PI's we're actually looking for the terminating '--'
    //(notice that the gramar doesn't allow the string '--' except as the terminating '-->'
    /*
    * S ::= '-' A | C S {out(C)}
    * A ::= '-' {break} | C S {out('-') out (C)}
    * C ::= Char - ('-')
    */
    StringBuffer result=new StringBuffer();
    boolean seenDash=false;
    do {
      if (!CharacterClasses.isChar(ch)) {
        if (ch==EOF) throw new EOFException("Unexpected end of input while parsing Comment");
        else throw new XmlPullParserException("Syntax error, invalid character while parsing Comment",this,null);
      }
      if (!seenDash) {
        if (ch==DASH) seenDash=true;
        else
          result.append(ch);
      } else {
        if (ch==DASH) break;
        else {
          result.append('-').append(ch);
          seenDash=false;
        }
      }
      nextChar();
    } while (true);
    nextChar();
    requireChar(GT,"Syntax error, comment must be terminated with '-->'");
    return result.toString();
  }

  private String nextCDataContent()throws IOException,XmlPullParserException {
    //[20]   	CData	   ::=   	(Char* - (Char* ']]>' Char*))
    //[21]   	CDEnd	   ::=   	']]>'
    //Assumes we already read '<![CDATA['
    //So, we're looking for the terminating ']]>'
    /* S::= ']' A | '>' S {out('>')} | C S {out(C)}
     * A::= ']' B | '>' S {out(']') out('>')} | C S {out(']') out(C)}
     * B::= ']' B {out(']')} | '>' S {break} | C S {out(']') out(']') out(C)}
     */
    StringBuffer result=new  StringBuffer();
    int seenRAB=0;
CDContent:
    do {
      if (!CharacterClasses.isChar(ch)) {
        if (ch==EOF) throw new EOFException("Unexpected end of input while parsing Comment");
        else throw new XmlPullParserException("Syntax error, invalid character while parsing Comment",this,null);
      }
      switch (seenRAB) {
        case 0:
          if (ch==RAB) {
            seenRAB=1;
          } else
            result.append(ch);
          break;
        case 1:
          if (ch==RAB) {
            seenRAB=2;
          } else {
            seenRAB = 0;
            result.append(']').append(ch);
          }
          break;
        case 2:
          switch(ch) {
            case RAB:result.append(']');break;
            case GT:break CDContent;
            default:seenRAB=0;
                    result.append(']').append(']').append(ch);
          }
          break;
      }
      nextChar();
    } while (true);
    nextChar();
    return result.toString();
  }
  private String nextCharData() throws IOException, XmlPullParserException{
    //[14]   	CharData	   ::=   	[^<&]* - ([^<&]* ']]>' [^<&]*)
    //It is interesting to note, that, while the characters '<' and '&' do not
    //belong into this production they only signal the end of it, while the
    //CDATA-section-close delimiter ']]>' actually signals a syntax error.
    /* S::= ']' A | '>' S {out('>')} | C S {out(C)} | '&' {break} | '<' {break}
     * A::= ']' B | '>' S {out(']') out('>')} | C S {out(']') out(C)} | '&' {out(']') break} | '<' {out(']') break}
     * B::= ']' B {out(']')} | '>' S {error} | C S {out(']') out(']') out(C)} | '&' {out(']') out(']') break} | '<' {out(']') out(']') break}
     */
    StringBuffer result=new  StringBuffer();
    int seenRAB=0;
CharData:
    do {
      if (!CharacterClasses.isChar(ch)) {
        if (ch==EOF) throw new EOFException("Unexpected end of input while parsing Character data");
        else throw new XmlPullParserException("Syntax error, invalid character while parsing Character data",this,null);
      }
      switch (seenRAB) {
        case 0:
          switch(ch) {
            case(RAB): seenRAB=1;break;
            case(AMP):
            case(LT): break CharData;
            default: result.append(ch);
          }
          break;
        case 1:
          switch(ch) {
            case(RAB): seenRAB=2;break;
            case(AMP):
            case(LT): result.append(']');
                      break CharData;
            default: seenRAB=0;
                     result.append(']').append(ch);
          }
          break;
        case 2:
          switch(ch) {
            case RAB:result.append(']');break;
            case GT:throw new XmlPullParserException("Syntax error, the CDATA-sesction-close delimiter ']]>' must not occur in Character data",this,null);
            case(AMP):
            case(LT): result.append(']').append(']');
                      break CharData;
            default:seenRAB=0;
                    result.append(']').append(']').append(ch);
          }
          break;
      }
      nextChar();
    } while (true);
    return result.toString();
  }
  
  private void nextContentMarkup() throws XmlPullParserException, IOException {
    //Distinguishes between
    //[40]    STag          ::=  '<' Name (S Attribute)* S? '>'
    //[44]    EmptyElemTag  ::=  '<' Name (S Attribute)* S? '/>'
    //[42]    ETag          ::=  '</' Name S? '>'
    //[15]    Comment       ::=  '<!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
    //[19]    CDStart       ::=  '<![CDATA['
    //[16]    PI            ::=  '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
    //[23]    XMLDecl       ::=  '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
    //assumes we already read the initial '<'
    switch(ch) {
      case QUES:
        eventType=PROCESSING_INSTRUCTION;
        nextChar();
        name=nextName();
        if (name.equalsIgnoreCase("xml"))
          throw new XmlPullParserException("The target '"+name+"' is not allowed for a processing instruction");
        text.setLength(0);
        text.append(name).append(nextPIContent());
        break;
      case EXCL:
        nextChar();
        switch (ch) {
          case DASH:
            eventType=COMMENT;
            nextChar();
            requireChar(DASH,"Syntax error, comments must begin with '<!--'");
            nextCommentContent();
            break;
          case LAB:
            eventType=CDSECT;
            nextChar();
            requireString("CDSECT[","Syntax error, only CDSECT marked sections are supported in XML");
            text.setLength(0);
            text.append(nextCDataContent());
          case 'D':
            eventType=DOCDECL;
            nextChar();
            requireString("OCTYPE","Syntax error, invalid characters after '<!'");
            throw new XmlPullParserException("This implementation doesn't support DOCTYPE declarations");
          default:
            throw new XmlPullParserException("Syntax error, invalid characters after '<!'");
        }
        break;
      case SLASH:
        eventType=END_TAG;
        nextChar();
        name=nextName();
        skipS();
        requireChar(GT,"Syntax error, end-tag must end with '>'");
        break;
      default:
        eventType=START_TAG;
        nextChar();
        name=nextName();
        nextStartTagContent();
    }
  }
  
  private void nextStartTagContent() throws XmlPullParserException, IOException  {
    //reads the following part from productions [40] & [44]:
    //(S Attribute)* S? '/'?'>'
    //Assumes we already read '<' Name
    //The state machine looks like this:
    // 0::= S 1 | '/' | '>'
    // 1::= Attribute 0 | '/' | '>'
    int state=0;
    isEmptyElemTag=false;
    attributeList.clear();
    attributeMap.clear();
AttList:    
    do {
      switch(state) {
        case 0:
          if (CharacterClasses.isS(ch)) {
            skipS();
            state=1;
            continue AttList;
          }
          switch(ch) {
            case SLASH: 
              isEmptyElemTag=true;
              nextChar();
            case GT:
              break AttList;
            default:
              throw new XmlPullParserException("Syntax error, unexpected character '"+ch+"' while parsing attribute list");
          }
        case 1:
          switch(ch) {
            case SLASH:
              isEmptyElemTag=true;
              nextChar();
            case GT:
              break AttList;
            default:
              nextAttribute();
              state=0;
              break;
          }
      }
    } while (true);
    requireChar(GT,"Syntax error while parsing "+(isEmptyElemTag?"EmptyElemTag":"STag")+ ": unexpected terminal character, must be '>'");
    eventType=START_TAG;
    depth++;
  }
  
  public boolean isWhitespace() throws XmlPullParserException {
    return false;
  }

  public String getText() {
    switch (eventType) {
      case START_DOCUMENT:
      case END_DOCUMENT:
      case START_TAG:
      case END_TAG:
      case DOCDECL:
        return null;
      default:
        return text.toString();

    }
  }
  
  private char[] getTextCharacters(StringBuilder s, int[] holderForStartAndLength) {
    holderForStartAndLength[0]=0;
    holderForStartAndLength[0]=s.length();    
    char[] result= new char[s.length()];
    s.getChars(0,s.length(),result,0);
    return result;    
  }
  
  private char[] getTextCharacters(String s, int[] holderForStartAndLength) {
    holderForStartAndLength[0]=0;
    holderForStartAndLength[0]=s.length();    
    char[] result= new char[s.length()];
    s.getChars(0,s.length(),result,0);
    return result;    
  }
  public char[] getTextCharacters(int[] holderForStartAndLength) {
    switch (eventType) {
      case START_DOCUMENT:
      case END_DOCUMENT:
      case START_TAG:
      case END_TAG:
      case DOCDECL:
        return null;
      case ENTITY_REF:
        return getTextCharacters(name,holderForStartAndLength);
      default:
        return getTextCharacters(text,holderForStartAndLength);

    }

  }

  public String getName() {
    switch(eventType) {
      case START_TAG:
      case END_TAG:
      case ENTITY_REF: return name;
      default: return null;
    }
  }

  public boolean isEmptyElementTag() throws XmlPullParserException {
    if (eventType==START_TAG)
      return isEmptyElemTag;
    else
      throw new XmlPullParserException("The function isEmptyElementTag() can only be called for start-tags");
  }

  public int getEventType() throws XmlPullParserException {
    return eventType;
  }

  public int next() throws IOException, XmlPullParserException {
    return 0;
  }

  public int nextToken() throws IOException, XmlPullParserException {
    return 0;
  }

  public void require(int _int, String string, String string2) throws
      IOException, XmlPullParserException {
  }

  public String nextText() throws IOException, XmlPullParserException {
    return "";
  }

  public int nextTag() throws IOException, XmlPullParserException {
    return 0;
  }
  
  private Test suite() {
    TestSuite t=new TestSuite();
    Method[] methods = LexerTest.class.getMethods();
    for (int i=0;i<methods.length;i++) {
      if (methods[i].getName().startsWith("test")&&Modifier.isPublic(methods[i].getModifiers())) {
        t.addTest(new LexerTest(methods[i].getName()));
      }
    }
    return t;
  }
  
  public class LexerTest extends TestCase {
    public LexerTest(String s) {
      super(s);
    }
    public void testNextReference() throws IOException, XmlPullParserException {
      setInput(new StringReader("&fooBar;&#64;&lt;&amp;"));
      nextChar();      
      System.out.print(nextReference());
      System.out.print(nextReference());
      System.out.print(nextReference());
      System.out.print(nextReference());
      //System.out.print(pp.nextAttValue());
      //System.out.println(pp.nextName());
      System.out.println();
    }
    public void testNextAttribute() throws IOException, XmlPullParserException {
      setInput(new StringReader("ap:kf  =   \n \r\n \"foo\r\n\n\r&amp;'xxx\"foofoo='wtf'"));
      nextChar();
      eventType=START_TAG;
      nextAttribute();
      nextAttribute();
      System.out.println("Number of attributes: "+getAttributeCount());
      for (int i=0;i<getAttributeCount();i++) {
        System.out.println("Attribute ["+i+"]:"+getAttributeName(i)+"="+getAttributeValue(i));
      }
    }
    public void testPIContent() throws IOException, XmlPullParserException {
      setInput(new StringReader("?> bla??? >>>>??? ? > ?hblah?>ffrrfraaafhr-->-->-aasdfasdf-asdfsad-asdfa->-asfd-->adasdf--asdf-->asdfasdf--->"));
      nextChar();
      System.out.println(nextPIContent());
      System.out.println(nextPIContent());
    }
    public void testCommentContent() throws IOException, XmlPullParserException {
      setInput(new StringReader("ffrrfraaafhr-->-->-aasdfasdf-asdfsad-asdfa->-asfd-->adasdf--asdf-->asdfasdf--->"));
      nextChar();
      System.out.println(nextCommentContent());
      System.out.println(nextCommentContent());
      System.out.println(nextCommentContent());
      try {
        System.out.println(nextCommentContent());
      } catch (XmlPullParserException e) {
        System.out.println("Test succesfull: "+e.getMessage());
      }
      System.out.println(nextCommentContent());
      try {
        System.out.println(nextCommentContent());
      } catch (XmlPullParserException e) {
        System.out.println("Test succesfull: "+e.getMessage());
      }
    }
    public void testCDataContent() throws IOException, XmlPullParserException {
      setInput(new StringReader("]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b]]>"));
      nextChar();
      System.out.println(nextCDataContent());
    }
    public void testCharData() throws IOException, XmlPullParserException {
      setInput(new StringReader("asdfasdfjh<skdjfhaskdjfh&askjfh<]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b<]]12]3]4]]]]5]]6]]7]]] >]]]8]>9012>>>>>]>]>]>]]b]]>asdf]]>asdf"));
      nextChar();
      System.out.println(nextCharData());
      nextChar();
      System.out.println(nextCharData());
      nextChar();
      System.out.println(nextCharData());
      nextChar();
      System.out.println(nextCharData());
      nextChar();
      try {
        System.out.println(nextCharData());
      } catch (XmlPullParserException e) {
        System.out.println("Test succesfull: "+e.getMessage());
      }
      nextChar();
      try {
        System.out.println(nextCharData());
      } catch (XmlPullParserException e) {
        System.out.println("Test succesfull: "+e.getMessage());
      }
    }
    public void testStartTagContent() throws IOException, XmlPullParserException {
      setInput(new StringReader(" foo='bar' bar='foo'> a='b' c='d'   > u='1' v='2'/>"));
      nextChar();
      nextStartTagContent();

      System.out.println("Number of attributes: "+getAttributeCount()+" empty? "+(isEmptyElementTag()?"YES":"NO"));
      for (int i=0;i<getAttributeCount();i++) {
        System.out.println("Attribute ["+i+"]:"+getAttributeName(i)+"="+getAttributeValue(i));
      }

      nextStartTagContent();

      System.out.println("Number of attributes: "+getAttributeCount()+" empty? "+(isEmptyElementTag()?"YES":"NO"));
      for (int i=0;i<getAttributeCount();i++) {
        System.out.println("Attribute ["+i+"]:"+getAttributeName(i)+"="+getAttributeValue(i));
      }

      nextStartTagContent();

      System.out.println("Number of attributes: "+getAttributeCount()+" empty? "+(isEmptyElementTag()?"YES":"NO"));
      for (int i=0;i<getAttributeCount();i++) {
        System.out.println("Attribute ["+i+"]:"+getAttributeName(i)+"="+getAttributeValue(i));
      }
    }
  }
  
  public static void main(String[] args) throws Exception{
    AIMLPullParser pp = new AIMLPullParser();
    TestRunner.run(pp.suite());
    /*
    pp.tests().testNextReference();
    pp.tests().testNextAttribute();   
    pp.tests().testPIContent();
    pp.tests().testCommentContent();    
    pp.tests().testCDataContent();
    pp.tests().testCharData();
    pp.tests().testStartTagContent();
    */
  }
}
