import java.util.*;

public class naiveBase {
    private List<List<String>> lines = new ArrayList<>();



    public naiveBase(List<List<String>> lines) {
        this.lines = lines;
    }


    /**
     * foes over the features.
     * @param trainEx = a column
     * @return hashmap of categories and list(column)
     */
    public LinkedHashMap checkKFeature(List<String> trainEx) {
        LinkedHashMap categories = new LinkedHashMap<List<String>, String>();
        for (int j = 0; j < trainEx.size(); j++) {
            if (!categories.containsKey(trainEx.get(j))) {
                categories.put(trainEx.get(j), trainEx);
            }
        }

        return categories;

    }
    /**
     * check intersection of categories with yes/no.
     * @param categories
     * @param  trainExFeature
     * @param trainExSurvived
     * @param classification
     * @return array of int with number of intersections.
     */
    public List<int[]> checkIntersection(LinkedHashMap categories,List<String> trainExFeature,List<String> trainExSurvived,String[] classification) {
        Iterator it = categories.entrySet().iterator();
        List<int[]> howMany = new ArrayList<int[]>();
        while (it.hasNext()) {
            howMany.add(new int[2]);
            Map.Entry pair = (Map.Entry) it.next();
        }
        int noCount=0;
        int yesCount=0;
        int t=0;
        it = categories.entrySet().iterator();
        for (int i = 0; i <trainExFeature.size(); i++) {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(trainExFeature.get(i).equals(pair.getKey())){
                    if(trainExSurvived.get(i).equals(classification[0])){
                        noCount++;
                    }
                    else{
                        yesCount++;
                    }
                }
               howMany.get(t)[0]+=noCount;
                howMany.get(t)[1]+=yesCount;
               t++;
               noCount=0;
               yesCount=0;

            }
            t=0;
            it = categories.entrySet().iterator();

            }
            //smooth
        for(int i=0;i<howMany.size();i++){
            howMany.get(i)[0]+=1;
            howMany.get(i)[1]+=1;
        }
            return howMany;

        }


    /**
     * calculates the number of each classification in the examples file.
     * @param trainSurvived
     * @param  classification
     * @return array of 2 ints with the count of classifications.
     */
     public int[] countClassify(List<String> trainSurvived,String[] classification){
         int[] countClassify = new int[2];
         int noCount=0;
         int yesCount =0;
         for (int i = 0; i <trainSurvived.size(); i++){
             if(trainSurvived.get(i).equals(classification[0])){
                 noCount++;
             }
             else{
                 yesCount++;
             }
         }
         countClassify[0] = noCount;
         countClassify[1]  = yesCount;
         return countClassify;
     }

    /**
     * calculates the probability of classification.
     * @param trainSurvived
     * @param classification
     * @return the probabilities of classification
     */
    public float[] probClassify(List<String> trainSurvived,String[] classification){
        float[] probClassify = new float[2];
        int noCount=0;
        int yesCount =0;
        for (int i = 0; i <trainSurvived.size(); i++){
            if(trainSurvived.get(i).equals(classification[0])){
                noCount++;
            }
            else{
                yesCount++;
            }
        }
        probClassify[0] = (float)noCount/(float)trainSurvived.size();
        probClassify[1] = (float)yesCount/(float)trainSurvived.size();
return probClassify;

    }

    /**
     * calculates the conditional probability with smooth.
     * @param countClassify
     * @param howMany
     * @return the conditional probabilities.
     */
    public List<float[]> smoothprobCond(int[] countClassify ,List<int[]> howMany ){
        List<float[]> probability = new ArrayList<float[]>();
        for (int i=0;i<howMany.size();i++){
            probability.add(new float[2]);
            probability.get(i)[0]= (float)howMany.get(i)[0]/(countClassify[0]+ howMany.size());
            probability.get(i)[1]= (float)howMany.get(i)[1]/(countClassify[1]+ howMany.size());
        }
        return probability;
    }

    /**
     * returns the right classification
     * @param line
     * @param columnsCategories
     * @param Probline
     * @param probClassification
     * @param classifications
     * @return the classification
     */
    public String YesNoCond(List<String> line ,List<LinkedHashMap> columnsCategories,List<List<float[]>>Probline,float[] probClassification,String[] classifications ) {
        float[] yes = new float[columnsCategories.size()];
        float[] no = new float[columnsCategories.size()];
        for (int i = 0; i < columnsCategories.size(); i++) {
            Iterator it = columnsCategories.get(i).entrySet().iterator();
            int t = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (line.get(i).equals(pair.getKey())) {
                  yes[i] = Probline.get(i).get(t)[1];
                  no[i] = Probline.get(i).get(t)[0];
                  break;
                }

                t++;

            }
            t = 0;
            it = columnsCategories.get(i).entrySet().iterator();

        }
        float multNo=probClassification[0];
        float multYes=probClassification[1];
        for(int i=0;i<yes.length;i++){
          multNo*=no[i];
          multYes*=yes[i];
        }
        if(multYes>multNo)
        {
            return classifications[1];
        }
        else{
            return classifications[0];
        }

    }


    public List<String>  runAlgo(List<List<String>> featuresClass) {
        List<LinkedHashMap> columnsCategories = new ArrayList();
        for (int i = 0; i < featuresClass.size()-1; i++) {
            columnsCategories.add(new LinkedHashMap());
           columnsCategories.set(i,checkKFeature(featuresClass.get(i)));

        }

        String[] classifications = ClassificaationOptions(featuresClass.get(featuresClass.size()-1));
        List<List<int[]>> countIntersectionsCol = new ArrayList();
        //List<List<float[]>> probIntersectionsCol = new ArrayList();
        for(int i=0;i<featuresClass.size()-1;i++){
            List<int[]> countIntersection = checkIntersection(columnsCategories.get(i),featuresClass.get(i),featuresClass.get(featuresClass.size()-1),classifications);
           countIntersectionsCol.add(countIntersection);
        }
        float[] probClassification = probClassify(featuresClass.get(featuresClass.size()-1),classifications);
        int[] countClassifi = countClassify(featuresClass.get(featuresClass.size()-1),classifications);

        List<List<float[]>> condProbs = new ArrayList();
        for(int i=0;i<countIntersectionsCol.size();i++){
            List<float[]> cond = smoothprobCond(countClassifi,countIntersectionsCol.get(i));
            condProbs.add(cond);
        }
       List<String> yesNo = new ArrayList<>();
        for(int i=0;i<this.lines.size();i++){
            yesNo.add(YesNoCond(lines.get(i),columnsCategories,condProbs,probClassification,classifications));
        }

return yesNo;

    }


    public String[] ClassificaationOptions(List<String> survivedEx) {
        String option1 = "no";
        String option2 = "yes";
        if(survivedEx.get(0).equals("false" )|| survivedEx.get(0).equals("true")){
            option1 = "false";
            option2="true";
        }

        String[] classifications = new String[2];

        classifications[0] = option1;
        classifications[1] = option2;
        return classifications;
    }

}