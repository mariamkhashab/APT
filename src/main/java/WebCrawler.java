import java.io.*;
//import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.*;
//import java.util.logging.Logger;


enum STATUS {
    UNTAKEN, TAKEN, CRAWLED
}

public class WebCrawler extends Thread implements Runnable{


    private static int check = 0;
    final private static int maxPages = 5000;
    private Set<String> Visited;
    private List<String> queue;
    private LinkedHashSet<String> urlsCompactStrings ;

    Database DB;





    public WebCrawler(Set<String> Visited,List<String> queue,LinkedHashSet<String> urlsCompactStrings,Database DB){
        this.queue = queue;
        this.Visited = Visited;
        this.urlsCompactStrings = urlsCompactStrings;
        this.DB = DB;
    }

    @Override
    public void run()
    {

        try
        {

            webCrawl();
        }
        catch (IOException ex)
        {

        }

    }

    public void webCrawl() throws IOException,MalformedURLException,HttpStatusException {
        {
            String currentUrl = null;

            while (true) {
	            synchronized (Visited) {
	                if (Visited.size() > maxPages){
	                    System.out.println("max reached");
	                    break;
	                }
	            }
                currentUrl = null;
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        synchronized (Visited) {
                            try {
                                String nextUrl = queue.remove(0);
                                currentUrl = Visited.contains(nextUrl) ? null : nextUrl;

                            } catch (Exception ignored) {

                            }
                            if (currentUrl != null) {
                                Visited.add(currentUrl);
                                DB.updateStatus(currentUrl,STATUS.CRAWLED.ordinal());

                            }
                        }
                    }
                }
                if (currentUrl != null) {
                    String content = getCompactContent(currentUrl);
                    if (urlsCompactStrings.contains(content)) {
//                        System.out.println("skipped url "+currentUrl+" its content "+content);
                        continue;
                    }
                    else
                    {
                        urlsCompactStrings.add(content);
                        LinkedList<String> links = new LinkedList<String>();
                        links = Scrape(currentUrl);
                        synchronized (queue) {
                            queue.addAll(links);
                        }
                        DB.createWebsites (links, STATUS.UNTAKEN.ordinal());
                        DB.updateContent(currentUrl,content);
                        DB.updateWebsiteHrefs(currentUrl,links);
                    }
                }
            }

        }
    }


    public static LinkedList<String> Scrape(String url) {
        LinkedList<String> links = new LinkedList<String>();
        try {

            Document htmlDocument = Jsoup.connect(url).get();
            Elements hyperLinks = htmlDocument.select("a[href]");
            for (Element link : hyperLinks) {
                String urlString = link.absUrl("href");
                URL url1 = new URL(urlString);


                if (!url1.getProtocol().equals("http") && !url1.getProtocol().equals("https"))
                    continue;
                String path = url1.getPath();
                if (path == null || path.isEmpty() || path.equals("/index.html") || path.equals("/#"))
                    path = "/";
                URI link1 = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost().toLowerCase(), url1.getPort(), path,
                        url1.getQuery(), url1.getRef());

                /*
                Check for robots and similar webpage
                */
                    if (!robotManager.isAllowed(link1.toURL())) {
//                        System.out.println("**** disallowed link = "+url1);
                        continue;
                }

                links.add(urlString);

            }
        } catch (MalformedURLException e){

        } catch (IOException ioe) {
            System.out.println("Error in the HTTP request " + ioe);
        } catch (PatternSyntaxException e) {
            System.out.println("Regex error " + e);
        } catch (IllegalArgumentException e) {

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return links;
    }


    public String getCompactContent(String url) throws IOException
    {
        String compContent="";
        boolean checkurl = isUrlValid(url);

        if(checkurl) {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            Elements MyElements=doc.select("p");

            for(Element MyElement: MyElements)
            {
                if(MyElement.text().length()>4)
                    compContent+=MyElement.text().substring(0, 4);
            }
        }

        return compContent;
    }
    public boolean isUrlValid(String url) {
        try {
            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }


}