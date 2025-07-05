 
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;
import javax.swing.filechooser.*;
import static java.nio.file.StandardCopyOption.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.io.FileWriter;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;

public class AuthenticationGUI extends GUI
{
    char pageOn = 'L';
    
    UserList allUsers = new UserList();
    
    JFrame authenticationFrame = new JFrame("Log In");
    JPanel authenticationPanel = new JPanel();
    
    JPasswordField pwfPassword = new JPasswordField();
    
    JTextField tfLogin = new JTextField();
    JTextField tfPassword = new JTextField();
    JTextField tfFirstName = new JTextField();
    JTextField tfLastName = new JTextField();
    JTextField tfEmail = new JTextField();
    JTextField tfConfirmPassword = new JTextField();
    
    JButton btnCreateAccount = new JButton();
    JLabel lblPass = new JLabel();
    JLabel lblConfirmPass = new JLabel();
    boolean emailConfirmed = false;
    User userWithForgottenPassword = new User();
    
    JLabel lblInvalidName = new JLabel();
    JLabel lblInvalidSurname = new JLabel();
    JLabel lblInvalidEmail = new JLabel();
    JLabel lblInvalidPassword = new JLabel();
    JLabel lblInvalidPasswordConfirmation = new JLabel();
    JLabel lblInvalidLogin = new JLabel();
    
    JButton btnReset = new JButton();

    public AuthenticationGUI() throws IOException, FontFormatException {
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        
        AuthenticationGUI obj = new AuthenticationGUI();
        obj.initAuthenticationFrame();
    }
    
    /**
     * Sets the layout of the autentication frame to no layout to allow freely
     * positioning of the components on the frame. Frame sets resizable to false
     * in order to prevent user from changing the frame size and avoid breaking 
     * the design and location of each component and panels in frame
       */
    public void initAuthenticationFrame()
    {
        authenticationFrame.setLayout(null);
        authenticationFrame.setResizable(false);
        authenticationFrame.setLocationRelativeTo(null);
        authenticationFrame.setSize(1180, 680);
        
        initAuthenticationPanel();
        //initialises all the components on the authentication panel
        authenticationFrame.add(authenticationPanel);
        //Adds the main authentication panel to the frame
        authenticationFrame.setVisible(true);
    }
    
    /**
     * Calls the loadLoginComponents function that initialises all the components
     * of the login page and adds them to the panel. Sets the background colour
     * of the authentication panel, it's layout to null to allow setting the 
     * components anywhere on the panel and sets the bounds to position the panel
     * in the top left corner of the frame and sets it's size to take the whole 
     * frame.
       */
    public void initAuthenticationPanel()
    {
        loadLoginComponents();
        
        authenticationPanel.setBackground(new Color(48, 78, 48));
        authenticationPanel.setLayout(null);
        authenticationPanel.setBounds(0, 0, 1180, 680);
        
    }
    
