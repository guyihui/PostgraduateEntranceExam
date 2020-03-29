package com.sjtu.hashtable;

import com.sjtu.Utils.FileUtils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class MyHashtableTest {

    public static final String smallInputPath = "testdata/hashtable/small.in";
    public static final String smallAnswerPath = "testdata/hashtable/small.ans";
    public static final String smallOutputPath = "testdata/hashtable/out/small.out";

    public static final String largeInputPath = "testdata/hashtable/large.in";
    public static final String largeAnswerPath = "testdata/hashtable/large.ans";
    public static final String largeOutputPath = "testdata/hashtable/out/large.out";

    public static final String initOutputPath = "testdata/hashtable/out/first-1000-latency.csv";
    public static final String performanceOutputPath = "testdata/hashtable/out/performance.csv";
    public static int initialSize = 10000;
    public static int sampleSize = 1000000;

    public static void main(String[] args) {

        FunctionTest.functionalTest();
        PerformanceTest.performanceTest();

    }

    private static class FunctionTest {
        private static void functionalTest() {
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
            generateOutput(t, largeInputPath, largeOutputPath);
            boolean pass = FileUtils.compareTextFile(largeOutputPath, largeAnswerPath);
            System.out.println("Large:" + (pass ? "Pass." : "Fail."));
        }

        private static void smallTest(MyHashtable t) {
            generateOutput(t, smallInputPath, smallOutputPath);
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

    private static class PerformanceTest {

        enum Operation {GET, SET,}

        private static Operation getOrSet(Random random, int getWeight, int setWeight) {
            int gs = random.nextInt(getWeight + setWeight);
            if (gs < getWeight) {
                return Operation.GET;
            } else {
                return Operation.SET;
            }
        }

        private static void performanceTest() {
            LinearHashtable linearHashtable = new LinearHashtable();
            CuckooHashtable cuckooHashtable = new CuckooHashtable();
            initTableForPerformanceTest(initOutputPath, linearHashtable, cuckooHashtable);
            try {
                FileWriter fw = new FileWriter(performanceOutputPath);
                fw.close();
                generateOutput(performanceOutputPath, 1, 0, linearHashtable, cuckooHashtable);
                generateOutput(performanceOutputPath, 5, 1, linearHashtable, cuckooHashtable);
                generateOutput(performanceOutputPath, 4, 1, linearHashtable, cuckooHashtable);
                generateOutput(performanceOutputPath, 3, 1, linearHashtable, cuckooHashtable);
                generateOutput(performanceOutputPath, 2, 1, linearHashtable, cuckooHashtable);
                generateOutput(performanceOutputPath, 1, 1, linearHashtable, cuckooHashtable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void generateOutput(String outputPath, int getWeight, int setWeight, MyHashtable... tables) {
            Random operationRandom = new Random();
            Random random = new Random();
            try {
                FileWriter fw = new FileWriter(outputPath, true);
                fw.write("\nget : set =," + getWeight + "," + setWeight + ",\n");
                fw.write("implementation,throughput(rq/s),min_latency(ns),max_latency(ns),avg_latency(ns),\n");

                for (MyHashtable hashtable : tables) {
                    fw.write(hashtable.getImplementation() + ",");
                    long totalLatency = 0;
                    long minLatency = Long.MAX_VALUE;
                    long maxLatency = 0;
                    for (int i = 0; i < sampleSize; i++) {
                        int key = random.nextInt(initialSize);
                        long start;
                        long end;
                        if (getOrSet(operationRandom, getWeight, setWeight) == Operation.GET) {
                            start = System.nanoTime();
                            hashtable.get(key);
                            end = System.nanoTime();
                        } else {
                            start = System.nanoTime();
                            hashtable.set(key, key);
                            end = System.nanoTime();
                        }
                        long latency = end - start;
                        totalLatency += latency;
                        minLatency = latency < minLatency ? latency : minLatency;
                        maxLatency = latency > maxLatency ? latency : maxLatency;
                    }
                    double throughput = ((double) sampleSize) / (double) totalLatency * 1000 * 1000 * 1000;
                    double avgLatency = ((double) totalLatency) / (double) sampleSize;
                    fw.write(throughput + "," + minLatency + "," + maxLatency + "," + avgLatency + ",\n");
                }
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void initTableForPerformanceTest(String outputPath, MyHashtable... tables) {
            Random random = new Random();
            try {
                FileWriter fw = new FileWriter(outputPath);
                for (MyHashtable t : tables) {
                    fw.write(t.getImplementation() + ",");
                }
                fw.write("\n");

                for (int i = 0; i < initialSize; i++) {
                    for (MyHashtable hashtable : tables) {
                        int key = random.nextInt(initialSize);
                        if (i < 1000) {
                            long start = System.nanoTime();
                            hashtable.set(key, key);
                            long end = System.nanoTime();
                            fw.write((end - start) + ",");
                            continue;
                        }
                        hashtable.set(key, key);
                    }
                    fw.write("\n");
                }
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
