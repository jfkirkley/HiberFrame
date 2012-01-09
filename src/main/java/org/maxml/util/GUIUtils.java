package org.maxml.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.maxml.reflect.CachedClass;
import org.maxml.util.Util;

public class GUIUtils {

    private static GUIUtils instance=null;
    
    public static GUIUtils i() {
        if(instance == null) {
            instance = new GUIUtils();
        }
        return instance;
    }
    
    public Container loadContainer() {

        Class clazz = Util.i().loadClassFromFile();
        String cname = CachedClass.getShortName(clazz);
        try {
            Object o = clazz.newInstance();
            Method m = clazz.getMethod("build" + cname, null);
            return (Container) m.invoke(o, null);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return the formatted text field used by the editor, or null if the editor
     * doesn't descend from JSpinner.DefaultEditor.
     */
    public JFormattedTextField getTextField(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            return ((JSpinner.DefaultEditor) editor).getTextField();
        } else {
            System.err.println("Unexpected editor type: "
                    + spinner.getEditor().getClass()
                    + " isn't a descendant of DefaultEditor");
            return null;
        }
    }

    public void setSpinnerNumColumns(JSpinner spinner, int numCols) {
        JFormattedTextField formattedTextField = getTextField(spinner);
        if (formattedTextField != null) {
            formattedTextField.setColumns(numCols);
        }
    }

    public int getValue(Integer val) {
        return val.intValue();
    }

    public double getValue(Double val) {
        return val.doubleValue();
    }

    public float getValue(Float val) {
        return val.floatValue();
    }

    public long getValue(Long val) {
        return val.longValue();
    }

    public boolean hasBorder(Border b) {

        if (b == null)
            return false;
        if (b instanceof CompoundBorder) {
            CompoundBorder cb = (CompoundBorder) b;
            if (!hasBorder(cb.getInsideBorder()))
                return false;
            if (!hasBorder(cb.getOutsideBorder()))
                return false;
        }
        if (b instanceof TitledBorder) {
            return hasBorder(((TitledBorder) b).getBorder());
        }

        return false;//BorderChooserPanel.getInstance().isSupportedBorder(b);
    }

    public String normalize(String s) {
        // return s.replace(' ', '_').replace(',', '_').replace(',',
        // '_').replace(
        // '&', '_').replace('!', '_').replace('*', '_').replace('@',
        // '_').replace(
        // '(', '_').replace('#', '_').replace(')', '_').replace('$',
        // '_').replace(
        // '-', '_').replace('%', '_').replace('=', '_').replace('^',
        // '_').replace(
        // '+', '_').replace('{', '_').replace('\\', '_').replace('}',
        // '_').replace(
        // '|', '_').replace('[', '_').replace('\'', '_').replace(']',
        // '_').replace(
        // '"', '_').replace('?', '_').replace(';', '_').replace('/',
        // '_').replace(
        // ':', '_').replace('`', '_').replace('<', '_').replace('~',
        // '_').replace(
        // '>', '_');
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && !Character.isJavaIdentifierStart(s.charAt(i)))
                sb.append('_');
            else if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                sb.append('_');
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    public int getSpinnerIntValue(JSpinner spinner) {
        Integer i = (Integer) spinner.getValue();
        return i.intValue();
    }

    public long getSpinnerLongValue(JSpinner spinner) {
        Object o = spinner.getValue();
        long l = 0;
        if (o instanceof Long) {
            Long lg = (Long) o;
            l = lg.longValue();
        } else if (o instanceof Integer) {
            Integer i = (Integer) o;
            l = i.longValue();
        }
        return l;
    }

    public float getSpinnerFloatValue(JSpinner spinner) {
        Object o = spinner.getValue();
        float ff = 0.0f;
        if (o instanceof Double) {
            Double f = (Double) o;
            ff = f.floatValue();
        } else if (o instanceof Float) {
            Float f = (Float) o;
            ff = f.floatValue();
        }
        return ff;
    }

    public double getSpinnerDoubleValue(JSpinner spinner) {
        Double d = (Double) spinner.getValue();
        return d.doubleValue();
    }

    public void addActionForJavaIdendifierKeys(Action a, JComponent jcomp) {
        for (int i = 0; i < 255; ++i) {
            char c = (char) i;
            if (Character.isLetterOrDigit(c)) {// Character.isJavaIdentifierPart(c))
                                                // {
            // System.out.println("adding " + KeyStroke.getKeyStroke(c) +
            // ":" +
            // Character.toString(c));
                jcomp.getInputMap().put(KeyStroke.getKeyStroke(c),
                        Character.toString(c));
                jcomp.getActionMap().put(Character.toString(c), a);
            }
        }
    }

    public void addActionForAllKeys(Action a, JComponent jcomp) {
        for (int i = 0; i < 1024; ++i) {
            char c = (char) i;
            jcomp.getInputMap().put(KeyStroke.getKeyStroke(c),
                    Character.toString(c));
            jcomp.getActionMap().put(Character.toString(c), a);
        }
    }
   
    public int getIndexOfItem(JComboBox comboBox, Object item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if( comboBox.getItemAt(i).equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public void removeActionListeners(JTextField textField) {
        ActionListener[] actionListeners = textField.getActionListeners();
        for (int i = 0; i < actionListeners.length; i++) {
            // System.out.println(actionListeners[i]);
            textField.removeActionListener(actionListeners[i]);
        }
    }

    public void printActionMap(JComponent jcomp) {
        Object[] keys = jcomp.getActionMap().keys();
        for (int i = 0; i < keys.length; i++) {
            System.out.println("keys: " + keys[i] + ": "
                    + jcomp.getActionMap().get(keys[i]));
        }
    }

    public void repaint(Component c){
        c.getParent().validate();
        c.repaint();
    }
}
