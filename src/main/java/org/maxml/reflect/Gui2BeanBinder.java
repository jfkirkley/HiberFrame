package org.maxml.reflect;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolTip;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.maxml.util.Util;

public class Gui2BeanBinder implements ChangeListener, CaretListener, ActionListener,
    ItemListener {
    
    private HashMap bindingMap;
    Object target;
    CachedClass cachedClass;
    public Gui2BeanBinder(Object target) {
        bindingMap = new HashMap();
        this.target = target;
        cachedClass = new CachedClass(target.getClass());
    }
    
    public void addBinding(String propertyName, Component comp ){ 
        bindingMap.put(propertyName, comp);
        if (comp instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) comp;
            textComponent.addCaretListener(this);
        } else if (comp instanceof JSpinner) {
            JSpinner spinner = (JSpinner) comp;
            spinner.addChangeListener(this);
        } else if (comp instanceof JSlider) {
            JSlider slider = (JSlider) comp;
            slider.addChangeListener(this);
        } else if (comp instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) comp;
            checkBox.addItemListener(this);
        } else if (comp instanceof JRadioButton) {
            JRadioButton radioButton = (JRadioButton) comp;
            radioButton.addItemListener(this);
        } else if (comp instanceof JProgressBar) {
            JProgressBar progressBar = (JProgressBar) comp;
            progressBar.addChangeListener(this);
//        } else if (comp instanceof JButton) {
//            JButton button = (JButton) comp;
//        } else if (comp instanceof JLabel) {
//            JLabel label = (JLabel) comp;
//        } else if (comp instanceof JToolTip) {
//            JToolTip toolTip = (JToolTip) comp;
        }
       
    }
    
    public void setCompValue(String propertyName) {
        Object value = cachedClass.invokeGetMethod(target, propertyName);
        Component comp = (Component) bindingMap.get(propertyName);
        if (comp instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) comp;
            textComponent.setText(value.toString());
        } else if (comp instanceof JSpinner) {
            JSpinner spinner = (JSpinner) comp;
            spinner.setValue(value);
        } else if (comp instanceof JSlider) {
            JSlider slider = (JSlider) comp;
            slider.setValue(((Number)value).intValue());
        } else if (comp instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) comp;
            checkBox.setSelected(((Boolean)value).booleanValue());
        } else if (comp instanceof JRadioButton) {
            JRadioButton radioButton = (JRadioButton) comp;
            radioButton.setSelected(((Boolean)value).booleanValue());
        } else if (comp instanceof JProgressBar) {
            JProgressBar progressBar = (JProgressBar) comp;
            progressBar.setValue(((Number)value).intValue());
        } else if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            button.setText(value.toString());
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            label.setText(value.toString());
//        } else if (comp instanceof JComboBox) {
//            JComboBox comboBox = (JComboBox) comp;
//            Collection collection = (Collection)value;
//            for (Iterator iter = collection.iterator(); iter.hasNext();) {
//                comboBox.addItem( iter.next() );
//            }
        } else if (comp instanceof JToolTip) {
            JToolTip toolTip = (JToolTip) comp;
            toolTip.setTipText(value.toString());
        }
    }
    
    public void setPropValue(Component comp) {
        Object value=null;
        String propertyName = (String)Util.i().getMapKey(bindingMap, comp);
        if (comp instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) comp;
            value = textComponent.getText();
        } else if (comp instanceof JSpinner) {
            JSpinner spinner = (JSpinner) comp;
            value = spinner.getValue();
        } else if (comp instanceof JSlider) {
            JSlider slider = (JSlider) comp;
            value = new Integer(slider.getValue());
        } else if (comp instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) comp;
            value = new Boolean(checkBox.isSelected());
        } else if (comp instanceof JRadioButton) {
            JRadioButton radioButton = (JRadioButton) comp;
            value = new Boolean(radioButton.isSelected());
        } else if (comp instanceof JProgressBar) {
            JProgressBar progressBar = (JProgressBar) comp;
            value = new Integer(progressBar.getValue());
        } else if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            value = button.getText();
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            value = label.getText();
        } else if (comp instanceof JToolTip) {
            JToolTip toolTip = (JToolTip) comp;
            value = toolTip.getTipText();
        }
        cachedClass.invokeSetMethod(target, propertyName, value);
        
        System.out.println("target: " + target);
    }
    
    
    public void caretUpdate(CaretEvent e) {
        setPropValue((Component)e.getSource());
    }

    public void stateChanged(ChangeEvent e) {
        setPropValue((Component)e.getSource());
    }

    public void itemStateChanged(ItemEvent e) {
        setPropValue((Component)e.getSource());
    }

    public void actionPerformed(ActionEvent e) {
        setPropValue((Component)e.getSource());
    }

}
