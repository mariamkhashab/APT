import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;


import opennlp.tools.stemmer.PorterStemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Models.Website;

import org.json.JSONObject;
import org.json.JSONException;

public class Indexer {
    Database db = new Database();

    Hashtable<String,JSONObject> titlesDict;
    Hashtable<String,JSONObject> headingDict;
    Hashtable<String,JSONObject> textDict;

    List<String> all_words = new ArrayList<>();
    List<JSONObject> all_dicts= new ArrayList<>();
    int all_count = 0;
   
    
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
 
    }

    public String stem(String input)
    {
        PorterStemmer porterStemmer = new PorterStemmer();
        return porterStemmer.stem(input.toLowerCase());
    }

    public void preprocessing(Website website) throws IOException, URISyntaxException, JSONException{
        
        String url = website.getURL();
        //get document
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36").get();
       
        //extract title elements
        Elements titleelements = doc.select("title");
        String[] titlewords = titleelements.text().split(" ");

        //extract heading elements
        Elements Headingelements = doc.select("h1");
        doc.select("h2").forEach((e)->{
            Headingelements.add(e);
        });
        doc.select("h3").forEach((e)->{
            Headingelements.add(e);
        });
        doc.select("h4").forEach((e)->{
            Headingelements.add(e);
        });
        doc.select("h5").forEach((e)->{
            Headingelements.add(e);
        });
        doc.select("h6").forEach((e)->{
            Headingelements.add(e);
        });
        String[] Headingwords = Headingelements.text().split(" ");

        //extract text elements
        Elements Textelements = doc.select("p");
        doc.select("li").forEach((e)->{
            Textelements.add(e);
        });
        doc.select("pre").forEach((e)->{
            Textelements.add(e);
        });
        String[] Textwords = Textelements.text().split(" ");
       
        // length of document for normalization
        all_count+= titlewords.length;
        all_count+= Headingwords.length;
        all_count+= Textwords.length;

       //process titles, headings &text
        Hashtable<String,JSONObject> titlesDict  = processTitles(titlewords,doc,url);
        Hashtable<String,JSONObject> headingDict  = processHeadings(Headingwords,doc,url);
        Hashtable<String,JSONObject> textDict  = processtext(Textwords,doc,url);

       //merge processed words in 1 list and 1 dict
        for (String key : titlesDict.keySet()) {
            JSONObject jo= titlesDict.get(key);
            jo = jo.getJSONObject("url");
            all_words.add(key);
            all_dicts.add(jo);
        }
        for (String key : headingDict.keySet()) {
            JSONObject jo= headingDict.get(key);
            jo = jo.getJSONObject("url");
            all_words.add(key);
            all_dicts.add(jo);
        }
        for (String key : textDict.keySet()) {
            JSONObject jo= textDict.get(key);
            jo = jo.getJSONObject("url");
            all_words.add(key);
            all_dicts.add(jo);
        }
        
        // add words to database
        db.insertWords(all_words, all_dicts);
        all_count=0;

        //update website status to indexed
        website.setStatus(3);
        db.updateWebpage(website);
    }

    public Hashtable<String,JSONObject> processTitles( String[] words,Document doc,String url) throws URISyntaxException, MalformedURLException
    {
        Hashtable<String,JSONObject> dict = new Hashtable<>();

        List<String> processed= new ArrayList<String>();
        for (String word:words)
        {
            //BY DETECT SPACES BARDO (TABS)
            word = word.replaceAll("[^A-Za-z]", "");
            if(word!=""  && !stoppingWords.contains(word.toLowerCase())){
                processed.add(stem(word));
            }
        
        }

        for(String word: processed)
        {
            if(dict.get(word) != null){

                int termFreq;
                int titleFreq;
                try {

                    termFreq = dict.get(word).getJSONObject("url").getInt("termFreq")+1;
                    titleFreq = dict.get(word).getJSONObject("url").getInt("titleFreq")+1;
                    dict.get(word).getJSONObject("url").put("termFreq", termFreq);
                    dict.get(word).getJSONObject("url").put("titleFreq", titleFreq);
                    dict.get(word).getJSONObject("url").put("normalFreq", (float)termFreq/all_count);
                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }
            
            }else{
                JSONObject obj = new JSONObject();
                JSONObject j = new JSONObject();
               
                try {
                    obj.put("termFreq", 1);
                    obj.put("headingsFreq", 0);
                    obj.put("titleFreq", 1);            
                    obj.put("textFreq", 0);
                    obj.put("normalFreq", 0);
                    obj.put("url", url);
                    j.put("url", obj);
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dict.put(word, j);
               
            }
        }
        
        return dict;
    }

    public Hashtable<String,JSONObject> processHeadings(String[] words,Document doc,String url)
    {
       
        Hashtable<String,JSONObject> dict = new Hashtable<>();
        
     
        List<String> processed= new ArrayList<String>();
        for (String word:words)
        {
            //BY DETECT SPACES BARDO
            word = word.replaceAll("[^A-Za-z\t]", "");
            if(word!=""  && !stoppingWords.contains(word.toLowerCase())){
                processed.add(stem(word));
            }
            
         }
       
        for(String word: processed)
        {
            if(dict.get(word) != null){

                int termFreq;
                int headingsFreq;
                try {

                    termFreq = dict.get(word).getJSONObject("url").getInt("termFreq")+1;
                    headingsFreq = dict.get(word).getJSONObject("url").getInt("headingsFreq")+1;
                    dict.get(word).getJSONObject("url").put("termFreq", termFreq);
                    dict.get(word).getJSONObject("url").put("headingsFreq", headingsFreq);
                    dict.get(word).getJSONObject("url").put("normalFreq", (float)termFreq/all_count);
                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }
            
            }else{
                JSONObject obj = new JSONObject();
                JSONObject j = new JSONObject();
               
                try {
                    obj.put("termFreq", 1);
                    obj.put("headingsFreq", 1);
                    obj.put("titleFreq", 0);            
                    obj.put("textFreq", 0);
                    obj.put("normalFreq", 0);
                    obj.put("url", url);
                    j.put("url", obj);
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dict.put(word, j);
              
            }
       }
        
        return dict;
    }

    public Hashtable<String,JSONObject> processtext(String[] words, Document doc,String url) throws URISyntaxException, MalformedURLException
    {
        Hashtable<String,JSONObject> dict = new Hashtable<>();
       
        
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
                int textFreq;
                try {

                    termFreq = dict.get(word).getJSONObject("url").getInt("termFreq")+1;
                    textFreq = dict.get(word).getJSONObject("url").getInt("textFreq")+1;
                    dict.get(word).getJSONObject("url").put("termFreq", termFreq);
                    dict.get(word).getJSONObject("url").put("textFreq", textFreq);
                    dict.get(word).getJSONObject("url").put("normalFreq",(float) termFreq/all_count);
                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }
            
            }else{
                JSONObject obj = new JSONObject();
                JSONObject j = new JSONObject();
                try {
                    obj.put("termFreq", 1);
                    obj.put("headingsFreq", 0);
                    obj.put("titleFreq", 0);            
                    obj.put("textFreq", 1);
                    obj.put("normalFreq", 0);
                    obj.put("url", url);
                    j.put("url", obj);
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dict.put(word, j);
             
            }
        }
        

        return dict;
    }
}
