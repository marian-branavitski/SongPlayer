
public class CustomPlaylist
{
    String title;
    String coverSource;
    
    /**
     * This is a public setter that allows all other classes in this module set attributes to each instance of this class. This setter has a selection in it
     * that checks one of the parameter - playlistCoverSource. In case user did not provide the source of the image for the playlist then the playlist will  be assigned 
     * with the default playlist image cover located in the assets
       */
    public void setAttributes(String playlistTitle, String playlistCoverSource)
    {
        title = playlistTitle;
        if(playlistCoverSource.equals("") || playlistCoverSource == null)
        {
            coverSource = "assets/no_cover.png";
        } else
        {
            coverSource = playlistCoverSource;
        }
    }
    
    /**
     * Overwrites the existing toString function to return a string of all the attributes of this class separated by comma. This is used in storing the data about the objects of this class
       */
    public String toString()
    {
        String allData = title+","+coverSource;
        return allData;
    }
}
