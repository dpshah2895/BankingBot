package Parser;
// @author Kiner Shah
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// Algorithm used in this code is referred from the slides titled "NLP Programming Tutorial 8: Phrase Structure Parsing"
// by Graham Neubig, Nara Institute of Science and Technology (NAIST)
class Rule {
    String lhs, rhs1, rhs2;
    double lprob;
}
class Preterminal {
    String lhs;
    double lprob;
}
class Symbol {
    String sym;
    int i, j;
}
class LeftRightSym {
    Symbol lsym, rsym;
}
public class PCYKParser {
    ArrayList<Rule> nonterm;
    HashMap<String, ArrayList<Preterminal>> preterm;
    HashMap<Symbol, Double> best_score;
    HashMap<Symbol, LeftRightSym> best_edge;
    public PCYKParser() throws IOException {
        // READ GRAMMAR
        nonterm = new ArrayList();
        preterm = new HashMap();
        InputStream in = PCYKParser.class.getResourceAsStream("wiki-en-test.grammar");
        //InputStream in = PCYKParser.class.getResourceAsStream("wiki-en-test.grammar");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String temp;
        while((temp = r.readLine()) != null) {
            String t[] = temp.split("\\t");
            String rhs_sym[] = t[1].split("\\s");
            if(rhs_sym.length == 1) {   // if this is a pre-terminal
                Preterminal new_pt = new Preterminal();
                //ArrayList<Preterminal> new_al = new ArrayList();
                new_pt.lhs = t[0]; new_pt.lprob = -Math.log(Double.parseDouble(t[2]));
                //new_al.add(new_pt); 
                //System.out.println(t[0] + "->" + rhs_sym[0] + " " + new_pt.lprob);
                //preterm.put(rhs_sym[0], new_al);*/
                if(preterm.containsKey(rhs_sym[0])) {
                    ArrayList<Preterminal> val = preterm.get(rhs_sym[0]);
                    val.add(new_pt);
                    preterm.put(rhs_sym[0], val);
                }
                else {
                    ArrayList<Preterminal> new_al = new ArrayList();
                    new_al.add(new_pt);
                    preterm.put(rhs_sym[0], new_al);
                }
            }
            else {
                Rule new_r = new Rule();
                new_r.lhs = t[0]; new_r.rhs1 = rhs_sym[0]; new_r.rhs2 = rhs_sym[1];
                new_r.lprob = -Math.log(Double.parseDouble(t[2]));
                nonterm.add(new_r);
            }
        }
    }
    private boolean containsKey(Symbol s, HashMap<Symbol, Double> m) {
        boolean flag = false;
        for(Map.Entry<Symbol, Double> e : m.entrySet()) {
            Symbol s1 = e.getKey();
            if(s1.i == s.i && s1.j == s.j && s1.sym.equals(s.sym)) {
                flag = true; break;
            }
        }
        return flag;
    }
    private double get(Symbol s, HashMap<Symbol, Double> m) {
        double k = Double.NEGATIVE_INFINITY;
        for(Map.Entry<Symbol, Double> e : m.entrySet()) {
            Symbol s1 = e.getKey();
            if(s1.i == s.i && s1.j == s.j && s1.sym.equals(s.sym)) {
                k = e.getValue();
                break;
            }
        }
        return k;
    }
    public String parse(String words[]) {
        /********** ADD PRE-TERMINALS **********/
        best_score = new HashMap();
        best_edge = new HashMap();
        int i, j, k, l = words.length;
        for(i = 0; i < l; i++) {
            // for each Preterminal in ArrayList of each preterm
            for(Map.Entry<String, ArrayList<Preterminal>> e : preterm.entrySet()) {
                ArrayList<Preterminal> temp = e.getValue();
                String rhs = e.getKey();
                // System.out.print(rhs + ": ");
                for(Preterminal p : temp) {
                    // System.out.println("(" + p.lhs + "->" + rhs + ", " + p.lprob + ") ");
                    // System.out.println("(" + i + ", " + (i+1) + "): " + p.lhs + "->" + rhs);
                    if(rhs.equals(words[i]) && p.lprob >= 0.0) {
                        Symbol s = new Symbol();
                        s.i = i; s.j = i + 1; s.sym = p.lhs;
                        best_score.put(s, p.lprob);
                    }  
                }
                // System.out.println();
            }
        }
        /*for(Map.Entry<Symbol, Double> e : best_score.entrySet()) {
            System.out.println("****************************");
            Symbol s = e.getKey();
            System.out.println("Symbol(" + s.i + ", " + s.j + "): " + s.sym);
            System.out.println("Log Prob.: " + e.getValue());
        }*/
        /********** COMBINE NON-TERMINALS ********/
        for(j = 2; j <= l; j++) {
            for(i = j - 2; i >= 0; i--) {
                for(k = i+1; k <= j - 1; k++) {
                    for(Rule r : nonterm) {
                        String sym = r.lhs, lsym = r.rhs1, rsym = r.rhs2;
                        double lprob = r.lprob;
                        Symbol lsymik = new Symbol();
                        lsymik.i = i; lsymik.j = k; lsymik.sym = lsym;
                        Symbol rsymkj = new Symbol();
                        rsymkj.i = k; rsymkj.j = j; rsymkj.sym = rsym;
                        boolean f1, f2, f3;
                        f1 = containsKey(lsymik, best_score);
                        f2 = containsKey(rsymkj, best_score);
                        //System.out.println("*********************");
                        //System.out.println("LRHS Sym(" + lsymik.i + ", " + lsymik.j + "): " + lsymik.sym + " " + containsKey(lsymik, best_score));
                        //System.out.println("RRHS Sym(" + rsymkj.i + ", " + rsymkj.j + "): " + rsymkj.sym + " " + containsKey(rsymkj, best_score));
                        if(f1 && f2) {
                            // System.out.println("REACHED");
                            double lprob1 = get(lsymik, best_score);
                            double lprob2 = get(rsymkj, best_score);
                            if(lprob1 > Double.NEGATIVE_INFINITY && lprob2 > Double.NEGATIVE_INFINITY) {
                                double my_lp = lprob1 + lprob2 + lprob;
                                Symbol symij = new Symbol();
                                symij.i = i; symij.j = j; symij.sym = sym;
                                f3 = containsKey(symij, best_score);
                                if(f3) {
                                    if(my_lp > get(symij, best_score)) {
                                        best_score.put(symij, my_lp);
                                        LeftRightSym lrs = new LeftRightSym();
                                        lrs.lsym = lsymik; lrs.rsym = rsymkj;
                                        best_edge.put(symij, lrs);
                                    }
                                }
                                else {
                                    // System.out.println("REACHED");
                                    best_score.put(symij, my_lp);
                                    LeftRightSym lrs = new LeftRightSym();
                                    lrs.lsym = lsymik; lrs.rsym = rsymkj;
                                    best_edge.put(symij, lrs);
                                }
                            }
                        } 
                    }
                }
            }
        }
        // Symbol s = new Symbol();
        // s.i = 3; s.j = 4; s.sym = "NN";
        /*for(Map.Entry<Symbol, Double> e : best_score.entrySet()) {
            System.out.println("****************************");
            Symbol s1 = e.getKey();
            // if(s1.i == s.i && s1.j == s.j && s1.sym.equals(s.sym)) System.out.println(true);
            System.out.println("Symbol(" + s1.i + ", " + s1.j + "): " + s1.sym);
            System.out.println("Log Prob.: " + e.getValue());
        }*/
        String startsymbol = "S";
        for(Map.Entry<Symbol, LeftRightSym> e : best_edge.entrySet()) {
            Symbol s = e.getKey();
            //LeftRightSym ll = e.getValue();
            //System.out.println("*******************************");
            //System.out.println("Symbol(" + s.i + ", " + s.j + "): " + s.sym);
            //System.out.println("Left: (" + ll.lsym.i + ", " + ll.lsym.j + ", " + ll.lsym.sym + "), Right: (" + ll.rsym.i + ", " + ll.rsym.j + ", " + ll.rsym.sym + ")");
            if(s.i == 0 && s.j == l) {
                startsymbol = s.sym;
            }
        }
        /************** PRINT TREE *********************/
        double prob = Double.MIN_VALUE;
        /*for(Map.Entry<Symbol, Double> e : best_score.entrySet()) {
            Symbol s = e.getKey();
            Double d = e.getValue(); System.out.println(s.sym + "\t" + d);
            if(d > prob) { prob = d; startsymbol = s.sym; }
        }*/
        //System.out.println("\n" + startsymbol + "\t" + prob);
        Symbol fin = new Symbol();
        fin.sym = startsymbol; fin.i = 0; fin.j = l;
        //System.out.println(l);
        String ans = print(fin, words);
        //System.out.println(ans);
        return ans;
    }
    private boolean containsKey2(Symbol s, HashMap<Symbol, LeftRightSym> m) {
        boolean flag = false;
        for(Map.Entry<Symbol, LeftRightSym> e : m.entrySet()) {
            Symbol s1 = e.getKey();
            if(s1.i == s.i && s1.j == s.j && s1.sym.equals(s.sym)) {
                flag = true; break;
            }
        }
        return flag;
    }
    private LeftRightSym get2(Symbol s, HashMap<Symbol, LeftRightSym> m) {
        LeftRightSym ll = new LeftRightSym();
        for(Map.Entry<Symbol, LeftRightSym> e : m.entrySet()) {
            Symbol s1 = e.getKey();
            if(s1.i == s.i && s1.j == s.j && s1.sym.equals(s.sym)) {
                ll = e.getValue(); break;
            }
        }
        return ll;
    }
    private String print(Symbol sij, String[] words) {
        String ans;
        if(containsKey2(sij, best_edge)) {
            LeftRightSym ll = get2(sij, best_edge);
            ans =  "(" + sij.sym + " " + print(ll.lsym, words) + " " + print(ll.rsym, words) + ")";
        }
        else {
            ans =  "(" + sij.sym + " " + words[sij.i] + ")";
        }
        return ans;
    }
    public static void main(String[] args) throws IOException {
        PCYKParser pcyk = new PCYKParser();
        long start = System.currentTimeMillis();
        String res = pcyk.parse("a computer can not give high performance .".split("\\s"));
        System.out.println(res);
        //pcyk.parse("Other semi-supervised techniques use large quantities of untagged corpora to provide co-occurrence information that supplements the tagged corpora .".split("\\s"));
        System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
        
        
        // (ROOT_S (NP (DT a) (NN computer)) (S' (VP (MD can) (VP' (RB not) (VP (VB be) (VP (VBN expected) (VP (TO to) (VP (VB give) (NP (JJ high) (NN performance)))))))) (. .)))
        // (S (NP (DT a) (NN computer)) (S' (VP (MD can) (VP' (RB not) (VP (VB be) (VP (VBN expected) (VP (TO to) (VP (VB give) (NP (JJ high) (NN performance)))))))) (. .)))
    }
}