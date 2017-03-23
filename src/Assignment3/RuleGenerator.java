package Assignment3;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static FileReader.FileReader.readTableFile;

/**
 * Created by Owner on 2017-02-25.
 */
public class RuleGenerator {
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
        List<List<String>> table = readTableFile(filename);
        /** Check that the data table is being created correctly    */
        for(int i = 0; i < table.get(0).size(); i++) {
            for (List<String> aTable : table) {
                System.out.print(aTable.get(i) + "\t");
            }
            System.out.println();
        }

        /** Generate item sets  */
        List<ItemSet> temp = new ArrayList<>(0);
        Set<ItemSet> kItemSets = temp.stream().collect(Collectors.toSet());
        Set<ItemSet> oneItemSets = findOneItemSets(table, Double.parseDouble(minSupport));
        Set<ItemSet> prev = oneItemSets;
        List<Set<ItemSet>> stagedItemSets = new ArrayList<>();
        stagedItemSets.add(oneItemSets);
        for(int k = 2; k <= table.size() && prev.size() > 0; k++) {
            prev = findK_ItemSets(table, prev, Double.parseDouble(minSupport));
            if(prev.size() > 0) {
                stagedItemSets.add(prev);
                kItemSets.addAll(prev);
            }
        }

        /** Generate rulesets   */
        RuleSet ruleSet = generateRulesets(stagedItemSets, table, Double.parseDouble(minConfidence), Double.parseDouble(minSupport));

        try(Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("Rules.txt"),
                        "utf-8"
                )
        )) {
            writer.write(ruleSet.toString());
        }
    }

    private static boolean containsNode(ItemNode n1, List<ItemNode> s) {
        for(ItemNode node : s) {
            if(node.getHeader().equals(n1.getHeader()) &&
                    node.getValue().equals(n1.getValue())) {
                return true;
            }
        }
        return false;
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

    /** generateRuleSets Algorithm
     *      Consider the above stagedItemSets list. This has the form of
     *
     *      A           B           C           D
     *        AB  AC  AD    BC  BD      CD
     *           ABC    ABD     ACD     BCD
     *                  ABCD
     *
     *      and so on and so forth for any set of size n, where each of the above rows corresponds to a set of
     *      ItemSets. Notice also that we can take advantage of a trickle-down algorithm. Thus, our algorithm
     *      for generating rulesets utilizes this as such:
     *
     *          RuleSet RS = new RuleSet();
     *          for(stagedItemList[k]) {
     *              for(stagedItemList[k][i]) {
     *                  ItemSet A = stagedItemList[k][i]
     *                  for(stagedItemList[k+1]) {
     *                      foreach(ItemSet S in stagedItemList[k+1]) {
     *                          if (S.antecedent contains A) {
     *                              consequent = all non-A members of S.antecedent
     *                              antecedent = A
     *                              Rule R = new Rule(antecedent, consequent, table)
     *
     *                              if (R.confidence higher than minimum confidence) {
     *                                  add R to RuleSet RS
     *                              }
     *                          }
     *                      }
     *                  }
     *              }
     *          }
     *
     *  @param stagedItemSets   -   List of ItemSets to scan
     *  @param table            -   Table for creating rules
     *  @param minConfidence    -   minimum confidence required to be added to the ruleset
     *
     *  @return RuleSet
     *  */
    private static RuleSet generateRulesets(List<Set<ItemSet>> stagedItemSets, List<List<String>> table, double minConfidence, double minSupport) {
        RuleSet ret = new RuleSet();

        for(int i = 0; i < stagedItemSets.size() - 1; i++) {
            for(ItemSet A : stagedItemSets.get(i)) {
                for(int j = i+1; j < stagedItemSets.size(); j++) {
                    for(ItemSet S : stagedItemSets.get(j)) {
                        if(S.containsAntecedent(A)) {
                            List<ItemNode> consequent = findConsequent(A, S);
                            Rule r = new Rule(A.getAntecedent().stream().collect(Collectors.toList()), consequent, table);
                            if(r.getConfidence() >= minConfidence && r.getSupport() >= minSupport) {
                                ret.add(r);
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    /** S is the larger item set, since it is the one from the next level down*/
    private static List<ItemNode> findConsequent(ItemSet a, ItemSet s) {
        List<ItemNode> consequent = new ArrayList<>();

        for(ItemNode node : s.getAntecedent()) {
            if(!containsNode(node, a.getAntecedent().stream().collect(Collectors.toList()))) {
                consequent.add(node);
            }
        }

        return consequent;
    }
}