    /**
     * Clears the authentication panel by removing all the components previously
     * added to the authentication panel. Sets the position of all the components
     * of the login page as well mouse event listeners if the component required 
     * it
       */
    public void loadLoginComponents()
    {
        authenticationPanel.removeAll();
        pageOn = 'L';
        allUsers = new UserList();
        allUsers.readUsersFromFile();
        
        JLabel lblUserIcon = new JLabel();
        lblUserIcon.setIcon(new ImageIcon(new ImageIcon("assets/account.png").getImage().getScaledInstance(155, 155, Image.SCALE_SMOOTH)));
        lblUserIcon.setBounds(510, 100, 155, 155);
        authenticationPanel.add(lblUserIcon);
        
        tfLogin.setText("");
        tfLogin.setBounds(366, 280, 455, 65);
        tfLogin.setVisible(true);
        authenticationPanel.add(tfLogin);
        
        JLabel lblLogin = new JLabel();
        lblLogin.setForeground(new Color(255,255,255));
        lblLogin.setText("Login: ");
        lblLogin.setBounds(366, 255, 115, 18);
        authenticationPanel.add(lblLogin);
        
        pwfPassword.setBounds(366, 376, 455, 65);
        pwfPassword.setText("");
        pwfPassword.setVisible(true);
        authenticationPanel.add(pwfPassword);
        /*
         * Clears the password field if it was filled out previously before 
         * sets it visible since the pasword field is declared globaly for this 
         * class and so could be used by other pages that could hide it.
           */
        
        JLabel lblPassword = new JLabel();
        lblPassword.setForeground(new Color(255,255,255));
        lblPassword.setText("Password: ");
        lblPassword.setBounds(366, 353, 118, 18);
        authenticationPanel.add(lblPassword);
        
        JButton btnLogIn = new JButton();
        btnLogIn.setText("Log In");
        btnLogIn.setBounds(490, 455, 200, 65);
        btnLogIn.addActionListener( AL->logInPressed() );
        authenticationPanel.add(btnLogIn);
        /*
         * Creates a button that when pressed will validate the data entered 
         * in all the fields
           */
        
        JLabel lblForgotPassword = new JLabel();
        lblForgotPassword.setForeground(new Color(255, 255, 255));
        lblForgotPassword.setText("Forgot Password");
        lblForgotPassword.setBounds(440, 536, 190, 18);
        lblForgotPassword.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadForgotPasswordComponents();
            }
        });
        /*
         * Overwrites the MouseAdapter class to make the mouseClicked function
         * call a different function loadForgotPasswordComponents which would
         * initialise the components for relevant page, making the label act
         * as a link to a different page 
           */
        authenticationPanel.add(lblForgotPassword);
        
        JLabel lblCreateAccount = new JLabel();
        lblCreateAccount.setForeground(new Color(255, 255, 255));
        lblCreateAccount.setText("Create Account");
        lblCreateAccount.setBounds(640, 534, 190, 18);
        lblCreateAccount.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadCreateAccountComponents();
            }
        });
        /*
         * Similarly to forgotPassword label a mouse listener is added and the
         * class mouse adapter is overwriten to make the label act as a link
         * to a different page when clicked
           */
        authenticationPanel.add(lblCreateAccount);
                
        JLabel lblTutorial = new JLabel();
        lblTutorial.setIcon(new ImageIcon(new ImageIcon("assets/Help.png").getImage().getScaledInstance(40, 35, Image.SCALE_SMOOTH)));
        lblTutorial.setBounds(1120, 12, 40, 35);
        lblTutorial.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadTutorial();
            }
        });
        /*
         * Adds a mouse listener and overwrites the mouse adapter class to 
         * make the label (which is just an icon) show a pop up with relevant
         * tutorial for the current page
           */
        authenticationPanel.add(lblTutorial);
        
        SwingUtilities.updateComponentTreeUI(authenticationFrame);
        /*
         * This function of class SwingUtilities is used to force the update 
         * of the authentication frame, since instead of using a tabbed pane
         * the program uses one main panel and clears its contents before 
         * populating them with different components of different page each 
         * function that initialises components needs to force the update
         * of the frame component as the updates will not take place until the
         * user interacts with the frame, by forcing an update the user will not
         * have to change the shape of the frame to display the components
           */
    }
    
    /**
     * This function is called when the log in button is pressed. It will call
     * a function of class UserList authenticate user which takes 2 string parameters
     * login and password, it will compare those parameters with all the objects
     * of class User stored in the array allUsers of that class. If both login
     * and password match one specific user the function will return the index
     * where that user can be found in array. That index is stored in the variable
     * if it's value is -1 (rouge value) a pop-up will display a message that 
     * some of entered data is invalid (it does not specify where exactly the mistake
     * was made for security reasons). If the value of variable index is a 
     * legit location of the user than that user object is pulled from the
     * global array of all users and is saved to the sessionUser attribute of
     * the child class GUI. CurrentUserID of the class GUI is also assigned with
     * the value of the user's id (attribute of user object). Then the authentication
     * frame is set to be invisible and a function of child class GUI is called
     * to initialise the main frame of that class 
       */
    public void logInPressed()
    {
        int index = allUsers.authenticateUser(tfLogin.getText(), pwfPassword.getText());
        if(index == -1)
        {
            JOptionPane.showMessageDialog(null, "Wrong details");
        } else{
            User sessionUser = new User();
            currentSessionUser = allUsers.allUsers[index];
            currentUserID = currentSessionUser.userId;
            authenticationFrame.setVisible(false);
            initFrame();
        }
    }
    
    /**
     * Initialises the components of the forgot password page. Clearing the panel
     * from previous functions beforehand. Initially the only the email field 
     * is visible and the button is set to display 'Confirm email'. First
     * the email is validated in a different function resetBtnClicked which is
     * called when the button is clicked. If the email has been validated
     * other components will appear to allow enter a new password and confirm 
     * the new password
       */
    public void loadForgotPasswordComponents()
    {
        authenticationPanel.removeAll();
        
        pageOn = 'F';
        emailConfirmed=false;
        userWithForgottenPassword = new User(); 
        //Clears the user object to ensure the previous one is not used
        
        JLabel lblUserIcon = new JLabel();
        lblUserIcon.setIcon(new ImageIcon(new ImageIcon("assets/account.png").getImage().getScaledInstance(155, 155, Image.SCALE_SMOOTH)));
        lblUserIcon.setBounds(523, 5, 155, 155);
        authenticationPanel.add(lblUserIcon);
        
        JLabel lblEmail = new JLabel();
        lblEmail.setForeground(new Color(255, 255, 255));
        lblEmail.setText("E-mail Address:");
        lblEmail.setBounds(373, 162, 190, 18);
        authenticationPanel.add(lblEmail);
        
        tfEmail.setText("");
        tfEmail.setBounds(373, 198, 455, 65);
        authenticationPanel.add(tfEmail);
        
        lblInvalidEmail.setForeground(new Color(255, 0, 0));
        lblInvalidEmail.setText("");
        lblInvalidEmail.setBounds(372, 255, 200, 18);
        authenticationPanel.add(lblInvalidEmail);
        
        
        lblPass.setForeground(new Color(255, 255, 255));
        lblPass.setText("New Password:");
        lblPass.setVisible(false);
        lblPass.setBounds(373, 281, 190, 18);
        authenticationPanel.add(lblPass);
        
        tfPassword.setText("");
        tfPassword.setVisible(false);
        tfPassword.setBounds(373, 320, 455, 65);
        authenticationPanel.add(tfPassword);
        
        lblInvalidPassword.setForeground(new Color(255, 0, 0));
        lblInvalidPassword.setText("");
        lblInvalidPassword.setBounds(373, 386, 400, 18);
        authenticationPanel.add(lblInvalidPassword);
        
        
        lblConfirmPass.setForeground(new Color(255, 255, 255));
        lblConfirmPass.setText("Confirm Password:");
        lblConfirmPass.setVisible(false);
        lblConfirmPass.setBounds(373, 403, 190, 18);
        authenticationPanel.add(lblConfirmPass);
        
        tfConfirmPassword.setText("");
        tfConfirmPassword.setVisible(false);
        tfConfirmPassword.setBounds(373, 439, 455, 65);
        authenticationPanel.add(tfConfirmPassword);
        
        lblInvalidPasswordConfirmation.setForeground(new Color(255, 0, 0));
        lblInvalidPasswordConfirmation.setText("");
        lblInvalidPasswordConfirmation.setBounds(373, 520, 400, 18);
        authenticationPanel.add(lblInvalidPasswordConfirmation);
        
        
        btnReset.setText("Confirm email");
        btnReset.setBounds(427, 547, 346, 65);
        btnReset.addActionListener( AL->resetBtnClicked() );
        authenticationPanel.add(btnReset);
        
        JLabel lblTutorial = new JLabel();
        lblTutorial.setIcon(new ImageIcon(new ImageIcon("assets/Help.png").getImage().getScaledInstance(40, 35, Image.SCALE_SMOOTH)));
        lblTutorial.setBounds(1120, 12, 40, 35);
        lblTutorial.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadTutorial();
            }
        });
        authenticationPanel.add(lblTutorial);
        
        JLabel lblGoBack = new JLabel();
        lblGoBack.setIcon(new ImageIcon(new ImageIcon("assets/arrow.png").getImage().getScaledInstance(40, 35, Image.SCALE_SMOOTH)));
        lblGoBack.setBounds(20, 12, 40, 35);
        
        lblGoBack.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                int response = JOptionPane.showConfirmDialog(null, "If you go back all data will be lost. Do you want to proceed?", "Do you want to exit?", JOptionPane.YES_NO_OPTION);
                if(response == 0)
                {
                    loadLoginComponents();
                }
            }
        });
        /*
         * In this label's mouse listener, mouse adapter is overwritten to ensure
         * it shows a pop up message asking the user to confirm their choice. If
         * user's response is 0 (clicked yes on the pop up window) the loadLoginComponents
         * function is executed which changes the panel components to display a 
         * login page, making this label act as a go back button
           */
        authenticationPanel.add(lblGoBack);
        
        SwingUtilities.updateComponentTreeUI(authenticationFrame);
    }
    
    /**
     * Called when the button on the forgot password page is pressed. Initially
     * valid is set to true. Global variable emailConfirmed changes based on wether the
     * user was able to provide correct 4 digit code that was sent to them via email.
     * If the email is not yet confirmed, the email text field is validated to ensure
     * it is not empty and that it exists. This is done by using the function of class
     * UserList findUserByMail which compares each object of array all users to find
     * the one with attribute email which is equal to the string parameter passed to
     * this function. Function result is stored in the integer variable (result is index
     * location of the object in the array), if the value of this variable is -1 this means
     * user with the provided email does not exist so the user will not be allowed to proceed
     * with the password reset since the valid is going to be set to false. If valid is true
     * it means so far the user has passed validation and has provided legit emai used when
     * registering for this system. In this case for loop and instance of random class is used
     * to randomly generate 4 digits and concatenate them to one single string. That string with
     * few other data added to it to form a mail message is passed to the function sendEmail as a
     * parameter. This function will send the email with the code to the user provided email address.
     * If the email belongs to the user trying to reset the password they will be able to access it
     * and enter a 4 digit code to the pop up window. User input is then compared with the randomly
     * generated code if it is the same user has passed verification and emailConfirmed is set to true
     * with relevant fields set to visible to allow type in new password and confirm it. The text on the
     * button is also changed from confirm email to reset password, userWithForgottenPassword is set to the object
     * of class user which is stored in the array at the location provided by the find function (this will be used
     * for further validation of the new password). If the user entered wrong code a pop up
     * appears stating they have failed verification and will need to reneter a new code sent to the email
     * (new code is generated to avoid posibility of guessing the code and so improving security)
     * 
     * When the user presses the button again after confirming the email, the function will validate 
     * the password fields first to ensure the password is at least 8 digits long, has letters, numbers
     * and special characters. This is achieved by iterating through the entered password and incrementing
     * relevant counters when letter, number or special character is encountered. If the user tries to enter a
     * password with a space it will set the valid to false as it is not allowed. Then all the counters are 
     * compared to ensure they are greater than 0 and so the password is valid. To ensure the user does not 
     * enter the password that was previously used by them, the string with new password will be compared against
     * the password attribute of userWithForgottenPassword. If passwords match, user will not be allowed to proceed
     * with reset and will be asked to choose a new password. If confirm password field does not match with the
     * password field, user will be asked to enter a matching password to verify the new password. If everything
     * is valid changePassword function of the user object is used to change from old to new password. Then 
     * changeUserAttributesAndSave method of user list object is used to overwrite the existing user in the array
     * with the new one (updating the user who reset the password) and write the array to text file to save the changes.
     * User is then redirected back to the login page
       */
    public void resetBtnClicked()
    {
        boolean valid = true;
        if(!emailConfirmed)
        {
            if(tfEmail.getText().equals(""))
            {
                valid = false;
                lblInvalidEmail.setText("Email is required");
            } else
            {
                lblInvalidEmail.setText("");
            }
            
            String email = tfEmail.getText();
            int userIndex = allUsers.findUserByMail(email);
            if(userIndex == -1)
            {
                lblInvalidEmail.setText("Email provided has not been used to create an account");
                lblInvalidEmail.setSize(400, 18);
                valid = false;
            } else{
                lblInvalidEmail.setText("");
                User tempUser = new User();
                tempUser = allUsers.allUsers[userIndex];
            }
            
            if(valid)
            {
                Random random = new Random();
                String verificationDigits = "";
                for(int i=0; i<4; i++)
                {
                    int randomDigit = random.nextInt(0, 10);
                    verificationDigits = verificationDigits+randomDigit;
                }
                String emailMessage = "Here is the code to reset your password: \n"+verificationDigits+"\nIf it was not you, immediate action is neccessary!";
                sendEmail(tfEmail.getText(), "Password reset", emailMessage);
                String userEnteredCode = JOptionPane.showInputDialog(null, "Enter the 4 digit code we sent you via email: ");
                
                if(userEnteredCode.equals(verificationDigits))
                {
                    emailConfirmed = true;
                    
                    tfPassword.setVisible(true);
                    tfConfirmPassword.setVisible(true);
                            
                    lblPass.setVisible(true);
                    lblConfirmPass.setVisible(true);
                    btnReset.setText("Reset Password");
                    
                    userWithForgottenPassword = allUsers.allUsers[userIndex];
                } else
                {
                    JOptionPane.showMessageDialog(null, "The code you entered is incorrect, you'll have to resend the code", "Wrong code", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else{
            lblInvalidPassword.setText("");
            if(userWithForgottenPassword.password.equals(tfPassword.getText()))
            {
                lblInvalidPassword.setText("New password cannot be the same as the last one");
                valid = false;
            }
            
            if(tfPassword.getText().equals(tfConfirmPassword.getText()) == false)
            {
                lblInvalidPasswordConfirmation.setText("Passwords do not match");
                valid = false;
            } else
            {
                lblInvalidPasswordConfirmation.setText("");
            }
            
            if(tfPassword.getText().equals(""))
            {
                lblInvalidPassword.setText("Password is required");
                valid = false;
            } 
            
            String passwordProvided = tfPassword.getText();
            int specialChars = 0;
            int letters = 0; 
            int nums = 0;
            for(int i=0; i<passwordProvided.length(); i++)
            {
                char passwordChar = passwordProvided.charAt(i);
                       
                if(Character.isLetter(passwordChar))
                {
                   letters++;
                }
                       
                if(Character.isDigit(passwordChar))
                {
                   nums++;
                }
                       
                if(!Character.isLetterOrDigit(passwordChar) && !Character.isWhitespace(passwordChar))
                {
                   specialChars++;
                }
                       
                if(Character.isWhitespace(passwordChar))
                {
                   letters = 0;
                }
            }
                   
            if(specialChars == 0)
            {
                lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
                lblInvalidPassword.setSize(500, 30);
                valid = false;
            }
            
            if(passwordProvided.length() < 8)
            {
                lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
                lblInvalidPassword.setSize(500, 30);
                valid = false;
            }
            
            if(letters == 0)
            {
                lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
                lblInvalidPassword.setSize(500, 30);
                valid = false;
            }
            
            if(nums == 0)
            {
                lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
                lblInvalidPassword.setSize(500, 30);
                valid = false;
            }
            
            if(valid)
            {
                userWithForgottenPassword.changePassword(tfPassword.getText());
                allUsers.changeUserAttributesAndSave(userWithForgottenPassword);
                clearAllAcountCreationFields();
                JOptionPane.showMessageDialog(null, "Successfully changed your password!", "Success", JOptionPane.INFORMATION_MESSAGE);
                userWithForgottenPassword = new User();
                loadLoginComponents();
            }
        }
        
    }
    
    /**
     * Clears the login panel from any previously loaded components. Populates the panel with the new
     * components that make up an account creation page. KeyListener is assigned to email, password and
     * password confirmation fields to validate the data as it is being typed by user, not allowing them
     * to press create an account until they provide valid password and email 
       */
    public void loadCreateAccountComponents()
    {
        authenticationPanel.removeAll();
        
        pageOn = 'S';
        boolean allValid = true;
        
        JLabel lblUserIcon = new JLabel();
        lblUserIcon.setIcon(new ImageIcon(new ImageIcon("assets/account.png").getImage().getScaledInstance(155, 155, Image.SCALE_SMOOTH)));
        lblUserIcon.setBounds(523, 5, 155, 155);
        authenticationPanel.add(lblUserIcon);
        
        JLabel lblFirstName = new JLabel();
        lblFirstName.setForeground(new Color(255, 255, 255));
        lblFirstName.setText("First Name: ");
        lblFirstName.setBounds(112, 170, 195, 18);
        authenticationPanel.add(lblFirstName);
        
        lblInvalidName.setForeground(new Color(255,0,0));
        lblInvalidName.setText("");
        lblInvalidName.setBounds(112, 275, 200, 18);
        authenticationPanel.add(lblInvalidName);
        
        tfFirstName.setBounds(112, 205, 455, 65);
        tfFirstName.setText("");
        authenticationPanel.add(tfFirstName);
        
        JLabel lblLastName = new JLabel();
        lblLastName.setForeground(new Color(255, 255, 255));
        lblLastName.setText("Last Name:");
        lblLastName.setBounds(112, 288, 190, 18);
        authenticationPanel.add(lblLastName);
        
        lblInvalidSurname.setForeground(new Color(255,0,0));
        lblInvalidSurname.setText("");
        lblInvalidSurname.setBounds(112, 395, 200, 18);
        authenticationPanel.add(lblInvalidSurname);
        
        tfLastName.setBounds(112, 327, 455, 65);
        tfLastName.setText("");
        authenticationPanel.add(tfLastName);
        
        JLabel lblEmail = new JLabel();
        lblEmail.setForeground(new Color(255, 255, 255));
        lblEmail.setText("E-mail Address:");
        lblEmail.setBounds(112, 410, 300, 18);
        authenticationPanel.add(lblEmail);
        
        lblInvalidEmail.setForeground(new Color(255,0,0));
        lblInvalidEmail.setText("");
        lblInvalidEmail.setBounds(112, 515, 200, 18);
        authenticationPanel.add(lblInvalidEmail);
        
        tfEmail.setBounds(112, 446, 455, 65);
        tfEmail.setText("");
        tfEmail.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e)
            {
                String email = tfEmail.getText();
                int atPresent = 0;
                int dotPresent = 0;
                for(int i=0; i<email.length(); i++)
                {
                    if(email.charAt(i) == '@')
                    {
                        atPresent++;
                    }
                    
                    if(email.charAt(i) == '.')
                    {
                        dotPresent++;
                    }
                }
                
                if(atPresent != 1)
                {
                    lblInvalidEmail.setText("Invalid email");
                    btnCreateAccount.setEnabled(false);
                }
                
                if(dotPresent < 1)
                {
                    lblInvalidEmail.setText("Invalid email");
                    btnCreateAccount.setEnabled(false);
                }
                
                if(dotPresent >= 1 && atPresent==1)
                {
                    btnCreateAccount.setEnabled(true);
                    lblInvalidEmail.setText("");
                }
            }
        });
        authenticationPanel.add(tfEmail);
        
        JLabel lblLogin = new JLabel();
        lblLogin.setForeground(new Color(255, 255, 255));
        lblLogin.setText("Login:");
        lblLogin.setBounds(634, 169, 190, 18);
        authenticationPanel.add(lblLogin);
        
        lblInvalidLogin.setForeground(new Color(255,0,0));
        lblInvalidLogin.setText("");
        lblInvalidLogin.setBounds(634, 275, 200, 18);
        authenticationPanel.add(lblInvalidLogin);
        
        tfLogin.setBounds(634, 205, 455, 65);
        tfLogin.setText("");
        authenticationPanel.add(tfLogin);
        
        JLabel lblPass = new JLabel();
        lblPass.setForeground(new Color(255, 255, 255));
        lblPass.setText("Password:");
        lblPass.setBounds(634, 288, 190, 18);
        authenticationPanel.add(lblPass);
        
        lblInvalidPassword.setForeground(new Color(255,0,0));
        lblInvalidPassword.setText("");
        lblInvalidPassword.setBounds(634, 393, 200, 18);
        authenticationPanel.add(lblInvalidPassword);
        
        tfPassword.setBounds(634, 327, 455, 65);
        tfPassword.setVisible(true);
        tfPassword.addKeyListener(new KeyAdapter(){
           public void keyTyped(KeyEvent e)
           {
               String password = tfPassword.getText();
               int specialChars = 0;
               int letters = 0; 
               int nums = 0;
               for(int i=0; i<password.length(); i++)
               {
                   char passwordChar = password.charAt(i);
                   
                   if(Character.isLetter(passwordChar))
                   {
                       letters++;
                   }
                   
                   if(Character.isDigit(passwordChar))
                   {
                       nums++;
                   }
                   
                   if(!Character.isLetterOrDigit(passwordChar) && !Character.isWhitespace(passwordChar))
                   {
                       specialChars++;
                   }
                   
                   if(Character.isWhitespace(passwordChar))
                   {
                       letters = 0;
                   }
               }
               
               if(specialChars == 0 || letters == 0 || nums == 0 || password.length() < 7)
               {
                   lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
                   lblInvalidPassword.setSize(500, 30);
                   btnCreateAccount.setEnabled(false);
               } else
               {
                   lblInvalidPassword.setText("");
                   btnCreateAccount.setEnabled(true);
               }
           }
        });
        tfPassword.setText("");
        
        authenticationPanel.add(tfPassword);
        
        JLabel lblConfirmPass = new JLabel();
        lblConfirmPass.setForeground(new Color(255, 255, 255));
        lblConfirmPass.setText("Confirm Password:");
        lblConfirmPass.setBounds(634, 410, 236, 18);
        authenticationPanel.add(lblConfirmPass);
        
        lblInvalidPasswordConfirmation.setForeground(new Color(255,0,0));
        lblInvalidPasswordConfirmation.setText("");
        lblInvalidPasswordConfirmation.setBounds(634, 515, 200, 18);
        authenticationPanel.add(lblInvalidPasswordConfirmation);
        
        tfConfirmPassword.setBounds(634, 446, 455, 65);
        tfConfirmPassword.setText("");
        tfConfirmPassword.setVisible(true);
        tfConfirmPassword.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e)
            {
                if(tfConfirmPassword.getText().equals(tfPassword.getText()) == false)
                {
                    btnCreateAccount.setEnabled(false);
                    lblInvalidPasswordConfirmation.setText("Password must be the same");
                } else
                {
                    btnCreateAccount.setEnabled(true);
                    lblInvalidPasswordConfirmation.setText("");
                }
            }
        });
        authenticationPanel.add(tfConfirmPassword);
        
        btnCreateAccount.setEnabled(true);
        btnCreateAccount.setText("Create Account");
        btnCreateAccount.addActionListener( AL->createAccountClicked() );
        btnCreateAccount.setBounds(743, 568, 346, 65);
        authenticationPanel.add(btnCreateAccount);
        
        JLabel lblTutorial = new JLabel();
        lblTutorial.setIcon(new ImageIcon(new ImageIcon("assets/Help.png").getImage().getScaledInstance(40, 35, Image.SCALE_SMOOTH)));
        lblTutorial.setBounds(1120, 12, 40, 35);
        lblTutorial.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadTutorial();
            }
        });
        authenticationPanel.add(lblTutorial);
        
        JLabel lblGoBack = new JLabel();
        lblGoBack.setIcon(new ImageIcon(new ImageIcon("assets/arrow.png").getImage().getScaledInstance(40, 35, Image.SCALE_SMOOTH)));
        lblGoBack.setBounds(20, 12, 40, 35);
        
        lblGoBack.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                int response = JOptionPane.showConfirmDialog(null, "If you go back all data will be lost. Do you want to proceed?", "Do you want to exit?", JOptionPane.YES_NO_OPTION);
                if(response == 0)
                {
                    loadLoginComponents();
                }
            }
        });
        authenticationPanel.add(lblGoBack);
        
        SwingUtilities.updateComponentTreeUI(authenticationFrame);
    }
    
    /**
     * Function validates all the data entered by user to ensure it is not null, valid for password and email and that
     * login is unique by checking through each individual object in the allUsers array and comparing their 
     * login attributes. If the user passed validation sendEmail is called to send the 4 randomly generated digits
     * to the user provided email. User is then asked to enter those digits into the pop up window, if the digits 
     * are correct temporary user object is created with the attributes assigned as they were entered by the user.
     * User ID is created by finding the last user, getting their number after 'U', adding 1 to it and assigning
     * 'U' in front of the new id. All the atributes are then added through the setAttribute method of the tempUser 
     * object. Then using addUserToArray, user is added to the global array of all users and using write all users to file
     * the array is written to the text file to permanently apply the changes. The directory specific for this user is then
     * created at the user folder at the root of this project. It is used to store playlists and all relevant to it data
     * unique to each user.
       */
    public void createAccountClicked()
    {
        boolean valid = true;
        
                
        if(tfFirstName.getText().equals(""))
        {
            valid = false;
            lblInvalidName.setText("Name is required");
        } else
        {
            lblInvalidName.setText("");
        }
        
        if(tfLastName.getText().equals(""))
        {
            valid = false;
            lblInvalidSurname.setText("Last Name is required");
        } else
        {
            lblInvalidSurname.setText("");
        }
        
        if(tfEmail.getText().equals(""))
        {
            valid = false;
            lblInvalidEmail.setText("Email is required");
        } else
        {
            lblInvalidEmail.setText("");
        }
        
        String email = tfEmail.getText();
        int atPresent = 0;
        int dotPresent = 0;
        for(int i=0; i<email.length(); i++)
        {
            if(email.charAt(i) == '@')
            {
               atPresent++;
            }
                    
            if(email.charAt(i) == '.')
            {
               dotPresent++;
            }
        }
                
        if(atPresent != 1)
        {
            lblInvalidEmail.setText("Invalid email");
            valid = false;
        }
                
        if(dotPresent < 1)
        {
            lblInvalidEmail.setText("Invalid email");
            valid = false;
        }
        
        if(tfLogin.getText().equals(""))
        {
            valid = false;
            lblInvalidLogin.setText("Login is required");
        } else 
        {
            lblInvalidLogin.setText("");
        }
        
        if(tfPassword.getText().equals(""))
        {
            valid = false;
            lblInvalidPassword.setText("Password is required");
        } else
        {
            lblInvalidPassword.setText("");
        }
        
        String passwordProvided = tfPassword.getText();
        int specialChars = 0;
        int letters = 0; 
        int nums = 0;
        for(int i=0; i<passwordProvided.length(); i++)
        {
            char passwordChar = passwordProvided.charAt(i);
                   
            if(Character.isLetter(passwordChar))
            {
               letters++;
            }
                   
            if(Character.isDigit(passwordChar))
            {
               nums++;
            }
                   
            if(!Character.isLetterOrDigit(passwordChar) && !Character.isWhitespace(passwordChar))
            {
               specialChars++;
            }
                   
            if(Character.isWhitespace(passwordChar))
            {
               letters = 0;
            }
        }
               
        if(specialChars == 0)
        {
            lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
            lblInvalidPassword.setSize(500, 30);
            valid = false;
        } else
        {
            lblInvalidPassword.setText("");
        }
        
        if(passwordProvided.length() < 8)
        {
            lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
            lblInvalidPassword.setSize(500, 30);
            valid = false;
        }else
        {
            lblInvalidPassword.setText("");
        }
        
        if(letters == 0)
        {
            lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
            lblInvalidPassword.setSize(500, 30);
            valid = false;
        }else
        {
            lblInvalidPassword.setText("");
        }
        
        if(nums == 0)
        {
            lblInvalidPassword.setText("Password must be at least 8 digits long and contain a number, letter, \nspecial character and no space");
            lblInvalidPassword.setSize(500, 30);
            valid = false;
        }else
        {
            lblInvalidPassword.setText("");
        }
        
        if(tfConfirmPassword.getText().equals(tfPassword.getText())==false)
        {
            valid = false;
            lblInvalidPasswordConfirmation.setText("Password must be the same");
        } else
        {
            lblInvalidPasswordConfirmation.setText("");
        }
        
        for(int i=0; i<allUsers.position; i++)
        {
            User tempUser = new User();
            tempUser = allUsers.allUsers[i];
            if(tempUser.login.equals(tfLogin.getText()))
            {
                valid = false;
                lblInvalidLogin.setText("Login already taken");
                break;
            }
        }
        
        for(int i=0; i<allUsers.position; i++)
        {
            User tempUser = new User();
            tempUser = allUsers.allUsers[i];
            if(tempUser.email.equals(tfEmail.getText()))
            {
                valid = false;
                lblInvalidEmail.setText("This e-mail has already been taken");
                lblInvalidEmail.setSize(400, 18);
                break;
            }
        }

        
        if(valid)
        {
            Random random = new Random();
            String verificationDigits = "";
            for(int i=0; i<4; i++)
            {
                int randomDigit = random.nextInt(0, 10);
                verificationDigits = verificationDigits+randomDigit;
            }
            String emailMessage = "Before creating the account we need to verify it is you. Please enter the code below when your system asks for it. \n"+verificationDigits+"\nIf it was not you, immediate action is neccessary!";
            sendEmail(tfEmail.getText(), "Verify your email", emailMessage);
            String userEnteredCode = JOptionPane.showInputDialog(null, "Enter the 4 digit code we sent you via email: ");
            if(userEnteredCode.equals(verificationDigits))
            {
                User tempUser = new User();
                tempUser = allUsers.allUsers[allUsers.position-1];
                String id = tempUser.userId;
                id = id.substring(1);
                int latestID = Integer.parseInt(id);
                int newID = latestID+1;
                tempUser = new User();
                String name = tfFirstName.getText();
                String lastName = tfLastName.getText();
                String emailAddress = tfEmail.getText();
                String userID = "U"+newID;
                char preferedCall = 'N'; // By default address user by their first name, other options: F(full name), L(login)
                String login = tfLogin.getText();
                String password = tfPassword.getText();
                tempUser.setUserAttributes(name, lastName, emailAddress, userID, preferedCall, login, password);
                allUsers.addUserToArray(tempUser);
                allUsers.writeAllUsersToFile();
                
                allUsers = new UserList();
                
                new File("users/"+userID).mkdir(); // Create a new directory where all the user's playlist are going to be stored to
                try{
                    FileWriter fw = new FileWriter("users/"+userID+"/allPlaylists.txt");
                    fw.write("EOF");
                    fw.close();
                } catch(Exception e)
                {
                    System.out.println(e);
                }
                /*
                 * Attempts to write to the file allPlaylists stored in the user's directory, since that file does not exist, file writer will create such file and write
                 * EOF into it. This file is then used to store all the playlists user will create.
                   */
                  
                JOptionPane.showInternalMessageDialog(null, "Your account has been successfully created!", "Success", JOptionPane.PLAIN_MESSAGE);
                clearAllAcountCreationFields();
                loadLoginComponents();
            } else{
                JOptionPane.showMessageDialog(null, "You entered the wrong verification code. Please try again!");
            }
            
        }
    }
    
    /**
     * Clears all the text fields that were filled out in account creation or password reset as
     * well as setting all the invalid label messages to empty string
       */
    public void clearAllAcountCreationFields()
    {
        tfConfirmPassword.setText("");
        tfEmail.setText("");
        tfFirstName.setText("");
        tfLastName.setText("");
        tfLogin.setText("");
        tfPassword.setText("");
        
        lblInvalidEmail.setText("");
        lblInvalidLogin.setText("");
        lblInvalidName.setText("");
        lblInvalidPassword.setText("");
        lblInvalidPasswordConfirmation.setText("");
        lblInvalidSurname.setText("");
        
    }
    
    /**
     * This function takes in 3 string parameters recepient email address, subject and main message of the mail. Using the object of class property the function is able to set up the
     * Simple Mail Transfer Protocol server that uses the gmail as a host to send mail and port 587 of that host to send mail from my machine to the gmail server on the web. Since this is 
     * a real server and not a local host, I need to provide sender email (my personal gmail in this case) and app password. App password can be generated in the google account page of the 
     * sender account. Using the instance of class session, the function creates a new mail service with all the config provided by properties (Host, port number, authorisation, enabled tls)
     * return new PasswordAuthentication(sender, senderPassword), is used to securely provide a user name and a password of the sender when the session connects to the mail server. MimeMessage
     * creates a new instance of that class which is the mail itself that uses MIME standard which allows the mail to have text and non-text attachments (e.g text file). The methods of that class
     * are later used to set the recipient, subject and mail body of the email before using Transport.send to actually send the message.
       */
    public void sendEmail(String recipient, String subject, String emailBody)
    {
        String host = "smtp.gmail.com";
        String sender = "noreply.spotifyproject@gmail.com";
        String senderPassword = "hsbz gxme owqt mpud";
        Properties properties = new Properties();
        
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        
        properties.put("mail.smtp.starttls.enable", "true");
        //properties.put("mail.debug", "true");
        Session session = Session.getInstance(properties, new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, senderPassword);
            }
        });
        /*
         * Overwrited the properties of class Authenticator (method getPasswordAuthentication) to return my password in a secure way.
           */
        
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(emailBody);
            Transport.send(message);
        } catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Function is called whenever the question mark icon is pressed. If the global variable pageOn
     * which represents each individual page the user is currently is on. If it is set to 'L'
     * tutorial relevant to the login page is loaded, 'S' - relevant to sign up/create account 
     * page, 'F' - relevant to the forgot password page
       */
    public void loadTutorial()
    {
        String tutorial = "";
        if(pageOn == 'L')
        {
            tutorial = "This is a login page. \nPlease provide your login and password that you set up when creating your account in a designated fields. \nIf you forgot your password click 'ForgotPassword'. \nIf you do not have an account, you can create one by pressing 'Create Account'.";
        } else if(pageOn == 'F')
        {
            tutorial = "This is 'forgot password' page. Before we can reset your password we need to clarify it's you. \nAs you noticed there is only one field present at start, which is your email field. Please provide your email that was used to create your account \nso that we could send you the verification code. \nYou will then be asked to provide that verification code. After which you can create a new password. \nRemember to make it at least 8 digits long and include one of each: \nLetter, number, special character e.g. @,!";
        } else if (pageOn == 'S')
        {
            tutorial = "We are very happy you decided to use our system. If you clicked here you likely do not have an account yet. To set it up all you need is to provide a few details about yourself: \nYour first and last name, \nE-mail address, \nUnique login that you will use to login into the system. \nDon't worry all the data we store about you is encrypted and held securely so no one will know who you are. \nYou will also need to create a password that is at least 8 digits long and has numbers, letters and special characters like @, !. \nIn the end we will ask you to verify your e-mail by entering a 4 digit code we sent you. This is just a small security procedure to ensure it is really you.";
        }
        JOptionPane.showMessageDialog(null, tutorial, "Help", JOptionPane.PLAIN_MESSAGE);
    }
}
