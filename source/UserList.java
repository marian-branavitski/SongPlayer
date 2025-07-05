import java.io.*;
public class UserList
{
    User[] allUsers = new User[100];
    int position = 0;
    
    /**
     * Function takes an object of class User and adds it to the next available location in the array. The position counter is then incremented to point at the next available position
     * in the array
       */
    public void addUserToArray(User tempUser)
    {
        allUsers[position] = tempUser;
        position++;
    }
    
    /**
     * Function uses object of class file writer to write every element of the array to the file as a single line. To convert each object into a string value that can be written
     * a toString function is used which returns all the attributes of the object stored at the 'i' position of the array as a single string separated by commas. Before that the 
     * encryptUserAttribute function will be called as all the attributes provided will be in plain text initially, thsi ensures all sensetive data is encrypted before being saved
     * to the file
       */
    public void writeAllUsersToFile(){
        
        try{
            FileWriter fw = new FileWriter("AllUsers.txt");
            for(int i=0; i<position; i++)
            {
                User tempUser = allUsers[i];
                tempUser.encryptUserAttributes();
                fw.write(tempUser.toString());
                fw.write("\r\n");
            }
            fw.write("EOF");
            fw.close();
        } catch(Exception e)
        {
            ;
        }
    }
    
    /**
     * By using object of class buffered reader the function reads each line of the file, splits it by commas, and assigns each attribute to one temporary object of class User. Since most of the 
     * attributes saved in file will be encrypted, the temporary user object uses a decrypt user attributes method to decrypt the object's attributes before adding it to the global array. 
       */
    public void readUsersFromFile()
    {
        try{
            BufferedReader br = new BufferedReader(new FileReader("AllUsers.txt"));
            String line = br.readLine();
            if(line != null && line.equals("EOF") == false)
            {
                while(line.equals("EOF") == false)
                {
                    User tempUser = new User();
                    String[] splitLine = line.split(",");
                    tempUser.firstName = splitLine[0];
                    tempUser.lastName = splitLine[1];
                    tempUser.email = splitLine[2];
                    tempUser.userId = splitLine[3];
                    tempUser.prefferGoBy = splitLine[4].charAt(0);
                    tempUser.login = splitLine[5];
                    tempUser.password = splitLine[6];
                    
                    tempUser.decryptUserAttributes();
                    allUsers[position] = tempUser;
                    position++;
                    
                    line = br.readLine();
                    if(line.equals("EOF"))
                    {
                        break;
                    }
                }
            }
            br.close();
        } catch(Exception e)
        {
            ;
        }
    }
    
    /**
     * Function first clears the array from any previous usages/ modifications. Then the array is repopulated with the values from the file by calling a function readUsersFromFile. A rouge value
     * is set to the foundAt variable which if returned will tell the other classes that no user was found with the parameters passed to the function meaning the credentials entered must be wrong.
     * By iterating through the entire array and using a temporary user object to compare its attributes with the value of parameters, function can find a location in the global array that 
     * will have both parameters matching to the single user and return that value which can then be used to pull that user from the array and use it later on in the program to provide relevant
     * GUI and files
       */
    public int authenticateUser(String providedLogin, String providedPassword)
    {
        allUsers = new User[100];
        position = 0;
        readUsersFromFile();
        int foundAt = -1;
        for(int i=0; i<position; i++)
        {
            User tempUser = allUsers[i];
            if(tempUser.login.equals(providedLogin) && tempUser.password.equals(providedPassword))
            {
                foundAt = i;
                break;
            }
        }
        
        return foundAt;
    }
    
    /**
     * This function iterates through the array trying to find the position of the user account that will have a matching email provided as a parameter to this function. If there is a match
     * the location index is saved as a index variable which is then returned allowing to prove the user with such email exists when reseting the password and allow to pull more information
     * by pulling an object of class User from the array at the location returned by this function. Initially the index value is set to a rouge value so if it is returned other classes will
     * know that the user with provided email does not exist
       */
    public int findUserByMail(String mail)
    {
        int index = -1;
        for(int i=0; i<position; i++)
        {
            User tempUser = new User();
            tempUser = allUsers[i];
            if(tempUser.email.equals(mail))
            {
                index = i;
            }
        }
        
        return index;
    }
    
    /**
     * This function will recieve the object of class user, which is the updated version of the object as it will have a few attributes changed without changing the key ones like id, email or
     * login. Function will then use the findUserByEmail to find the location of this user in the global array, it will then overwrite the existing user with the new one and save all the changes
     * to the text file by calling a writeAllUsersToFile method.
       */
    public void changeUserAttributesAndSave(User tempUser)
    {
        int index = findUserByMail(tempUser.email);
        allUsers[index] = tempUser;
        writeAllUsersToFile();
    }
}
