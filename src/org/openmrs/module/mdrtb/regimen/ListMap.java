package org.openmrs.module.mdrtb.regimen;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ListMap<K, V> extends TreeMap<K, List<V>> {

    public ListMap() {
        super();
    }
    
    public void add(K key, V value) {
        List<V> list = get(key);
        if (list == null) {
            list = new ArrayList<V>();
            put(key, list);
        }
        list.add(value);
    }
    
}
