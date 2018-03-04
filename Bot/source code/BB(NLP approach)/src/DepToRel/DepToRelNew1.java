/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepToRel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kiner Shah
 */
class WordNumber {
    String word;
    int number;
}
class Relations {
    String relation, first, second;
}
public class DepToRelNew1 {
    HashMap<WordNumber, WordNumber> compoundRelations;
    public DepToRelNew1() {
        this.compoundRelations = new HashMap();
    }
    private WordNumber[] processDependencies(String dep) {
        WordNumber temp[] = new WordNumber[3];
        temp[0] = new WordNumber();
        temp[1] = new WordNumber();
        temp[2] = new WordNumber();
        // temp[0] = RELATION, temp[1] = arg[1], temp[2] = arg[2]
        int pos1 = dep.indexOf("(");
        int pos2 = dep.indexOf(",");
        int pos3 = dep.indexOf(")");
        temp[0].word = dep.substring(0, pos1); //temp[1] = dep.substring(pos1+1, pos2); temp[2] = dep.substring(pos2+2, pos3);
        String temp2[] = dep.substring(pos1 + 1, pos2).split("[-]");
        if(temp2.length == 2) {
            temp[1].word = temp2[0];
            temp[1].number = Integer.parseInt(temp2[1]);
        }
        else if(temp2.length == 3) {
            temp[1].word = temp2[0] + "-" + temp2[1];
            temp[1].number = Integer.parseInt(temp2[2]);
        }
        temp2 = dep.substring(pos2 + 2, pos3).split("[-]");
        if(temp2.length == 2) {
            temp[2].word = temp2[0];
            temp[2].number = Integer.parseInt(temp2[1]);
        }
        else if(temp2.length == 3) {
            temp[2].word = temp2[0] + "-" + temp2[1];
            temp[2].number = Integer.parseInt(temp2[2]);
        }
        return temp;
    }
    private boolean containsKey(WordNumber source) {
        boolean flag = false;
        for(Map.Entry<WordNumber, WordNumber> e : compoundRelations.entrySet()) {
            WordNumber wn = e.getKey();
            if(source.word.equals(wn.word) && source.number == wn.number) {
                flag = true; break;
            } 
        }
        return flag;
    }
    private WordNumber get(WordNumber source) {
        WordNumber wn = null;
        for(Map.Entry<WordNumber, WordNumber> e : compoundRelations.entrySet()) {
            wn = e.getKey();
            if(source.word.equals(wn.word) && source.number == wn.number) {
                break;
            } 
        }
        return wn;
    }
    private void addCompund(WordNumber source, WordNumber val) {
        WordNumber ans = source;
        if(containsKey(source)) {
            WordNumber value = get(source);
            //String newValue = value + "-" + val;
            //System.out.println(value.word + " " + value.number);
            value.word = value.word + "-" + val.word;
            //String newValue = val + "-";
            this.compoundRelations.replace(source, value);
        }
        else {
            this.compoundRelations.put(source, val);
        }
        //return ans;
    }
    private WordNumber replace(WordNumber source) {
        WordNumber ans = source;
        for(Map.Entry<WordNumber, WordNumber> e : this.compoundRelations.entrySet()) {
            WordNumber key = e.getKey();
            if(key.word.equals(source.word)) {
                if(key.number == source.number) {
                    ans = e.getValue();
                    break;
                }
            }
        }
        if(!ans.word.equals(source.word)) {
            ans.word = ans.word + "-" + source.word;
            return ans;
        }
        else return ans;
    }
    public Set<Relations> dependenciesToRelations(String deps[]) {
        Set<Relations> relations = new HashSet();
        boolean is_case = false; String case_prep = "";
        for(String s : deps) {
            WordNumber temp[] = processDependencies(s);
            // The man has been killed by the police => agent(killed, police)
            if(temp[0].word.equals("agent")) { 
                temp[1] = replace(temp[1]); //relations.add("ACTION=" + temp[1]); relations.add("ACTION_CAUSED_BY=" + temp[2]); 
                temp[2] = replace(temp[2]); //relations.add("CAUSED_BY=(" + temp[1].word + "," + temp[2].word + ")");
                Relations r = new Relations();
                r.relation = "Caused_by"; r.first = temp[1].word; r.second = temp[2].word;
                relations.add(r);
            }
            //The accident happened as the night was falling => advcl(happened, falling)
            if(temp[0].word.equals("advcl")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]); //relations.add("WHEN/BECAUSE/FOR=(" + temp[1].word + "," + temp[2].word + ")");
                Relations r = new Relations();
                r.relation = "When/Because/For"; r.first = temp[1].word; r.second = temp[2].word;
                relations.add(r);
            }
            // He works efficiently on his own. => advmod(works, efficiently)
            if(temp[0].word.equals("advmod")) {
                Relations r = new Relations();
                if(temp[2].word.startsWith("Wh") || temp[2].word.startsWith("wh")) { //relations.add("CHARACTERISTIC=(?," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-WH"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("Did") || temp[2].word.equals("Does")) { //relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-BOOL"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("How")) { //relations.add("CHARACTERISTIC=(REASONING," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-REASON"; r.second = temp[1].word;
                }
                else { //relations.add("CHARACTERISTIC=(" + temp[2].word + "," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = temp[2].word; r.second = temp[1].word;
                }
                relations.add(r);
            }
            // Sam eats red apple => amod(apple, red)
            if(temp[0].word.equals("amod")) { 
                //relations.add("CHARACTERISTIC_OF=" + temp[1]); relations.add("CHARACTERISTIC=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                Relations r = new Relations();
                if(temp[2].word.startsWith("Wh") || temp[2].word.startsWith("wh")) { //relations.add("CHARACTERISTIC=(?," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-WH"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("Did") || temp[2].word.equals("Does")) { //relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-BOOL"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("How")) { //relations.add("CHARACTERISTIC=(REASONING," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-REASON"; r.second = temp[1].word;
                }
                else { //relations.add("CHARACTERISTIC=(" + temp[2].word + "," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = temp[2].word; r.second = temp[1].word;
                }
                relations.add(r);
            }
            // Sam, my brother, arrived. => appos(Sam, brother)
            if(temp[0].word.equals("appos")) { 
                //relations.add("IDENTIFIED_AS=" + temp[1]); relations.add("WHAT_IDENTIFIED=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]); //relations.add("IDENTIFIED=(" + temp[2].word + "," + temp[1].word + ")");
                Relations r = new Relations();
                r.relation = "Identified"; r.first = temp[2].word; r.second = temp[1].word;
                relations.add(r);
            }
            if(!is_case && temp[0].word.equals("case")) {
                is_case = true; case_prep = temp[2].word;
            }
            // Bill is big and honest => cc(big, and)
            if(temp[0].word.equals("cc")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].word.equalsIgnoreCase("not")) { //relations.add("BOOL_NEGATIVE=" + temp[1].word);
                    Relations r = new Relations();
                    r.relation = "When/Because/For"; r.first = temp[1].word; r.second = temp[2].word;
                    relations.add(r);
                }
                Relations r = new Relations(); //relations.add("CONJUNCTION_BY=" + temp[2].word); 
                r.relation = "Conjunction_by"; r.first = temp[2].word; 
                relations.add(r);
            } 
            if(temp[0].word.equals("compound")) {
                this.addCompund(temp[1], temp[2]);
            }
            if(temp[0].word.equals("compound:prt")) {
                this.addCompund(temp[2], temp[1]);
            }
            // Which policy best suits me? => dep(policy, best)
            if(temp[0].word.equals("dep")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                //this.compoundRelations.put(temp[1], temp[2] + "-" + temp[1]);
                Relations r = new Relations();
                if(temp[2].word.startsWith("Wh") || temp[2].word.startsWith("wh")) { //relations.add("CHARACTERISTIC=(?," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-WH"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("Did") || temp[2].word.equals("Does")) { //relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-BOOL"; r.second = temp[1].word;
                }
                else if(temp[2].word.equalsIgnoreCase("How")) { //relations.add("CHARACTERISTIC=(REASONING," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = "TYPE-REASON"; r.second = temp[1].word;
                }
                else { //relations.add("CHARACTERISTIC=(" + temp[2].word + "," + temp[1].word + ")");
                    r.relation = "Characteristic"; r.first = temp[2].word; r.second = temp[1].word;
                }
                relations.add(r);
            }
            // She gave me a raise => dobj(gave, raise)
            if(temp[0].word.equals("dobj")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].word.equalsIgnoreCase("What")) { //relations.add("OBJECT=?");
                    Relations r = new Relations();
                    r.relation = "Object"; r.first = "TYPE-WH";
                    relations.add(r);
                }
                else { //relations.add("OBJECT=" + temp[2].word);
                    Relations r = new Relations();
                    r.relation = "Object"; r.first = temp[2].word;
                    relations.add(r);
                }
                Relations r = new Relations(); //relations.add("ACTION=" + temp[1].word);
                r.relation = "Action"; r.first = temp[1].word;
                relations.add(r);
            }
            // There is a ghost in the room. => expl(is, There)
            if(temp[0].word.equals("expl")) {
                Relations r = new Relations(); //relations.add("EXPLETIVE=(" + temp[2].word + "," + temp[1].word + ")");
                r.relation = "Expletive"; r.first = temp[2].word; r.second = temp[1].word;
                relations.add(r);
            }
            // She gave me a raise => iobj(game, me)
            if(temp[0].word.equals("iobj")) { //relations.add("INDIRECT_OBJECT=" + temp[2].word); 
                Relations r = new Relations();
                r.relation = "Indirect_object"; r.first = temp[2].word;
                relations.add(r);
            }
            if(temp[0].word.equals("nsubj") || temp[0].word.equals("nsubj:xsubj") || temp[0].word.equals("vocative") || temp[0].word.equals("nsubjpass")) {
                temp[2] = replace(temp[2]); //relations.add("SUBJECT=" + temp[2].word);
                Relations r = new Relations();
                r.relation = "Subject"; r.first = temp[2].word;
                relations.add(r);
                //relations.add("ACTION_DONE_BY=(" + temp[1] + "," + temp[2] + ")");
            }
            // Bill saw no accidents. => neg(accidents, no)
            if(temp[0].word.equals("neg")) {
                Relations r = new Relations(); //relations.add("BOOL_NEGATIVE=" + temp[1].word);
                r.relation = "Bool_neg"; r.first = temp[1].word;
                relations.add(r);
            }
            if(is_case && temp[0].word.equals("nmod")) {
                is_case = false; //relations.add("PREP_" + case_prep + "=(" + temp[1].word + "," + temp[2].word + ")");
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                Relations r = new Relations();
                r.relation = "Prep_" + case_prep; r.first = temp[1].word; r.second = temp[2].word;
                relations.add(r);
            }
            // Sam ate 3 sheep => num(sheep, 3)
            if(temp[0].word.equals("num") || temp[0].word.equals("nummod")) {
		//relations.add("QUANTITY=" + temp[2]); relations.add("QUANTITY_OF=" + temp[1]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]); //relations.add("QUANTITY=(" + temp[2].word + "," + temp[1].word + ")");
                Relations r = new Relations();
                r.relation = "Quantity"; r.first = temp[2].word; r.second = temp[1].word;
                relations.add(r);
            } 
            // Bill's clothes => poss(clothes, Bill)
            if(temp[0].word.equals("poss") || temp[0].word.equals("nmod:poss")) {
		//relations.add("WHOSE_OBJECT=" + temp[1]); relations.add("WHAT_OBJECT=" + temp[2]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]); //relations.add("POSSESSED_BY=(" + temp[2].word + "," + temp[1].word + ")");
                Relations r = new Relations();
                r.relation = "Possessd_by"; r.first = temp[2].word; r.second = temp[1].word;
                relations.add(r);
            }
            // Last night, I swam in the pool => tmod(swam, night)
            if(temp[0].word.equals("tmod") || temp[0].word.equals("nmod:tmod")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                //relations.add("ACTION=" + temp[1]); relations.add("ACTION_WHEN=" + temp[2]);
                //relations.add("ACTION_WHEN=(" + temp[2].word + "," + temp[1].word + ")");
                Relations r = new Relations();
                r.relation = "Action_when"; r.first = temp[1].word; r.second = temp[2].word;
                relations.add(r);
            }
            // Tom likes to eat fish => xsubj(eat, Tom)
            if(temp[0].word.equals("xsubj")) {
                temp[2] = replace(temp[2]); //relations.add("CONTROLLING_SUBJECT=" + temp[2].word);
                Relations r = new Relations();
                r.relation = "Controlling_subject"; r.first = temp[2].word;
                relations.add(r);
            }
        }
        //for(Relations r : relations) { System.out.println("(" + r.first + ", " + r.relation + ", " + r.second + ")"); }
        //System.out.println();
        return relations;
    }
        // @param args command line arguments
    public static void main(String[] args) throws IOException {
        DepToRelNew1 dtr = new DepToRelNew1();
        ArrayList<String> test1 = new ArrayList(); String p;
        InputStream in = DepToRel.class.getResourceAsStream("sampledependencies.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        //BufferedWriter write = new BufferedWriter( new FileWriter("..\\BB\\src\\DepToRel\\relations.txt"));
        while((p = r.readLine()) != null) {
            //System.out.println(test1);
            if(p.trim().isEmpty()) {
                String test[] = test1.toArray(new String[test1.size()]);
                //test = dtr.transformDependencies(test);
                Set<Relations> rr = dtr.dependenciesToRelations(test);
                for(Relations rel : rr) { 
                    //write.write(rel.first + "," + rel.relation + "," + rel.second + "\n"); 
                    //write.flush();
                    System.out.println(rel.first + "," + rel.relation + "," + rel.second);
                } System.out.println();
                //write.write("\n"); write.flush();
                test1.clear();
                dtr.compoundRelations.clear();
            }
            else test1.add(p);
        }
    }
}
