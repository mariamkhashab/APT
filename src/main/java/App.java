import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;

import Models.Website;

public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, JSONException
    {
        System.out.println( "Hello World!" );
        QueryProcessor queryProcessor = new QueryProcessor();
        Website w = new Website();
        Indexer indexer = new Indexer();
        w.setURL("https://www.geeksforgeeks.org/");
        w.setStatus(2);
        
        // Test QueryProcessing
        queryProcessor.insertWebpage(w);
        w = queryProcessor.getWebpage("https://www.geeksforgeeks.org/");
        System.out.println(w.getURL());

        indexer.preprocessing(w);
    }
    

}
