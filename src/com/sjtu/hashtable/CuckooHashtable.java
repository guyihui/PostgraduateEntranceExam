package com.sjtu.hashtable;

import com.sjtu.Utils.KVPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CuckooHashtable implements MyHashtable {
    public static final int INITIAL_TABLE_CAPACITY = 8;
    public static final int CUCKOO_TABLES = 2;
    public static final int INITIAL_CAPACITY = INITIAL_TABLE_CAPACITY * CUCKOO_TABLES;// 16
    public static final String IMPLEMENTATION = "Cuckoo hashing";

    private int H1(int key) {
        if (key >= 0) {
            return key % tableCapacity;
        } else {
            return key + ((key + 1) / -tableCapacity + 1) * tableCapacity;
        }
    }

    private int H2(int key) {
        // 负数除法题目未定义
        return H1(key / tableCapacity);
    }

    @Override
    public String toString() {
        return "-" + Arrays.toString(table1) + "\n=" + Arrays.toString(table2);
    }

    private KVPair[] table1 = new KVPair[INITIAL_TABLE_CAPACITY];
    private KVPair[] table2 = new KVPair[INITIAL_TABLE_CAPACITY];
    private Integer tableCapacity = INITIAL_TABLE_CAPACITY;
    private Integer capacity = INITIAL_CAPACITY;
    private Integer size = 0;

    CuckooHashtable() {
        System.out.println("initializing table...");
        KVPair.initArray(table1);
        KVPair.initArray(table2);
        System.out.println("OK:\t" + IMPLEMENTATION);
    }

    @Override
    public Boolean set(Integer key, Integer value) {
        while (hasCycle(key)) {
            resize();
        }
        if (table1[H1(key)].isUsed) {
            moveToH2(table1[H1(key)]);
        }
        table1[H1(key)].set(key, value);
        size++;
        return true;
    }

    private boolean moveToH1(KVPair pair) {
        KVPair next = table1[H1(pair.key)];
        if (next.isUsed) {
            moveToH2(next);
        }
        table1[H1(pair.key)].set(pair.key, pair.value);
        return true;
    }

    private boolean moveToH2(KVPair pair) {
        KVPair next = table2[H2(pair.key)];
        if (next.isUsed) {
            moveToH1(next);
        }
        table2[H2(pair.key)].set(pair.key, pair.value);
        return true;
    }

    @Override
    public Integer get(Integer key) {
        if (table1[H1(key)].checkKey(key)) return table1[H1(key)].value;
        if (table2[H2(key)].checkKey(key)) return table2[H2(key)].value;
        return null;
    }

    @Override
    public Boolean delete(Integer key) {
        if (table1[H1(key)].checkKey(key)) {
            size--;
            table1[H1(key)].remove();
            return true;
        }
        if (table2[H2(key)].checkKey(key)) {
            size--;
            table2[H2(key)].remove();
            return true;
        }
        return false;
    }

    @Override
    public Integer resize() {
        KVPair[] oldTable1 = table1;
        KVPair[] oldTable2 = table2;
        Integer oldTableCapacity = tableCapacity;

        tableCapacity *= 2;
        capacity *= 2;
        size = 0;
        table1 = new KVPair[tableCapacity];
        table2 = new KVPair[tableCapacity];
        KVPair.initArray(table1);
        KVPair.initArray(table2);
        restoreOldTable(oldTable1, oldTableCapacity);
        restoreOldTable(oldTable2, oldTableCapacity);
        return capacity;
    }

    private void restoreOldTable(KVPair[] oldTable2, Integer oldTableCapacity) {
        for (int i = 0; i < oldTableCapacity; i++) {
            if (oldTable2[i].isUsed) {
                this.set(oldTable2[i].key, oldTable2[i].value);
            }
        }
    }

    @Override
    public Integer size() {
        return size;
    }

    @Override
    public Integer getCapacity() {
        return capacity;
    }

    private boolean hasCycle(Integer key) {
        KVPair pair = table1[H1(key)];
        if (!pair.isUsed) return false;

        int t = 1;
        Set<KVPair> set = new HashSet<>();
        while (pair.isUsed) {
            if (set.contains(pair)) return true;
            set.add(pair);
            assert t == 1 || t == 2;
            pair = t == 1 ? table2[H2(pair.key)] : table1[H1(pair.key)];
            t = 3 - t;// switch between 1 and 2
        }
        return false;
    }
}
