package Assignment3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 2017-03-04.
 */
public class RuleSet {
    private List<Rule> ruleSet;

    public RuleSet() {
        ruleSet = new ArrayList<>();
    }

    public void add(Rule r) {
        ruleSet.add(r);
    }

    public List<Rule> getRuleSet() {
        return ruleSet;
    }

    @Override
    public String toString() {
        String msg = "";

        for(Rule r : ruleSet) {
            msg += r + "\n";
        }

        return msg;
    }
}
