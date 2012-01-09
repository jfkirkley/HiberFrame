package org.maxml.reflect;

import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import org.maxml.util.GUIUtils;

public class PropertySetterFactory {

    public static class IntPropertySetter implements PropertySetter {
        JSpinner spinner;
        String   name;

        public IntPropertySetter(String name) {
            this.name = name;
            spinner = new JSpinner(new SpinnerNumberModel(0, -100, 1000, 1));
            GUIUtils.i().setSpinnerNumColumns(spinner, 3);
        }

        public Component getGUIComponent() {
            return spinner;
        }

        public Object getValue() {
            return spinner.getValue();
        }

        public String getDeclCode() {
            return "    JSpinner " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
            + propName + "( \" + GUIUtils.i().getSpinnerIntValue(" + name
            + ") + \");\\n\" );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setValue( new Integer(" + compName
                    + "." + propName + "() ));\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName
                    + "( GUIUtils.i().getSpinnerIntValue(" + name + "));\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JSpinner(new SpinnerNumberModel(0, -100, 1000, 1));\n"
                    + "        GUIUtils.i().setSpinnerNumColumns(" + name
                    + ", 3);\n";
        }
    }

    public static class LongPropertySetter implements PropertySetter {
        JSpinner spinner;
        String   name;

        public LongPropertySetter(String name) {
            this.name = name;
            spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
            GUIUtils.i().setSpinnerNumColumns(spinner, 3);
        }

        public Component getGUIComponent() {
            return spinner;
        }

        public Object getValue() {
            return spinner.getValue();
        }

        public String getDeclCode() {
            return "    JSpinner " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \" + GUIUtils.i().getSpinnerLongValue(" + name
                    + ") + \");\\n\" );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setValue( new Long(" + compName + "."
                    + propName + "() ));\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName
                    + "( GUIUtils.i().getSpinnerLongValue(" + name + "));\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));\n"
                    + "        GUIUtils.i().setSpinnerNumColumns(" + name
                    + ", 3);\n";
        }
    }

    public static class DoublePropertySetter implements PropertySetter {
        JSpinner spinner;
        String   name;

