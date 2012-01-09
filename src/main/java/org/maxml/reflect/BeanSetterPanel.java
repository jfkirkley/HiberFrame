package org.maxml.reflect;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.maxml.util.Util;


public class BeanSetterPanel extends JPanel {
    CachedClass cachedClass;

    TreeMap     propertySetters;

    public BeanSetterPanel() {
    }

    public BeanSetterPanel(CachedClass cachedClass, boolean doFields) {
        super();
        this.cachedClass = cachedClass;
        propertySetters = (doFields) ? getPropFieldSetters(cachedClass.getNonStaticFields())
                : getPropertySetters(cachedClass.setMethods());

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(gbl);
        gbc.gridx = gbc.gridy = 0;
        Iterator iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);

            JLabel label = new JLabel(pname);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            gbc.gridx = 0;
            gbc.insets = new Insets(5, 0, 0, 10);
            gbc.anchor = GridBagConstraints.EAST;
            gbl.setConstraints(label, gbc);
            this.add(label);

            Component c = ps.getGUIComponent();
            gbc.insets = new Insets(5, 10, 0, 0);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbl.setConstraints(c, gbc);
            this.add(c);

            gbc.gridy++;
        }
        // this.removeAll();
        // this.setLayout(new BorderLayout());
        // this.add(new GBL());
        // System.out.println(getCodeStr(propertySetters,
        // cachedClass.getShortName(), cachedClass));
    }

    // String pkg = "builders"; // "layout";//

    private Class[] clist      = { JPanel.class, JButton.class,
            JCheckBox.class, JRadioButton.class, JTabbedPane.class,
            JScrollPane.class, JSplitPane.class, JLabel.class, JSpinner.class,
            JSlider.class, JCheckBoxMenuItem.class, JComboBox.class, Box.class,
            JFormattedTextField.class, JList.class, JMenu.class,
            JMenuBar.class, JMenuItem.class, JPasswordField.class,
            JPopupMenu.class, JProgressBar.class, JRadioButtonMenuItem.class,
            JSeparator.class, JTable.class, JTextField.class, JTextArea.class,
            JTextPane.class, JToolBar.class, JToolTip.class, JTree.class, };

    String          cntpkg     = "containers";

    private Class[] cntList    = { JPanel.class, JTabbedPane.class,
            JScrollPane.class, JSplitPane.class };

    String          comppkg    = "components";

    private Class[] compList   = { JButton.class, JCheckBox.class,
            JRadioButton.class, JLabel.class, JSpinner.class, JSlider.class,
            JComboBox.class, Box.class, JFormattedTextField.class, JList.class,
            JPasswordField.class, JProgressBar.class, JTable.class,
            JTextField.class, JTextArea.class, JTextPane.class, JToolTip.class,
            JTree.class, JTableHeader.class };

    String          menupkg    = "menu";

    private Class[] menuList   = { JCheckBoxMenuItem.class, JMenu.class,
            JMenuBar.class, JMenuItem.class, JPopupMenu.class,
            JRadioButtonMenuItem.class, JSeparator.class, JToolBar.class };

    private Class[] layoutList = { GridBagLayout.class, BorderLayout.class,
            FlowLayout.class, GridLayout.class };

    private Class[] modelList  = { DefaultTableModel.class };
    private Class[] hackList   = { JTree.class };

    public void makeCList() {
//        makeCList(cntList, "org.maxml.gui.builders." + cntpkg);
//        makeCList(compList, "org.maxml.gui.builders." + comppkg);
//        makeCList(menuList, "org.maxml.gui.builders." + menupkg);
//         makeCList(modelList, "org.maxml.gui.builders.model" );
        // makeCList(layoutList, "layout");
        makeCList(hackList, "org.maxml.gui.builders." + comppkg);
    }

    public void makeCList(Class[] cl, String pkg) {
        for (int i = 0; i < cl.length; i++) {
            CachedClass cc = new CachedClass(cl[i]);// ReflectCache.getInstance().getClassCache(cl[i]);
            TreeMap ps = getPropertySetters(cc.setMethods());

            StringBuffer sb = getCodeStr(ps, cc.getShortName(), cc, pkg);
            try {
                File f = new File(Util.RD + File.separator
                        + pkg.replace('.', File.separatorChar) + File.separator
                        + cc.getShortName() + "Builder.java");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuffer getCodeStr(TreeMap propertySetters, String className,
            CachedClass cachedClass, String tpackage) {
        StringBuffer sb = new StringBuffer();

        sb.append("package " + tpackage + ";\n" + "\n"
                + "import javax.swing.JPanel;\n" + "import java.awt.Insets;\n"
                + "import java.awt.Component;\n"
                + "import java.awt.Container;\n"
                + "import java.awt.GridBagConstraints;\n"
                + "import java.awt.GridBagLayout;\n"
                + "import javax.swing.JButton;\n"
                + "import javax.swing.JCheckBox;\n"
                + "import org.maxml.gui.GUIUtils;\n"
                + "import org.maxml.gui.ComponentTree;\n"
                + "import org.maxml.gui.builders.ComponentBuilder;\n"
                + "import org.maxml.gui.builders.ContainedCompHandler;\n"
                + "import " + cachedClass.getThisClass().getName() + ";\n"
                + "import javax.swing.JLabel;\n"
                + "import javax.swing.JComponent;\n"
                + "import javax.swing.JTextField;\n"
                + "import javax.swing.SpinnerNumberModel;\n"
                + "import javax.swing.JComboBox;\n");

        if (!cachedClass.isOfType(Component.class)) {
            sb.append("import org.maxml.gui.ObjectBuilder;\n");
        }

        sb.append("import javax.swing.JSpinner;\n" + "\n" + "public class "
                + className + "Builder extends JPanel ");

        if (cachedClass.isOfType(Component.class)) {
            sb.append("implements ComponentBuilder {\n\n");
        } else {
            sb.append("implements ObjectBuilder {\n\n");
        }

        Iterator iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
                continue;
            sb.append(ps.getDeclCode());
        }

        if (cachedClass.isOfType(Component.class)) {
            sb.append("    private ContainedCompHandler containedCompHandler=null;\n");
        }

        if (propertySetters.containsKey("Name")) {
            sb.append("    private int nameNum=0;\n");
        }

        sb.append("\n\n    public " + className + "Builder() {\n");

        if (cachedClass.isOfType(Component.class)) {
            sb.append("        this(null);\n" + "    }\n\n");

            sb.append("\n\n    public " + className
                    + "Builder(ContainedCompHandler containedCompHandler) {\n"
                    + "        super();\n" + "\n");
            sb.append("        this.containedCompHandler = containedCompHandler;\n");
        }

        sb.append("        GridBagLayout gbl = new GridBagLayout();\n");
        sb.append("        GridBagConstraints gbc = new GridBagConstraints();\n");
        sb.append("        this.setLayout(gbl);\n");
        sb.append("        gbc.insets=new Insets(0,15,0,15);\n");
        sb.append("        gbc.gridx = gbc.gridy = 0;\n");
        int n = 0;
        int offset = 0;
        int gy, ay, by, cy;
        gy = ay = by = cy = -1;
        iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
                continue;

            if (ps.getGUIComponent() instanceof JSpinner) {
                offset = 2;
                ++ay;
                gy = ay;
            } else if (ps.getGUIComponent() instanceof JTextField) {
                offset = 4;
                ++by;
                gy = by;
            } else {
                offset = 0;
                ++cy;
                gy = cy;
            }

            String vname = lowcaseFirstLetter(pname) + "Comp";
            sb.append("        JLabel label" + n + " = new JLabel(\"" + pname
                    + "\");\n");
            sb.append("        gbc.gridx = " + offset + "; gbc.gridy=" + gy
                    + "; gbc.anchor=GridBagConstraints.WEST;\n");
            sb.append("        gbl.setConstraints(label" + n + ", gbc);\n");
            sb.append("        this.add(label" + n + ");\n\n");

            sb.append(ps.getInitCode());
            sb.append("        gbc.gridx = " + (offset + 1)
                    + "; gbc.anchor=GridBagConstraints.EAST;\n");
            sb.append("        gbl.setConstraints(" + vname + ", gbc);\n");
            sb.append("        this.add(" + vname + ");\n\n\n");

            if (ps.getGUIComponent() instanceof JTextField) {
                sb.append("        " + vname
                        + ".addCaretListener(ComponentTree.getInstance());\n");
            } else if (ps.getGUIComponent() instanceof JCheckBox) {
                sb.append("        " + vname
                        + ".addItemListener(ComponentTree.getInstance());\n");
            } else {
                sb.append("        " + vname
                        + ".addChangeListener(ComponentTree.getInstance());\n");
            }
            n++;
        }
        sb.append("        setObjectGUI( new " + className + "());\n");
        if (propertySetters.containsKey("Name")) {
            sb.append("        setName();\n");
        }
        sb.append("    }\n\n\n");

        sb.append("    public Object buildObject() {\n");
        sb.append("                return buildObject(null);\n");
        sb.append("    }\n\n");
        sb.append("    public Object buildObject(Object c) {\n");
        sb.append("        " + className + " comp = (c==null)? new " + className + "(): ("
                + className + ")c;\n");

        if (propertySetters.containsKey("Name")) {
            sb.append("        if(c==null) setName();\n");
        }

        iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
                continue;
            sb.append(ps.getSetCode("comp", pname));
        }
        
        StringBuffer genCodeBuf = new StringBuffer();
        
        sb.append("        return comp;\n\n    }\n\n");
        sb.append("    public String buildCode(String initCode) {\n");
        sb.append("        return buildCode(initCode, null);\n\n    }\n\n");
        sb.append("    public String buildCode(String initCode, Object model) {\n");
        sb.append("        StringBuffer buf = new StringBuffer();\n");
        sb.append("        buf.append(\"        " + className + " comp = new " + className
                + "();\\n\");\n");
        sb.append("        if( initCode != null ) buf.append(\"        \" + initCode + \"\\n\");\n");

        iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
                continue;
            genCodeBuf.append(ps.getGenCode("comp", pname));
        }
        if (cachedClass.isOfType(JComponent.class)) {

            genCodeBuf.append("        JComponent jcomp = (JComponent)model;\n");
            genCodeBuf.append("        if( jcomp.getFont() != null ) {\n");

            genCodeBuf.append("            buf.append(\"        comp.setFont( new Font(\\\"\"\n"
                    + "                    + jcomp.getFont().getFontName() + \"\\\", \"\n"
                    + "                    + jcomp.getFont().getStyle() + \", \" + jcomp.getFont().getSize()\n"
                    + "                    + \"));\\n\");\n");
            genCodeBuf.append("        }\n");

            genCodeBuf.append("        buf.append(\"        comp.setBackground( new Color(\"\n"
                    + "                + jcomp.getBackground().getRed() + \", \"\n"
                    + "                + jcomp.getBackground().getGreen() + \", \"\n"
                    + "                + jcomp.getBackground().getBlue() + \", \"\n"
                    + "                + jcomp.getBackground().getAlpha() + \"));\\n\");\n");
            genCodeBuf.append("        buf.append(\"        comp.setForeground( new Color(\"\n"
                    + "                + jcomp.getForeground().getRed() + \", \"\n"
                    + "                + jcomp.getForeground().getGreen() + \", \"\n"
                    + "                + jcomp.getForeground().getBlue() + \", \"\n"
                    + "                + jcomp.getForeground().getAlpha() + \"));\\n\");\n");
            
            genCodeBuf.append("        if( GUIUtils.i().hasBorder(jcomp.getBorder())) {\n");
            genCodeBuf.append("            buf.append(\"        comp.setBorder( get\" + GUIUtils.i().normalize(jcomp.getName()) + \"Border());\\n\");\n"); 
            genCodeBuf.append("        }\n");
            
        }
        if (cachedClass.isOfType(JTable.class)) {
            genCodeBuf.append("        buf.append(\"        comp.setModel( build\" + jcomp.getName() + \"Model(comp));\\n\");\n"); 
        }
        if (cachedClass.isOfType(JTree.class)) {
            genCodeBuf.append("        buf.append(\"        addExtensionsTo\" + jcomp.getName() + \"(comp);\\n\");\n"); 
        }
        genCodeBuf.append("        return buf.toString();\n\n    }\n\n");
        
        sb.append( genCodeBuf);
        
        sb.append("    public String buildSetCode(String initCode) {\n");
        sb.append("        return buildSetCode(initCode, null);\n\n    }\n\n");
        sb.append("    public String buildSetCode(String initCode, Object model) {\n");
        sb.append("        StringBuffer buf = new StringBuffer();\n");
        sb.append("        buf.append(\"        " + className + " comp = (" + className + ")o;\\n\");\n");
        sb.append("        if( initCode != null ) buf.append(\"        \" + initCode + \"\\n\");\n");

        sb.append(genCodeBuf);
        
        
        sb.append("    public void setObjectGUI( Object c ){\n");
        sb.append("        " + className + " comp = (" + className + ") c;\n");

        iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
                continue;
            if (cachedClass.getGetMethodName(pname) != null)
                sb.append(ps.getGetCode("comp",
                        cachedClass.getGetMethodName(pname)));
        }

        sb.append("    \n    }\n\n");

        sb.append("    public String getCode( Object c, String initCode ){\n");
        sb.append("        StringBuffer buf = new StringBuffer();\n");
        sb.append("        " + className + " comp = (" + className + ") c;\n");
        sb.append("        buf.append(\"        " + className + " comp = new " + className
                + "();\\n\");\n");
        sb.append("        if( initCode != null ) buf.append(\"        \" + initCode + \"\\n\");\n");

        iter = propertySetters.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            PropertySetter ps = (PropertySetter) propertySetters.get(pname);
            // if (ps instanceof PropertySetterFactory.AnyTypePropertySetter)
            // continue;
            String getMethName = cachedClass.getGetMethodName(pname);
            if (getMethName != null) {
                String quoteChar = "";
                if (cachedClass.propertyIsOfType(pname, String.class))
                    quoteChar = "\\\"";
                if (cachedClass.propertyIsOfType(pname, Character.TYPE))
                    quoteChar = "'";
                sb.append("        buf.append(\"        comp.set" + pname
                        + "( " + quoteChar + "\" + comp." + getMethName
                        + "() + \"" + quoteChar + " );\\n\" );\n");
            }
        }
        sb.append("        return buf.toString();\n\n    }\n\n");

        sb.append("    public Class getObjectClass() { return " + className
                + ".class; }\n\n");

        sb.append("    public JPanel getGUI() { return this; }\n");

        if (cachedClass.isOfType(Component.class)) {

            sb.append("    public Component addComponent( Container container, Component c, Object constraint ){\n");
            sb.append("        return containedCompHandler.add(container, c, constraint);\n    }\n");

            sb.append("    public Component removeComponent( Container container, Component c ){\n");
            sb.append("        return containedCompHandler.remove(container, c);\n    }\n");

            sb.append("    public ContainedCompHandler getContainedCompHandler() {\n");
            sb.append("        return containedCompHandler;\n");
            sb.append("    }\n\n");

            sb.append("    public void setContainedCompHandler(ContainedCompHandler containedCompHandler) {\n");
            sb.append("        this.containedCompHandler = containedCompHandler;\n");
            sb.append("    }\n\n");
        }

        if (propertySetters.containsKey("Name")) {
            sb.append("    public void setName() {\n");
            sb.append("        this.nameComp.setText( \"" + className
                    + "\" + (nameNum++));\n");
            sb.append("    }\n\n");
        }
        sb.append("}\n");

        return sb;
    }

    private String lowcaseFirstLetter(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private TreeMap getPropertySetters(TreeMap setMethods) {
        TreeMap propSetterMap = new TreeMap();

        Iterator iter = setMethods.keySet().iterator();
        while (iter.hasNext()) {
            String pname = (String) iter.next();
            String vname = lowcaseFirstLetter(pname) + "Comp";
            Method m = (Method) setMethods.get(pname);
            Class[] ptypes = m.getParameterTypes();
            if (ptypes.length == 1)
                propSetterMap.put(pname,
                        PropertySetterFactory.getPropertySetter(ptypes[0],
                                vname));
        }
        return propSetterMap;

    }

    private TreeMap getPropFieldSetters(List fields) {
        TreeMap propSetterMap = new TreeMap();

        Iterator iter = fields.iterator();
        while (iter.hasNext()) {
            Field f = (Field) iter.next();
            String vname = lowcaseFirstLetter(f.getName()) + "Comp";
            propSetterMap.put(f.getName(),
                    PropertySetterFactory.getPropertySetter(f.getType(), vname));
        }

        return propSetterMap;
    }

    /**
     * @param args
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Bean Setter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        BeanSetterPanel newContentPane = new BeanSetterPanel(new CachedClass(
                JTable.class, true), false);
        JScrollPane jsp = new JScrollPane(newContentPane);
        // newContentPane.setOpaque(true); //content panes must be opaque
        frame.getContentPane().add(jsp);
        frame.setSize(500, 800);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    class T {
        int       intVal;

        boolean   boolVal;

        String    strVal;

        double    doubleVal;

        Date      dateVal;

        ListModel listModel;

        public int getIntVal() {
            return intVal;
        }

        public void setIntVal(int intVal) {
            this.intVal = intVal;
        }

        public boolean isBoolVal() {
            return boolVal;
        }

        public void setBoolVal(boolean boolVal) {
            this.boolVal = boolVal;
        }

        public String getStrVal() {
            return strVal;
        }

        public void setStrVal(String strVal) {
            this.strVal = strVal;
        }

        public double getDoubleVal() {
            return doubleVal;
        }

        public void setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
        }

        public Date getDateVal() {
            return dateVal;
        }

        public void setDateVal(Date dateVal) {
            this.dateVal = dateVal;
        }

        public ListModel getListModel() {
            return listModel;
        }

        public void setListModel(ListModel listModel) {
            this.listModel = listModel;
        }
    }

    public static void main(String[] args) throws Exception {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.

        ReflectCache.i().addJar(
                "/home/jkirkley/jrp/setfer/lib/swing.jar");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
