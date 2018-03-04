/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package normalizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kiner Shah
 */
public class SpellCorrectorDeveloper {
    HashMap<String, Long> bi, uni;
    long total_bi, total_uni;
    SpellCorrectorDeveloper() {
        this.parseUnigramFile(); 
        this.parseBigramFile();
    }
    private void parseBigramFile() {
        BufferedReader r;
        bi = new HashMap();
        long total_bi = 0;
        try {
            InputStream in = Normalizer.class.getResourceAsStream("bigrams.txt");
            r = new BufferedReader(new InputStreamReader(in));
            String x, temp[] = new String[3];
            while((x = r.readLine()) != null) {
                temp = x.split("\\s+");
                bi.put(temp[0] + " " + temp[1], Long.parseLong(temp[2]));
                total_bi += Long.parseLong(temp[2]);
            }
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    private void parseUnigramFile() {
        BufferedReader r;
        uni = new HashMap();
        total_uni = 0;
        try {
            // r = new BufferedReader(new FileReader("C:\\Users\\user\\Documents\\NetBeansProjects\\BB\\src\\normalizer\\unigram.csv"));
            InputStream in = Normalizer.class.getResourceAsStream("unigram.csv");
            r = new BufferedReader(new InputStreamReader(in));
            String x, temp[] = new String[2];
            while((x = r.readLine()) != null) {
                temp = x.split(",");
                uni.put(temp[0], Long.parseLong(temp[1]));
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        // long count = 0;
        for(Map.Entry<String, Long> f : this.uni.entrySet()) {
            total_uni += f.getValue(); // count++;
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
        String temporary, ans = null;
        ArrayList<String> l = new ArrayList(); // for storing the string
        ArrayList<Integer> li = new ArrayList(); // for storing the distance value
        // ArrayList<Double> lp = new ArrayList(); // for storing the log probability value
        InputStream in = Normalizer.class.getResourceAsStream("unigram.csv");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        int min = 100001;
        if(isInteger(x)) { ans = x; return ans; }
        if(x.matches("[.?!]")) { ans = x; return ans; }
        while((temporary = r.readLine()) != null) {
            String y[] = temporary.split("[,]"); 
            y[0] = y[0].toLowerCase();
            int val = levensteinDistance(x, y[0]);
            if(min > val) { 
                min = val; 
                ans = y[0]; 
                l.clear(); li.clear();
                l.add(ans); li.add(val);
            }
            else if(min == val) { 
                ans = y[0]; 
                l.add(y[0]); li.add(val);
            }
        }
        // System.out.println(l + ", " + li);
        // for(int i = 0; i < l.size(); i++) System.out.println(l.get(i) + " " + li.get(i));
        long total_unival = total_uni, total_bival = total_bi;
        int len = l.size(), pos = -1, fval = 0;
        double minp = Double.MAX_VALUE;
        // System.out.println(this.uni.size());
        for(int i = 0; i < len; i++) { // find string with minimum negative log probability
            String t = l.get(i).toUpperCase();    // removed toUpperCase()
            long freq;
            if(!uni.containsKey(t)) freq = 0;
            else freq = uni.get(t);
            double logprob = -Math.log((double) (freq + 1) / (total_unival + 99096));
            //System.out.println(t + " " + logprob);
            if(logprob < minp) {
                pos = i;
                minp = logprob;
                fval = li.get(i);
            }
            /*if(p.equals("<S>")) {
                long val;
                if(!this.bi.containsKey("<S> " + t)) val = 0;
                else val = this.bi.get("<S> " + t);
                double bi_p = -Math.log((double) (val + 1) / (total_bival + 286355));
                if(minp > bi_p) {
                    pos = i; minp = bi_p; fval = li.get(i);
                }
            }
            else {
                long unival, bival;
                if(!this.uni.containsKey(p)) unival = 0;
                else unival = this.uni.get(p.toUpperCase());
                double uni_p = -Math.log((double) (unival + 1) / (total_unival + 99096));
                if(!this.bi.containsKey(p + " " + t)) bival = 0;
                else bival = this.bi.get(p + " " + t);
                double bi_p = -Math.log((double) (bival + 1) / (total_bival + 286355));
                double logprob = bi_p - uni_p;
                if(minp > logprob) {
                    pos = i; minp = logprob; fval = li.get(i);
                }
            }*/
        } //System.out.println(pos);
        if(pos != -1) { ans = l.get(pos); } // <-- System.out.format("Word: %s\tLog Probability: %.5f\tDist.: %d\n", ans, minp, fval); 
        return ans;
    }
    public static void main(String[] args) throws IOException {
        // Accuracy Unigram: 49.391727%
        // Accuracy Bigram: 49.878345%
        // Accuract Bigram with Add 1 smoothing: 48.90511%
        SpellCorrectorDeveloper scd = new SpellCorrectorDeveloper();
        InputStream in = SpellCorrectorDeveloper.class.getResourceAsStream("spelltest1.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String temp; long total_count = 0, correct_count = 0;
        while((temp = r.readLine()) != null) {
            int pos = temp.indexOf(":");
            String cval = temp.substring(0, pos); //System.out.println(cval);
            String tval[] = temp.substring(pos + 1).split("\\s+");
            total_count += tval.length; String ans;
            //System.out.println("Words count: " + total_count);
            for(String p : tval) {
                if(p.trim().isEmpty()) continue;
                //System.out.print(p + " ");
                ans = scd.minDistance(p, "<S>"); 
                if(ans != null && ans.equals(cval)) correct_count++;
            }
            //System.out.println();
        }
        System.out.format("Total correct: %d Total words: %d ", correct_count, total_count);
        System.out.println("Acurracy: " + ((float) correct_count / total_count * 100));
    }
}
