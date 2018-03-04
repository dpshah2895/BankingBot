import java.io.*;
import java.util.*;
class TaggerModel {
	public HashMap<String, Integer> transition;
    public HashMap<String, Integer> emit;
    public HashMap<String, Integer> context;
    private HashMap<String, Integer> makeChanges(HashMap<String, Integer> a, String b) {
        if(a.containsKey(b)) {
            int val = a.get(b);
            a.remove(b);
            a.put(b, val + 1);
        } 
        else a.put(b, 1);
        return a;
    }
	private void training(String fileName) throws IOException {
        
        //model_list = new ArrayList();
        //InputStream in = TagAssigner.class.getResourceAsStream("wiki-en-test.norm_pos");
        // InputStream in = TagAssigner.class.getResourceAsStream("05-train-input.txt");
        //BufferedReader r = new BufferedReader(new InputStreamReader(in));
        BufferedReader r = new BufferedReader(new FileReader("E:\\brown\\" + fileName));
        BufferedWriter w = new BufferedWriter(new FileWriter("C:\\Users\\user\\Desktop\\tagged_model.txt"));
        // String path = file.getAbsolutePath();
        // System.out.println(path);
        String line, previous;
        
        while((line = r.readLine()) != null) {
        	if(line.trim().isEmpty()) continue;
        	line = line.trim();
            previous = "<s>";       // previous tag (for starting tag there is no previous tag, so <s> is the previous tag)
            context = makeChanges(context, previous); // increment the counter for the previous tag 
            String split_line[] = line.split("\\s");
            for(String wt : split_line) {
                String wts[] = wt.split("[/]");
                if(wts.length == 0) { System.out.println(fileName); continue; }
                if(wts.length == 1) { System.out.println(fileName + "\t" + wts[0]); continue; }
                String word = wts[0];
                String tag = wts[1];
                transition = makeChanges(this.transition, previous + " " + tag); // increment the count for transition from previous tag to current tag
                context = makeChanges(this.context, tag);    // increment the counter for the current tag
                emit = makeChanges(this.emit, tag + " " + word); // increment the counter for emission of the current tag for the word
                previous = tag;
            }
            transition = makeChanges(this.transition, previous + " </s>"); // increment the transition from the last tag to tag after it(</s>)
        }
        for(Map.Entry<String, Integer> kv : this.transition.entrySet()) {
            String key = kv.getKey();
            int val = kv.getValue();
            String pw[] = key.split("\\s");
            if(pw.length == 0) { System.out.println(fileName); continue; }
            if(pw.length == 1) { System.out.println(fileName + "\t" + pw[0]); continue; }
            String prev = pw[0], word = pw[1];
            //System.out.println("T, " + key + ", " + (double) val / context.get(prev));
            w.append("T " + key + " " + (double) val / context.get(prev) + "\n");
            //Model new_model = new Model();
            //new_model.type = "T";
            //new_model.p = prev; new_model.n = word;
            //new_model.prob = (double) val / context.get(prev);
            //model_list.add(new_model);
        }
        for(Map.Entry<String, Integer> kv : this.emit.entrySet()) {
            String key = kv.getKey();
            int val = kv.getValue();
            String pw[] = key.split("\\s");
            if(pw.length == 0) { System.out.println(fileName); continue; }
            if(pw.length == 1) { System.out.println(fileName + "\t" + pw[0]); continue; }
            String prev = pw[0], word = pw[1];
            //System.out.println("E, " + key + ", " + (double) val / context.get(prev));
            w.append("E " + key + " " + (double) val / context.get(prev) + "\n");
            //Model new_model = new Model();
            //new_model.type = "E";
            //new_model.p = prev; new_model.n = word;
            //new_model.prob = (double) val / context.get(prev);
            //model_list.add(new_model);
        }
    }
	public static void main(String args[]) throws IOException {
		TaggerModel tm = new TaggerModel();
		tm.transition = new HashMap();     // for transition probabilities
        tm.emit = new HashMap();           // for emission probabilities
        tm.context = new HashMap();        // for tags/contexts
		File folder = new File("E:\\brown\\");
		File[] files = folder.listFiles(); int count = 0;
		for(File f : files) {
			if(f.isFile()) {
				//System.out.println("File: " + f.getName());
				tm.training(f.getName());
				System.out.println("Read file " + count);
				count++;
			}
		}
	}
}