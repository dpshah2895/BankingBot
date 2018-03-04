/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepToRel;

import java.io.BufferedReader;
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
public class DepToRel {
    HashMap<String, String> compoundRelations;
    public DepToRel() {
        this.compoundRelations = new HashMap();
    }
    private String[] processDependencies(String dep) {
        String temp[] = new String[3];
        /*
        temp[0] = RELATION
        temp[1] = arg[1]
        temp[2] = arg[2]
        */
        int pos1 = dep.indexOf("(");
        int pos2 = dep.indexOf(",");
        int pos3 = dep.indexOf(")");
        temp[0] = dep.substring(0, pos1);
        String temp2[] = dep.substring(pos1 + 1, pos2).split("[-]");
        if(temp2.length == 2) temp[1] = temp2[0];
        else if(temp2.length == 3) temp[1] = temp2[0] + "-" + temp2[1];
        temp2 = dep.substring(pos2 + 2, pos3).split("[-]");
        if(temp2.length == 2) temp[2] = temp2[0];
        else if(temp2.length == 3) temp[2] = temp2[0] + "-" + temp2[1];
        return temp;
    }
    private String replace(String source) {
        String ans = source;
        for(Map.Entry<String, String> e : this.compoundRelations.entrySet()) {
            String key = e.getKey();
            if(key.equals(source)) {
                ans = e.getValue();
                break;
            }
        }
        return ans;
    }
    // @param deps dependencies
    public void dependecniesToRelations(String deps[]) {
        Set<String> relations = new HashSet();
        for(String s : deps) {
            String temp[] = processDependencies(s);
            // She looks very beautiful => acomp(looks, beautiful)
            if(temp[0].equals("acomp")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("ACTION=" + temp[1]); relations.add("CHARACATERISTIC=" + temp[2]); 
            }
            // Genetically modified food => advmod(modified, Genetically)
            if(temp[0].equals("advmod")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].startsWith("Wh") || temp[2].startsWith("How")) {
                    relations.add("RETRIEVE_RESULTS=true"); relations.add("RETRIEVE_WHAT=" + temp[2] + "-" + temp[1]);
                }
                else relations.add("ACTION=" + temp[1]); relations.add("ACT_CHARACACTERISTIC=" + temp[2]); 
            } // SERIOUSLY-REVIEW
            // The man has been killed by the police => agent(killed, police)
            if(temp[0].equals("agent")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("ACTION=" + temp[1]); relations.add("ACTION_CAUSED_BY=" + temp[2]); 
            }
            // Sam eats red apple => amod(apple, red)
            if(temp[0].equals("amod")) { 
                //relations.add("CHARACTERISTIC_OF=" + temp[1]); relations.add("CHARACTERISTIC=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("CHARACTERISTIC=(" + temp[2] + "," + temp[1] + ")");
            }
            // Sam, my brother, arrived. => appos(Sam, brother)
            if(temp[0].equals("appos")) { 
                //relations.add("IDENTIFIED_AS=" + temp[1]); relations.add("WHAT_IDENTIFIED=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("IDENTIFIED=(" + temp[2] + "," + temp[1] + ")");
            }
            // Reagan has died => aux(died, has)
            // Do you think he will have left when we come? => aux(left, will), aux(left, have)
            if(temp[0].equals("aux")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                this.compoundRelations.put(temp[1], temp[2] + "-" + temp[1]);
            }
            // Bill is big and honest => cc(big, and)
            if(temp[0].equals("cc")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("CONJUNCTION_BY=" + temp[2]); 
            } // REVIEW
            // Credit card => compound(card, credit)
            if(temp[0].equals("compound")) { 
                //System.out.println(temp[1] + " " + temp[2]);
                this.compoundRelations.put(temp[1], temp[2] + "-" + temp[1]);
            }
            //The accident happened as the night was falling => advcl(happened, falling)
            if(temp[0].equals("advcl")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("CLAUSE_RELATION=" + temp[1] + "-WHEN/BECAUSE/FOR-" + temp[2]);
            }
            // Which policy best suits me? => dep(policy, best)
            if(temp[0].equals("dep")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                this.compoundRelations.put(temp[1], temp[2] + "-" + temp[1]);
            }
            // The man is here => det(man, the)
            // which book do you prefer? => det(book, Which)
            if(temp[0].equals("det")) {
                if(temp[2].equalsIgnoreCase("what") || temp[2].equalsIgnoreCase("which")) {
                    temp[1] = replace(temp[1]);
                    temp[2] = replace(temp[2]);
                    relations.add("RETRIVE_RESULTS=true");
                    relations.add("RETRIEVE_WHAT=" + temp[1]);		
		}
            } // REVIEW
            // She gave me a raise => dobj(gave, raise)
            if(temp[0].equals("dobj")) {
                temp[2] = replace(temp[2]);
                relations.add("OBJECT=" + temp[2]);
            }
            // She gave me a raise => iobj(game, me)
            if(temp[0].equals("iobj")) relations.add("INDIRECT_OBJECT=" + temp[2]);
            // Clinton defeated Dole => nsubj(defeated, Clinton)
            if(temp[0].equals("nsubj") || temp[0].equals("nsubj:xsubj") || temp[0].equals("vocative")) {
                temp[2] = replace(temp[2]);
                relations.add("SUBJECT=" + temp[2]);
                
                //relations.add("ACTION_DONE_BY=(" + temp[1] + "," + temp[2] + ")");
            }	// REVIEW
            // Dole was defeated by Clinton => nsubj(defeated, Dole)
            if(temp[0].equals("nsubjpass")) {
                temp[2] = replace(temp[2]);
                relations.add("SUBJECT=" + temp[2]);
                relations.add("ACTION_DONE_ON=(" + temp[1] + "," + temp[2] + ")");
            }	// REVIEW
            // Sam ate 3 sheep => num(sheep, 3)
            if(temp[0].equals("num") || temp[0].equals("nummod")) {
		//relations.add("QUANTITY=" + temp[2]); relations.add("QUANTITY_OF=" + temp[1]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("QUANTITY=(" + temp[2] + "," + temp[1] + ")");
            } // REVIEW
            // Bill's clothes => poss(clothes, Bill)
            if(temp[0].equals("poss")) {
		//relations.add("WHOSE_OBJECT=" + temp[1]); relations.add("WHAT_OBJECT=" + temp[2]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("POSSESSED_BY=(" + temp[2] + "," + temp[1] + ")");
            }
            // I ate 40 oranges => root(ROOT, ate)
            if(temp[0].equals("root")) {
                relations.add("MAIN_ACTION=" + temp[2]);
            }
            // Last night, I swam in the pool => tmod(swam, night)
            if(temp[0].equals("tmod") || temp[0].equals("nmod:tmod")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                //relations.add("ACTION=" + temp[1]); relations.add("ACTION_WHEN=" + temp[2]);
                relations.add("ACTION_WHEN=(" + temp[2] + "," + temp[1] + ")");
            }
            // Tom likes to eat fish => xsubj(eat, Tom)
            if(temp[0].equals("xsubj")) {
                temp[2] = replace(temp[2]);
                relations.add("CONTROLLING_SUBJECT=" + temp[2]);
            }
        }
        /*for(Map.Entry<String, String> e : this.compoundRelations.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }*/
        System.out.println(relations);
    }
    // @param args command line arguments
    public static void main(String[] args) throws IOException {
        DepToRel dtr = new DepToRel();
        ArrayList<String> test1 = new ArrayList(); String p;
        InputStream in = DepToRel.class.getResourceAsStream("sampledependencies.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        while((p = r.readLine()) != null) {
            //System.out.println(test1);
            if(p.trim().isEmpty()) {
                String test[] = test1.toArray(new String[test1.size()]);
                dtr.dependecniesToRelations(test);
                test1.clear();
                dtr.compoundRelations.clear();
            }
            else test1.add(p);
        }
        
    }
}