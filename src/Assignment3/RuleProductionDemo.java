package Assignment3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Owner on 2017-02-25.
 */
public class RuleProductionDemo {
    public static void main(String[] args) throws IOException {
        String filename, minSupport, minConfidence, line;
        /** Currently want to keep this for rulesets. Refine later  */
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
        /** Check that the data table is being created correctly    */
        for(int i = 0; i < table.get(0).size(); i++) {
            for(int j = 0; j < table.size(); j++) {
                System.out.print(table.get(j).get(i) + "\t");
            }
            System.out.println();
        }

        List<ItemSet> temp = new ArrayList<>(0);
        Set<ItemSet> kItemSets = temp.stream().collect(Collectors.toSet());
        Set<ItemSet> oneItemSets = findOneItemSets(table, Double.parseDouble(minSupport));
        Set<ItemSet> prev = oneItemSets;
        for(int k = 2; k <= table.size() && prev.size() > 0; k++) {
            prev = findK_ItemSets(table, prev, Double.parseDouble(minSupport));
            kItemSets.addAll(prev);
        }
//        System.out.println(kItemSets);


    }

    private static Set<ItemSet> findOneItemSets(List<List<String>> table, double minSupport) {
        List<ItemNode> preds = new ArrayList<>();
        List<ItemSet> itemSets = new ArrayList<>();
        for(int col = 0; col < table.size(); col++) {
            String colName = table.get(col).get(0);
            List<String> colVals = new ArrayList<>();
            for(int row = 1; row < table.get(col).size(); row++) {
                String val = table.get(col).get(row);
                if(!colVals.contains(val)) {
                    colVals.add(val);
                    preds.add(new ItemNode(colName, val));
                    ItemSet itemSet = new ItemSet(preds, table);
                    if(itemSet.getSupport() >= minSupport) {
                        itemSets.add(itemSet);
                    }
                }
                preds.clear();
            }
        }
        return itemSets.stream().collect(Collectors.toSet());
    }

    private static Set<ItemSet> findK_ItemSets(List<List<String>> table, Set<ItemSet> prevSet, double minSupport) {
        Object prevObj = prevSet.toArray()[0];
        ItemSet s;
        int prevSize = 0;
        try {
            s = (ItemSet)prevObj;
            prevSize = s.getAntecedent().size();
        } catch (Exception e) {
            System.err.println("Item (s) is not an ItemSet");
        }
        List<ItemSet> retList = new ArrayList<>();
        if(prevSize > 0 && prevSize < table.size()) {
            List<ItemSet> prevList = new ArrayList<>(prevSet);
            for(int i = 0; i < prevList.size(); i++) {
                List<ItemNode> newAntecedent;
                ItemSet alpha = prevList.get(i), beta;
                for(int j = i+1; j < prevList.size(); j++) {
                    /** Find new antecedent */
                    beta = prevList.get(j);
                    /** If more than one itemNode in the antecedent, then join based on set-by-set comparison */
                    if(alpha.getAntecedent().size() > 1) {
                        /** Step 1: grab the antecedents of the itemset. Gratitude to Raphael for mentioning that
                         *          the last element of two itemsets is sufficient if all of their predecessors are
                         *          equal to one another.
                         *  */
                        List<ItemNode> alphaAntecedent = alpha.getAntecedent().stream().collect(Collectors.toList());
                        List<ItemNode> betaAntecedent = beta.getAntecedent().stream().collect(Collectors.toList());
                        List<ItemNode> alphaSublist = alphaAntecedent.subList(0, alphaAntecedent.size()-2);
                        List<ItemNode> betaSublist = betaAntecedent.subList(0, betaAntecedent.size()-2);

                        /** Step 2: Check that the prefixes and final ItemNodes for the antecedents match. If they
                         *          do, then we can perform a join.
                         *  */
                        if(equals(alphaSublist, betaSublist) &&
                                alphaAntecedent.get(alphaAntecedent.size()-1).equals(betaAntecedent.get(betaAntecedent.size()-1)))
                        {
                            List<ItemNode> temp = new ArrayList<>(alphaAntecedent);
                            for(ItemNode b : betaAntecedent) {
                                if(!temp.contains(b)) {
                                    temp.add(b);
                                }
                            }
                            ItemSet newSet = new ItemSet(temp, table);

                            if(newSet.getSupport() >= minSupport) {
                                retList.add(newSet);
                            }
                        }
                    } else {
                        /** If only one member of the antecedent, then can simply join*/
                        newAntecedent = new ArrayList<>();
                        newAntecedent.add(alpha.getAntecedent().stream().collect(Collectors.toList()).get(0));
                        newAntecedent.add(beta.getAntecedent().stream().collect(Collectors.toList()).get(0));
                        ItemSet newItemSet = new ItemSet(newAntecedent, table);
                        if(newItemSet.getSupport() >= minSupport) {
                            retList.add(newItemSet);
                        }
                    }
                }
            }
        }
        return retList.stream().collect(Collectors.toSet());
    }

    private static boolean equals(List<ItemNode> alphaSublist, List<ItemNode> betaSublist) {
        for(int i = 0; i < alphaSublist.size(); i++) {
            if(!alphaSublist.get(i).equals(betaSublist.get(i))) {
                return false;
            }
        }
        return true;
    }
}
