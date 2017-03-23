package Assignment4;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Owner on 2017-03-10.
 */
public class DecisionTreeGenerator {
    public static void main(String[] args) throws FileNotFoundException {
        String filename;
        /** Currently want to keep this for rulesets. Refine later  */
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter the file you would like to scan from: ");
        filename = in.nextLine();

        System.out.println("Scanning files...");
        List<List<String>> table = FileReader.FileReader.readTableFile(filename);

        for(int i = 0; i < table.get(0).size(); i++) {
            for (List<String> aTable : table) {
                System.out.print(aTable.get(i) + "\t");
            }
            System.out.println();
        }

        System.out.print("Please enter a predictor column: ");
        String predictor = in.nextLine();
        int predictorColumnIndex = -1;
        boolean found = false;
        for(int col = 0; col < table.size() && predictorColumnIndex < 0; col++) {
            if(table.get(col).get(0).equals(predictor)) {
                predictorColumnIndex = col;
            }
        }
        if(predictorColumnIndex < 0) {
            System.out.println("There is no column with a header of " + predictor);
            System.exit(0);
        }

        /** Save the predictor column for reference later*/
        List<String> predictorColumn = new ArrayList<>(table.get(predictorColumnIndex));

        /** Pre-step: for each value in the target/predictor column, change values to be "+" or "-" */
        TargetPair[] targetPairs = {new TargetPair("", "-"), new TargetPair("", "+")};
        int mappedPos = 0;
        for(int i = 1; i < table.get(predictorColumnIndex).size(); i++) {
            if( mappedPos < targetPairs.length && targetPairs[mappedPos].getTableValue().isEmpty()) {
                targetPairs[mappedPos].setTableValue(table.get(predictorColumnIndex).get(i));
                if(mappedPos > 0 && targetPairs[mappedPos].getTableValue().equals(targetPairs[mappedPos-1].getTableValue())) {
                    targetPairs[mappedPos].setTableValue("");
                    mappedPos--;
                }
                mappedPos++;
            }
            for(TargetPair p : targetPairs) {
                if(table.get(predictorColumnIndex).get(i).equals(p.getTableValue())) {
                    table.get(predictorColumnIndex).set(i, p.getMappedValue());
                }
            }
        }

        /** Notice that now, all values in the predictor set have been changed to +/- values.*/
//        for(int i = 0; i < predictorColumn.size(); i++) {
//            System.out.println("Mapped values - " + predictorColumn.get(i) + "\t" + table.get(predictorColumnIndex).get(i));
//        }

        System.out.println(getDataGain(table.get(0), table.get(4)));
    }

    private static double getDataGain(List<String> target, List<String> column) {
        return getEntropy(target) + getEntropy(target, column);
    }

    private static double getEntropy(List<String> column) {
        /** Get set of unique values in column  */
        List<Pair> pairs = getPairs(column);
        double entropy = 0;
        for(Pair p : pairs) {
            entropy += (p.getOccurrences()/(double)column.size()-1)*(Math.log(p.getOccurrences()/(double)(column.size()-1))/Math.log(2));
        }
        return -entropy;
    }

    private static double getEntropy(List<String> target, List<String> column) {
        List<Pair> columnPairs = getPairs(column);
        double entropy = 0;
        for(Pair p : columnPairs) {
            entropy += getEntropy(column) * (p.getOccurrences()/(double)target.size());
        }

        return -entropy;
    }

    private static List<Pair> getPairs(List<String> column) {
        List<String> tracker = new ArrayList<>();
        List<Pair> pairs = new ArrayList<>();
        for(String s : column.subList(1, column.size())) {
            if(tracker.contains(s)) {
                for(Pair p : pairs) {
                    if(p.getValue().equals(s)) {
                        p.incrementOccurrences();
                    }
                }
            } else {
                tracker.add(s);
                pairs.add(new Pair(s));
            }
        }
        return pairs;
    }

    private static class Pair {
        private String value;
        private int occurrences;

        public Pair(String val) {
            value = val;
            occurrences = 1;
        }

        public String getValue() {
            return value;
        }

        public int getOccurrences() {
            return occurrences;
        }

        public void incrementOccurrences() {
            occurrences++;
        }
    }

    private static class TargetPair {
        private String tableValue;
        private String mappedValue;

        public TargetPair(String tableValue, String mappedValue) {
            this.tableValue = tableValue;
            this.mappedValue = mappedValue;
        }

        public String getTableValue() {
            return tableValue;
        }

        public String getMappedValue() {
            return mappedValue;
        }

        public void setTableValue(String value) {
            this.tableValue = value;
        }

    }
}
