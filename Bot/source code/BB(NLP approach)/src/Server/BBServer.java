/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/**
 *
 * @author Kiner Shah
 */
public class BBServer implements BBInterface {
    // accessNormalizer
    public String accessNormalizer(String text) {
        normalizer.Normalizer n = new normalizer.Normalizer(text);
        String temp[] = n.tokenize(), fsent = n.normalize(temp[0], "<S>") + " "; // fsent - final sentence
        for(int i = 1; i < temp.length; i++) {
            fsent = fsent + n.normalize(temp[i], temp[i - 1]) + " ";
        }
        return fsent;
    }
    // accessPOSTagger
    public void accessPOSTagger(String s[]) {
        POSTagger.TagAssigner ta = new POSTagger.TagAssigner();
        //System.out.println(ta.hashCode());
        try { ta.loadModelAndTagSentence(s); }
        catch(IOException e) { e.printStackTrace(); }
    }
    // accessParser
    public void accessParser(String s[]) {
        try {
            Parser.PCYKParser parser = new Parser.PCYKParser();
            String res = parser.parse(s);
            Parser.ParserDemo depparser = new Parser.ParserDemo();
            depparser.convertToDependencies(res);
        }
        catch(IOException e) { e.printStackTrace(); }
    }
    public static void main(String[] args) {
        try {
            BBServer bbs = new BBServer();
            int port = 6666;
            BBInterface stub = (BBInterface) UnicastRemoteObject.exportObject((Remote) bbs, port);
            Registry registry = LocateRegistry.createRegistry(6666);
            registry.bind("BBInterface", (Remote) stub);
        }
        catch(Exception e) { e.printStackTrace(); }
    }
}