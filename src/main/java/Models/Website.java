package Models;

public class Website {
    private String _id;
    private String url;
    private int status;
    private String content;

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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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
}
