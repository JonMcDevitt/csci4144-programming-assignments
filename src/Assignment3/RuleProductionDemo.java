package Assignment3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Owner on 2017-02-25.
 */
public class RuleProductionDemo {
    public static void main(String[] args) throws IOException {
        String filename, minSupport, minConfidence, line;
        /** Currently want to keep this for rulesets. Refine later  */
        String[] headers;
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter the file you would like to scan from: ");
        filename = in.nextLine();
        System.out.print("Please enter the minimum support required for your rules: ");
        minSupport = in.nextLine();
        System.out.print("Please enter the minimum confidence required for your rules: ");
        minConfidence = in.nextLine();

        System.out.println("Scanning files...");
        List<List<String>> table = new ArrayList<>();
        in = new Scanner(new File(filename));
        int lineNum = -1;

        /** Process file. Create facsimile for table as a set of ArrayLists*/
        while(in.hasNext()) {
            lineNum++;
            line = in.nextLine();
            String[] tokens = line.split("\\s+|\\t+");

            if (lineNum == 0) {
                /** Header line. Add headers to the header list. This will define the columns of the table. */
                headers = new String[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    headers[i] = tokens[i];
                    table.add(new ArrayList<>());
                    table.get(i).add(headers[i]);
                }
            } else {
                /** Data row. Add values to each column.    */
                for (int i = 0; i < table.size(); i++) {
                    table.get(i).add(tokens[i]);
                }
            }
        }
        /** Check that the data table is being created correctly    */
        for(int i = 0; i < table.get(0).size(); i++) {
            for(int j = 0; j < table.size(); j++) {
                System.out.print(table.get(j).get(i) + "\t");
            }
            System.out.println();
        }

        Set<ItemSet> oneItemSets = findOneItemSets(table, Double.parseDouble(minSupport));
    }

    private static Set<ItemSet> findOneItemSets(List<List<String>> table, double minSupport) {
        List<ItemNode> preds = new ArrayList<>();
        List<ItemSet> itemSets = new ArrayList<>();
        for(int col = 0; col < table.size(); col++) {
            String colName = table.get(col).get(0);
            List<String> colVals = new ArrayList<>();
            for(int row = 0; row < table.get(col).size(); row++) {
                String val = table.get(col).get(row);
                if(!colVals.contains(val)) {
                    colVals.add(val);
                    preds.add(new ItemNode(colName, val));
                    ItemSet itemSet = new ItemSet(preds, table);
                    if(itemSet.getSupport() >= minSupport) {
                        itemSets.add(itemSet);
                    }
                }
            }
        }
        return itemSets.stream().collect(Collectors.toSet());
    }

    private static Set<ItemSet> findNumItemSets(int k, Set<ItemSet> freqSet, List<List<String>> table) {
        if(k > table.size()) {
            return freqSet;
        }
        List<ItemSet> vals = new ArrayList<>();
        vals.addAll(freqSet);
        for(ItemSet toAdd : freqSet) {
            List<ItemSet> add = new ArrayList<>();
            for(ItemSet set : freqSet) {
                /** If our current set does not have this header in it already, add to the set  */
                boolean hasHeader;
                for(ItemNode node : toAdd.getAntecedent()) {

                }
            }
        }
    }

    private static boolean checkIfHeaderRepeats(ItemSet set1, ItemSet set2) {
        boolean repeats = false;
        List<String> headers = new ArrayList<>();
        List<ItemNode> set = new ArrayList<>();
        for(ItemNode n1 : set1.getAntecedent()) {
            for(ItemNode n2 : set2.getAntecedent()){
                if(!n1.getHeader().equals(n2.getHeader()) && !headers.contains(n1.getHeader())) {
                    headers.add(n1.getHeader());
                    set.add(new ItemNode(n1.getHeader(), n1.getValue()));
                }
            }
        }
    }
}
