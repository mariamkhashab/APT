import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class CrawlerandIndexerMain {




    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("==================Doodle Admin Panel==================");
        System.out.println("|                                                    |");
        System.out.println("|                 1- Start Crawling                  |");
        System.out.println("|                                                    |");
        System.out.println("|                 2- Start Indexing                  |");
        System.out.println("|                                                    |");
        System.out.println("======================================================");
        System.out.println("==================Enter Your Chocice:=================");

        String str= "";
        while(!str.equals("1")  && !str.equals("2")){
            str = sc.nextLine();
        }

        if(str.equals("1")){


//            MyDatabaseConnection myDatabaseConnection = new MyDatabaseConnection();
//            myDatabaseConnection.initializeCrawlerData();
//            SeedsController seedsController = new SeedsController();
            /*
             * Initiall5izing database with seeds
             */
//            seedsController.loadSeedsToDatabase();



            System.out.print("Enter number of threads: ");

            int numberOfThreads = sc.nextInt();

            sc.close();

            Database DB = new Database();

            List<String> queue = new LinkedList<String>();
            List<String> temp_visited = new LinkedList<String>();
            Set<String> visited = new HashSet<String>();
            LinkedHashSet<String> urlsCompactStrings = new LinkedHashSet<String>();
            temp_visited = DB.getWebsitesByStatus(2);
            queue = DB.getWebsitesByStatus(0);
            urlsCompactStrings = DB.getWebsitesContents();
            urlsCompactStrings.add("RussRussMikhBut The The The The BuckThe ElisRaniElisRaniWhilManyThe The StorEmaiSendFollWhy ");
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

            System.out.println("there are "+numberOfThreads +" threads and the Time taken to crawl 50 is " + (System.currentTimeMillis() - beforeTime) / 1000 + " seconds.");
        }
        else {
            // indexer code
//            DB.createWebsites()  will return list of all urls websites in the database 
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


