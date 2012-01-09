package org.maxml.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;

import org.maxml.reflect.CachedClass;

public class LookUpTable {

    TreeMap<String,String> table;
    String  currKey;

    public LookUpTable() {
        this(new TreeMap());
    }

    public LookUpTable(TreeMap<String,String> table) {
        this.table = table;
        currKey = "";
    }

    public void append(Map<String,String> map) {
        table.putAll(map);
    }

    public void appendKey(String ch) {
        currKey += ch;
    }

    public void setKey(String val) {
        currKey = val;
    }

    public void reset() {
        currKey = "";
    }

    public List<String> getMatchList() {
        ArrayList<String> matchList = new ArrayList<String>();
        String tkey = currKey.toLowerCase();
        for ( String key: table.values() ) {
            if (key.toLowerCase().startsWith(tkey)) {
                matchList.add(key);
            }
        }
        return matchList;
    }


    public void removeEntries(Map<String,String> map) {
        Util.i().removeMap(map, table);
    }

    public static LookUpTable makeCommonTypeLookUpTable() {
        TreeMap<String,String> map = new TreeMap<String,String>();

        for (int i = 0; i < TypeInfo.primTypes.length; i++) {
            String type = TypeInfo.primTypes[i];
            map.put(type, type);
        }
        Class<?>[] types = { String.class, Integer.class, Long.class, Byte.class, Boolean.class, Character.class,
                Float.class, Double.class, Short.class, StringBuffer.class, Object.class, List.class, Set.class,
                Map.class, Collection.class, ArrayList.class, BitSet.class, Calendar.class, Currency.class, Date.class,
                Dictionary.class, GregorianCalendar.class, HashMap.class, HashSet.class, Hashtable.class,
                IdentityHashMap.class, LinkedHashMap.class, LinkedHashSet.class, LinkedList.class,
                ListResourceBundle.class, Properties.class, PropertyResourceBundle.class, ResourceBundle.class,
                SimpleTimeZone.class, Stack.class, TimeZone.class, TreeMap.class, TreeSet.class, Vector.class,
                WeakHashMap.class };

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            map.put(CachedClass.getShortName(type), CachedClass.getShortName(type) + " - "
                    + type.getPackage().getName());
        }

        return new LookUpTable(map);
    }

    public static LookUpTable makePersistantTypeLookUpTable() {
        TreeMap<String,String> map = new TreeMap<String,String>();

        for (int i = 0; i < TypeInfo.primTypes.length; i++) {
            String type = TypeInfo.primTypes[i];
            map.put(type, type);
        }
        Class<?>[] types = { String.class, Integer.class, Long.class, Byte.class, Boolean.class, Character.class,
                Float.class, Double.class, Short.class, StringBuffer.class, Calendar.class, Currency.class, Date.class,
                GregorianCalendar.class, SimpleTimeZone.class, TimeZone.class };

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            map.put(CachedClass.getShortName(type), CachedClass.getShortName(type) + " - "
                    + type.getPackage().getName());
        }

        return new LookUpTable(map);
    }

}
