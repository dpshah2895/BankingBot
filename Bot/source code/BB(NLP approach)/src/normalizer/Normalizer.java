/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import normalizer.Abbreviations;
/*
* @author Kiner B. Shah
* @author Mohit S. Shetty
* @author Darshan P. Shah
*/
public class Normalizer {
    protected String sentence;
    protected HashMap<String, Long> uni;
    protected HashMap<String, Long> bi;
    protected long total_bi;
    protected long total_uni;
    public Normalizer()  { this.sentence = null; this.parseUnigramFile(); this.parseBigramFile(); }
    public Normalizer(String a) { this.sentence = a; this.parseUnigramFile(); this.parseBigramFile(); }
    public void setString(String a) { this.sentence  = a; } 
    public String[] tokenize() {
        Abbreviations abv = new Abbreviations();
        String[] abbrv = abv.getAbbrv();
        ArrayList<String> l1 = new ArrayList(Arrays.asList(abbrv)); // list of all abbreviations
        String temp = this.sentence;
        String[] split_op = null;
        ArrayList<String> op = new ArrayList();
        try {
            // 'll->will, 've->have
            if(temp.contains("'ll")) temp = temp.replace("'ll", " will");
            if(temp.contains("'ve")) temp = temp.replace("'ve", " have");
            if(temp.contains("'m")) temp = temp.replace("'m", " am");
            if(temp.contains("n't")) temp = temp.replace("n't", " not");
            if(temp.contains("'s")) temp = temp.replace("'s", " is");
            if(temp.contains("'re")) temp = temp.replace("'re", " are");
            split_op = temp.split("[^A-Za-z0-9.?!]");
            int i, len = split_op.length, pos;
            String pat1 = "(^[A-Za-z][.])+"; // U.S.A.
            String pat2 = "(^[A-Za-z][a-z]+[.])"; // Inc., Dr.
            String pat3 = "(^[0-9]+[.][0-9]+)"; // 0.03, 1.59
            HashMap<String, String> map = abv.getAbbAndFullForm();
            Pattern p1 = Pattern.compile(pat1), p2 = Pattern.compile(pat2), p3 = Pattern.compile(pat3);
            Matcher m1, m2, m3;
            for(i = 0; i < len; i++) {
                if(!split_op[i].trim().isEmpty()) {
                    if(!l1.contains(split_op[i])) {
                        m3 = p3.matcher(split_op[i]);
                        if(!m3.find()) split_op[i] = split_op[i].replaceAll("([.?!])"," $1"); // I am a boy. -> I am a boy .
                    }
                    /*else {
                        split_op[i] = split_op[i].substring(0, split_op[i].lastIndexOf('.')); // U.S.A. -> U.S.A
                        split_op[i] = split_op[i].replaceAll("[.]", "");
                    }*/
                    else {
                        m1 = p1.matcher(split_op[i]);
                        m2 = p2.matcher(split_op[i]);
                        //System.out.println(split_op[i] + " " + map.get(split_op[i]));
                        if(m1.find()) split_op[i] = split_op[i].replaceAll("[.]", ""); // U.S.A. -> USA
                        if(m2.find()) split_op[i] = map.get(split_op[i]);
                    }
                    // System.out.println(split_op[i]);
                    if(split_op[i].contains(" ")) {
                        String punct_split[] = split_op[i].split("\\s");
                        op.add(punct_split[0]); op.add(punct_split[1]);
                    }
                    else op.add(split_op[i]);
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        Object[] ob = op.toArray();
        return Arrays.copyOf(ob, ob.length, String[].class);
    }
    public String normalize(String word, String prev) {
        String temp_word = word;
        //find hyphenated words eg. co-ordinate -> co ordinate
        String c = "([a-zA-z0-9]+)-([a-zA-Z0-9]+)"; 
        Pattern p = Pattern.compile(c);
        Matcher m = p.matcher(temp_word);
        String x = new String();
        boolean flag = false;
        while(m.find()) { flag = true; x += m.group(1) + " " + m.group(2); }
        if(flag) temp_word = x; //|([a-zA-Z]+[.]?)[\\s+]([0-9]{2}(th|st|rd|nd)?([,])?([0-9]{2,4})?)
        // Jan. -> January, Mon. -> Monday, etc.
        /*String ab[] = {"Mon.","Tue.","Wed.","Thu.","Fri.","Sat.","Sun.","Jan.","Feb.","Mar.","Apr.","Jun.","Jul.","Aug.","Sept.","Oct.","Nov.","Dec."};
        ArrayList<String> l = new ArrayList(Arrays.asList(ab));
        Abbreviations a = new Abbreviations();
        HashMap<String, String> map = a.getAbbAndFullForm();
        if(l.contains(temp_word+'.')) temp_word = map.get(temp_word+'.');*/
        // r -> are, u -> you, v -> we, b -> be, c -> see, n -> and
        if(temp_word.trim().equals("r")) temp_word = "are";
        if(temp_word.trim().equals("u")) temp_word = "you";
        if(temp_word.trim().equals("v")) temp_word = "we";
        if(temp_word.trim().equals("b")) temp_word = "be";
        if(temp_word.trim().equals("c")) temp_word = "see";
        if(temp_word.trim().equals("n")) temp_word = "and";
        try {
            //System.out.print(temp_word + " ");
            temp_word = minDistance(temp_word, prev); // find the string similar to the word (in case of typos) 
            //System.out.println(temp_word);
        }
        catch(IOException e) { e.printStackTrace(); }
        return temp_word;
    }
    private void parseBigramFile() {
        BufferedReader r;
        this.bi = new HashMap();
        this.total_bi = 0;
        try {
            InputStream in = Normalizer.class.getResourceAsStream("bigrams.txt");
            r = new BufferedReader(new InputStreamReader(in));
            String x, temp[] = new String[3];
            while((x = r.readLine()) != null) {
                temp = x.split("\\s+");
                this.bi.put(temp[0] + " " + temp[1], Long.parseLong(temp[2]));
                this.total_bi += Long.parseLong(temp[2]);
            }
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    private void parseUnigramFile() {
        BufferedReader r;
        this.uni = new HashMap();
        this.total_uni = 0;
        try {
            // r = new BufferedReader(new FileReader("C:\\Users\\user\\Documents\\NetBeansProjects\\BB\\src\\normalizer\\unigram.csv"));
            InputStream in = Normalizer.class.getResourceAsStream("unigram.csv");
            r = new BufferedReader(new InputStreamReader(in));
            String x, temp[] = new String[2];
            while((x = r.readLine()) != null) {
                temp = x.split(",");
                this.uni.put(temp[0], Long.parseLong(temp[1]));
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        // long count = 0;
        for(Map.Entry<String, Long> f : this.uni.entrySet()) {
            this.total_uni += f.getValue(); // count++;
        }
        // System.out.println(count + " " + this.total_uni);
    }
    // @param x     current string
    // @param y     string to be matched for similarity
    private int levensteinDistance(String x, String y) {
        int t,i,j,l1 = x.length(), l2 = y.length();
        if(l1 == 0) return l2;
        else if(l2 == 0) return l1;
        else {
            int[][] a = new int[l1+1][l2+1];
            for(i = 0; i <= l1; i++) a[i][0] = i;
            for(j = 0; j <= l2; j++) a[0][j] = j;
            for(i = 1; i <= l1; i++) {
                for(j = 1; j <= l2; j++) {
                    // cost of substitution = 2, compare by ignoring cases
                    // wikipedia, it's given cost of substitution as 1
                    t = (x.charAt(i-1) == y.charAt(j-1) || x.charAt(i-1)+32 == y.charAt(j-1)) ? 0 : 1; 
                    int temp1 = a[i-1][j] + 1; // cost of deletion = 1
                    int temp2 = a[i][j-1] + 1; // cost of insertion = 1
                    int temp3 = a[i-1][j-1] + t;
                    // Assign minimum cost to the cell
                    if(temp1 <= temp2 && temp1 <= temp3) a[i][j] = temp1;
                    else if(temp2 <= temp1 && temp2 <= temp3) a[i][j] = temp2;
                    else if(temp3 <= temp1 && temp3 <= temp2) a[i][j] = temp3;
                    if(i > 1 && j > 1 && x.charAt(i-2) == y.charAt(j-1) && x.charAt(i-1) == y.charAt(j-2))
                        a[i][j] = a[i][j] < a[i-2][j-2] + t ? a[i][j] : a[i-2][j-2] + t;
                }
            }
            return a[l1][l2];
        }
    }
    // @param x     current string
    private boolean isInteger(String x) {
        boolean flag = false;
        String c = "([0-9]+)";
        Pattern p = Pattern.compile(c);
        Matcher m = p.matcher(x);
        if(m.find()) flag = true;
        return flag;
    }
    // @param x     current string
    // @param p     previous string
    private String minDistance(String x, String p) throws IOException {
        String y, ans = null;
        ArrayList<String> l = new ArrayList(); // for storing the string
        ArrayList<Integer> li = new ArrayList(); // for storing the distance value
        // ArrayList<Double> lp = new ArrayList(); // for storing the log probability value
        InputStream in = Normalizer.class.getResourceAsStream("words.csv");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        int min = 100001;
        if(isInteger(x)) { ans = x; return ans; }
        if(x.matches("[.?!]")) { ans = x; return ans; }
        while((y = r.readLine()) != null) {
            y = y.toLowerCase(); 
            int val = levensteinDistance(x, y);
            // 
            if(min > val) { 
                min = val; 
                ans = y; 
                l.clear(); li.clear();
                l.add(ans); li.add(val);
            }
            else if(min == val) { 
                ans = y; 
                l.add(y); li.add(val);
            }
            if(val == 0) { 
                ans = y; 
                break; 
            }
        }
        // for(int i = 0; i < l.size(); i++) System.out.println(l.get(i) + " " + li.get(i));
        long total_unival = this.total_uni, total_bival = this.total_bi;
        int len = l.size(), pos = -1, fval = 0;
        double minp = Double.MAX_VALUE;
        // System.out.println(this.uni.size());
        for(int i = 0; i < len; i++) { // find string with minimum negative log probability
            String t = l.get(i);    // removed toUpperCase()
            if(!this.uni.containsKey(t)) continue;
            long freq = this.uni.get(t);
            double logprob = -Math.log((double) freq / total_unival);
            if(logprob < minp) {
                pos = i;
                minp = logprob;
                fval = li.get(i);
            }
            /*if(p.equals("<S>")) {
                if(!this.bi.containsKey("<S> " + t)) continue;
                double bi_p = -Math.log((double) this.bi.get("<S> " + t) / total_bival);
                if(minp > bi_p) {
                    pos = i; minp = bi_p; fval = li.get(i);
                }
            }
            else {
                if(!this.uni.containsKey(p)) continue;
                double uni_p = -Math.log((double) this.uni.get(p.toUpperCase()) / total_unival);
                if(!this.bi.containsKey(p + " " + t)) continue;
                double bi_p = -Math.log((double) this.bi.get(p + " " + t) / total_bival);
                double logprob = bi_p - uni_p;
                if(minp > logprob) {
                    pos = i; minp = logprob; fval = li.get(i);
                }
            }*/
            
        }
        if(pos != -1) { ans = l.get(pos); System.out.format("Word: %s\tLog Probability: %.5f\tDist.: %d\n", ans, minp, fval); }
        return ans;
    }
}