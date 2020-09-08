

import java.util.ArrayList;
import java.util.List;

public class KNN {
    private int k;
    private ArrayList<double[][]> list = new ArrayList<double[][]>();



    public KNN(int k) {
        this.k = k;
    }

    /**
     * calculates the distance between strings.
     * @param str1=first string
     * @param str2 = second string
     */
    public double hammingDist(String str1, String str2) {
        double count;
        if(str1.equals(str2)){
            count=0;
        }
        else{
            count=1;
        }
        return count;
    }


    /**
     * matrix of distances.
     * @param trainEx = the train list
     * @param  sol = the test list
     */
    public double[][] distanceMatrix(List<String> trainEx, List<String> sol) {
        double[][] distance = new double[sol.size()][trainEx.size()];
        for (int i = 0; i < sol.size(); i++) {
            for (int j = 0; j < trainEx.size(); j++) {
                distance[i][j] = hammingDist(sol.get(i), trainEx.get(j));
            }
        }
        return distance;
    }

    /**
     * calculates the sum of distances.
     * @param list = each ceil contains a distance between example and test
     * @param sol = number of test lines
     * @param ex = number of train lines.
     */
    public int[][] sumDistance(ArrayList<double[][]> list, int sol, int ex) {
        int[][] d = new int[sol][ex];
        for (int i = 0; i < sol; i++) {
            for (int j = 0; j < ex; j++) {
                d[i][j]=0;
            }
        }
        for (int i = 0; i < sol; i++) {
            for (int j = 0; j < ex; j++) {
                for (double[][]matrix:list) {

                    d[i][j] += matrix[i][j];
                }

            }
        }
        return d;
    }


    public List<String>  runAlgo(List<List<String>> featuresClass ,  List<List<String>> features) {
        int sol = features.get(0).size();
        int ex = featuresClass.get(0).size();
        for(int i=0;i<features.size()-1;i++){
            list.add(new double[sol][ex]);
            list.set(i,distanceMatrix(featuresClass.get(i), features.get(i)));
        }


        int[][] finalMatrix;
        finalMatrix = sumDistance(list, sol, ex);
        List<List<Integer>> BestSol = new ArrayList<>();
        List<String> finalClass = new ArrayList<>();
        for (int i = 0; i < sol; i++) {
            BestSol.add(new ArrayList<>());
            BestSol.set(i,topK(finalMatrix[i]));
            String classification = bestClassification(BestSol.get(i),featuresClass.get(featuresClass.size()-1));
            finalClass.add(classification);

        }
        return finalClass;
    }

    /**
     * choose top K examples.
     * @param matrix = matrix of distances
     * @return list of most resemble examples.
     */
    public List<Integer> topK(int[] matrix) {
        List<Integer> topK = new ArrayList<Integer>();
        int bestEx = 0;
        for(int t=0;t<this.k;t++) {
            int min = matrix[0];
            int i;
            for (i = 0; i < matrix.length; i++) {
                if (matrix[i] < min) {
                    min = matrix[i];
                    bestEx=i;
                }
            }
            topK.add(bestEx);
            matrix[bestEx] = 100;
        }
return topK;

    }

    /**
     * calculates the best classification.
     * @param sol
     * @param survivedEx
     */
    public String bestClassification(List<Integer>sol,List<String> survivedEx){
        String option1 = "no";
        String option2 = "yes";
        if(survivedEx.get(0).equals("false" )|| survivedEx.get(0).equals("true")){
            option1 = "false";
            option2="true";
        }
        int noCount=0;
        int yesCount=0;
        for(int j=0;j<this.k;j++){

            if(survivedEx.get(sol.get(j)).equals(option1)){
                noCount++;
            }
            else {
                yesCount++;
            }
        }
        if (noCount>yesCount){
            return option1;
        }
        else{
            return option2;
        }
    }
}

