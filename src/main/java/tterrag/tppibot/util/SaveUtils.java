package tterrag.tppibot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SaveUtils {

    public static String readTextFile(String filename) {
        return readTextFile(new File(filename));
    }

    public static String readTextFile(File file) {
        try {
            Scanner scan = new Scanner(file);

            String s = "";

            while (scan.hasNextLine()) {
                s += scan.nextLine() + "\n";
            }

            if (s.length() > 0)
                s = s.substring(0, s.length() - 1);

            scan.close();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Reading of file " + file.getName() + " failed! Returning empty string...");
            return "";
        }
    }

    public static boolean saveAllToFile(String filename, String... strings) {
        return saveAllToFile(new File(filename), strings);
    }

    public static boolean saveAllToFile(File file, String... strings) {
        try {
            FileWriter fw = new FileWriter(file);

            for (String s : strings) {
                fw.write(s);
            }

            fw.flush();
            fw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Saving to file " + file.getName() + " failed! Aborting shut down...");
            return false;
        }
    }

    public static boolean addToFile(String filename, String... strings) {
        return addToFile(new File(filename), strings);
    }

    public static boolean addToFile(File file, String... strings) {
        try {
            Scanner scan = new Scanner(file);

            List<String> lines = new ArrayList<String>();

            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }

            lines.addAll(Arrays.asList(strings));

            scan.close();

            FileWriter fw = new FileWriter(file);

            for (String s : lines) {
                fw.write(s);
            }

            fw.flush();
            fw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Adding to file " + file.getName() + " failed! Aborting shut down...");
            return false;
        }
    }
}
