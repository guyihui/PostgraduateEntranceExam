package com.sjtu.Utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {

    public static Scanner getTextFileScanner(String pathname) {
        File file = new File(pathname);
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compareTextFile(String output, String answer) {
        File outFile = new File(output);
        File ansFile = new File(answer);
        try {
            Scanner outSc = new Scanner(outFile);
            Scanner ansSc = new Scanner(ansFile);
            while (outSc.hasNextLine() && ansSc.hasNextLine()) {
                if (!outSc.nextLine().equals(ansSc.nextLine())) {
                    return false;
                }
            }
            if (outSc.hasNextLine() || ansSc.hasNextLine()) {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
