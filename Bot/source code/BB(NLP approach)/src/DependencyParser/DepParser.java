/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DependencyParser;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Kiner Shah
 */
class SQEntry {
    int id; String word, pos;
}
class ArcEntry {
    int left_index,right_index;
    String arc_label;
}
class LRDependency {
    String ldep, rdep;
}
public class DepParser {
    int weights, weightl, weightr;
    ArrayList<SQEntry> queue;
    Stack<SQEntry> stack;
    boolean underscore_is_informative;
    ArrayList<ArcEntry> arcs;
    int heads[];
    DepParser() {
        weights = 1; weightl = 1; weightr = 1;
        underscore_is_informative = false;
    }
    public ArrayList<String> extractFeatures() {
        ArrayList<String> result = new ArrayList();
        if(!this.stack.empty()) {
            SQEntry topentry = this.stack.peek();
            if(checkInformative(topentry.word, true)) result.add("STK_0_FORM_" + topentry.word);
            if(checkInformative(topentry.pos, false)) result.add("STK_0_TAG_" + topentry.pos);
            LRDependency dep = findLRDep(topentry.id, arcs);
            if(checkInformative(dep.ldep, false)) result.add("STK_0_LDEP_" + dep.ldep);
            if(checkInformative(dep.rdep, false)) result.add("STK_0_RDEP_" + dep.rdep);
        }
        if(!this.queue.isEmpty()) {
            SQEntry frontentry = this.queue.get(0);
            if(checkInformative(frontentry.pos, false)) result.add("BUF_0_TAG_" + frontentry.pos);
            if(checkInformative(frontentry.word, true)) result.add("BUF_0_FORM_" + frontentry.word);
            LRDependency dep = findLRDep(frontentry.id, arcs);
            if(checkInformative(dep.ldep, false)) result.add("BUF_0_LDEP_" + dep.ldep);
            if(checkInformative(dep.rdep, false)) result.add("BUF_0_RDEP_" + dep.rdep);
            if(this.queue.size() > 1) {
                SQEntry front1entry = this.queue.get(1);
                if(checkInformative(front1entry.pos, false)) result.add("BUF_1_TAG_" + front1entry.pos);
                if(checkInformative(front1entry.word, true)) result.add("BUF_1_FORM_" + front1entry.word);
            }
            if(this.queue.size() > 2) {
                SQEntry front2entry = this.queue.get(2);
                if(checkInformative(front2entry.pos, false)) result.add("BUF_2_TAG_" + front2entry.pos);
                if(checkInformative(front2entry.word, true)) result.add("BUF_2_FORM_" + front2entry.word);
            }
        }
        return result;
    }
    public LRDependency findLRDep(int idx, ArrayList<ArcEntry> l) {
        int lmost = 100000, rmost = -1;
        String dlmost = "", drmost = "";
        for(ArcEntry a : l) {
            if(a.left_index == idx) {
                if(a.right_index > a.left_index && a.right_index > rmost) {
                    rmost = a.right_index;
                    drmost = a.arc_label;
                }
                if(a.left_index > a.right_index && a.right_index < lmost) {
                    lmost = a.right_index;
                    dlmost = a.arc_label;
                }
            }
        }
        LRDependency dep = new LRDependency();
        dep.ldep = dlmost; dep.rdep = drmost;
        return dep;
    }
    public boolean checkInformative(String feat, boolean uis) {
        this.underscore_is_informative = uis;
        if(feat == null) return false;
        if(feat.isEmpty()) return false;
        return !(!this.underscore_is_informative && feat.equals("_"));
    }
    public int left_arc(String relation) {
        if(this.queue == null && this.stack == null) return -1;
        SQEntry ss = this.stack.peek();
        if(ss.id == 0) return -1;
        int f = 0;
        for(ArcEntry a : this.arcs) {
            if(ss.id == a.right_index) { f = 1; break; }
        }
        if(f == 1) return -1;
        SQEntry bb = this.queue.get(0);
        this.stack.pop();
        ArcEntry new_arc = new ArcEntry();
        new_arc.arc_label = relation;
        new_arc.left_index = bb.id;
        new_arc.right_index = ss.id;
        this.arcs.add(new_arc);
        return 0;
    }
    public int right_arc(String relation) {
        if(this.queue == null && this.stack == null) return -1;
        SQEntry wi = this.stack.peek();
        SQEntry wj = this.queue.remove(0);
        this.stack.add(wj);
        ArcEntry new_arc = new ArcEntry();
        new_arc.arc_label = relation;
        new_arc.left_index = wi.id;
        new_arc.right_index = wj.id;
        this.arcs.add(new_arc);
        return 0;
    }
    public int reduce() {
        if(this.stack == null) return -1;
        SQEntry ss = this.stack.get(1);
        int f = 0;
        for(ArcEntry a : this.arcs) {
            if(ss.id == a.right_index) { f = 1; this.stack.pop(); }
        }
        if(f == 0) return -1;
        return 0;
    }
    public static void main(String[] args) {
        DepParser dp = new DepParser();
        dp.stack = new Stack(); 
        SQEntry sq = new SQEntry();
        dp.arcs = new ArrayList();
        sq.id = 0; sq.pos = "ROOT"; sq.word = "ROOT";
        dp.stack.add(sq);
        
        dp.queue = new ArrayList();
        SQEntry sq1 = new SQEntry();
        sq1.id = 1; sq1.pos = "NNP"; sq1.word = "I";
        dp.queue.add(sq1);
        SQEntry sq2 = new SQEntry();
        sq2.id = 2; sq2.pos = "VBD"; sq2.word = "cried";
        dp.queue.add(sq2);
        ArrayList<String> list = dp.extractFeatures(); System.out.println(list);
    }
}