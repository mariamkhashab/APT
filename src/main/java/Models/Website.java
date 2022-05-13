package Models;

public class Website {
    private String _id;
    private String url;
    private int status;

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
}
