package utils;

import difflib.DiffUtils;
import difflib.Patch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * DiffUtils helper functions
 *
 * Created by Alan on 6/15/2015.
 */
public class DiffHelper {
    /**
     * Returns the diff patch for two files.
     *
     * @param f1_name   First input filename
     * @param f2_name   Second input filename
     * @return          Patch diff
     */
    public static Patch<String> compare(String f1_name, String f2_name) {
        List<String> f1 = fileToLines(f1_name);
        List<String> f2 = fileToLines(f2_name);

        return DiffUtils.diff(f1, f2);
    }

    /**
     * Reads a file into a series of lines.
     *
     * @param   filename    Input filename
     * @return              Linked list of strings
     */
    private static List<String> fileToLines(String filename) {
        List<String> lines = new LinkedList<>();
        String line;

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Prints the line by line deltas of a patch.
     *
     * @param patch Input patch
     */
    public static void printPatch(Patch<String> patch) {
        if (patch == null) {
            System.out.println("No changes");
            return ;
        }

        patch.getDeltas().forEach(System.out::println);
    }
}
