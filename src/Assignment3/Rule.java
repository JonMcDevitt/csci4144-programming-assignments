package Assignment3;

import java.util.ArrayList;
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
     * @param antecedent    -   The list of strings which are set candidates
     * @param table         -   The list-of-lists which acts as our view of the table.
     */
    public Rule(List<ItemNode> antecedent, List<ItemNode> consequent, List<List<String>> table) {
        super(antecedent, table);
        this.consequent = consequent.stream().collect(Collectors.toSet());
        this.confidence = calcConfidence(table);
    }

    private double calcConfidence(List<List<String>> table) {
        /** Count number of times X and Y exist in table    */
        int xOccur = 0, bothOccur=0;
        for(int row = 1; row < table.get(0).size(); row++) {
            List<ItemNode> predVals = new ArrayList<>();
            findAntecedentInSet(this.getAntecedent(), table, row, predVals);
            /** Row contains the predicate  */
            if(predVals.size() == this.getAntecedent().size()) {
                xOccur++;
                List<ItemNode> resultVals = new ArrayList<>();
                findAntecedentInSet(consequent, table, row, resultVals);
                if(resultVals.size() == consequent.size()) {
                    bothOccur++;
                }
            }
        }
        return (double)bothOccur/(double)xOccur;
    }
}
