import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );

        Website w = new Website();
        Indexer indexer = new Indexer();
    w.setID(0);
    w.setURL("https://www.geeksforgeeks.org/");
    w.setStatus(2);
    indexer.preprocessing(w);
    }
    

}
