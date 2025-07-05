import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.io.BufferedReader;

public class CustomSongList
{
    CustomSong[] allSongs = new CustomSong[100];
    int position=0;
    
    /**
     * Function takes in an object of class CustomSong and adds it into the global array with all the songs of the instance of this class (individual playlist). Position counter 
     * is then incremented to point at the next available position
       */
    public void addCustomSongToList(CustomSong tempSong)
    {
        allSongs[position] = tempSong;
        position ++;
    }
    
    /**
       Function takes a playlist title and a user id as a parameter. First it will check if the specified user directory exists in the users folder
       getting the path to users/'user ID' and using the function exists of class Files which returns true if the path exists. In case
       the function returned true then the method checks for existance of text file with the name of the playlist. If such file already exist
       then the method will not allow to create another file with the same name. If such file does not exist then the method uses file writer object
       to write every object of class CustomSong in the array allSongs by iterating through the said array and creating a temporary object of 
       class custom song that will then be written to the file with the name passed as a playlist title. In the case of missing user directory
       the function will throw a warning stating that the user account has likely been corrupted
       */
    public boolean writeAllSongsToFile(String playlistTitle, String userID)
    {
        boolean fileNameTaken = false;
        Path userDir = Paths.get("users/"+userID);
        if(Files.exists(userDir))
        {
            Path playlistFile = Paths.get(userDir+"/"+playlistTitle+".txt");
            System.out.println("Directory exists");
            if(Files.exists(playlistFile))
            {
                System.out.println("File already exists, reconsider the name");
                fileNameTaken = true;
            } else
            {
                try{
                    FileWriter fw = new FileWriter("users/"+userID+"/"+playlistTitle+".txt");
                    
                    for (int i=0; i<position; i++)
                    {
                        CustomSong tempSong = allSongs[i];
                        fw.write(tempSong.toString());
                        fw.write("\r\n");
                    }
                    fw.write("EOF"); //Indicates the end of the file
                    fw.close();
                    
                    
                } catch(Exception e)
                {
                    System.out.println("Error with writing to file");
                }
            }
        } else
        {
            System.out.println("No directory");
        }
        return fileNameTaken;
    }
    
