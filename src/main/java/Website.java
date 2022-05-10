
public class Website {
    private int _id;
    private String url;
    private int status;

    public int getID()
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

    public void setID(int id)
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
