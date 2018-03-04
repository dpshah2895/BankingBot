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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author Kiner Shah
 */
public class ResponseFinder {
    // TODO:
    // If response belongs to class ACCOUNT and LoggedIn status is false, give "Can't respond" output
    // Else, respond accordingly.
    private boolean wh_flag, reas_flag, bool_flag;
    private HashMap<Integer, String> sentenceID;
    private HashMap<Integer, String> classID;
    private String currentFrame, currentUser;
    public ResponseFinder(String frameName, String userName) {
        currentFrame = frameName;
        currentUser = userName;
        wh_flag = false; reas_flag = false; bool_flag = false;
        sentenceID = new HashMap();
        classID = new HashMap();
        int id = 0;
        InputStream in = ResponseFinder.class.getResourceAsStream("sentences.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        InputStream in1 = ResponseFinder.class.getResourceAsStream("classes.txt");
        BufferedReader r1 = new BufferedReader(new InputStreamReader(in1));
        try {
            String p;
            while((p = r.readLine()) != null) {
                if(p.trim().isEmpty()) continue;
                sentenceID.put(++id, p);
            }
            id = 0;
            while((p = r1.readLine()) != null) {
                if(p.trim().isEmpty()) continue;
                classID.put(++id, p);
            }
            r.close();
            r1.close();
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    private String convertPersons(String t) {
        String p = t;
        if(t.equalsIgnoreCase("I") || t.equalsIgnoreCase("me") || t.equalsIgnoreCase("we")) p = "you";
        else if(t.equalsIgnoreCase("myself")) p = "yourself";
        else if(t.equalsIgnoreCase("ours")) p = "yours";
        else if(t.equalsIgnoreCase("our") || t.equalsIgnoreCase("my") || t.equalsIgnoreCase("mine")) p = "your";
        else if(t.equalsIgnoreCase("ourselves")) p = "yourselves";
        else if(t.equalsIgnoreCase("you")) p = "i";
        else if(t.equalsIgnoreCase("your")) p = "my";
        else if(t.equalsIgnoreCase("yourself")) p = "myself";
        //System.out.println(t + " " + p);
        return p;
    }
    private int findScore(List<Relations> source, List<Relations> target) {
        /* Process target first
        1. Convert first person to second person and vice versa
        2. Convert all words to lower case (same for source)
        */
        //I, me, we, myself, ours, our, my, mine, ourselves
        //you, yourself, yours, your, yourselves
        int tlength = target.size();
        int score = 0; int slength = source.size();
        for(int i = 0; i < tlength; i++) {
            String f = target.get(i).first;
            String s = target.get(i).second;
            String r = target.get(i).relation;
            if(f != null && f.equals("TYPE_WH")) wh_flag = true;
            if(f != null && f.equals("TYPE_REASON")) reas_flag = true;
            if(f != null && f.equals("TYPE_BOOL")) bool_flag = true;
            for(int j = 0; j < slength; j++) {
                /*
                1. If relation, first word and second word matches => 10 points
                2. If relation and first word matches => 5 points
                3. If relation and second word matches => 5 points
                4. If first word matches => 2 points
                5. If second word matches => 2 points
                EDIT:
                If first, second and relation are not null, then follow above scheme
                else if first and relation are not null, then,
                1. If first and relation matches, 10 points
                2. If first matches, 2 points
                */
                String sf = source.get(j).first;
                String ss = source.get(j).second;
                String sr = source.get(j).relation;
                if(sf != null && !sf.matches("[A-Z_]+")) sf = sf.toLowerCase();
                if(ss != null && !ss.matches("[A-Z_]+")) ss = ss.toLowerCase();
                //System.out.println(f + " " + r + " " + s + "\t" + sf + " " + sr + " " + ss);
                boolean fbool = false, sbool = false, rbool = false;
                if(f != null && sf != null && sf.equals(f)) fbool = true;
                if(s != null && ss != null && ss.equals(s)) sbool = true;
                if(sr.equals(r)) rbool = true;
                //System.out.println(fbool + " " + rbool + " " + sbool);
                if(fbool && sbool && rbool) score += 10;
                else if(fbool && rbool) score += 5;
                else if(sbool && rbool) score += 10; // first value was 5
                else if(fbool || sbool) score += 2;
                //System.out.println(score);
            }
        }
        return score;
    }
    private boolean contains(HashMap<List<Relations>, Integer> m, List<Relations> l) {
        boolean flag = false;
        for(Map.Entry<List<Relations>, Integer> e : m.entrySet()) {
            List<Relations> ll = e.getKey();
            if(ll.size() == l.size()) {
                int len = l.size();
                for(int i = 0; i < len; i++) {
                    Relations r1 = l.get(i);
                    Relations r2 = ll.get(i);
                    if(r1.first.equals(r2.first) && r1.second.equals(r2.second) && r1.relation.equals(r2.relation)) {
                        flag = true;
                    }
                    else { flag = false; break; }
                }
                if(flag) break;
            }
        }
        return flag;
    }
    private int get(HashMap<List<Relations>, Integer> m, List<Relations> l) {
        int id = -1; boolean flag = false;
        for(Map.Entry<List<Relations>, Integer> e : m.entrySet()) {
            List<Relations> ll = e.getKey();
            if(ll.size() == l.size()) {
                int len = l.size();
                for(int i = 0; i < len; i++) {
                    Relations r1 = l.get(i);
                    Relations r2 = ll.get(i);
                    if(r1.first.equals(r2.first) && r1.second.equals(r2.second) && r1.relation.equals(r2.relation)) {
                        flag = true;
                    }
                    else { flag = false; break; }
                }
                if(flag) { id = e.getValue(); break; }
            }
        }
        return id;
    }
    public String findResponses(Set<Relations> target) {
        InputStream in = ResponseFinder.class.getResourceAsStream("relations.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        List<Relations> source = new ArrayList();
        int id = 0;
        HashMap<List<Relations>, Integer> relationID = new HashMap();
        //for(Relations rrr : target) System.out.println(rrr.first + " " + rrr.relation + " " + rrr.second); System.out.println();
        List<Relations> target_as_list = new ArrayList(target);
        HashMap<List<Relations>, Integer> rel_score = new HashMap(); 
        for(int i = 0; i < target_as_list.size(); i++) {
            String f = target_as_list.get(i).first;
            String s = target_as_list.get(i).second;
            if(f != null && !f.matches("[A-Z_]+")) f = f.toLowerCase();
            if(s != null && !s.matches("[A-Z_]+")) s = s.toLowerCase();
            if(f != null) target_as_list.get(i).first = convertPersons(f);
            if(s != null) target_as_list.get(i).second = convertPersons(s);
        }
        String temp, sentenceAns = "";
        try {
            while((temp = r.readLine()) != null) {
                if(temp.trim().isEmpty()) {
                    //find score and store it along with sentence no.
                    if(!contains(relationID, source)) relationID.put(source, ++id);
                    int score = findScore(source, target_as_list);
                    //System.out.println(score);
                    //for(Relations rrr : source) System.out.println(rrr.first + " " + rrr.relation + " " + rrr.second); System.out.println();
                    rel_score.put(source, score);
                    source = new ArrayList();
                }
                else {
                    String temp1[] = temp.split("[,]");
                    //System.out.println(temp1[0] + " " + temp1[1] + " " + temp1[2]);
                    Relations new_rel = new Relations();
                    new_rel.first = temp1[0]; new_rel.relation = temp1[1]; new_rel.second = temp1[2];
                    source.add(new_rel);
                }
            }
            int score = findScore(source, target_as_list);
            rel_score.put(source, score);
            source.clear();
            int max = Integer.MIN_VALUE; List<Relations> ans = new ArrayList();
            for(Map.Entry<List<Relations>, Integer> e : rel_score.entrySet()) {
                int value = e.getValue(); //System.out.println(e.getKey().size());
                if(value >= max) { // was >= before
                    max = value; ans = e.getKey();
                }
            }
            System.out.println(max);
            for(Relations rrr : ans) System.out.println(rrr.first + " " + rrr.relation + " " + rrr.second); System.out.println();
            //System.out.println(get(relationID, ans));
            if(max != 0) {
                int sentenceKey = get(relationID, ans);
                //System.out.println(sentenceID.get(sentenceKey));
                //if(classID.get(sentenceKey).equals("ACCOUNT")) sentenceAns = "Sorry, you have to login!";
                //else sentenceAns = sentenceID.get(sentenceKey);
                if(classID.get(sentenceKey).equals("ACCOUNT") && !currentFrame.equals("LoggedIn")) sentenceAns = "Sorry, you have to log in!";
                else {
                    sentenceAns = sentenceID.get(sentenceKey);
                    if(sentenceAns.endsWith("(ACTION=BALANCE)")) {
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankingbotdb", "root", "");
                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery("select Balance from account where UserName='" + currentUser + "'");
                        String bal = "";
                        if(rs.next()) bal = rs.getString("Balance");
                        sentenceAns = "Your balance is " + bal + " rupees";
                    }
                    else if(sentenceAns.endsWith("(ACTION=BALANCESHEET)")) {
                        JDBCTutorial.ViewingTransactions vt = new JDBCTutorial.ViewingTransactions();
                        vt.setVisible(true);
                        sentenceAns = sentenceAns.substring(0, 36);
                    }
                    else if(sentenceAns.endsWith("(ACTION=CHEQUEBOOK)")) {
                        JDBCTutorial.ChequeBookForm cbf = new JDBCTutorial.ChequeBookForm();
                        cbf.setVisible(true);
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankingbotdb", "root", "");
                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery("select AcNo from account where UserName='" + currentUser + "'");
                        String acno = "";
                        if(rs.next()) { cbf.setAcNo(rs.getString("AcNo")); acno = rs.getString("AcNo"); }
                        //System.out.println(rs.getString("AcNo"));
                        statement = con.createStatement();
                        rs = statement.executeQuery("select FName,MName,LName,ResidentialAddress from accountholder where AcNo=" + acno + ";");
                        if(rs.next()) { 
                            cbf.setAddress(rs.getString("ResidentialAddress"));
                            cbf.setAcName(rs.getString("FName") + " " + rs.getString("MName") + " " + rs.getString("LName"));
                        }
                        cbf.setDate();
                        sentenceAns = sentenceAns.substring(0, 55);
                    }
                }
            }
            else sentenceAns = "Sorry, I don't know much about that!";
        }
        catch(Exception e) { e.printStackTrace(); }
        return sentenceAns;
    }
    public static void main(String[] args) {
        //DepToRelNew1 dtr = new DepToRelNew1();
        ResponseFinder rf = new ResponseFinder("Home", "");
        Set<Relations> rr = new HashSet();
        InputStream in = ResponseFinder.class.getResourceAsStream("relationsques.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        Set<Relations> l = new HashSet();
        try {
            String temp;
            while((temp = r.readLine()) != null) {
                if(temp.trim().isEmpty()) {
                    rr.addAll(l);
                    System.out.println("---------------------------------------------------------------------"); 
                    for(Relations rrr : rr) System.out.println(rrr.first + " " + rrr.relation + " " + rrr.second); System.out.println();
                    System.out.println(rf.findResponses(rr));
                    System.out.println("---------------------------------------------------------------------");
                    l = new HashSet();
                    rr = new HashSet();
                }
                else {
                    String temp1[] = temp.split("[,]");
                    Relations r1 = new Relations();
                    if(temp1[0].equals("null")) r1.first = null;
                    else r1.first = temp1[0];
                    if(temp1[2].equals("null")) r1.second = null;
                    else r1.second = temp1[2];
                    r1.relation = temp1[1];
                    l.add(r1);
                }
            }
            r.close();
        }
        catch(Exception e) { e.printStackTrace(); }
        /*String deps[] = {
            "aux(expected-5, Can-1)",
            "det(computer-3, a-2)",
            "nsubjpass(expected-5, computer-3)",
            "auxpass(expected-5, be-4)",
            "root(ROOT-0, expected-5)",
            "mark(give-7, to-6)",
            "xcomp(expected-5, give-7)",
            "amod(performance-9, good-8)",
            "dobj(give-7, performance-9)",
        };
        rr.addAll(dtr.dependenciesToRelations(deps));
        //for(Relations r : rr) { System.out.println("(" + r.first + ", " + r.relation + ", " + r.second + ")"); }
        //System.out.println();
        rf.findResponses(rr);*/
    }
}
