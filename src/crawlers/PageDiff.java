package crawlers;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Data structure to hold a Delta and its URL
 *
 * Created by Alan on 6/18/2015.
 */
public class PageDiff {
    public String URL;
    public Delta delta;

    public PageDiff(String url, Delta delta) {
        this.URL = url;
        this.delta = delta;
    }

    /**
     * Returns the PageDiffs between two site states
     *
     * @param f1_name   First input filename
     * @param f2_name   Second input filename
     * @return          Patch diff
     */
    public static List<PageDiff> getPageDiffs(String f1_name, String f2_name) {
        List<String> f1 = fileToLines(f1_name);
        List<String> f2 = fileToLines(f2_name);

        Patch<String> patch = DiffUtils.diff(f1, f2);

        List<PageDiff> pageDiffs = new ArrayList<>();
        patch.getDeltas().forEach((Delta delta) -> {
            int pos = delta.getRevised().getPosition();

            // Find URL of delta
            for (int i = pos; i >= 0; i--) {
                if (f2.get(i).startsWith("URL: ")) {
                    pageDiffs.add(new PageDiff(f2.get(i), delta));
                    break;
                }
            }
        });

        return pageDiffs;
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

    public String toString() {
        String str = "";
        str += URL + "\n";
        str += delta.toString();

        return str;
    }
}
