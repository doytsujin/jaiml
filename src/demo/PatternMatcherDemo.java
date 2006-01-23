/*
    jaiml - java AIML library
    Copyright (C) 2004-2005  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package demo;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.util.regex.*;

import aiml.classifier.*;
import aiml.classifier.node.*;
import aiml.context.*;

/**
 * <p>A demonstration applet/application to show of the capabilities of the new
 * optimized pattern matcher.</p>
 *
 * <p>This applet consists of a main output area used to relay messages to the user,
 * a context input area with textfields that bind to the individual contexts,
 * and an input area used to control the other functions of the program.</p>
 *
 * <p>Before any matching can be demonstrated, the patterns and contexts first
 * have to be loaded into the pattern tree. If this applet is run standalone,
 * patterns can be loaded from disk. Otherwise, patterns have to be added manually.</p>
 * @author Kim Sullivan
 * @version 1.0
 */

public class PatternMatcherDemo
    extends Applet {
  private boolean isStandalone = false;
  BorderLayout borderLayout1 = new BorderLayout();
  TextArea output = new TextArea();
  Panel panel1 = new Panel();
  Label label1 = new Label();
  BorderLayout borderLayout2 = new BorderLayout();
  Panel contextInput = new Panel();
  Panel panel2 = new Panel();
  Button btnLoad = new Button();
  GridLayout gridLayout1 = new GridLayout();
  Button btnMatch = new Button();
  Panel panel3 = new Panel();
  BorderLayout borderLayout3 = new BorderLayout();
  Button btnReset = new Button();
  Button btnClearPat = new Button();
  Panel panel4 = new Panel();
  BorderLayout borderLayout4 = new BorderLayout();
  TextField input = new TextField();
  Button btnClear = new Button();
  Panel panel5 = new Panel();
  BorderLayout borderLayout5 = new BorderLayout();
  Panel panel6 = new Panel();
  Button btnAddPth = new Button();
  Button btnAddCtx = new Button();

  java.util.regex.Pattern pathpattern=java.util.regex.Pattern.compile("\\[(.+?)\\](.*?)\\[/\\]");

  //Get a parameter value
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
        (getParameter(key) != null ? getParameter(key) : def);
  }

  //Construct the applet
  public PatternMatcherDemo() {
    //HashMapNode.register();
    //StringBranchNode.register();
    AIMLMatcher.registerDefaultNodeHandlers();
  }

  //Initialize the applet
  public void init() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    label1.setAlignment(Label.CENTER);
    label1.setFont(new java.awt.Font("Dialog", 1, 12));
    label1.setText("Context variables");
    panel1.setLocale(java.util.Locale.getDefault());
    panel1.setLayout(borderLayout2);
    btnLoad.setLabel("Load");
    btnLoad.addActionListener(new PatternMatcherDemo_btnLoad_actionAdapter(this));
    contextInput.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(0);
    btnMatch.setLabel("Match");
    btnMatch.addActionListener(new PatternMatcherDemo_btnMatch_actionAdapter(this));
    panel3.setLayout(borderLayout3);
    btnReset.setLabel("Reset All");
    btnReset.addActionListener(new PatternMatcherDemo_btnReset_actionAdapter(this));
    btnClearPat.setActionCommand("Clear Patterns");
    btnClearPat.setForeground(Color.black);
    btnClearPat.setLabel("Clear Patterns");
    btnClearPat.addActionListener(new
                                  PatternMatcherDemo_btnClearPat_actionAdapter(this));
    panel4.setLayout(borderLayout4);
    borderLayout4.setHgap(0);
    borderLayout4.setVgap(0);
    btnClear.setLabel("Clear output");
    btnClear.addActionListener(new PatternMatcherDemo_btnClear_actionAdapter(this));
    panel5.setLayout(borderLayout5);
    btnAddPth.setLabel("Add Path");
    btnAddPth.addActionListener(new PatternMatcherDemo_btnAddPth_actionAdapter(this));
    btnAddCtx.setLabel("Add context");
    btnAddCtx.addActionListener(new PatternMatcherDemo_btnAddCtx_actionAdapter(this));
    this.add(output, BorderLayout.CENTER);
    this.add(panel1, BorderLayout.EAST);
    panel1.add(label1, BorderLayout.NORTH);
    panel1.add(panel3, BorderLayout.CENTER);
    panel3.add(contextInput,  BorderLayout.NORTH);
    if (isStandalone) {
      panel2.add(btnLoad, null);
    }
    panel2.add(btnReset, null);
    panel2.add(btnClearPat, null);
    panel2.add(btnClear, null);
    panel2.add(btnMatch, null);
    panel4.add(panel5, BorderLayout.SOUTH);
    panel5.add(input, BorderLayout.CENTER);
    panel5.add(panel6, BorderLayout.EAST);
    panel6.add(btnAddCtx, null);
    panel6.add(btnAddPth, null);
    this.add(panel4, BorderLayout.SOUTH);
    panel4.add(panel2, BorderLayout.CENTER);
  }

  //Start the applet
  public void start() {
  }

  //Stop the applet
  public void stop() {
  }

  //Destroy the applet
  public void destroy() {
  }

  //Get Applet information
  public String getAppletInfo() {
    return "Applet Information";
  }

  //Get parameter info
  public String[][] getParameterInfo() {
    return null;
  }

  /**
   * Add a new context source to the context input panel.
   * @param name the name of the context
   */
  void addContextSource(String name) {

    TextField textField = new TextField(20);
    TextContext tc = new TextContext(name, textField);
    ContextInfo.registerContext(tc);
    textField.addActionListener(new PatternMatcherDemo_btnMatch_actionAdapter(this));

    Label label = new Label(tc.getOrder() + ": \"" + name + "\"");
    Panel p = new Panel();
    p.add(label);
    p.add(textField);
    contextInput.add(p);

    validate();
  }

  /**
   * Add the path specified by the string s to the matching tree. The format is
   * a sequence of zero or more <code>[name]pattern[/]</code> blocks, where
   * <code>name</code> is the name of a context, and <code>pattern</code> is the
   * pattern. The pattern can be an empty string, the name can not.

   * @param s a path
   * @throws BadPathFormatException
   * @throws DuplicatePathException
   * @throws MultipleContextsException 
   */
  void addPath(String s) throws BadPathFormatException, DuplicatePathException, MultipleContextsException{
    Matcher m = pathpattern.matcher(s);
    Path path = new Path();
    while (m.find()) {
      path.add(m.group(1), m.group(2));
    }
    if ( (path.getLength() == 0) && (s.length()>0)) {
      throw new BadPathFormatException();
    }
    AIMLMatcher.add(path, path);
  }

  //Main method
  public static void main(String[] args) {
    PatternMatcherDemo applet = new PatternMatcherDemo();
    applet.isStandalone = true;
    Frame frame;
    frame = new Frame();
    frame.setTitle("Applet Frame");
    frame.add(applet, BorderLayout.CENTER);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    applet.init();
    applet.start();
    frame.setSize(640, 300);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation( (d.width - frame.getSize().width) / 2,
                      (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }

  void btnLoad_actionPerformed(ActionEvent e) {
    Frame fdf;
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    if (this.getParent() instanceof Frame) {
      fdf = (Frame)this.getParent();
    }
    else {
      fdf = new Frame("Frame title");
      fdf.setSize(400, 300);
      fdf.setLocationRelativeTo(null);
    }

    FileDialog fd = new FileDialog(fdf, "Load patterns from file");
    fd.setFile("patterns.txt");
    fd.show();

    if (fd.getFile() == null) {
      return;
    }

    final String filename = fd.getDirectory() + fd.getFile();

    Thread t = new Thread() {
      public void run() {
        output.append("Loading patterns...\n");
        int numcontext;
        int linecount = 0;
        int errcount = 0;
        int dupcount = 0;
        //output.append("...\n");
        try {
          //output.append("...\n");
          BufferedReader in = new BufferedReader(
              new FileReader(filename));
          output.append("From file: " + filename + "\n");
          try {
            numcontext = Integer.parseInt(in.readLine());
            linecount++;
          }
          catch (NumberFormatException x) {
            errcount++;
            throw new LoadErrorException(
                "Bad input file format - first line must be nuber of contexts\n",
                x);
          }

          String line;
          ContextInfo.reset();
          contextInput.removeAll();
          for (int i = 0; i < numcontext; i++) {
            line = in.readLine();
            if (line != null) {
              linecount++;
            }
            if ( (line == null) || (pathpattern.matcher(line).matches())) {
              errcount++;
              ContextInfo.reset();
              contextInput.removeAll();
              throw new LoadErrorException("Not enough contexts specified");
            }
            addContextSource(line);
          }

          output.append("Loaded contexts: " + numcontext + "\n");
          btnLoad.setEnabled(false);
          lineloop:while ( (line = in.readLine()) != null) {
            linecount++;
            try {
              //if (linecount==683)
              //  System.out.println(linecount+":"+line);
              addPath(line);
            } catch (BadPathFormatException x) {
              output.append("Bad input file format on line " + linecount + "\n");
              errcount++;
              continue lineloop;
            } catch (DuplicatePathException x) {
              output.append("Duplicate path: " + line + "\n");
              dupcount++;

            }
            if ( ( (AIMLMatcher.getCount()) % 50000) == 0) {
              if (AIMLMatcher.getCount() > 0) {
                output.append("Loaded " + AIMLMatcher.getCount() +
                              " patterns...\n");
              }
            }

          }

        }
        catch (LoadErrorException x) {
          output.append(x.getMessage());
        }
        catch (IOException x) {
          output.append("Error loading file " + x.getMessage() + "\n");
          return;
        } catch (Exception x) {
          output.append(x.toString());
          x.printStackTrace();
        }

        output.append("Path count: " + AIMLMatcher.getCount() + "\n");
        if (dupcount > 0) {
          output.append("Duplicates ignored: " + dupcount + "\n");
        }
        if (errcount > 0) {
          output.append("Errors: " + errcount + "\n");
        }
        output.append("Loading finished.\n");
      }
    };
    t.start();

  }

  void btnMatch_actionPerformed(ActionEvent e) {
    MatchState m;
    try {
      m = AIMLMatcher.match();
    }
    catch (NoContextPresentException x) {
      output.append("No contexts registered\n");
      return;
    }
    if (m != null) {
      output.append("Found a match:\n " + m);
    }
    else {
      output.append("No match found.\n");

    }
  }

  void btnReset_actionPerformed(ActionEvent e) {
    ContextInfo.reset();
    contextInput.removeAll();
    AIMLMatcher.reset();
    btnLoad.setEnabled(true);
    output.append("Reset all\n");
  }

  void btnClearPat_actionPerformed(ActionEvent e) {
    AIMLMatcher.reset();
    output.append("Cleared paths\n");
  }

  void btnClear_actionPerformed(ActionEvent e) {
    output.setText(null);
  }

  void btnAddCtx_actionPerformed(ActionEvent e) {
    try {
      addContextSource(input.getText());
      output.append("Added context "+input.getText()+"\n");
    }
    catch (DuplicateContextException x) {
      output.append("Context "+input.getText()+" already registered\n");
    }

  }

  void btnAddPth_actionPerformed(ActionEvent e) {
    try {
      addPath(input.getText());
      output.append("Added path "+input.getText()+"\n");
    } catch (BadPathFormatException x) {
      output.append("Bad path format\nUse: [contextname]value[/][name]value[/]...\n");
    } catch (DuplicatePathException x) {
      output.append("Duplicate path: " + input.getText() + "\n");
    } catch (UnknownContextException x) {
      output.append(x.getMessage()+"\n");
    } catch (MultipleContextsException x) {
      output.append(x.getMessage()+"\n");
    }
  }

}

class PatternMatcherDemo_btnLoad_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnLoad_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnLoad_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnMatch_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnMatch_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnMatch_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnReset_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnReset_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnReset_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnClearPat_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnClearPat_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnClearPat_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnClear_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnClear_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnClear_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnAddCtx_actionAdapter
    implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnAddCtx_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.btnAddCtx_actionPerformed(e);
  }
}

class PatternMatcherDemo_btnAddPth_actionAdapter implements java.awt.event.ActionListener {
  PatternMatcherDemo adaptee;

  PatternMatcherDemo_btnAddPth_actionAdapter(PatternMatcherDemo adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.btnAddPth_actionPerformed(e);
  }
}
