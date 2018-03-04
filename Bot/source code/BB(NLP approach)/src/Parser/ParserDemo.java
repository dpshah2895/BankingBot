/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import java.util.*;
//import java.util.regex.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
public class ParserDemo {
    /*@Override
    public String word() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWord(String word) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
    
public String[] convertToDependencies1(String sent) {
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"});
    String[] split_sent = sent.split(" ");
 
    //List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
    List<CoreLabel> rawWords = new ArrayList();
    for(String a : split_sent) {
        CoreLabelTokenFactory cl = new CoreLabelTokenFactory();
        rawWords.add(cl.makeToken(a, 0, a.length()));
    }
    Tree parse = lp.apply(rawWords); //parse.indentedXMLPrint();
    //parse.pennPrint();
    //System.out.println();
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    ArrayList<String> depAsStr = new ArrayList();
    for(TypedDependency td : tdl) depAsStr.add(td.toString());
    return depAsStr.toArray(new String[0]);
}
public String[] convertToDependencies(String sent1) {
    StringBuilder sb = new StringBuilder();
    ArrayList<Tree> ll = new ArrayList();
    ArrayList<Tree> ll1 = new ArrayList();
    Stack<Character> stack = new Stack();
    for(int i = 0; i < sent1.length(); i++) {
        char c = sent1.charAt(i); //System.out.print(c);
        if(c != ')') stack.push(c);
        else {
            char top = stack.peek();
            while(top != '(') {
                if(top == ' ') {
                    if(sb.length() > 0) {
                        Label label = new CoreLabel();
                        label.setValue(sb.toString()); sb.delete(0, sb.length());
                        ll.add(new LabeledScoredTreeNode(label));
                    }
                    //System.out.println(ll.get(ll.size() - 1).toString());
                    stack.pop();
                }
                else {
                    sb.insert(0, stack.pop());
                }
                top = stack.peek();
            }
            stack.pop();
            //for(Tree t1 : ll) { t1.pennPrint(); }
            if(sb.length() > 0) {
                Label label = new CoreLabel();
                label.setValue(sb.toString());
                sb.delete(0, sb.length());
                Tree nt = new LabeledScoredTreeNode(label);
                //System.out.println(nt.toString());
                if(!ll.isEmpty()) {
                    for (Tree t1 : ll) { nt.addChild(t1); }
                    ll.clear(); ll1.add(nt); //System.out.println(nt.toString());
                }
                else {
                    //for(Tree t1 : ll1) { nt.addChild(t1); }
                    //ll1.clear(); ll1.add(nt); 
                    Tree t1, t2;
                    t1 = ll1.remove(ll1.size() - 1);
                    if(ll1.size() > 0) {
                        t2 = ll1.remove(ll1.size() - 1);
                        nt.addChild(t2); 
                    }
                    nt.addChild(t1); ll1.add(nt);
                    //System.out.println(nt.toString());
                }
            }
        }
        
    }
    Tree t = ll1.get(0); //System.out.println("Custom");
    t.pennPrint();
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    ArrayList<String> depAsStr = new ArrayList();
    for(TypedDependency td : tdl) depAsStr.add(td.toString());
    return depAsStr.toArray(new String[0]);
    //System.out.println(tdl);
    //System.out.println();
    //TreePrint tp = new TreePrint("originalDependencies");
    //tp.printTree(t);
}    
public static void main(String[] args) {
    
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"});
    String[] sent = "How old are you ?".split(" ");
 
    //List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
    List<CoreLabel> rawWords = new ArrayList();
    for(String a : sent) {
        CoreLabelTokenFactory cl = new CoreLabelTokenFactory();
        rawWords.add(cl.makeToken(a, 0, a.length()));
    }
    String sent1 = "(S (NP (DT a) (NN computer)) (VP (MD can) (VP' (RB not) (VP (VB be) (VP (VBN expected) (S_VP (TO to) (VP (VB give) (NP (JJ very) (NP' (NP (JJ high) (NN performance)) (. .))))))))))";
    //String sent1 = "(S (NP (JJ Other) (NP' (JJ semi-supervised) (NNS techniques))) (S' (VP (VB use) (S (NP (JJ large) (NNS quantities)) (S' (IN of) (S' (NP (JJ untagged) (NN corpora)) (VP (TO to) (VP (VBP provide) (NP (NP (JJ co-occurrence) (NN information)) (NP (NP_DT that) (VP (VBZ supplements) (NP (NP (DT the) (JJ tagged)) (NN corpora))))))))))) (. .)))";
    //String sent1 = "(S (NP (PRP He)) (VP (VBD sat) (PRT (RP down))) (. .))";
    //String sent1 = "(NP' (NP (DT a) (NN computer)) (. .))";
    //Tree t = null;
    StringBuilder sb = new StringBuilder();
    ArrayList<Tree> ll = new ArrayList();
    ArrayList<Tree> ll1 = new ArrayList();
    Stack<Character> stack = new Stack();
    for(int i = 0; i < sent1.length(); i++) {
        char c = sent1.charAt(i); //System.out.print(c);
        if(c != ')') stack.push(c);
        else {
            char top = stack.peek();
            while(top != '(') {
                if(top == ' ') {
                    if(sb.length() > 0) {
                        Label label = new CoreLabel();
                        label.setValue(sb.toString()); sb.delete(0, sb.length());
                        ll.add(new LabeledScoredTreeNode(label));
                    }
                    //System.out.println(ll.get(ll.size() - 1).toString());
                    stack.pop();
                }
                else {
                    sb.insert(0, stack.pop());
                }
                top = stack.peek();
            }
            stack.pop();
            //for(Tree t1 : ll) { t1.pennPrint(); }
            if(sb.length() > 0) {
                Label label = new CoreLabel();
                label.setValue(sb.toString());
                sb.delete(0, sb.length());
                Tree nt = new LabeledScoredTreeNode(label);
                //System.out.println(nt.toString());
                if(!ll.isEmpty()) {
                    for (Tree t1 : ll) { nt.addChild(t1); }
                    ll.clear(); ll1.add(nt); //System.out.println(nt.toString());
                }
                else {
                    //for(Tree t1 : ll1) { nt.addChild(t1); }
                    //ll1.clear(); ll1.add(nt); 
                    Tree t1, t2;
                    t1 = ll1.remove(ll1.size() - 1);
                    if(ll1.size() > 0) {
                        t2 = ll1.remove(ll1.size() - 1);
                        nt.addChild(t2); 
                    }
                    nt.addChild(t1); ll1.add(nt);
                    //System.out.println(nt.toString());
                }
            }
        }
        
    }
    Tree t = ll1.get(0); //System.out.println("Custom");
    //t.pennPrint();
    //System.out.println("Raw Words: " + rawWords);
    Tree parse = lp.apply(rawWords); //parse.indentedXMLPrint();
    parse.pennPrint();
    System.out.println();
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    //System.out.println(tdl);
    //for(TypedDependency td : tdl) System.out.println(td.toString());
    System.out.println("TREEPRINT");
    TreePrint tp = new TreePrint("typedDependencies");
    tp.printTree(parse);
}
// Parse the sentence
// Create a LabelFactory, Label
// Take reference from Tree's source to convert sentence to Tree
/*List<Label> ll = new ArrayList();
 for(String a : sent) {
     Label l = new Label() {
         String value;
         @Override
         public String value() {
             return this.value;
             //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }

         @Override
         public void setValue(String value) {
             throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }

         @Override
         public void setFromString(String labelStr) {
             this.value = labelStr;
             //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }

         @Override
         public LabelFactory labelFactory() {
             throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }
     };
     l.setFromString(a);
     ll.add(l); System.out.println(l.value());
 }
 final TreeFactory tf = new LabeledScoredTreeFactory();
 Tree t = new LabeledScoredTreeNode();
 for(Label l : ll) {
     t.setLabel(l); 
 } 
 for(Label l : parse) {
     System.out.println(l.value());
 }*/     
    
 }