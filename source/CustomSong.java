import java.util.*; 
import java.io.File;


public class CustomSong
{
    String artist;
    String title;
    double duration;
    int rating; 
    int totalListens;
    String source;
    String genre;
    String album = null;
    
    /**
     * This setter allows other classes to call it in order to set all the necessary attributes of the object of custom song
     * most attributes are of regular data types outside the songSource which is of class file as it is a source file for the music.
     * This function also allow to pass empty string for song album in which case the song will be saved as a single
       */
    public void setSongAttributes(String songName, double songDuration, int songRating, String songAlbum, String artistName, String songGenre, int listensNum, String songSource)
    {
        artist = artistName;
        title = songName;
        duration = songDuration;
        rating = songRating;
        totalListens = listensNum;
        source = songSource;
        genre = songGenre;
        if(songAlbum.equals(""))
        {
            album = "Single";
        } else
        {
            album = songAlbum;
        }
    }

    public void incrementListensNum()
    {
        totalListens++;
    }
    
    /**
     * Overwrites the default toString function to return the string with all the attributes of this class separated by comma
       */
    public String toString()
    {
        String allData = artist+","+title+","+duration+","+rating+","+totalListens+","+source+","+genre+","+album;
        return allData;
    }
   
}
