package Assignment3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Owner on 2017-02-25.
 */
public class ItemSet {
    private Set<ItemNode> antecedent;
    private double support;

    /**
     *  constructor
     *
     *  Given a antecedent of possible predicates and possible results, accept a table and create a rule with the
     *  confidence and support rate.
     *
     *  @param  antecedent     -   The list of strings which are antecedent candidates
     *  @param  table   -   The list-of-lists which acts as our view of the table.
     *  */
    public ItemSet(List<ItemNode> antecedent, List<List<String>> table) {
        this.antecedent = antecedent.stream().collect(Collectors.toSet());
        this.support = calcSupport(table);
    }

    /**
     *  function calcSupport()
     *
     *  Given a table, find the support rate for our rule. We calculate the support rate with the following function:
     *
     *      Support(X->Y)   =   (occurrences of X and Y)
     *                          ------------------------
     *                                 (Row count)
     *
     *  @param  table   -   The table to scan to report the support value
     *
     *  @return double  -   The support rate for this particular rule
     *  */
    private double calcSupport(List<List<String>> table) {
        /** Count number of times X and Y exist in table    */
        int bothOccur = 0;
        for(int row = 1; row < table.get(0).size(); row++) {
            List<ItemNode> setVals = new ArrayList<>();
            findCellInSet(antecedent, table, row, setVals);
            /** Row contains the antecedent  */
            if(setVals.size() == antecedent.size()) {
                bothOccur++;
            }
        }
        /** Return (XY)/(rows-excluding-header) */
        return (double)bothOccur/(double)(table.get(0).size()-1);
    }


    /**
     *  function findCellInSet()
     *
     *  Given a antecedent and table, scan a row and add values into the list if the hashcodes match.
     *
     *  @param  set     -   The antecedent that we are scanning. This can be either the antecedent antecedent or the result antecedent.
     *  @param  table   -   The table that we are scanning. This is the data table antecedent up in a list-of-lists format.
     *  @param  row     -   The row of the table that we are currently inspecting.
     *  @param  list    -   The list that we are adding a cell to.
     *
     *  @return void    -   Since we are modifying the above list, we do not need to return.
     *  */
    protected void findCellInSet(Set<ItemNode> set, List<List<String>> table, int row, List<ItemNode> list) {
        for(int col = 0; col < table.size(); col++) {
            String colName = table.get(col).get(0), cell = table.get(col).get(row);
            for(ItemNode n : set) {
                if(n.getValue().equals(cell)) {
                    list.add(new ItemNode(colName, cell));
                }
            }
        }
    }

    public Set<ItemNode> getAntecedent() {
        return antecedent;
    }


    public double getSupport() {
        return support;
    }

    @Override
    public String toString() {
        return "[" + antecedent + "]";
    }
}
