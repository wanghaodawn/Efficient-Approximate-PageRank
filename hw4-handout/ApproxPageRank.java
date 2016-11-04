/**
 * 10605 HW4 - Efficient Approximate PageRank
 * 
 * Compute page ranks and subsample a graph of local community given a seed.
 *
 * Input
 *   argv[0]: path to adj graph. Each line has the following format
 *            <src>\t<dst1>\t<dst2>...
 *   argv[1]: seed node
 *   argv[2]: alpha
 *   argv[3]: epsilon
 *
 * Output
 *   Print to stdout. Lines have the following format
 *     <v1>\t<pagerank1>\n
 *     <vr>\t<pagerank2>\n
 *     ...
 *   Order does NOT matter.
 *   
 *   @author Hao Wang (haow2)
 */

import java.io.*;
import java.util.*;

public class ApproxPageRank {
    /**
     * Instance Variables
     */
    private String inputPath;
    private String seed;
    private double alpha;
    private double oneMinusAlpha;
    private double epsilon;
    private Map<String, Double> prMap;
    private Map<String, Double> rMap;
    private Map<String, String> neighboursMap;
    private final String SPLITTER = "\t";
    private final String NEW_LINE = "\n";

    /**
     * Constructor
     */
    public ApproxPageRank() {
        // ApproxPageRank("", "", 0.0, 0.0);
    }

    /**
     * Constructor
     */
    public ApproxPageRank(String inputPath, String seed, double alpha, double epsilon) {
        this.inputPath = inputPath;
        this.seed = seed;
        this.alpha = alpha;
        this.oneMinusAlpha = 1.0 - alpha;
        this.epsilon = epsilon;

        prMap = new HashMap<String, Double>();
        rMap = new HashMap<String, Double>();
        neighboursMap = new HashMap<String, String>();

        prMap.put(seed, 0.0);
        rMap.put(seed, 1.0);
    }

    /**
     * Train
     */
    public void trainPR() throws IOException {

        // long time = System.currentTimeMillis();

        // Read test data
        File inFile = new File(inputPath);
        
        // If file doesnt exists, then exit
        // if (!inFile.exists()) {
        //     System.err.println("No file called: " + inputPath);
        //     System.exit(-1);
        // }

        // Read string from the input file
        BufferedReader br = null;
        String currLine;

        boolean needContinue = true;

        while (needContinue) {
            br = new BufferedReader(new FileReader(inFile));
            needContinue = false;
            while ((currLine = br.readLine()) != null) {
                String[] ss = currLine.split(SPLITTER);
                // if (ss.length == 0) {
                //     continue;
                // }
                
                // If not in rMap, then we are not interested in this node
                String key = ss[0];
                // if (key.length() == 0) {
                //     continue;
                // }

                if (!rMap.containsKey(key)) {
                    continue;
                }

                // Parse the line
                // List<String> tempList = new ArrayList<String>();
                // for (int i = 1; i < ss.length; i++) {
                //     String word = ss[i].trim();
                //     // if (word.length() == 0) {
                //     //     continue;
                //     // }
                //     tempList.add(word);
                // }

                // Compute
                // Get r from map
                double r = rMap.get(key);
                double d = ss.length - 1;
                double rate = r / d;

                // System.out.println(key + "\t" + tempList.size());
                // System.out.println(key + "\trate\t" + rate);
                // System.out.println(key + "\tr\t" + rMap.get(key));
                // System.out.println(key + "\tpr\t" + prMap.get(key));

                if (rate <= epsilon) {
                    continue;
                }

                // If rate is no smaller than epsilon, then update needContinue
                needContinue = true;

                // Get pr from map
                double pr = 0.0;
                if (prMap.containsKey(key)) {
                    pr = prMap.get(key);
                }

                // PUSH
                // Update r and pr
                prMap.put(key, pr + alpha * r);

                double newR = oneMinusAlpha * r / 2.0;
                rMap.put(key, newR);
                
                newR = newR / d;

                // Update neighbours
                int firstSplitter = currLine.indexOf(SPLITTER);
                neighboursMap.put(key, currLine.substring(firstSplitter));

                // Update r for neighbours
                for (int i = 1; i < ss.length; i++) {
                    String nodeKey = ss[i];
                    double nodeR = 0.0;
                    if (rMap.containsKey(nodeKey)) {
                        nodeR = rMap.get(nodeKey);
                    }
                    rMap.put(nodeKey, nodeR + newR);
                    // System.out.println(nodeKey + "\tnodeKey\t" + rMap.get(nodeKey));
                }
                // System.out.println(key + "\tr\t" + rMap.get(key));
                // System.out.println(key + "\tpr\t" + prMap.get(key));
            }
        }
        // System.out.println("Time\t" + (System.currentTimeMillis() - time));

        br.close();

        // System.out.println("r");
        // for (String key: rMap.keySet()) {
        //     System.out.println(key + "\t" + rMap.get(key));
        // }

        // System.out.println("pr");
        // for (String key: prMap.keySet()) {
        //     System.out.println(key + "\t" + prMap.get(key));
        // }
    }

