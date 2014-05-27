package tterrag.tppibot.saveutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SaveUtils
{
    public static String readTextFile(String filename)
    {
        try
        {
            File file = new File(filename);

            Scanner scan = new Scanner(file);

            String s = "";

            while (scan.hasNextLine())
            {
                s += scan.nextLine() + "\n";
            }

            scan.close();
            return s;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Reading of file " + filename + " failed! Returning empty string...");
            return "";
        }
    }

    public static boolean saveAllToFile(String filename, String... strings)
    {
        try
        {
            File file = new File(filename);
            FileWriter fw = new FileWriter(file);

            for (String s : strings)
            {
                fw.write(s);
            }

            fw.flush();
            fw.close();

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Saving to file " + filename + " failed! Aborting shut down...");
            return false;
        }
    }
    
    public static boolean addToFile(String filename, String... strings)
    {
        try
        {
            File file = new File(filename);
            Scanner scan = new Scanner(file);
            
            List<String> lines = new ArrayList<String>();
            
            while (scan.hasNextLine())
            {
                lines.add(scan.nextLine());
            }
            
            for (String s : strings)
            {
                lines.add(s);
            }
            
            scan.close();
            
            FileWriter fw = new FileWriter(file);
            
            for (String s : lines)
            {
                fw.write(s);
            }
            
            fw.flush();
            fw.close();
            
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Adding to file " + filename + " failed! Aborting shut down...");
            return false;
        }
    }
}
