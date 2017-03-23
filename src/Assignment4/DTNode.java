package Assignment4;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Owner on 2017-03-12.
 */
public class DTNode {
    private List<DTEdge> children;
    private DTEdge parentEdge;
    private DTNode parentNode;
    private List<String> columnPath;
    private String columnName;
    private List<String> optionsList;
    private List<List<Integer>> attributeIndices;
    private String value;

    /**
     *  Assumption: The table we pass into this constructor is modified so that the predictor column has a +/- String
     *  associated with it
     *
     *  @param table        -   The table we are inspecting.
     *  @param parentEdge   -   The row indexes of the parent edge
     * */
    public DTNode(List<List<String>> table, DTEdge parentEdge, String columnName, String predictor, String value,
                  List<String> predictorColumn, Set<Integer> predictorSet) {
        if(value != null) {
            this.value = value;
        } else {
            value = "";
        }

        if(!value.isEmpty()) {
            children = null;
            this.parentEdge = parentEdge;
            this.parentNode = parentEdge.getParent();
            this.columnName = predictor;
            this.optionsList = null;
            this.attributeIndices = null;
        } else {
            /** We are not a root   */
            columnPath = new ArrayList<>();
            if(parentEdge != null) {
                for(String s : parentEdge.getParent().getColumnPath()) {
                    columnPath.add(s);
                }
                this.parentEdge = parentEdge;
            }
            optionsList = new ArrayList<>();
            int columnIndex = 0;
            for(int i = 0; i < table.size(); i++) {
                String col = table.get(i).get(0);
                if(col.equals(columnName)) {
                    columnIndex = i;
                }
            }
            /** Column doesn't exist    */
            if(columnIndex < 0) {
                System.out.println("Column " + columnName + " does not exist.");
                System.exit(0);
            }

            /** Check what possible values there are to filter on   */
            for(int row = 1; row < table.get(columnIndex).size(); row++) {
                String option = table.get(columnIndex).get(row);
                if(!optionsList.contains(option)) {
                    optionsList.add(option);
                }
            }

            /** Find the indexes in the table that correspond to each option    */
            attributeIndices = new ArrayList<>();
            for(String option : optionsList) {
                List<Integer> indices = new ArrayList<>();
                for(int row = 1; row < table.get(columnIndex).size(); row++) {
                    if(table.get(columnIndex).get(row).equals(option)) {
                        indices.add(row);
                    }
                }
                attributeIndices.add(indices);
            }

            /** Construct child edges   */
            children = new ArrayList<>();
            for(int i = 0; i < optionsList.size(); i++) {
                children.add(new DTEdge(this, optionsList.get(i), attributeIndices.get(i)));
            }

            /** Construct child nodes   */

            /** For each option, we evaluate the entropy of all other nodes and construct our tree around them.
             * */
            for(int i = 0; i < children.size(); i++) {
                children.get(i).evaluateChildren(table, predictor, predictorColumn, predictorSet);
            }
        }
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public List<String> getColumnPath() {
        List<String> workingColumnPath = new ArrayList<>(columnPath);
        workingColumnPath.add(columnName);
        return workingColumnPath;
    }

    private class DTEdge {
        private DTNode parent, child;
        private String attribute;
        private List<Integer> indices;

        public DTEdge(DTNode parent, String attribute, List<Integer> indices) {
            this.parent = parent;
            this.child = null;
            this.attribute = attribute;
            this.indices = indices;
        }

        public DTNode getParent() {
            return parent;
        }

        public DTNode getChild() {
            return child;
        }

        public String getAttribute() {
            return attribute;
        }

        public List<Integer> getIndices() {
            return indices;
        }

        void evaluateChildren(List<List<String>> table, String predictor, List<String> predictorColumn, Set<Integer> predictorSet) {
            /** Pre-step:   Check that we cannot use a leaf node    */
            List<String> options = new ArrayList<>();
            for(int i = 0; i < indices.size(); i++) {
                String val = predictorColumn.get(indices.get(i));
                if(!options.contains(val)) {
                    options.add(val);
                }
            }

            if(options.size() == 1) {
                setLeaf(options, predictorSet, predictorColumn, table, predictor);
                return;
            } else if(options.size() < 1) {
                System.err.println("Error, invalid column.");
                return;
            }

            /** Step 1: Get list of columns which we cannot use */
            List<String> previousColumns = parent.getColumnPath();
            List<Integer> prevColIndices = new ArrayList<>();

            /** For each column, if it is not already in our path in the tree and is not the predictor, then we consider
             *  it a candidate for entropy
             *  */
            for(int i = 0; i < table.size(); i++) {
                if(!table.get(i).get(0).equals(predictor) && !previousColumns.contains(table.get(i).get(0))) {
                    prevColIndices.add(i);
                }
            }


            /** Step 2: evaluate the entropy of each */
            double[] entropies = getEntropy(prevColIndices, predictorColumn, table);
        }

        /** TODO:   Come back to this   */
        private double[] getEntropy(List<Integer> prevColIndices, List<String> predictorColumn, List<List<String>> table) {
            double[] entropies = new double[prevColIndices.size()];
            List<String> currColumn;
            for(int i = 0; i < prevColIndices.size(); i++) {
                /** Get a column from the table that is not the target or one of the previous columns   */
                currColumn = table.get(prevColIndices.get(i));
                double entropy = getEntropy(predictorColumn, currColumn);
            }

            return entropies;
        }

        private double getEntropy(List<String> predictorColumn, List<String> currColumn) {
            List<Integer[]> entropySet = new ArrayList<>();
            for(Integer[] element : entropySet) {
                element = new Integer[2];
            }

            return 0;
        }

        private void setLeaf(List<String> values, Set<Integer> predictorSet, List<String> predictorColumn, List<List<String>> table, String predictor) {
            if(values.size() > 1) {
                System.err.println("Too many characters in values list.");
            } else {
                this.child = new DTNode(table, this, predictor, predictor, values.get(0), predictorColumn, predictorSet);
            }
        }
    }
}
