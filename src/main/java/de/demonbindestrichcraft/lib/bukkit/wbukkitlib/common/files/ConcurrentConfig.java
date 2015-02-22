/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public final class ConcurrentConfig {

    private Map<String, String> properties;
    File propertiesFile;
    
     public ConcurrentConfig() {
        properties = new ConcurrentHashMap<String, String>();
        propertiesFile = new File("config.werri.txt");
    }

    public ConcurrentConfig(File file) {
        this();
        init(file);
    }

    public ConcurrentConfig(File file, Map<String, String> properties) {
        this(file);
        update(properties);
    }
    
    public synchronized void init(File file)
    {
        if(!(file instanceof File))
            file = this.propertiesFile;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ConcurrentConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        propertiesFile = file;
    }
    
    public synchronized void updateIncorrectValueMap(Map properties) {
        Map<String,String> temp = new ConcurrentHashMap<String, String>();
        Set keys = new HashSet();
        keys.addAll(properties.keySet());
        for(Object key : keys)
        {
            temp.put(key.toString(), properties.get(key).toString());
        }
        update(temp);
    }
       
    public synchronized void update(Map<String, String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, String> getCopyOfProperties() {
        return new ConcurrentHashMap<String, String>(properties);
    }

    public String getString(String key) throws NullPointerException, ClassCastException {
        return properties.get(key);
    }

    public int getInt(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Integer.parseInt(properties.get(key));
    }

    public float getFloat(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Float.parseFloat(properties.get(key));
    }

    public double getDouble(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Double.parseDouble(properties.get(key));
    }

    public long getLong(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Long.parseLong(properties.get(key));
    }

    public short getShort(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Short.parseShort(properties.get(key));
    }

    public boolean getBoolean(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Boolean.parseBoolean(properties.get(key));
    }

    public synchronized void save(String splitchar) {
        GenerallyFileManager.FileWrite(properties, propertiesFile, splitchar);
    }
    
    public synchronized void load(File file, String splitchar) {
        init(file);
        Map<String,String> content = GenerallyFileManager.AllgemeinFileReader(file, splitchar);
        if(content == null)
            return;
        update(content);
    }
}