        public DoublePropertySetter(String name) {
            this.name = name;
            spinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0,
                    0.5));
            GUIUtils.i().setSpinnerNumColumns(spinner, 3);
        }

        public Component getGUIComponent() {
            return spinner;
        }

        public Object getValue() {
            return spinner.getValue();
        }

        public String getDeclCode() {
            return "    JSpinner " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \" + GUIUtils.i().getSpinnerDoubleValue("
                    + name + ") + \");\\n\" );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setValue( new Double(" + compName
                    + "." + propName + "() ));\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName
                    + "( GUIUtils.i().getSpinnerDoubleValue(" + name + "));\n";
        }

        public String getInitCode() {
            return "        "
                    + name
                    + " = new JSpinner(new SpinnerNumberModel(0.0, -100.0, 100.0, 0.5));\n"
                    + "        GUIUtils.i().setSpinnerNumColumns(" + name
                    + ", 3);\n";
        }
    }

    public static class FloatPropertySetter implements PropertySetter {
        JSpinner spinner;
        String   name;

        public FloatPropertySetter(String name) {
            this.name = name;
            spinner = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 100.0f,
                    0.5f));
            GUIUtils.i().setSpinnerNumColumns(spinner, 3);
        }

        public Component getGUIComponent() {
            return spinner;
        }

        public Object getValue() {
            return spinner.getValue();
        }

        public String getDeclCode() {
            return "    JSpinner " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \" + GUIUtils.i().getSpinnerFloatValue(" + name
                    + ") + \"f );\\n\" );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setValue( new Float(" + compName + "."
                    + propName + "() ));\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName
                    + "( GUIUtils.i().getSpinnerFloatValue(" + name + "));\n";
        }

        public String getInitCode() {
            return "        "
                    + name
                    + " = new JSpinner(new SpinnerNumberModel(0.0f, -100.0f, 100.0f, 0.5f));\n"
                    + "        GUIUtils.i().setSpinnerNumColumns(" + name
                    + ", 3);\n";
        }
    }

    public static class DatePropertySetter implements PropertySetter {
        JSpinner spinner;
        String   name;

        public DatePropertySetter(String name) {
            this.name = name;
            spinner = new JSpinner(new SpinnerDateModel());
            GUIUtils.i().setSpinnerNumColumns(spinner, 3);
        }

        public Component getGUIComponent() {
            return spinner;
        }

        public Object getValue() {
            return spinner.getValue();
        }

        public String getDeclCode() {
            return "    JSpinner " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \" + GUIUtils.i().getSpinnerDateValue(" + name
                    + ") + \");\\n\" );\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName
                    + "( GUIUtils.i().getSpinnerDateValue(" + name + "));\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setValue(" + compName + "." + propName
                    + "() );\n";
        }

        public String getInitCode() {
            return "        " + name
                    + " = new JSpinner(new SpinnerDateModel());\n"
                    + "        GUIUtils.i().setSpinnerNumColumns(" + name
                    + ", 3);\n";
        }
    }

    public static class BooleanPropertySetter implements PropertySetter {
        JCheckBox checkBox;
        String    name;

        public BooleanPropertySetter(String name) {
            this.name = name;
            checkBox = new JCheckBox();
        }

        public Component getGUIComponent() {
            return checkBox;
        }

        public Object getValue() {
            return new Boolean(checkBox.isSelected());
        }

        public String getDeclCode() {
            return "    JCheckBox " + name + ";\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName + "( " + name
                    + ".isSelected() );\n";
        }

        public String getGetCode(String compName, String getFuncName) {
            return "        " + name + ".setSelected(" + compName + "."
                    + getFuncName + "() );\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \" + ((" + name
                    + ".isSelected())? \"true\": \"false\") + \");\\n\""
                    + ");\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JCheckBox();\n";
        }
    }

    public static class StringPropertySetter implements PropertySetter {
        JTextField textField;
        String     name;

        public StringPropertySetter(String name) {
            this.name = name;
            textField = new JTextField();
            textField.setColumns(10);
        }

        public Component getGUIComponent() {
            return textField;
        }

        public Object getValue() {
            return textField.getText();
        }

        public String getDeclCode() {
            return "    JTextField " + name + ";\n";
        }
        //buf.append("        comp.setActionCommand( " + actionCommandComp.getText() + ");\n" );

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \\\"\" + " + name
                    + ".getText() + \"\\\");\\n\" );\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName + "( " + name
                    + ".getText() );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setText(" + compName + "." + propName
                    + "() );\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JTextField();\n" + "        "
                    + name + ".setColumns(10);\n";
        }
    }

    public static class CharPropertySetter implements PropertySetter {
        JTextField textField;
        String     name;

        public CharPropertySetter(String name) {
            this.name = name;
            textField = new JTextField();
            textField.setColumns(1);
        }

        public Component getGUIComponent() {
            return textField;
        }

        public Object getValue() {
            return textField.getText();
        }

        public String getDeclCode() {
            return "    JTextField " + name + ";\n";
        }
        //buf.append("        comp.setActionCommand( " + actionCommandComp.getText() + ");\n" );

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( \\\'\" + " + name
                    + ".getText() + \"\\\');\\n\" );\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName + "( " + name
                    + ".getText().charAt(0) );\n";
        }

        public String getGetCode(String compName, String propName) {
            return "        " + name + ".setText( Character.toString(" + compName + "." + propName
                    + "()) );\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JTextField();\n" + "        "
                    + name + ".setColumns(1);\n";
        }
    }

    
    public static class AnyTypePropertySetter implements PropertySetter {
        JComboBox comboBox;
        String    name;
        Class     type;

        public AnyTypePropertySetter(Class type, String name) {
            this.name = name;
            this.type = type;
            Vector types = new Vector(
                    ReflectCache.i().getClassesOfType(type));
            comboBox = new JComboBox(types);
            comboBox.setFont(new Font(comboBox.getFont().getFontName(),
                    Font.PLAIN, 10));
        }

        public Component getGUIComponent() {
            return comboBox;
        }

        public Object getValue() {
            return comboBox.getSelectedItem();
        }

        public String getGetCode(String compName, String propName) {
            return null;
        }

        public String getDeclCode() {
            return "    JComboBox " + name + ";\n";
        }

        public String getGenCode(String compName, String propName) {
            return "        buf.append(\"        " + compName + ".set"
                    + propName + "( (" + type.getName() + ")\" + " + name
                    + ".getSelectedItem()" + " + \");\\n\" );\n";
        }

        public String getSetCode(String compName, String propName) {
            return "        " + compName + ".set" + propName + "( ("
                    + type.getName() + ")" + name + ".getSelectedItem() );\n";
        }

        public String getInitCode() {
            return "        " + name + " = new JComboBox();\n" + "        "
                    + name + ".setFont( new Font(" + name
                    + ".getFont().getFontName(), Font.PLAIN, 10));\n";
        }

    }

    public static PropertySetter getPropertySetter(Class c, String name) {
        if (c == Integer.TYPE)
            return new IntPropertySetter(name);

        if (c == Long.TYPE)
            return new LongPropertySetter(name);

        if (c == Boolean.TYPE)
            return new BooleanPropertySetter(name);

        if (c == Character.TYPE)
            return new CharPropertySetter(name);

        if (c == Byte.TYPE)
            return null;

        if (c == Short.TYPE)
            return null;

        if (c == Double.TYPE)
            return new DoublePropertySetter(name);

        if (c == Float.TYPE)
            return new FloatPropertySetter(name);

        if (c == Void.TYPE)
            return null;

        if (c == String.class)
            return new StringPropertySetter(name);

        if (c == Date.class)
            return new DatePropertySetter(name);

        return new AnyTypePropertySetter(c, name);
    }

}
