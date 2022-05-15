package Models;

import java.util.ArrayList;
import java.util.List;

public class Word {
    
    private String _id;
    private String doc_id;
    private String word;
    private int df=0;
    private List<String> urls = new ArrayList<>();
   

    public String getID()
    {
        return _id;
    }
    public void setID(String id)
    {
        this._id = id;
    }

    // public String getdocID()
    // {
    //     return doc_id;
    // }
    // public void setdocID(String id)
    // {
    //     this.doc_id = id;
    // }

    public int getDf() {
        return df;
    }

    public void setDF(int docFreq) {
        df = docFreq;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String w)
    {
        word = w;
    }

    public List<String> getURLS()
    {
    return urls;
    }

    public void addURL(String u) {
        urls.add(u);
    }

}
