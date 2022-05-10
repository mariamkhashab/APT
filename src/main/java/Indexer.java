import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import opennlp.tools.stemmer.PorterStemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import org.json.JSONException;

public class Indexer {
    
    List<String> stoppingWords = new ArrayList<String>();
    
    Indexer()
    {
        // fill stoppingWords list from text file
        File file = new File("stoppingWords.txt");
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine())
            {
                stoppingWords.add(in.nextLine());
            }
            in.close();
        } catch (FileNotFoundException e) {
           
            e.printStackTrace();
        }
       // System.out.print(stoppingWords); 
    }

    public String stem(String input)
    {
        PorterStemmer porterStemmer = new PorterStemmer();
        return porterStemmer.stem(input.toLowerCase());
    }

    public void preprocessing(Website website) throws IOException{
        
        String url = website.getURL();
        Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
        //System.out.print(doc); 
        Hashtable<String,JSONObject> titlesDict  = processTitles(doc,url);
        
    }

    public Hashtable<String,JSONObject> processTitles( Document doc,String url)
    {
        Hashtable<String,JSONObject> dict = new Hashtable<>();
        Elements elements = doc.select("title");
        String[] words = elements.text().split(" ");
        System.out.println(elements);
        List<String> processed= new ArrayList<String>();
        for (String word:words)
        {
            //BY DETECT SPACES BARDO
            word = word.replaceAll("[^A-Za-z]", "");
            if(word!=""  && !stoppingWords.contains(word.toLowerCase())){
                processed.add(stem(word));
            }
        
        }
        for(String word: processed)
        {
            if(dict.get(word) != null){

                int termFreq;
                int titlefreq;
                try {

                    termFreq = dict.get(word).getJSONObject("url").getInt("termFreq")+1;
                    titlefreq = dict.get(word).getJSONObject("url").getInt("titlefreq")+1;
                    dict.get(word).getJSONObject("url").put("termFreq", termFreq);
                    dict.get(word).getJSONObject("url").put("frequency", titlefreq);

                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }
            
            }else{
                JSONObject obj = new JSONObject();
               
                try {
                    obj.put("termFreq", 1);
                    obj.put("headingsFreq", 0);
                    obj.put("titlefreq", 1);            
                    obj.put("textFreq", 0);
                    obj.put("url", url);
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dict.put(word, obj);
                // System.out.println(obj);
                // System.out.println(json); 
                // System.out.println("------------------------------ "); 
            }
        }
        
        return dict;
    }
}
