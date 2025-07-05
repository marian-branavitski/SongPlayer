import java.io.*;

public class EncryptionAndHashing
{
    String[] hashTableArray = new String[999];
    int valuesInArray = 0;
    String[] hashedValues = new String[999];
    
    /**
     * Function recieves a string as a parameter (plain text), this string is then encoded into a sequence of bytes based on the platform's default charset the system is executed on. 
     * This sequence is then stored into an array of type byte with each element being an object of class Byte representing each individual character of the string. 
     * Function will then iterate through that array 2 times. Each individual byte undergoes bitwise XOR operation between itself and integer 67 (and 15 in the next loop). Before saving the 
     * outcome back to the array the result is casted back to a byte type as bitwise operations can result in values outside the byte range (0-255) so casting allows to prevent that. By doing
     * so each byte in the array becomes scrambled as XOR function gives 1 only when the bits are different (1 and 0). By the end of the first loop the byte array is a completely new sequence
     * of bytes causing the original text to become scrambled and unreadable (turns into cipher text), by performing another XOR operation but with a different integer (15) the data becomes
     * even more different to the original. What this does is essentially create 2 different layers of XOR encryption with 2 different XOR bytes. Real life system would have much more layers
     * than 2 and the XOR operation would likely be followed by some other reversable mathimatical function, this function acts as a simplified version of the real life symetric encryption with
     * the 2 XOR bytes acting as a key. 
       */
    public String encryptPlainText(String plainText)
    {
        byte[] plainTextBytes = plainText.getBytes();
        for(int i=0; i<plainTextBytes.length; i++)
        {
            plainTextBytes[i] = (byte) (plainTextBytes[i] ^ 67);
        }
        for(int i=0; i<plainTextBytes.length; i++)
        {
            plainTextBytes[i] = (byte) (plainTextBytes[i] ^ 15);
        }
        String encrypted = new String(plainTextBytes);
        return encrypted;
    }
    
    /**
     * Function recieves a string which supposed to be an encrypted message and applies the XOR bytes in reverse order to the order used in encryption. Since the XOR operation returns 1 only
     * when 2 bits are unique, by applying the same XOR byte twice the result is the original data before any operations were performed. After the 2 layers were applied, new object of class 
     * String is initialised which holds the value of the sequence of bytes in byte array. Since we performed the XOR in reverse order to previous function the sequence will now give a 
     * string equivalent to the one passed as a parameter in the first function essentially decrypting the cipher text.
       */
    public String decryptCipherText(String cipherText)
    {
        byte[] cipherTextBytes = cipherText.getBytes();
        for(int i=0; i<cipherTextBytes.length; i++)
        {
            cipherTextBytes[i] = (byte) (cipherTextBytes[i] ^ 15);
        }
        for(int i=0; i<cipherTextBytes.length; i++)
        {
            cipherTextBytes[i] = (byte) (cipherTextBytes[i] ^ 67);
        }
        String decrypted = new String(cipherTextBytes);
        return decrypted;
    }
    
    /**
     * The function uploads the hash table from the file to the global array. The hash file contain the value and the hashed address (array index) of each password. By taking in the string 
     * value as the password the function first creates a byte array of the password value which is a sequence of binary representing each character of the password. The function then iterates 
     * through that byte array adding each value to one string variable. As the result the end value might end up way too big for java to handle any calculations so if the lenght is more than
     * 17 the string value is cut by first counting the bits to remove and then using integer division whih truncates the value giving the indecies of charatcers to be removed from the end
     * and start of the string. After the new string is parsed into a value of type long which allows java to perform mathematical operations on the value. The hashing algorithm works by taking 
     * the sanatised value and dividing it by 13 this ensures the algorithm is less likely to cause collision (when 2 different values hash to the same value) in a real world this mathematical
     * function would have much more operation, but for the scope of this system a division by 13 and modular division by 123 is enough to prevent collisions. The modular operator is used
     * just like in real hashing algorithm as it is one of the irreversable mathematical operators, so knowing the output and the number used to modular divide by is not enough to give the 
     * output, unless the hashing table itself which contains the values get leaked. After the hash output has been calculated the value is checked against all known values in the hashing
     * table, if it does not exist than the value  is added to the array after which the value and its output is saved to the hash table file. 
       */
    public String hashPassword(String password)
    {
        readHashTableFile();
        
        String hashOutput="";
        byte[] inputBytes = password.getBytes();
        String allInputBytes = "";
        for(int i=0; i<inputBytes.length; i++)
        {
            allInputBytes = allInputBytes + inputBytes[i];
        }
        if(allInputBytes.length() > 17)
        {
            int bitsToRemove = allInputBytes.length() - 17;
            bitsToRemove = bitsToRemove/2;
            allInputBytes = allInputBytes.substring(bitsToRemove+1, allInputBytes.length()-bitsToRemove);
        }
        long input = Long.parseLong(allInputBytes);
        long output = input/13;
        output = output%123;
        hashOutput = output+"";
        String index = output+"";
        if(hashTableArray[Integer.parseInt(index)]==null)
        {
            savePasswordToHashTable(password, Integer.parseInt(index));
        }
        
        
        return hashOutput;
    }
    
    /**
     * Function reads each line of the hash table file separating each value in the line by the comma. The first value in the line is used to represent the index of the array where the hash
     * input(2nd value) is going to be stored. The global array is then filled out with all the values from the file 
       */
    public void readHashTableFile()
    {
        hashTableArray = new String[999];
        hashedValues = new String[999];
        valuesInArray = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader("HashTable.txt"));
            String line = br.readLine();
            if(line != null && line.equals("EOF")==false)
            {
                while(line.equals("EOF") == false)
                {
                    hashedValues[valuesInArray] = line;
                    String[] tempValues = line.split(",");
                    hashTableArray[Integer.parseInt(tempValues[0])] = tempValues[1];
                    valuesInArray++;
                    
                    line = br.readLine();
                    
                    if(line.equals("EOF"))
                    {
                        break;
                    }
                }
            }
        } catch(Exception e)
        {
            ;
        }
    }
    
    /**
     * After each new password and its hash output is stored to the array, all of the used indecies as well as their respective values are added to the array hashedValues as a single string
     * separated by commas, this is essential to save storage as not all of the array is going to be filled and not all the values are going to be close to each other so to save space 
     * there is no need to store null values especially in the array of size 999
       */
    public void savePasswordToHashTable(String password, int index)
    {
        hashTableArray[index] = password;
        hashedValues[valuesInArray++] = index+","+password;
        try{
            FileWriter fw = new FileWriter("HashTable.txt");
            for(int i=0; i<valuesInArray; i++)
            {
                fw.write(hashedValues[i]);
                fw.write("\r\n");
            }
            fw.write("EOF");
            fw.close();
        } catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
