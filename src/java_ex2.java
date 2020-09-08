import java.io.BufferedReader;
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class java_ex2 {

    public static void main(String[] args) {
        List<List<String>> featuresClass = new ArrayList<>();
        List<List<String>> features = new ArrayList<>();
        List<List<String>> linesOfTrain =  new ArrayList<>();
        List<List<String>> linesOfTest =  new ArrayList<>();
        featuresClass=ReadFile("train.txt");
        features=ReadFile("test.txt");
        linesOfTest = readLines("test.txt");
        linesOfTrain = readLines("train.txt");
        for(int i=0;i<features.size();i++){
            features.get(i).remove(0);
        }
        List<String>testClass = features.get(features.size()-1);

        //ID3 algorithm
        ID3 dtl=new ID3(linesOfTrain .get(0),linesOfTrain ,linesOfTest) ;
        List<String> DTClassification = dtl.runAlgo();


        for(int i=0;i<featuresClass.size();i++){
            featuresClass.get(i).remove(0);
        }

        //knn algorithm
       int k=5;
        KNN knn = new KNN(k);
        List<String> KNNClassification = knn.runAlgo(featuresClass,features);

        //naiveBase algorithm
        naiveBase naive = new naiveBase(linesOfTest);
        List<String> naiveClassification = naive.runAlgo(featuresClass);

        float DTaccuracy = checkAccuracy(DTClassification,testClass);
        float KNNaccuracy = checkAccuracy(KNNClassification,testClass);
        float naiveaccuracy  = checkAccuracy(naiveClassification,testClass);
        writeResult(DTClassification,KNNClassification,naiveClassification,DTaccuracy,KNNaccuracy,naiveaccuracy);
    }

    /**
     * read the given file.
     * @param fileName = the file's name
     * @return list of features and classifications/
     */
    public static List<List<String>> ReadFile(String fileName) {
        List<List<String>> columns=new ArrayList<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("Couldn't file input file");
                System.exit(1);
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = br.readLine();
            String[] tokens = line.split("\\s+");

            for(int i=0;i<tokens.length;i++) {
                columns.add(new ArrayList<>());
                columns.get(i).add(tokens[i]);
            }
            while ((line = br.readLine()) != null){
                String[] tokens2 = line.split("\\s+");
                for(int i=0;i<tokens.length;i++) {
                    columns.get(i).add(tokens2[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return columns;
    }


    /**
     * read the lines of the file
     * @param fileName = the file's name
     * @return list of the lines of the file.
     */
    public static List<List<String>> readLines(String fileName){
        List<List<String>> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("Couldn't file input file");
                System.exit(1);
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line = br.readLine();
            List<String> tokens = Arrays.asList(line.split("\\s+"));
            lines.add(tokens);

            while ((line = br.readLine()) != null){
                List<String> tokens2 = Arrays.asList(line.split("\\s+"));
                lines.add(tokens2);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    /**
     * write results to output file.
     * @param DTClassification = list of classifications of algorithm id3
     * @param KNNClassification = list of classifications of algorithm KNN
     * @param naiveClassification = list of classifications of algorithm naive Base
     * @param DTaccuracy = accuracy of algorithm DT
     * @param  KNNaccuracy = accuracy of algorithm KNN
     * @param naiveaccuracy = accuracy of algorithm naiveBase
     */
   public static void writeResult(List<String> DTClassification,List<String> KNNClassification,List<String> naiveClassification,
                                  float DTaccuracy,float KNNaccuracy,float naiveaccuracy) {
        PrintWriter writer=null;
        try {
            writer = new PrintWriter("output.txt");
            writer.println("Num"+"\t"+"DT"+"\t"+"KNN"+"\t"+"naiveBase");

            for (int i=1;i<=KNNClassification.size();i++){
                writer.println(i+"\t"+DTClassification.get(i-1)+"\t"+KNNClassification.get(i-1)+"\t"+naiveClassification.get(i-1));
            }
            writer.println("\t"+String.format("%.2f",DTaccuracy)+"\t"+String.format("%.2f",KNNaccuracy)+"\t"+String.format("%.2f",naiveaccuracy));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if(writer!=null) {
                writer.close();
            }
        }
    }

    /**
     * calculate the accuracy of the algorithm.
     * @param algoClassification = the classifications of the algorithm
     * @param testClass =  the classifications of the test
     */
    public static float checkAccuracy(List<String> algoClassification,List<String>testClass) {
        int examples = testClass.size();
        int right =0;
        float accuracy=1;
        for(int i=0;i<algoClassification.size();i++){
            if(algoClassification.get(i).equals(testClass.get(i))){
                right++;
            }
        }
        accuracy = (float)right/examples;
        return accuracy;
    }

}



