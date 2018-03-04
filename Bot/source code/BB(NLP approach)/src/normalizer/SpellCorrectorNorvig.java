/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package normalizer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
/*
* @author Kiner Shah
*/

/************************** REFERENCES ***************************/
// www.norvig.com/spell-correct.html 
/*****************************************************************/
class Two {
    String L;
    String R;
}
public class SpellCorrectorNorvig {
    String words[];
    HashMap<String, Long> uni;
    long total_uni;
    String tempString;
    private String[] getWords(String text) {
        text = text.toLowerCase(); // System.out.println(text);
        Pattern p = Pattern.compile("\\b[A-Za-z0-9_-]+\\b");
        Matcher m = p.matcher(text);
        ArrayList<String> words1 = new ArrayList();
        while(m.find()) {
            //System.out.println(m.group(0));
            words1.add(m.group(0));
        }
        //System.out.println(words1);
        Object o[] = words1.toArray();
        String ans[] = Arrays.copyOf(o, o.length, String[].class);
        return ans;
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
                this.uni.put(temp[0].toLowerCase(), Long.parseLong(temp[1]));
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        // long count = 0;
        for(Map.Entry<String, Long> f : this.uni.entrySet()) {
            this.total_uni += f.getValue(); // count++;
        }
        // System.out.println(count + " " + this.total_uni);
    }
    private double P(String word) {
        return (double) this.uni.get(word) / this.total_uni;
    }
    SpellCorrectorNorvig(String text) {
        this.words = getWords(text);
        parseUnigramFile();
        // System.out.println(this.total_uni);
        // System.out.println(this.uni.size());
        for(String k : this.words) {
            // System.out.println(k);
            String corrected = correction(k);
            System.out.println(corrected);
            tempString = corrected;
        }
    }
    private String[] known(String words[]) {
        ArrayList<String> l = new ArrayList();
        String ans[];
        for(String w : words) {
            if(this.uni.containsKey(w)) l.add(w);
        }
        Object o[] = l.toArray();
        ans = Arrays.copyOf(o, o.length, String[].class);
        // System.out.println(ans.length);
        // for(String x : ans) System.out.println("Known: " + x);
        return ans;
    }
    private String[] edit1(String word) {
        int i; String letters = "abcdefghijklmnopqrstuvwxyz";
        ArrayList<Two> splits = new ArrayList();
        for(i = 0; i < word.length(); i++) {
            Two nt = new Two();
            nt.L = word.substring(0,i);
            nt.R = word.substring(i);
            // System.out.println(nt.L + " " + nt.R);
            splits.add(nt);
        }
        ArrayList<String> deletes = new ArrayList();
        ArrayList<String> transposes = new ArrayList();
        ArrayList<String> replaces = new ArrayList();
        ArrayList<String> inserts = new ArrayList();
        for(Two t : splits) {
            if(t.R != null) { deletes.add(t.L + t.R.substring(1)); }
        }
        for(Two t : splits) {
            if(t.R.length() > 1) { transposes.add(t.L + t.R.charAt(1) + t.R.charAt(0) + t.R.substring(2)); }
        }
        for(Two t : splits) {
            if(t.R != null) { 
                for(int j = 0; j < 26; j++) {
                    replaces.add(t.L + letters.charAt(j) + t.R.substring(1));
                }
            }
        }
        for(Two t : splits) {
            for(int j = 0; j < 26; j++) {
                inserts.add(t.L + letters.charAt(j) + t.R);
            }
        }
        
        ArrayList<String> all_together = new ArrayList(deletes);
        all_together.addAll(transposes);
        all_together.addAll(replaces);
        all_together.addAll(inserts);
        // System.out.println(all_together);
        Object o[] = all_together.toArray();
        String ans[] = Arrays.copyOf(o, o.length, String[].class);
        // for(String x : ans) System.out.println("Edit 1: " + x);
        return ans;
    }
    private String[] edit2(String word) {
        String e1[] = edit1(word);
        ArrayList<String> l = new ArrayList();
        for(String t : e1) {
            String e2[] = edit1(t);
            l.addAll(Arrays.asList(e2));
        }
        Object o[] = l.toArray();
        String ans[] = Arrays.copyOf(o, o.length, String[].class);
        // for(String x : ans) System.out.println("Edit 2: " + x);
        return ans;
    }
    private String[] candidates(String word) {
        String l1[] = {word};
        // System.out.println(l1[0]);
        String a1[] = known(l1); // System.out.println("REACHED");
        // for(String x : a1) System.out.println("Known: " + x);
        if(a1.length == 0) {
            a1 = known(edit1(word)); 
            if(a1.length == 0) {
                a1 = known(edit2(word));
                if(a1.length == 0) {
                    a1 = new String[l1.length];
                    System.arraycopy(l1, 0, a1, 0, l1.length);
                }
            }
        }
        // for(String x : a1) System.out.println("Candidate: " + x);
        return a1;
    }
    private String correction(String word) {
        String ans = "";
        String candidate_words[] = candidates(word);
        // System.out.println(candidate_words.length);
        long max = -1;
        for(String t : candidate_words) {
            // System.out.println(t);
            if(!this.uni.containsKey(t)) continue;
            long val = this.uni.get(t);
            if(val > max) { max = val; ans = t; }
        }
        return ans;
    }
    public static void main(String[] args) throws Exception {
        InputStream in = SpellCorrectorNorvig.class.getResourceAsStream("spelltest1.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String temp; int total_count = 0, correct_count = 0;
        System.out.println("Executing Sample Test 1...");
        while((temp = r.readLine()) != null) {
            int pos = temp.indexOf(":");
            String cval = temp.substring(0, pos); // System.out.println(cval);
            String tval[] = temp.substring(pos + 1).split("\\s+");
            total_count += tval.length;
            for(String check : tval) {
                // System.out.println(check);
                if(check.trim().isEmpty()) continue;
                SpellCorrectorNorvig sc = new SpellCorrectorNorvig(check);
                String ss = sc.tempString; //System.out.println(cval + " " + ss);
                if(ss.equals(cval)) correct_count++;
            }
            // System.out.println();
        }
        System.out.format("Total correct: %d Total words: %d ", correct_count, total_count);
        System.out.println("Acurracy: " + ((float) correct_count / total_count * 100));
        /*long start = System.currentTimeMillis();
        SpellCorrectorNorvig sc = new SpellCorrectorNorvig("I wann akmow more about this policy which you csn benefit me a lot");
        long end = System.currentTimeMillis();
        System.out.println("Time for Spelling Correction: " + (end - start) + " ms");*/
        // System.out.println("Correctness for Sample Test 1: " + ((double) correct_count / total_count));
    }
}
