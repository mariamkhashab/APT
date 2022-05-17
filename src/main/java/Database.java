import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.client.*;
import com.mongodb.client.model.*;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import Models.Website;
import Models.Word;

public class Database {

    private MongoClient mongoClient = null;
    MongoCollection<Document> webpagesCollection;
    MongoCollection<Document> indexerCollection;


    public void mongoConnect() throws Exception {
        try {

            if (mongoClient == null) 
            {
                mongoClient = MongoClients.create("mongodb+srv://admin:admin@searchengine.g39y2.mongodb.net/SearchEngine?retryWrites=true&w=majority");
            }
        } catch (Exception e) {
            throw e;
        }
    }
    public boolean createWebsites(LinkedList<String> url, int status) {
        try {
            mongoConnect();
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true);

            List<WriteModel<Document>> updates = new ArrayList<WriteModel<Document>>();
            for (int i = 0; i < url.size(); i++) {
                Bson update = Updates.setOnInsert("status", status);
                updates.add(new UpdateOneModel<Document>(new Document("url", url.get(i)).append("content",""), update,
                        new UpdateOptions().upsert(true)));
            }
            // com.mongodb.bulk.BulkWriteResult bulkWriteResult =
            crawlerCollection.bulkWrite(updates);

            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }
    public synchronized boolean updateStatus(String url, int status) {

        try {

            mongoConnect();
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            Bson queryFilter = Filters.eq("url", url);
            Bson updateFilter = Updates.set("status", status);
            Document result = crawlerCollection.findOneAndUpdate(queryFilter, updateFilter);
            if (result == null) {

                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public synchronized boolean updateContent(String url, String content) {

        try {

            mongoConnect();
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            Bson queryFilter = Filters.eq("url", url);
            Bson updateFilter = Updates.set("content", content);
            Document result = crawlerCollection.findOneAndUpdate(queryFilter, updateFilter);
            if (result == null) {

                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }
    public  LinkedList<String> getWebsitesByStatus(int status) {
        try {
            mongoConnect();
            Bson filter = Filters.eq("status", status);
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            FindIterable<Document> websites = crawlerCollection.find(filter);
            LinkedList<String> urls = new LinkedList<String>();
            for (Document doc : websites) {
                urls.add(doc.getString("url"));
            }

            return urls;
        } catch (Exception e) {
            System.out.println(e.toString());

        }
        return null;

    }

    public  LinkedHashSet<String> getWebsitesContents() {
        try {
            mongoConnect();
            Bson filter = Filters.ne("content", "");
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            FindIterable<Document> websites = crawlerCollection.find(filter);
            LinkedHashSet<String> contents = new LinkedHashSet<String>();

            for (Document doc : websites) {
                contents.add(doc.getString("content"));
            }

            return contents;
        } catch (Exception e) {
            System.out.println(e.toString());

        }
        return null;

    }


    public  LinkedList<String> getWebsites() {
        try {
            mongoConnect();
            MongoDatabase mDatabase = mongoClient.getDatabase("SearchEngine");
            MongoCollection<Document> crawlerCollection = mDatabase.getCollection("webpages");
            FindIterable<Document> websites = crawlerCollection.find();

            LinkedList<String> webpages = new LinkedList<>();

            for (Document doc : websites) {
                webpages.add(doc.getString("url"));
            }

            return webpages;
        } catch (Exception e) {
            System.out.println(e.toString());

        }
        return null;

    }


    public void insertWebpage(Website website) {
        try {
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            webpagesCollection = db.getCollection("webpages");
            Document doc = new Document();
            doc.append("url", website.getURL());
            doc.append("status", website.getStatus());
            webpagesCollection.insertOne(doc);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void updateWebpage(Website website) {
        try {
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            webpagesCollection = db.getCollection("webpages");
            Document doc = new Document();
            doc.append("url", website.getURL());
            doc.append("status", website.getStatus());
            webpagesCollection.updateOne(Filters.eq("_id", website.getID()), new Document("$set", doc));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public Website getWebpage(String url) {
        try {
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            webpagesCollection = db.getCollection("webpages");
            Document doc = webpagesCollection.find(Filters.eq("url", url)).first();
            if (doc != null) {
                Website website = new Website();
                website.setID(doc.getObjectId("_id").toString());
                website.setURL(doc.getString("url"));
                website.setStatus(doc.getInteger("status"));
                return website;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public Website getWebpageByID(String id) {
        try {
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            webpagesCollection = db.getCollection("webpages");
            Document doc = webpagesCollection.find(Filters.eq("_id", id)).first();
            if (doc != null) {
                Website website = new Website();
                website.setID(doc.getObjectId("_id").toString());
                website.setURL(doc.getString("url"));
                website.setStatus(doc.getInteger("status"));
                return website;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }


    public void addWord(Website website, List<Word> words,List<JSONObject> dicts)
    {
        
        try {
          
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            indexerCollection = db.getCollection("indexer");
            webpagesCollection = db.getCollection("webpages");
            for (Word word: words)
            {
                if (!(word.getURLS()).contains(website.getURL()))
                {
                    word.addURL(website.getURL());
                }
               
               
            }

            updateDF(words);
           

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public void updateDF( List<Word> words)
    { 
        for (Word word: words)
        {
            word.setDF(word.getURLS().size());
        }

    }
}