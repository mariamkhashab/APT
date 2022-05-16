import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.json.JSONObject;

import Models.Website;

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


    public void insertWords(List<String> words,List<JSONObject> dicts)
    {
        
        try {
          
            mongoConnect();
            MongoDatabase db = mongoClient.getDatabase("SearchEngine");
            indexerCollection = db.getCollection("indexer");

            for (int i = 0; i < words.size(); i++)
            {
                Document doc = new Document();
                doc.append("word", words.get(i));
                Object o = BasicDBObject.parse(dicts.get(i).toString());
                DBObject dbObj = (DBObject) o;
                Document found = indexerCollection.find(Filters.eq("word", words.get(i))).first();
                if (found != null)
                {
                    doc.append("df", found.getInteger("df") + 1);
                    List<DBObject> appearancesList  = (List<DBObject>) found.get("metadata");
                    appearancesList.add(dbObj);
                    doc.append("metadata",appearancesList);
                    indexerCollection.updateOne(Filters.eq("word", words.get(i)), new Document("$set", doc));
                }
                else
                {
                    List<DBObject> appearancesList = new ArrayList<DBObject>();
                    appearancesList.add(dbObj);
                    doc.append("df", 1);
                    doc.append("metadata", appearancesList);
                    indexerCollection.insertOne(doc);
                }
            }
           

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}