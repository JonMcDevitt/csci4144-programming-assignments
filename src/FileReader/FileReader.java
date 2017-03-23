package FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Owner on 2017-03-10.
 */
public class FileReader {
    public static List<List<String>> readTableFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename));
        String line;
        List<List<String>> table = new ArrayList<>();
        int lineNum = -1;
        while(in.hasNext()) {
            lineNum++;
            line = in.nextLine();
            String[] tokens = line.split("\\s+|\\t+");

            if (lineNum == 0) {
                /** Header line. Add headers to the header list. This will define the columns of the table. */
                for (int i = 0; i < tokens.length; i++) {
                    table.add(new ArrayList<>());
                    table.get(i).add(tokens[i]);
                }
            } else {
                /** Data row. Add values to each column.    */
                for (int i = 0; i < table.size(); i++) {
                    table.get(i).add(tokens[i]);
                }
            }
        }
        in.close();
        return table;
    }
}
