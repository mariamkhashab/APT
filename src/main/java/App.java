import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONException;

import Models.Website;

public class App 
{
   
    public static void main( String[] args ) throws IOException, URISyntaxException, JSONException, InterruptedException
    {
        
            Database DB = new Database();
            Scanner sc = new Scanner(System.in);
            Indexer indexer = new Indexer();

            System.out.println("====================Search Engine=====================");
            System.out.println("|                 1- Start Crawling                  |");
            System.out.println("|                                                    |");
            System.out.println("|                 2- Start Indexing                  |");
            System.out.println("|                                                    |");
            System.out.println("==================Enter Your Chocice:=================");
    
            String str= "";
            while(!str.equals("1")  && !str.equals("2")){
                str = sc.nextLine();
            }
    
            if(str.equals("1")){
    
                System.out.print("Enter number of threads: ");
    
                int numberOfThreads = sc.nextInt();
    
                sc.close();
    
              
    
                List<String> queue = new LinkedList<String>();
                List<String> temp_visited = new LinkedList<String>();
                Set<String> visited = new HashSet<String>();
                LinkedHashSet<String> urlsCompactStrings = new LinkedHashSet<String>();
                temp_visited = DB.getWebsitesByStatus(2);
                queue = DB.getWebsitesByStatus(0);
                urlsCompactStrings = DB.getWebsitesContents();
                visited.addAll(temp_visited) ;
                System.out.println("queue len = "+queue.size()+" visited length "+temp_visited.size());


                if (queue.size() ==0 && visited.size() == 0)
                {
                    readSeedList(queue);
                    DB.createWebsites((LinkedList<String>) queue,0);
    
                }
    
    
    
                Thread[] threadArray;
    
                threadArray = new Thread[numberOfThreads];
                long beforeTime = System.currentTimeMillis();
                for (int i = 0; i < numberOfThreads; i++) {
                    threadArray[i] = new WebCrawler(visited,queue,urlsCompactStrings,DB);
                    threadArray[i].setName("Crawler "+ String.valueOf(i));
                    threadArray[i].start();
    
    
                }
                for (int i = 0; i < numberOfThreads; i++) {
                    threadArray[i].join();
                }
    
                System.out.println("there are "+numberOfThreads +" threads and the Time taken to crawl 5000 is " + (System.currentTimeMillis() - beforeTime) / 1000 + " seconds.");
            }
            else {
                // indexer code
                List<String> indexer_input = new LinkedList<String>();
                indexer_input =  DB.getWebsitesByStatus(2); // crawled but not indexed
                System.out.println(indexer_input);
                for (int i = 0 ; i <indexer_input.size(); i++ )
                {
                    Website w = DB.getWebpage(indexer_input.get(i));
                    indexer.preprocessing(w);
                }
            }
    
    



        
    }
    
    public static void readSeedList(List<String> queue) {
        try {
            File myObj = new File("seeds.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                queue.add(data);

//                System.out.println("url ==> "+data);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading seed list.");
            e.printStackTrace();
        }
    }


}
