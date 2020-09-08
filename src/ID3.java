import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ID3 {
    private List<String> features;
    private List<List<String>> linesOfTrain;
    private List<List<String>> linesOfTest;
    private int numOfFeatures;
    private Map<String, List<String>> featuresOptions;


    public ID3(List<String> features, List<List<String>> trainDataVectors, List<List<String>> testDataVectors) {
        this.features = features;
        testDataVectors.remove(0);
        this.linesOfTest = testDataVectors;
        trainDataVectors.remove(0);
        this.linesOfTrain = trainDataVectors;
        this.numOfFeatures = features.size() - 1;
        this.featuresOptions = new LinkedHashMap<String, List<String>>();

    }

    public List<String> runAlgo(){
        List<String> attributes= new ArrayList<String>();
        for (int i = 0; i <features.size()-1 ; i++) {
            attributes.add(features.get(i));
        }
        setCategories();
        Node def=new Node(MostCommonClassification(linesOfTrain));
        ID3Tree tree=buildTree(linesOfTrain,attributes,def);
        tree.printTreeToFile();
        List<String> classify = finalClassification(tree);
        return classify;

    }

    /**
     * set the categories of the columns.
     */
    public void setCategories() {
        for (int i = 0; i < this.numOfFeatures; i++) {
            String currFeature = this.features.get(i);
            this.featuresOptions.put(currFeature, new ArrayList<String>());
            for (List<String> trainVector : this.linesOfTrain) {
                String optVal = trainVector.get(i);
                if (!this.featuresOptions.get(currFeature).contains(optVal)) {
                    this.featuresOptions.get(currFeature).add(optVal);
                }
            }
        }
    }

    /**
     * check the most common classification.
     * @param examples = the train examples
     * @return the most common classification
     */
    public String MostCommonClassification(List<List<String>> examples) {
        int yesCount = 0;
        int noCount = 0;
        int flag=0;
        for (List<String> vector : examples) {
            if (vector.get(numOfFeatures).equals("yes") || vector.get(numOfFeatures).equals("true")  ) {
                yesCount++;
                if(vector.get(numOfFeatures).equals("yes")){
                    flag=1;
                }
            } else {
                noCount++;
                if(vector.get(numOfFeatures).equals("no")){
                    flag=1;
                }
            }
        }
        String maxClassification;
        if (yesCount >= noCount) {
            if(flag==1){
                maxClassification = "yes";
            }
            else {
                maxClassification = "true";
            }

        } else {
            if(flag==1){
                maxClassification = "no";
            }
            else{
                maxClassification = "false";
            }

        }
        return maxClassification;

    }


    /**
     * build the dt3 tree.
     * @param examples = the train examples
     * @param attributes = the attributes.
     * @param def = default node
     */
    public ID3Tree buildTree(List<List<String>> examples, List<String> attributes, Node def) {
        ID3Tree tree = new ID3Tree ();
        int yesCount = 0;
        int noCount = 0;
        int flag=0;
        for (List<String> vector : examples) {
            if (vector.get(numOfFeatures).equals("yes") || vector.get(numOfFeatures).equals("true")) {
                yesCount++;
                if(vector.get(numOfFeatures).equals("yes")){
                    flag=1;
                }
            } else {
                noCount++;
                if(vector.get(numOfFeatures).equals("no")){
                    flag=1;
                }
            }
        }
        String maxClassification;
        if (yesCount >= noCount) {
            if(flag==1){
                maxClassification = "yes";
            }
            else{
                maxClassification = "true";
            }

        } else {
            if(flag==1){
                maxClassification = "no";
            }
            else{
                maxClassification = "false";
            }
        }
        if (examples.isEmpty()) {
            tree.setRoot(def);
            return tree;
        } else if (yesCount == 0) {
            if(flag==1)
            {tree.setRoot(new Node("no"));}
            else{tree.setRoot(new Node("false"));}
            return tree;
        } else if (noCount == 0) {
            if(flag==1)
            {tree.setRoot(new Node("yes"));}
            else{tree.setRoot(new Node("true"));}
            return tree;
        } else if (attributes.isEmpty()) {
            tree.setRoot(new Node(maxClassification));
            return tree;
        } else {
            String best = chooseAttribute(attributes, examples);
            tree.setRoot(new Node(best));
            for (int i = 0; i < featuresOptions.get(best).size(); i++) {
                String currVal= featuresOptions.get(best).get(i);
                List<List<String>> examplesI = new ArrayList<List<String>>();
                for (List<String> vector : examples) {
                    if (vector.get(features.indexOf(best)).equals(currVal)) {
                        examplesI.add(vector);
                    }
                }
                List<String> modifyAttributes = new ArrayList<String>();
                modifyAttributes.addAll(attributes);
                if (modifyAttributes.contains(best)) {
                    modifyAttributes.remove(best);
                }
                ID3Tree subTree = buildTree(examplesI, modifyAttributes, new Node(maxClassification));
                tree.getRoot().addChild(featuresOptions.get(best).get(i), subTree.getRoot());
            }
            return tree;
        }
    }

    /**
     * choose the attribute with the best gain.
     * @param examples = the train examples
     * @return the best attribute
     */
    public String chooseAttribute(List<String> attributes, List<List<String>> examples) {
        if (attributes.size() == 1) {
            return attributes.get(0);
        }
        double maxGain = -8.0;
        String best = null;
        int countYes = 0;
        int countNo = 0;
        for (List<String> line : examples) {
            if (line.get(numOfFeatures).equals("yes") || line.get(numOfFeatures).equals("true")  ) {
                countYes++;
            } else {
                countNo++;
            }
        }
        double entropyTotal = entropy(countYes, countNo, examples.size());
        for (String attribute : attributes) {
            double gain = entropyTotal;
            for (String value : this.featuresOptions.get(attribute)) {
                int currentExamples = 0;
                countYes = 0;
                countNo = 0;
                for (List<String> line : examples) {
                    if (line.get(features.indexOf(attribute)).equals(value)) {
                        currentExamples++;
                        if (line.get(numOfFeatures).equals("yes") || line.get(numOfFeatures).equals("true")  ) {
                            countYes++;
                        } else if (line.get(numOfFeatures).equals("no") || line.get(numOfFeatures).equals("false") ) {
                            countNo++;
                        }
                    }
                }
                double entropy = entropy(countYes, countNo, currentExamples);
                gain -= ((double) currentExamples / examples.size()) * entropy;
            }
            if (gain > maxGain) {
                maxGain = gain;
                best = attribute;
            }

        }
        return best;
    }

    /**
     * calculates the entropy.
     * @param countYes = count the "yes"/"true" classification
     * @param  countNo = count the "no"/"false" classification
     */
    public double entropy(int countYes, int countNo, int total) {
        if (countNo == 0 || countYes == 0) {
            return 0.0;
        }
        double entropy = (-1) * ((double) countYes / total) * Math.log((double) countYes / total)
                - ((double) countNo / total) * Math.log((double) countNo / total);
        return entropy;
    }


    /**
     * calculates the entropy.
     * @param dtl = the id3 tree
     * @return the list of classifications
     */
    public List<String> finalClassification(ID3Tree dtl) {
        List<String> classification = new ArrayList<>(linesOfTest.size());
        for (List<String> testLine : linesOfTest) {
            Node currNode = dtl.getRoot();
            while (!currNode.isLeaf()) {
                String attVal = testLine.get(features.indexOf(currNode.getData()));
                currNode = currNode.getChildren().get(attVal);
            }

            classification.add(currNode.getData());
        }
        return classification;
    }
}
