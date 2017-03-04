package Assignment3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Owner on 2017-02-25.
 */
public class Rule extends ItemSet {
    private Set<ItemNode> consequent;
    private double confidence;

    /**
     * constructor
     * <p>
     * Given a set of possible predicates and possible results, accept a table and create a rule with the
     * confidence and support rate.
     *
     * @param antecedent -   The list of strings which are set candidates
     * @param table      -   The list-of-lists which acts as our view of the table.
     */
    public Rule(List<ItemNode> antecedent, List<ItemNode> consequent, List<List<String>> table) {
        super(antecedent, table);
        consequent.sort((o1, o2) -> {
            if (o1.getHeader().equals(o2.getHeader())) {
                return o1.getHeader().compareTo(o2.getHeader());
            } else {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        this.consequent = consequent.stream().collect(Collectors.toSet());
        this.confidence = calcConfidence(table);
    }

    private double calcSupport(List<List<String>> table) {
        /** Find support of X->Y    */
        List<ItemNode> fullExpression = new ArrayList<>(this.getAntecedent().stream().collect(Collectors.toList()));
        fullExpression.addAll(consequent.stream().collect(Collectors.toList()));

        int bothOccur = 0;
        for (int row = 1; row < table.get(0).size(); row++) {
            List<ItemNode> predVals = new ArrayList<>();
            findAntecedentInSet(this.getAntecedent(), table, row, predVals);
            /** Row contains the predicate  */
            if (predVals.size() == this.getAntecedent().size()) {
                List<ItemNode> resultVals = new ArrayList<>();
                findAntecedentInSet(consequent, table, row, resultVals);
                if (resultVals.size() == consequent.size()) {
                    bothOccur++;
                }
            }
        }

        double support = (double)bothOccur/(double)(table.get(0).size()-1);
        return support / super.getSupport();
    }

    private double calcConfidence(List<List<String>> table) {
        /** Count number of times X and Y exist in table    */
        int xOccur = 0, bothOccur = 0;
        for (int row = 1; row < table.get(0).size(); row++) {
            List<ItemNode> predVals = new ArrayList<>();
            findAntecedentInSet(this.getAntecedent(), table, row, predVals);
            /** Row contains the predicate  */
            if (predVals.size() == this.getAntecedent().size()) {
                xOccur++;
                List<ItemNode> resultVals = new ArrayList<>();
                findAntecedentInSet(consequent, table, row, resultVals);
                if (resultVals.size() == consequent.size()) {
                    bothOccur++;
                }
            }
        }
        return (double) bothOccur / (double) xOccur;
    }

    public double getConfidence() {
        return confidence;
    }

    public Set<ItemNode> getConsequent() {
        return consequent;
    }

    public void setConsequent(Set<ItemNode> consequent) {
        this.consequent = consequent;
    }

    @Override
    public String toString() {
        return super.toString() + " ==>\n" +
                "\t\t[ " + consequent + " ]";
    }
}
