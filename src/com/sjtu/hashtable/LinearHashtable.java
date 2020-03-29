package com.sjtu.hashtable;

import com.sjtu.Utils.KVPair;

import java.util.Arrays;

public final class LinearHashtable implements MyHashtable {
    public static final int INITIAL_CAPACITY = 8;
    public static final String IMPLEMENTATION = "Linear";

    @Override
    public String getImplementation() {
        return IMPLEMENTATION;
    }

    private int H(int key) {
        if (key >= 0) {
            return key % capacity;
        } else {
            return key + ((key + 1) / -capacity + 1) * capacity;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(table);
    }

    private KVPair[] table = new KVPair[INITIAL_CAPACITY];
    private Integer capacity = INITIAL_CAPACITY;
    private Integer size = 0;

    LinearHashtable() {
        System.out.println("initializing table...");
        KVPair.initArray(table);
        System.out.println("OK:\t" + IMPLEMENTATION);
    }

    @Override
    public Boolean set(Integer key, Integer value) {
        int base = H(key);
        for (int i = 0; i < capacity; i++) {
            int pos = H(base + i);
            if (table[pos].isUsed && table[pos].key == key) {
                return table[pos].set(key, value);
            }
            if (!table[pos].isUsed) {
                if (size + 1 > capacity / 2) {
                    resize();
                    return this.set(key, value);
                } else {
                    size++;
                    return table[pos].set(key, value);
                }
            }
        }
        return false;
    }

    @Override
    public Integer get(Integer key) {
        int base = H(key);
        for (int i = 0; i < capacity; i++) {
            int pos = H(base + i);
            if (!table[pos].isUsed) {
                return null;
            } else {
                if (table[pos].key == key) {
                    return table[pos].value;
                }
            }
        }
        return null;
    }

    @Override
    public Boolean delete(Integer key) {
        int base = H(key);
        for (int i = 0; i < capacity; i++) {
            int pos = H(base + i);
            if (!table[pos].isUsed) {
                return false;
            } else {
                if (table[pos].key == key) {
                    size--;
                    table[pos].remove();
                    int start = findStart(base);
                    forward(start, pos);
                    return true;
                }
            }
        }
        return null;
    }

    @Override
    public Integer resize() {
        KVPair[] oldTable = table;
        Integer oldCapacity = capacity;

        capacity *= 2;
        size = 0;
        table = new KVPair[capacity];
        KVPair.initArray(table);
        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable[i].isUsed) {
                this.set(oldTable[i].key, oldTable[i].value);
            }
        }
        return capacity;
    }

    @Override
    public Integer size() {
        return size;
    }

    @Override
    public Integer getCapacity() {
        return capacity;
    }

    private int findStart(int pos) {
        for (int i = 1; i <= capacity; i++) {
            int itr = H(pos + capacity - i);
            if (!table[itr].isUsed) {
                return H(itr + 1);
            }
        }
        return -1;
    }

    // 在 blank 之后连续的 pair 中，寻找 H值属于[start, blank]的pair，填入blank，递归
    private void forward(int start, int blank) {
        int selected = blank;
        for (int i = 1; i <= capacity; i++) {
            int pos = H(blank + i);
            if (!table[pos].isUsed) {
                if (selected == blank) {
                    return;
                } else {
                    table[blank].copy(table[selected]);
                    table[selected].remove();
                    forward(H(blank + 1), selected);
                    return;
                }
            } else {
                if (isBetween(H(table[pos].key), start, blank)) {
                    selected = pos;
                }
            }
        }
    }

    private boolean isBetween(int hash, int start, int end) {
        if (start <= end) {
            return start <= hash && hash <= end;
        } else {
            return start <= hash || hash <= end;
        }
    }

}
