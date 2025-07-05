
public class User extends EncryptionAndHashing
{
    String firstName;
    String lastName;
    String email;
    String userId;
    char prefferGoBy;
    String login;
    String password;
    
    /**
     * This function allows to set the attributes to this object by passing their value as parameters. When the user is being created this values will be unencrypted
     * when the program will read the users from the file the attributes provided will be encrypted
       */
    public void setUserAttributes(String name, String surname, String emailAddress, String userID, char preferedCall, String userLogin, String pass)
    {
        firstName = name;
        lastName = surname;
        email = emailAddress;
        userId = userID;
        prefferGoBy = preferedCall;
        login = userLogin;
        password = pass;
    }
    
    /**
     * This function calls encryptPlainText function on the sensetive attributes: name, email, etc. and applies a hashPassword function to the password attribute which will 
     * return the memory location of the password in the hash table
       */
    public void encryptUserAttributes()
    {
        firstName = encryptPlainText(firstName);
        lastName = encryptPlainText(lastName);
        email = encryptPlainText(email);
        userId = userId;
        prefferGoBy = prefferGoBy;
        login = encryptPlainText(login);
        password = hashPassword(password);
    }
    
    /**
     * Decrypt function is used to decrypt all the attributes and get the password from the hash table. This funtion is used when adding all of the users to the array from the file
     * as the attributes stored in file are going to be encrypted
       */
    public void decryptUserAttributes()
    {
        firstName = decryptCipherText(firstName);
        lastName = decryptCipherText(lastName);
        email = decryptCipherText(email);
        userId = userId;
        prefferGoBy = prefferGoBy;
        login = decryptCipherText(login);
        readHashTableFile();
        password = hashTableArray[Integer.parseInt(password)];
    }
    
    /**
     * This function changes the password to the new value passed as the parameter. Since the value is plain text the new password will need to be hashed which could be done by
     * setting all the attributes as plain text before calling the encryption function
       */
    public void changePassword(String newPassword)
    {
        password = newPassword;
    }
    
    /**
     * This function is used to allow easily change attributes that are not as important. This is used instead of setAttributes as it decreases the chances of changing primary data 
     * used to identify the account like the id.
       */
    public void changeUnessentialAttributes(String name, String surname, char addressBy)
    {
        firstName = name;
        lastName = surname;
        prefferGoBy = addressBy;
    }
    
    /**
     * Overwrites the default toString function to return all the attributes as a single string separated by commas. 
       */
    public String toString()
    {
        String allData = firstName+","+lastName+","+email+","+userId+","+prefferGoBy+","+login+","+password;
        return allData;
    }
}
