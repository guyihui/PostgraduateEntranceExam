package com.sjtu.hashtable;

import com.sjtu.Utils.FileUtils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MyHashtableTest {

    public static final String smallInputPath = "testdata/hashtable/small.in";
    public static final String smallOutputPath = "testdata/hashtable/small.out";
    public static final String smallAnswerPath = "testdata/hashtable/small.ans";

    public static final String largeInputPath = "testdata/hashtable/large.in";
    public static final String largeOutputPath = "testdata/hashtable/large.out";
    public static final String largeAnswerPath = "testdata/hashtable/large.ans";

    public static void main(String[] args) {

        LinearHashtable table1 = new LinearHashtable();
        smallTest(table1);
        LinearHashtable table2 = new LinearHashtable();
        largeTest(table2);

        CuckooHashtable cuckooHashtable1 = new CuckooHashtable();
        smallTest(cuckooHashtable1);
        CuckooHashtable cuckooHashtable2 = new CuckooHashtable();
        largeTest(cuckooHashtable2);

    }

    private static void largeTest(MyHashtable t) {
        assert generateOutput(t, largeInputPath, largeOutputPath);
        boolean pass2 = FileUtils.compareTextFile(largeOutputPath, largeAnswerPath);
        System.out.println("Large:" + (pass2 ? "Pass." : "Fail."));
    }

    private static void smallTest(MyHashtable t) {
        assert generateOutput(t, smallInputPath, smallOutputPath);
        boolean pass = FileUtils.compareTextFile(smallOutputPath, smallAnswerPath);
        System.out.println("Small:" + (pass ? "Pass." : "Fail."));
    }

    private static boolean generateOutput(MyHashtable table, String inputPath, String outputPath) {
        Scanner sc = FileUtils.getTextFileScanner(inputPath);
        if (sc == null) return false;
        try {
            FileWriter fw = new FileWriter(outputPath);
            while (sc.hasNextLine()) {
                String[] operation = sc.nextLine().split(" ");
                switch (operation[0]) {
                    case "Set":
                        table.set(Integer.valueOf(operation[1]), Integer.valueOf(operation[2]));
                        break;
                    case "Get":
                        Integer value = table.get(Integer.valueOf(operation[1]));
                        fw.write(value == null ? "null\n" : value + "\n");
                        break;
                    case "Del":
                        table.delete(Integer.valueOf(operation[1]));
                        break;
                    default:
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("output: " + outputPath);
        return true;
    }
}
