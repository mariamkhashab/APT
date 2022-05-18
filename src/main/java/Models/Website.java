package Models;

import java.util.ArrayList;

public class Website {
    private String _id;
    private String url;
    private int status;
    private String content;
    private ArrayList<String> links;

    public String getID()
    {
        return _id;
    }

    public String getURL()
    {
        return url;
    }

    public int getStatus()
    {
        return status;
    }

    public void setID(String id)
    {
        this._id = id;
    }

    public void setURL(String URL)
    {
        this.url = URL;
    }

    public void setStatus(int stat)
    {
        this.status = stat;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
    public String getContent()
    {
        return this.content;
    }
    public void setLinks(ArrayList<String> links)
    {
        this.links = links;
    }
    public ArrayList<String> getLinks()
    {
        return this.links;
    }

}
