/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POSTagger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/*
* @author Kiner B. Shah
*/
// Algorithm used in this code is referred from the slides titled "NLP Programming Tutorial 5: Parts of Speech Tagging with Hidden Markov Models"
// by Graham Neubig, Nara Institute of Science and Technology (NAIST)
class Model {
    String type;
    String p;
    String n;
    double prob;
}
public class TagAssigner {
    private String tags[];
    private ArrayList<Model> model_list;
    // @param a     hashmap where changes are to be made
    // @param b     string b, whose value in the hashmap is to be changed
    private HashMap<String, Integer> makeChanges(HashMap<String, Integer> a, String b) {
        if(a.containsKey(b)) {
            int val = a.get(b);
            a.remove(b);
            a.put(b, val + 1);
        } 
        else a.put(b, 1);
        return a;
    }
    // @param a     hashmap where changes are to be made
    // @param b     string b, whose value in the hashmap is to be changed
    /* private static HashMap<String, Double> makeChanges2(HashMap<String, Double> a, String b) {
        if(a.containsKey(b)) {
            double val = a.get(b);
            a.remove(b);
            a.put(b, val + 1);
        } 
        else a.put(b, 1.0);
        return a;
    } */
    private void training() throws IOException {
        HashMap<String, Integer> transition;
        HashMap<String, Integer> emit;
        HashMap<String, Integer> context;
        model_list = new ArrayList();
        InputStream in = TagAssigner.class.getResourceAsStream("wiki-en-test.norm_pos");
        // InputStream in = TagAssigner.class.getResourceAsStream("05-train-input.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        // BufferedWriter w = new BufferedWriter(new FileWriter("C:\\Users\\user\\Documents\\NetBeansProjects\\BB\\src\\POSTagger\\model_file.txt"));
        // String path = file.getAbsolutePath();
        // System.out.println(path);
        String line, previous;
        transition = new HashMap();     // for transition probabilities
        emit = new HashMap();           // for emission probabilities
        context = new HashMap();        // for tags/contexts
        while((line = r.readLine()) != null) {
            previous = "<s>";       // previous tag (for starting tag there is no previous tag, so <s> is the previous tag)
            context = makeChanges(context, previous); // increment the counter for the previous tag 
            String split_line[] = line.split("\\s");
            for(String wt : split_line) {
                String wts[] = wt.split("[_]");
                String word = wts[0];
                String tag = wts[1];
                transition = makeChanges(transition, previous + " " + tag); // increment the count for transition from previous tag to current tag
                context = makeChanges(context, tag);    // increment the counter for the current tag
                emit = makeChanges(emit, tag + " " + word); // increment the counter for emission of the current tag for the word
                previous = tag;
            }
            transition = makeChanges(transition, previous + " </s>"); // increment the transition from the last tag to tag after it(</s>)
        }
        for(Map.Entry<String, Integer> kv : transition.entrySet()) {
            String key = kv.getKey();
            int val = kv.getValue();
            String pw[] = key.split("\\s");
            String prev = pw[0], word = pw[1];
            // System.out.println("T, " + key + ", " + (double) val / context.get(prev));
            // w.write("T " + key + " " + (double) val / context.get(prev) + "\n");
            Model new_model = new Model();
            new_model.type = "T";
            new_model.p = prev; new_model.n = word;
            new_model.prob = (double) val / context.get(prev);
            model_list.add(new_model);
        }
        for(Map.Entry<String, Integer> kv : emit.entrySet()) {
            String key = kv.getKey();
            int val = kv.getValue();
            String pw[] = key.split("\\s");
            String prev = pw[0], word = pw[1];
            // System.out.println("E, " + key + ", " + (double) val / context.get(prev));
            // w.write("E " + key + " " + (double) val / context.get(prev) + "\n");
            Model new_model = new Model();
            new_model.type = "E";
            new_model.p = prev; new_model.n = word;
            new_model.prob = (double) val / context.get(prev);
            model_list.add(new_model);
        }
    }
    /******************** Complete the below function
    * @param words
    * @throws java.io.IOException ***********/
    public void loadModelAndTagSentence(String[] words) throws IOException {
        /* LOAD MODEL */
        HashMap<String, Double> trans = new HashMap();
        HashMap<String, Double> emits = new HashMap();
        HashMap<String, Integer> possible_tags = new HashMap();
        // InputStream fin = TagAssigner.class.getResourceAsStream("model_file.txt");
        // BufferedReader r = new BufferedReader(new InputStreamReader(fin));
        // String line;
        // System.out.println(model_list.size());
        for(Model t : model_list) {
            // System.out.println(t.type + " " + t.p + " " + t.n + " " + t.prob);
            possible_tags.put(t.p, 1);
            if(t.type.equals("T")) trans.put("" + t.p + " " + t.n, t.prob);
            else emits.put("" + t.p + " " + t.n, t.prob);
        }
        /*for(Map.Entry<String, Double> e : emits.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }*/
        /* FORWARD STEP */
        // String words[] = sentence.split("\\s");     // change the delimiter here later
        int l = words.length, i; // System.out.println(l);
        HashMap<String, Double> best_score = new HashMap();
        HashMap<String, String> best_edges = new HashMap();
        best_score.put("0 <s>", 0.0);
        best_edges.put("0 <s>", "");
        Set<String> ks = possible_tags.keySet();
        for(i = 1; i <= l; i++) {
            for(String prev : ks) {
                for(String next : ks) {
                    // System.out.println(prev + " " + next + " " + words[i] + " " + best_score.containsKey("" + i + " " + prev) + " " + trans.containsKey("" + prev + " " + next));
                    if(best_score.containsKey("" + (i-1) + " " + prev) && trans.containsKey("" + prev + " " + next)) {
                        // System.out.println("Executed...");
                        double val;
                        if(!emits.containsKey("" + next + " " + words[i-1])) val = 0.0;
                        else val = emits.get("" + next + " " + words[i-1]);
                        double score = best_score.get("" + (i-1) + " " + prev) - Math.log(trans.get("" + prev + " " + next)) - Math.log(val);
                        // System.out.println(prev + " " + next + " " + words[i] + " " + best_score.get("" + i + " " + prev) + " " + (- Math.log(trans.get("" + prev + " " + next))) + " " + (- Math.log(val)) + " " + score + " " + val);
                        // System.out.println(prev + " " + (i) + " " + next + " " + score + " " + best_score.containsKey("" + (i) + " " + next));
                        if(!best_score.containsKey("" + (i) + " " + next)) {
                            best_score.put("" + (i) + " " + next, score);
                            best_edges.put("" + (i) + " " + next, "" + (i-1) + " " + prev);
                        }
                        else if(best_score.get("" + (i) + " " + next) > score) {
                            best_score.remove("" + (i) + " " + next);
                            best_edges.remove("" + (i) + " " + next);
                            best_score.put("" + (i) + " " + next, score);
                            best_edges.put("" + (i) + " " + next, "" + (i-1) + " " + prev);
                        }
                    }
                }
            }
        }
        // for last end-of-sentence marker </s>
        for(String prev : ks) {
            if(best_score.containsKey("" + (l) + " " + prev) && trans.containsKey("" + prev + " </s>")) {
                // System.out.println("Executed...");
                double score = best_score.get("" + (l) + " " + prev) - Math.log(trans.get("" + prev + " </s>"));
                // System.out.println(prev + " </s> " + best_score.get("" + l + " " + prev) + " " + (- Math.log(trans.get("" + prev + " </s>"))) + " " + score);
                // System.out.println(prev + " " + (l+1) + " </s> " + score);
                if(!best_score.containsKey("" + (l+1) + " </s>")) {
                    best_score.put("" + (l+1) + " </s>", score);
                    best_edges.put("" + (l+1) + " </s>", "" + (l) + " " + prev);
                }
                else if(best_score.get("" + (l+1) + " </s>") > score) {
                    best_score.remove("" + (l+1) + " </s>");
                    best_edges.remove("" + (l+1) + " </s>");
                    best_score.put("" + (l+1) + " </s>", score);
                    best_edges.put("" + (l+1) + " </s>", "" + (l) + " " + prev);
                }
            }
        }
        /*for(Map.Entry<String, Double> e: best_score.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }*/
        /*for(Map.Entry<String, String> e: best_edges.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }*/
        /* BACKWARD STEP */
        ArrayList<String> solution_tags = new ArrayList();
        // System.out.println(best_edges.containsKey("" + (l+1) + " </s>"));
        String next_edge = best_edges.get("" + (l+1) + " </s>");
        while(!next_edge.equals("0 <s>")) {
            String temp[] = next_edge.split("\\s");
            solution_tags.add(temp[1]);
            next_edge = best_edges.get(next_edge);
        }
        Collections.reverse(solution_tags);
        // System.out.println(solution_tags);
        /*for(i = 0; i < l; i++) {
            System.out.print(words[i] + "/" + solution_tags.get(i) + " ");
        }
        System.out.println();*/
    }
    public TagAssigner() {
        try {
            training();
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    // @param args      sring array to store command-line arguments
    /*public static void main(String[] args) throws IOException {
        TagAssigner ta = new TagAssigner();
        long start = System.currentTimeMillis();
        ta.loadModelAndTagSentence("a computer is a machine .".split("\\s+"));
        long end = System.currentTimeMillis();
        System.out.println("Time for loading model and tagging: " + (end - start) + " ms");
    }*/
}
