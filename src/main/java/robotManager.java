import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class robotManager {

    static LinkedHashMap<String, RobotRules> urls = new LinkedHashMap<String, RobotRules>();
    static String name = "Fa5orgy";

    public static void readRobotTxt(URL url) {

        // Intialize Rules and download robot.txt
        String robotTxtUrl = url.getProtocol() + "://" + url.getHost() + "/robots.txt";
        RobotRules rules = new RobotRules();
        urls.put(url.getHost(), rules);
        Document doc = null;
        // In case domain doesnt have robot.txt file 404 status code is returned
        try {
            doc = Jsoup.connect(robotTxtUrl).get();
        } catch (IOException e) {
            return;
        }
        String text = doc.body().text();

        // Get Index of Crawler Bots Group
        Pattern pattern = Pattern.compile("User-agent: " + robotManager.name);
        Matcher matcher = pattern.matcher(text);
        boolean isFound = matcher.find();
        if (!isFound) {
            pattern = Pattern.compile("User-agent: \\*");
            matcher = pattern.matcher(text);
            isFound = matcher.find();
        }
        try {

            // Get RobotsTXT Rules
            while (isFound) {
                String group[] = text.substring(matcher.start()).split(" ");
                int numberOfRules = 0;
                boolean groupFinished = false;
                for (int i = 0; i < group.length; i++) {
                    switch (group[i]) {
                        case "User-agent:":
                            if (numberOfRules != 0)
                                groupFinished = true;
                            i++;
                            break;
                        case "Disallow:":
                            numberOfRules++;
                            i++;
                            if (i < group.length)
                                if (!rules.addDisallowed(group[i])) {
                                    i--;
                                }
                            break;
                        case "Allow:":
                            numberOfRules++;
                            i++;
                            if (i < group.length)
                                if (!rules.addAllowed(group[i])) {
                                    i--;
                                }
                            break;
                    }
                    if (groupFinished)
                        break;
                }
                isFound = matcher.find();
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(url.toString() + " Is inValid");
        }

    }

    public static boolean isAllowed(URL url) {

        // Check if hostName was read before
        String hostName = url.getHost();
        if (!urls.containsKey(hostName))
            readRobotTxt(url);

        // Get Path
        String path = url.getPath();
        if (url.getQuery() != null && !url.getQuery().isEmpty())
            path += "?" + url.getQuery();

        RobotRules rule = urls.get(hostName);

        return rule.isAllowed(path);
    }

    public static void main(String[] args) {
        try{

            Document doc = Jsoup.connect("https://www.imdb.com/list/ls500936554/?ref_=watch_wchgd_1_5_m_wtw_disney_i").get();

            // System.out.println("Downloading "+Thread.currentThread().getName() +
            // urlString);
            // Download the page
            int i = 0;
            // Extract URLs
            Elements hrefs = doc.select("a");

            for (Element href : hrefs) {
                String absLink = href.attr("abs:href");
                if (absLink.length() == 0)
                    continue;
                URL temp = new URL(absLink);

                if (!temp.getProtocol().equals("http") && !temp.getProtocol().equals("https"))
                    continue;
                String path = temp.getPath();
                if (path == null || path.isEmpty() || path.equals("/index.html") || path.equals("/#"))
                    path = "/";
                URI link = new URI(temp.getProtocol(), temp.getUserInfo(), temp.getHost().toLowerCase(), temp.getPort(),
                        path, temp.getQuery(), temp.getRef());

                if (!robotManager.isAllowed(link.toURL())) {
                    System.out.println("link Disallowed: "+link.toASCIIString());
                    continue;
                }
                i++;
                System.out.println("link allowed: "+link.toASCIIString());

                // System.out.println(absLink+ " Allowed");
                String url = link.toASCIIString();
            }

            System.out.println("Number of new links " + i);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class RobotRules {
    LinkedList<Pattern> allowed = new LinkedList<Pattern>();
    LinkedList<Pattern> disallowed = new LinkedList<Pattern>();

    Boolean addAllowed(String path) {
        // Ignore Directive if path is empty
        if (path.isEmpty() || !path.startsWith("/"))
            return false;
        allowed.add(createPattern(path));
        return true;
    }

    Boolean addDisallowed(String path) {
        // Ignore Directive if path is empty
        if (path.isEmpty() || !path.startsWith("/"))
            return false;
        disallowed.add(createPattern(path));
        return true;
    }

    Pattern createPattern(String path) {
        return Pattern.compile(path.replace("*", ".*").replace("?", "\\?").replace("+", "\\+"));
    }

    boolean isDisallowed(String path) {

        // Check DisAllowed Patterns
        for (int i = 0; i < disallowed.size(); i++) {
            Matcher matcher = disallowed.get(i).matcher(path);
            if (matcher.find())
                return true;
        }
        return false;
    }

    boolean isAllowed(String path) {

        // Check If DisAllowed
        if (!this.isDisallowed(path)) {
            return true;
        }

        // Check Allowed Patterns
        for (int i = 0; i < allowed.size(); i++) {
            Matcher matcher = allowed.get(i).matcher(path);
            if (matcher.find())
                return true;
        }
        return false;
    }
}