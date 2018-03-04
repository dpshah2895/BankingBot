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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author Kiner Shah
 */
public class DepToRelNew {
    HashMap<String, String> compoundRelations;
    public DepToRelNew() {
        this.compoundRelations = new HashMap();
    }
    private String[] processDependencies(String dep) {
        String temp[] = new String[3];
        // temp[0] = RELATION, temp[1] = arg[1], temp[2] = arg[2]
        int pos1 = dep.indexOf("(");
        int pos2 = dep.indexOf(",");
        int pos3 = dep.indexOf(")");
        temp[0] = dep.substring(0, pos1); //temp[1] = dep.substring(pos1+1, pos2); temp[2] = dep.substring(pos2+2, pos3);
        String temp2[] = dep.substring(pos1 + 1, pos2).split("[-]");
        if(temp2.length == 2) temp[1] = temp2[0];
        else if(temp2.length == 3) temp[1] = temp2[0] + "-" + temp2[1];
        temp2 = dep.substring(pos2 + 2, pos3).split("[-]");
        if(temp2.length == 2) temp[2] = temp2[0];
        else if(temp2.length == 3) temp[2] = temp2[0] + "-" + temp2[1];
        return temp;
    }
    private void addCompund(String source, String val) {
        String ans = source;
        if(this.compoundRelations.containsKey(source)) {
            String value = this.compoundRelations.get(source);
            String newValue = value + "-" + val;
            //String newValue = val + "-";
            this.compoundRelations.replace(source, newValue);
        }
        else {
            this.compoundRelations.put(source, val);
        }
        //return ans;
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
        if(!ans.equals(source)) return ans + "-" + source;
        else return ans;
    }
    /*public String[] transformDependencies(String t[]) {
        ArrayList<String> l = new ArrayList(Arrays.asList(t));
        ArrayList<String> r = new ArrayList();
        // Three passes:
        //1. Merge compounds
        //2. Merge cops and mods
        //3. Merge cases 
        boolean compoundFlag = false;
        String compound = "", compoundStart = "";
        for(String p : l) {
            String temp[] = processDependencies(p);
            if(!compoundFlag && temp[0].equals("compound")) {
                compoundFlag = true;
                compoundStart = temp[1];
                compound += temp[2];
            }
            else if(compoundFlag && temp[0].equals("compound")) {
                compound += "-" + temp[2];
            }
            else if(compoundFlag) {
                compoundFlag = false;
                compound += "-" + compoundStart;
                //System.out.println(compound);
                this.compoundRelations.put(compoundStart, compound);
                r.add(p);
            }
        }
        //System.out.println(r);
        return r.toArray(new String[0]);
    }*/
    // TODO:
    // acl, acl:relcl, aux, auxpass, case, ccomp, conj, cop, csubj, csubjpass, det
    // det:predet, discourse, foreign, goeswith, list, mark, mwe, name, nmod, nmod:npmod
    // parataxis, punct (not needed), remnant, reparandum (not needed), root, xcomp
    public void dependenciesToRelations(String deps[]) {
        Set<String> relations = new HashSet();
        boolean is_case = false; String case_prep = "";
        for(String s : deps) {
            String temp[] = processDependencies(s);
            // The man has been killed by the police => agent(killed, police)
            if(temp[0].equals("agent")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                //relations.add("ACTION=" + temp[1]); relations.add("ACTION_CAUSED_BY=" + temp[2]); 
                relations.add("CAUSED_BY=(" + temp[1] + "," + temp[2] + ")");
            }
            //The accident happened as the night was falling => advcl(happened, falling)
            if(temp[0].equals("advcl")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("WHEN/BECAUSE/FOR=(" + temp[1] + "," + temp[2] + ")");
            }
            // He works efficiently on his own. => advmod(works, efficiently)
            if(temp[0].equals("advmod")) {
                if(temp[2].startsWith("Wh") || temp[2].startsWith("wh")) relations.add("CHARACTERISTIC=(?," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("Did") || temp[2].equals("Does")) relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("How")) relations.add("CHARACTERISTIC=(REASONING," + temp[1] + ")");
                else relations.add("CHARACTERISTIC=(" + temp[2] + "," + temp[1] + ")");
            }
            // Sam eats red apple => amod(apple, red)
            if(temp[0].equals("amod")) { 
                //relations.add("CHARACTERISTIC_OF=" + temp[1]); relations.add("CHARACTERISTIC=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].startsWith("Wh") || temp[2].startsWith("wh")) relations.add("CHARACTERISTIC=(?," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("Did") || temp[2].equals("Does")) relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("How")) relations.add("CHARACTERISTIC=(REASONING," + temp[1] + ")");
                else relations.add("CHARACTERISTIC=(" + temp[2] + "," + temp[1] + ")");
            }
            // Sam, my brother, arrived. => appos(Sam, brother)
            if(temp[0].equals("appos")) { 
                //relations.add("IDENTIFIED_AS=" + temp[1]); relations.add("WHAT_IDENTIFIED=" + temp[2]); 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("IDENTIFIED=(" + temp[2] + "," + temp[1] + ")");
            }
            if(!is_case && temp[0].equals("case")) {
                is_case = true; case_prep = temp[2];
            }
            // Bill is big and honest => cc(big, and)
            if(temp[0].equals("cc")) { 
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].equalsIgnoreCase("not")) relations.add("BOOL_NEGATIVE=" + temp[1]);
                relations.add("CONJUNCTION_BY=" + temp[2]); 
            } 
            if(temp[0].equals("compound")) {
                this.addCompund(temp[1], temp[2]);
            }
            if(temp[0].equals("compound:prt")) {
                this.addCompund(temp[2], temp[1]);
            }
            // Which policy best suits me? => dep(policy, best)
            if(temp[0].equals("dep")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                //this.compoundRelations.put(temp[1], temp[2] + "-" + temp[1]);
                if(temp[2].startsWith("Wh") || temp[2].startsWith("Wh")) relations.add("CHARACTERISTIC=(?," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("Did") || temp[2].equals("Does") || temp[2].equals("Do")) relations.add("CHARACTERISTIC=(BOOLEAN," + temp[1] + ")");
                else if(temp[2].equalsIgnoreCase("How")) relations.add("CHARACTERISTIC=(REASONING," + temp[1] + ")");
                else relations.add("CHARACTERISTIC=(" + temp[2] + "," + temp[1] + ")");
            }
            // She gave me a raise => dobj(gave, raise)
            if(temp[0].equals("dobj")) {
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                if(temp[2].equalsIgnoreCase("What")) relations.add("OBJECT=?");
                else relations.add("OBJECT=" + temp[2]);
                relations.add("ACTION=" + temp[1]);
            }
            // There is a ghost in the room. => expl(is, There)
            if(temp[0].equals("expl")) {
                relations.add("EXPLETIVE=(" + temp[2] + "," + temp[1] + ")");
            }
            // She gave me a raise => iobj(game, me)
            if(temp[0].equals("iobj")) relations.add("INDIRECT_OBJECT=" + temp[2]); 
            if(temp[0].equals("nsubj") || temp[0].equals("nsubj:xsubj") || temp[0].equals("vocative") || temp[0].equals("nsubjpass")) {
                temp[2] = replace(temp[2]);
                relations.add("SUBJECT=" + temp[2]);
                //relations.add("ACTION_DONE_BY=(" + temp[1] + "," + temp[2] + ")");
            }
            // Bill saw no accidents. => neg(accidents, no)
            if(temp[0].equals("neg")) {
                relations.add("BOOL_NEGATIVE=" + temp[1]);
            }
            if(is_case && temp[0].equals("nmod")) {
                is_case = false; relations.add("PREP_" + case_prep + "=(" + temp[1] + "," + temp[2] + ")");
            }
            // Sam ate 3 sheep => num(sheep, 3)
            if(temp[0].equals("num") || temp[0].equals("nummod")) {
		//relations.add("QUANTITY=" + temp[2]); relations.add("QUANTITY_OF=" + temp[1]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("QUANTITY=(" + temp[2] + "," + temp[1] + ")");
            } 
            // Bill's clothes => poss(clothes, Bill)
            if(temp[0].equals("poss") || temp[0].equals("nmod:poss")) {
		//relations.add("WHOSE_OBJECT=" + temp[1]); relations.add("WHAT_OBJECT=" + temp[2]);
                temp[1] = replace(temp[1]);
                temp[2] = replace(temp[2]);
                relations.add("POSSESSED_BY=(" + temp[2] + "," + temp[1] + ")");
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
        System.out.println(relations);
    }
    // @param args command line arguments
    public static void main(String[] args) throws IOException {
        DepToRelNew dtr = new DepToRelNew();
        ArrayList<String> test1 = new ArrayList(); String p;
        InputStream in = DepToRel.class.getResourceAsStream("sampledependencies.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        while((p = r.readLine()) != null) {
            //System.out.println(test1);
            if(p.trim().isEmpty()) {
                String test[] = test1.toArray(new String[test1.size()]);
                //test = dtr.transformDependencies(test);
                dtr.dependenciesToRelations(test);
                test1.clear();
                dtr.compoundRelations.clear();
            }
            else test1.add(p);
        }
        
    }
}
