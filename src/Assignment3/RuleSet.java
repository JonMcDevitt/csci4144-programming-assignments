package Assignment3;

import java.text.DecimalFormat;
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
        String msg = "Rules:\n\n";
        int i = 1;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        for(Rule r : ruleSet) {
            msg += "Rule #" + i + " ( Support: " + df.format(r.getRuleSupport()) + ", Confidence: " + df.format(r.getConfidence()) + ")\n";
            msg += r + "\n";
            i++;
        }

        return msg;
    }
}
