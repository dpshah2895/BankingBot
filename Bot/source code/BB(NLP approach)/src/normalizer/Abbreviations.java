/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package normalizer;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 *
 * @author Kiner B. Shah
 * @author Mohit S. Shetty
 * @author Darshan P. Shah
 */
public class Abbreviations {
    private HashMap<String, String> hmap;
    public Abbreviations() {
        String csvpath = "C:\\Users\\user\\Documents\\NetBeansProjects\\BB\\src\\normalizer\\sample.csv";
        String temp;
        this.hmap = new HashMap();
        try {
            BufferedReader r = new BufferedReader(new FileReader(csvpath));
            while((temp = r.readLine()) != null) {
                temp = temp.trim();
                if(temp.isEmpty()) continue;
                String[] data = temp.split(",");
                this.hmap.put(data[0], data[1]);
            }
        }
        catch(Exception e) { e.printStackTrace(); }
    }
    public HashMap<String,String> getAbbAndFullForm() { return this.hmap; }
    public String[] getAbbrv() {
        Set<String> s = this.hmap.keySet();
        Object[] o = s.toArray();
        String[] t = Arrays.copyOf(o, o.length, String[].class);
        return t;
    }
    public String[] getFullForm() {
        Collection<String> s = this.hmap.values();
        Object[] o = s.toArray();
        String[] t = Arrays.copyOf(o, o.length, String[].class);
        return t;
    }
}
