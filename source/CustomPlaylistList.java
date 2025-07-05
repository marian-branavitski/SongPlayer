import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.io.BufferedReader;

public class CustomPlaylistList
{
    CustomPlaylist[] allPlaylists = new CustomPlaylist[100];
    int position = 0;
    
    /**
     * This function takes in the object of class custom playlist as a parameter. Function then adds the object to the next available location in the array. The position counter is then
     * incremented to point to the next available location
       */
    public void addPlaylistToArray(CustomPlaylist tempPlaylist)
    {
        allPlaylists[position] = tempPlaylist;
        position++;
    }
    
    /**
     * The function takes user id as a parameter. Each user has a unique folder that has the same name as their id in the users directory found at the root of this module. Function first
     * ensures the folder and the file that stores all of the playlists created by the user exist as if it doesn't exist than the user directory has likely been corrupted or modified 
     * which could lead to further problems, so the function will notify about the missing directory if it does not find the correct one. If the directory does exist an object of class
     * Buffered Reader is used to read each individual line of the file until the end indicated by 'EOF' is reached. Each line is then splitted by the commas which creates an array with
     * all the attributes of the object of class custom playlist. Temporary object is created with these attributes that is then assigned to the global array allPlaylists, representing every
     * playlist created by the user. 
       */
    public void readDataFromPlaylistFile(String userID)
    {
        if(Files.exists(Paths.get("users/"+userID+"/allPlaylists.txt")))
        {
            try{
                BufferedReader br = new BufferedReader(new FileReader("users/"+userID+"/allPlaylists.txt"));
                String line = br.readLine();
                
                
                if(line != null && line.equals("EOF")==false)
                {
                    while(line.equals("EOF") == false)
                    {
                        String[] splitLine = line.split(",");
                        CustomPlaylist tempPlaylist = new CustomPlaylist();
                        
                        tempPlaylist.title = splitLine[0];
                        tempPlaylist.coverSource = splitLine[1];
                        
                        allPlaylists[position] = tempPlaylist;
                        position++;
                        
                        line = br.readLine();
                        if(line.equals("EOF"))
                        {
                            break;
                        }
                    }
                    
                    br.close();
                }
            } catch(Exception e)
            {
                System.out.println("Cannot read the file with all the playlists "+e);
            }
            
        } else
        {
            System.out.println("User directory might be corrupted, please check it.");
        }
    }
    
    /**
     * The function first reads from the file to ensure all of the previously saved playlists are loaded in correctly before any new playlists are saved. Then using an object of class file writer
     * the function writes each individual element of the global array to the file with all the playlists. By using overwritten toString function each element returns a string of attributes
     * separated by commas.
       */
    public void writeAllPlaylistsToFile(String userID)
    {
        readDataFromPlaylistFile(userID);
        try{
            FileWriter fw = new FileWriter("users/"+userID+"/allPlaylists.txt");
            for(int i=0; i<position; i++)
            {
                fw.write(allPlaylists[i].toString());
                fw.write("\r\n");
            }
            fw.write("EOF");
            fw.close();
        } catch(Exception e)
        {
            System.out.println("Error while writing to file");
        }
    }

    /**
     * Function creates a temporary array of all the playlists, it will write all the playlists from the global array
     * to this array if the title of the playlist does not match the title passed to the parameter. This way a new
     * array will  be created which will be used to overwrite the file with all the playlists of the user with all the
     * existing playlists without the one that was chosen to be deleted. The function then tries to delete the file
     * of that particular playlist, if any error was encountered a message will be displayed in GUI to the user through
     * the use of the pop up window*/
    public void deletePlaylist(String userID, String playlistName){
        CustomPlaylist[] tempPlaylistArray = new CustomPlaylist[position-1];
        int index = 0;

        for(int i=0; i<position; i++){
            CustomPlaylist tempPlaylist = allPlaylists[i];
            if(tempPlaylist.title.equals(playlistName) == false)
            {
                tempPlaylistArray[index] = tempPlaylist;
                index++;
            }
        }

        try {
            FileWriter fw = new FileWriter("users/"+userID+"/allPlaylists.txt");
            for (int i=0; i<index; i++){
                fw.write(allPlaylists[i].toString());
                fw.write("\r\n");
            }
            fw.write("EOF");
            fw.close();

            File playlistFile = new File("users/"+userID+"/"+playlistName+".txt");
            if(playlistFile.delete()){
                JOptionPane.showMessageDialog(null, "Playlist "+playlistName+" has been deleted.");
            } else {
                JOptionPane.showMessageDialog(null, "Playlist "+playlistName+" could not be deleted.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while writing to file with all the playlists "+e);
        }


    }

}