    /**
     * Function takes in 2 strings user id and playlist title. Both strings are used to identify the correct file and directory of the correct playlist of the apropriate user. First like the 
     * writing function, the read all function checks for the existance of the directory passed to the function by getting the path to the file through the use of class Path. Only if 
     * directory and file does exist, the function uses buffered reader to read each individual line, separate it by the comma sreating an array of values each value being an attribute of the 
     * object of class CustomSong. Temporary object of class Custom Song is then assigned with these values and is then added to the global array.
       */
    public void readAllSongsFromFile(String userID, String playlistTitle)
    {
        Path userDir = Paths.get("users/"+userID);
        if(Files.exists(userDir))
        {
            Path playlistFile = Paths.get(userDir+"/"+playlistTitle+".txt");
            if(Files.exists(playlistFile))
            {
                try{
                    String fileToRead = playlistFile+"";
                    BufferedReader br = new BufferedReader(new FileReader(fileToRead));
                    
                    allSongs = new CustomSong[100];
                    position = 0;
                    //Resets the array and the position to allow to fill the array with new data read from the playlist file
                    
                    String line = br.readLine();
                    if(line != null)
                    {
                        while(line.equals("EOF") == false)
                        {
                            String[] splitLine = line.split(","); // Splits the line by commas used later to add all attributes to custom song
                            CustomSong tempSong = new CustomSong();
                            
                            tempSong.artist = splitLine[0];
                            tempSong.title = splitLine[1];
                            tempSong.duration = Double.parseDouble(splitLine[2]);
                            tempSong.rating = Integer.parseInt(splitLine[3]);
                            tempSong.totalListens = Integer.parseInt(splitLine[4]);
                            tempSong.source = splitLine[5];
                            tempSong.genre = splitLine[6];
                            tempSong.album = splitLine[7];
                            
                            allSongs[position] = tempSong;
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
                    System.out.println("Error while reading the file");
                }
            }
        } else
        {
            System.out.println("Your user directory was not found");
        }
    }
    
    /**
     * This function uses a bubble sort. Outer loop iterates through the array taking each individual element, inner loop compares the taken element to the next element in the array if the
     * current object's rating is lower than the next object's; then the current song is saved to a temporary variable, the current location of the array is overwritten with the object found
     * in the next position and the next position is overwritten with the value from the temporary variable this way a swap is made and the swapMade variable is set to true, with each iteration
     * of the outer loop it is set to false, that way if the inner loop made no swaps the value will never change causing the function to break from the inner loop and move on to comparing the
     * next pair of values. Such use of variable reduces the time taken for the sort significantly improving the efficiency of this sorting function.
       */
    public void sortSongByRating()
    {
        boolean swapMade = false;
        for(int i=0; i<position; i++)
        {
            swapMade = false;
            for(int j=0; j<position-1; j++)
            {
                CustomSong currentSong = new CustomSong();
                currentSong = allSongs[j];
                
                CustomSong nextSong = new CustomSong();
                nextSong = allSongs[j+1];
                
                if(currentSong.rating < nextSong.rating)
                {
                    CustomSong tempSong = new CustomSong();
                    tempSong = currentSong;
                    allSongs[j] = nextSong;
                    allSongs[j+1] = tempSong;
                    swapMade = true;
                }
            }
            
            if(!swapMade)
            {
                break;
            }
        }
    }
    
    /**
     * This function takes in the string value used in the search as the search value. It uses the linear search to find any song by either it's title, artist name, album or its genre that way
     * the user do not need to specify the field they are attempting the search in which improves the ux as less interactions are needed to perform the desired action. By being a linear search 
     * the global array does not need to be sorted and the values could be of type string. The user also does not need to enter the full name of the song, artist, etc. Instead the function
     * compares the start of each value with the search term, making it easier to find the correct song. When the relevant to the search value result is found it is added to string variable 
     * all results, each result is separated by comma allowing all the results being returned at once by the function. 
       */
    public String findSongByStringValues(String searchTerm)
    {
        String searchResults = "";
        for(int i=0; i<position; i++)
        {
            CustomSong tempSong = allSongs[i];
            if(tempSong.title.startsWith(searchTerm) || tempSong.artist.startsWith(searchTerm) || tempSong.album.startsWith(searchTerm) || tempSong.genre.startsWith(searchTerm))
            {
                if(searchResults.equals("") == false)
                {
                    searchResults = searchResults+","+i;
                } else
                {
                    searchResults = searchResults+i+"";
                }
            }
        }
        
        return searchResults;
    }
    
    /**
     * The function reads the file with all the songs from the current playlist ensuring all the songs are added to the array first. The new song which is passed to the function as a parameter
     * is then added to the array after which the file with the playlist is overwritten with all the previous plus the new song by using the object of class file writer. 
       */
    public void addSongToExistingPlaylist(String userID, String playlistTitle, CustomSong songToAdd)
    {
        readAllSongsFromFile(userID, playlistTitle);
        allSongs[position] = songToAdd;
        position++;
        try
        {
            FileWriter fw = new FileWriter("users/"+userID+"/"+playlistTitle+".txt");
                    
            for (int i=0; i<position; i++)
            {
               CustomSong tempSong = allSongs[i];
               fw.write(tempSong.toString());
               fw.write("\r\n");
            }
            fw.write("EOF"); //Indicates the end of the file
            fw.close();
        } catch(Exception e)
        {
            System.out.println("An error writing to the file");
        }
    }
    
    /**
     * Function reads through the file of the playlist if the song name of the object of custom song does not match with the name passed
     * as a parameter, then that object is added to the temporary array. At the end there is an array with one of the elements from original array
     * missing (that is the deleted song object). This new temporary array is written to the file using basic file writer which causes the original
     * file to be overwritten, thus deleting the song user selected to delete
       */
    public void deleteSong(String userID, String playlistTitle, String songName)
    {
        readAllSongsFromFile(userID, playlistTitle);
        //Ensure the correct playlist is being edited as the array will be overwritten with the values of playlist and user id parameter file
        CustomSong[] tempArray = new CustomSong[position-1];
        int index = 0;
        for(int i=0; i<position; i++)
        {
            CustomSong tempSong = allSongs[i];
            if(tempSong.title.equals(songName)==false)
            {
               tempArray[index] = tempSong;
               index++;
            }
        }
        
        try{
            FileWriter fw = new FileWriter("users/"+userID+"/"+playlistTitle+".txt");
            
            for(int i=0; i<tempArray.length; i++)
            {
                fw.write(tempArray[i].toString());
                fw.write("\r\n");
            }
            fw.write("EOF");
            fw.close();
        } catch(Exception e)
        {
            System.out.println("Error while writing to the file");
        }
    }

    /**
     * Function takes in the user id and playlist title to overwrite as parameters. Using file writer the function
     * overwrites the existing playlist file with the new data items.*/
    public void overwriteFile(String userID, String playlistTitle)
    {
        Path filePath = Paths.get("users/"+userID+"/"+playlistTitle+".txt");
        if(filePath.toFile().exists())
        {
            try{
                FileWriter fw = new FileWriter(filePath+"");
                for (int i=0; i<position; i++)
                {
                    fw.write(allSongs[i].toString());
                    fw.write("\r\n");
                }
                fw.write("EOF");
                fw.close();
            } catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "An error writing to the file");
            }
        } else {
            JOptionPane.showMessageDialog(null, "File does not exist");
        }
    }

    /**
     * Increments the number of total listens of specified song object before overwriting that object to the array
     * of all songs. Calls function to overwrite the file with the new array data.*/
    public void changeListensNumOfSong(int index, String playlistTitle, String userID)
    {
        CustomSong tempSong = allSongs[index];
        tempSong.incrementListensNum();
        allSongs[index] = tempSong;
        overwriteFile(userID, playlistTitle);
    }
}