    /**
     * Output SubGraph
     */
    public void printSubGraph() throws IOException {
        // Init graph parameters
        // long time = System.currentTimeMillis();
        String[] tempS = neighboursMap.get(seed).split(SPLITTER);
        double volume = tempS.length;
        // At the begining, let conductance to be the biggest number, that is 1 here
        double boundary = volume;
        double conductance = 1.0;

        // Init set S
        Set<String> S = new HashSet<String>();
        S.add(seed);

        Item[] itemsArray = new Item[prMap.size()];

        // Add all nodes in prMap
        int i = 0;
        for (String key : prMap.keySet()) {
            Item item = new Item(key, prMap.get(key));
            itemsArray[i++] = item;
        }
        // System.out.println("Time\t" + (System.currentTimeMillis() - time));

        // Sort nodes according to pagerank score, DESC
        // long time = System.currentTimeMillis();
        // Arrays.sort(itemsArray, new Comparator<Item>() {
        //     @Override
        //     public int compare(Item item1, Item item2) {
        //         double pr1 = item1.getPR();
        //         double pr2 = item2.getPR();
        //         if (pr1 != pr2) {
        //             return (int) (pr2 - pr1);
        //         }
        //         // If equal, use key to compare
        //         return item1.getKey().compareTo(item2.getKey());
        //     }
        // });
        // System.out.println("Time\t" + (System.currentTimeMillis() - time));

        // long time = System.currentTimeMillis();
        for (i = 0; i < itemsArray.length; i++) {
            // Get the current item
            Item item = itemsArray[i];
            String key = item.getKey();
            double pr = item.getPR();

            // Skip seed
            if (key.equals(seed)) {
                continue;
            }
            S.add(key);

            // Update volume and boundary
            String[] tempNeighbor = neighboursMap.get(key).split(SPLITTER);
            volume += tempNeighbor.length;

            boundary = 0.0;
            for (int j = 0; j < tempNeighbor.length; j++) {
                if (S.contains(tempNeighbor[j])) {
                    continue;
                } else {
                    boundary++;
                }
            }

            // If get a smaller conductance, then update the conductance
            if (boundary / volume < conductance) {
                conductance = boundary / volume;
            }
        }
        // System.out.println("Time\t" + (System.currentTimeMillis() - time));

        // Output
        // long time = System.currentTimeMillis();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < itemsArray.length; i++) {
            Item item = itemsArray[i];
            String key = item.getKey();
            double pr = item.getPR();
            sb.append(key);
            sb.append(SPLITTER);
            sb.append(String.valueOf(pr));
            sb.append(NEW_LINE);
        }
        bw.append(sb.toString());
        // bw.append("Time\t" + (System.currentTimeMillis() - time));
        bw.close();
    }

    /**
     * Main Function
     */
    public static void main(String[] args) throws IOException {
        // TODO: start your code here
        // Get input arguments
        if (args == null || args.length != 4) {
            throw new IllegalArgumentException("Illegal Input Arguments: Incorrect Length");
        }

        String inputPath = args[0];
        String seed = args[1];
        double alpha = 0.0;
        double epsilon = 0.0;
        try {
            alpha = Double.parseDouble(args[2]);
            epsilon = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal Input Arguments: alpha or epsilon is not numbers");
        }

        ApproxPageRank arp = new ApproxPageRank(inputPath, seed, alpha, epsilon);
        arp.trainPR();
        arp.printSubGraph();
    }
}