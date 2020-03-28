package com.sjtu.Utils;

public class KVPair {

    public int key;
    public int value;
    public boolean isUsed = false;

    public boolean set(int key, int value) {
        this.key = key;
        this.value = value;
        isUsed = true;
        return true;
    }

    public void remove() {
        isUsed = false;
    }

    public void copy(KVPair other) {
        key = other.key;
        value = other.value;
        isUsed = other.isUsed;
    }

    public boolean checkKey(int key) {
        return isUsed && this.key == key;
    }

    @Override
    public String toString() {
        return " " + (isUsed ? key : "-");
    }

    public static void initArray(KVPair[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = new KVPair();
        }
    }
}
