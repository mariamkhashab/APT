import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;

import Models.Website;

public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, JSONException
    {
        System.out.println( "Hello World!" );
        Database database = new Database();
        Website w = new Website();
        Indexer indexer = new Indexer();
        w.setURL("https://stackoverflow.com/questions/56539582/how-to-insert-json-data-into-database");
        w.setStatus(2);

        //this function will loop over each website
        indexer.preprocessing(w);
        
        // Test QueryProcessing
        //queryProcessor.insertWebpage(w);
        // w = queryProcessor.getWebpage("https://www.geeksforgeeks.org/");
        // System.out.println(w.getURL());

        
    }
    

}
