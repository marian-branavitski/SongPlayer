import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.*;
import javax.swing.filechooser.*;
import javax.swing.text.TableView;

import static java.nio.file.StandardCopyOption.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

public class GUI
{
    User currentSessionUser = new User();
    String currentUserID;
    String currentPage = "";
    CustomSongList currentSongList = new CustomSongList();
    CustomPlaylist currentPlaylist = new CustomPlaylist();
    JLabel lblArtistName = new JLabel();
    JLabel lblNowPlaying = new JLabel();
    
    boolean mediaIsPlaying = false;
    String currentlyPlayingSongSource = "";
    Media media;
    MediaPlayer mediaPlayer;
    
    JTextField tfFirstName = new JTextField();
    JTextField tfLastName = new JTextField();
    String[] goByOpts = {"First Name", "Full Name", "Login"};
    JComboBox<String> cbxPrefferedGoBy = new JComboBox<String>(goByOpts);
    JLabel lblInvalidFN = new JLabel();
    JLabel lblInvalidLN = new JLabel();
    
    CustomPlaylistList allPlaylists = new CustomPlaylistList();
    
    CustomSongList songList = new CustomSongList();
    
    String playlistCoverOrigin = "";
    String playlistCoverDestination = "";
    
    String[] songSourceOrigin = new String[100];
    String[] songSourceDestination = new String[100];
    int songSourceArraysPosition = 0;
    
    String singleSongOrigin="";
    String singleSongDestination="";
    
    JFrame mainFrame = new JFrame("Custom playlists");
    
    InputStream is = GUI.class.getResourceAsStream("assets/fonts/KronaOne-Regular.ttf");
    Font kronaFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(12f);
    
    JPanel sidebarPanel = new JPanel(null);
    
    JLabel lblHomeLink = new JLabel();
    JLabel lblLibraryLink = new JLabel();
    JLabel lblAccountLink = new JLabel();
    JLabel lblSettingsLink = new JLabel();
    JLabel lblHelpLink = new JLabel();
    
    
    
    JPanel mainPanel = new JPanel(null);
    
    String[] inPlaylistHeadings = {"Song", "Duration", "Rating", "Album", "Artist", "Genre", "Listens"};
    DefaultTableModel playlistTableModel = new DefaultTableModel(inPlaylistHeadings,0);
    JTable songsInPlaylistTable = new JTable(playlistTableModel){
        private static final long serialVersionUID = 1L;
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
        
    };
    /*
     * By overwriting variable of type long and function of class JTable we make
     * the function isCellEditable return false, this makes the table uneditable preventing
     * the user from accidentely modifying the data in the table (it wouldn't modify the actual
     * data permanently, just it's instance in the table) without it delete feature would be 
     * imposible as instead of deleting the selected song, clicking the backspace would just delete a
     * character of whatever value was stored in the cell
       */
    JScrollPane scrollableInPlaylistTable = new JScrollPane(songsInPlaylistTable);
    JLabel lblPause = new JLabel();
    CustomSong currentSong = new CustomSong();
    int indexOfCurrentSong = 0;
    
    JLabel lblAdd = new JLabel();
    
    JTextField tfSearchBar = new JTextField();
    JButton btnSearch = new JButton();
    
        
    JTextField tfPlaylistTitle = new JTextField();
    JLabel lblInvalidPlaylistTitle = new JLabel();
    
    JLabel lblPlaylistCoverPreview = new JLabel();
    JLabel lblMusicSourcePreview = new JLabel();
    JTextField tfSongName = new JTextField();
    JTextField tfArtistName = new JTextField();
    JTextField tfDuration = new JTextField();
    JTextField tfTotalListens = new JTextField();
    Integer[] ratingOpts = {1, 2, 3, 4, 5};
    JComboBox<Integer> cbxRating = new JComboBox<Integer>(ratingOpts);
    String[] genreOpts = {"Rock", "Pop", "Metal", "Punk rock", "Hip-Hop", "Funk", "Jazz"};
    JComboBox<String> cbxGenre = new JComboBox<String>(genreOpts);
    JTextField tfAlbum = new JTextField();
    
    JLabel lblInvalidSongName = new JLabel();
    JLabel lblInvalidArtistName = new JLabel();
    JLabel lblInvalidDuration = new JLabel();
    JLabel lblInvalidTotalListens = new JLabel();
    JLabel lblInvalidMusicFile = new JLabel();
    
    DefaultListModel songsAddedListModel = new DefaultListModel<>();
    JList<String> songsAddedList = new JList(songsAddedListModel);
    JScrollPane scrollableSongsAddedList = new JScrollPane(songsAddedList);

    public GUI() throws IOException, FontFormatException {
    }

    /**
     * Sets the layout of the main frame to null to allow freely positioning of the 2 panels. Sets resizable
     * to false so the user could not change the size of the frame which would break the design of each page
     * as no layout is being used. Sets the location relative to null to make the frame open up in the middle 
     * of the screen. loadSidebar is called to populate and fully initialise the side panel of the system.
     * Sidebar panel is then added to the main frame. loadMainPanel populates and initialises main panel
     * which is then added to the frame. New instance of JFXPanel is initiated to allow usage of javafx 
     * components further in the system like the media player. By simply initalising it at the start
     * and doing nothing with it, it will not appear in the frame while still allowing to use all the 
     * other javafx components.
       */
    public void initFrame()
    {
        mainFrame.setLayout(null);
        //this will allow to freely position different containers on the frame
        mainFrame.setSize(1180, 680);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        
        loadSideBar();
        mainFrame.add(sidebarPanel);
        
        loadMainPanel();
        mainFrame.add(mainPanel);
        
        mainFrame.setVisible(true);
        new JFXPanel();
        /*
         * The program relies on using a media player to allow user control music, which is a javafx component that cannot be initialised without
         * JFXPanel. By simply initialising a new JFXPanel in the method that initialises all components, the media player will be initilised 
         * and accessible without any other unneccessary component being added to the frame
           */
    }
    
    /**
     * Sets the layout of the sidebar panel to null to allow freely positioning of all the components. Unlike the components for the main panel
     * there is no load components functions instead all components are positioned and loaded within the function since the sidebar
     * and its components are static and do not change throughout the system unlike the components of the main panel that have to change
     * based on each page. This function creates all the icons and assigns mouse listeners to them to make them function like links to
     * a different page
       */
    public void loadSideBar()
    {
        sidebarPanel.setLayout(null);
        sidebarPanel.setBounds(0, 0, 250, 680); 
        /*
         * This will create a container that will contain the navigation menu 
         * elements and will be placed on the left side of the frame
           */
        
        lblAccountLink.setIcon(new ImageIcon("assets/account.png"));
        
        lblAccountLink.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
                lblAccountLink.setIcon(new ImageIcon(new ImageIcon("assets/account.png").getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH)));
                lblAccountLink.setBounds(15, 5, 55, 55);
            }
            public void mouseExited(MouseEvent e)
            {
                lblAccountLink.setIcon(new ImageIcon("assets/account.png"));
                lblAccountLink.setBounds(15, 10, 45, 45);
                /*
                   Once user stops hovering over the label, all sizes are reset to the default once
                   */
            }
            /*
             * This mouse adapter overwrites the mouse entered and mouse exited functions of mouse adapter to change the dimension of the icon
             * when the mouse is hovered over the label, giving the user a visual clue as to what they are about to click
               */
        });
        
        lblAccountLink.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                redirectAccountPage();
            }
        });
        
        lblAccountLink.setBounds(15, 10, 45, 45);
        sidebarPanel.add(lblAccountLink);
        
        
        lblHomeLink.setIcon(new ImageIcon("assets/Home.png"));
        lblHomeLink.setFont(kronaFont);
        lblHomeLink.setText("HOME");
        /*
           Sets icon and a text to the label which will act as an interactable
           link to redirect to reative page
           */
        
        lblHomeLink.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
                lblHomeLink.setIcon(new ImageIcon(new ImageIcon("assets/Home.png").getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH)));
                lblHomeLink.setSize(270, 58);
            }
            public void mouseExited(MouseEvent e)
            {
                lblHomeLink.setIcon(new ImageIcon("assets/Home.png"));
                lblHomeLink.setSize(223, 48);
                /*
                   Once user stops hovering over the label, all sizes are reset to the default once
                   */
            }
            /*
             * This mouse adapter overwrites the mouse entered and mouse exited functions of mouse adapter to change the dimension of the icon
             * when the mouse is hovered over the label, giving the user a visual clue as to what they are about to click
               */
        });
          
        lblHomeLink.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadHomeComponents();
            }
        });
        /*
         * Creates a mouse listener by using mouse adapter class. When the 
         * label is clicked, redirectHome function of this GUI class is called
         * which redirects user back to the home;
           */
        lblHomeLink.setBounds(15, 60, 223, 48); 
        
        sidebarPanel.add(lblHomeLink);
        
        lblLibraryLink.setIcon(new ImageIcon("assets/Music.png"));
        lblLibraryLink.setFont(kronaFont);
        lblLibraryLink.setText("LIBRARY");
        
        lblLibraryLink.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
                lblLibraryLink.setIcon(new ImageIcon(new ImageIcon("assets/Music.png").getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH)));
                lblLibraryLink.setSize(200, 58);
            }
            public void mouseExited(MouseEvent e)
            {
                lblLibraryLink.setIcon(new ImageIcon("assets/Music.png"));
                lblLibraryLink.setSize(175, 48);
                /*
                   Once user stops hovering over the label, all sizes are reset to the default once
                   */
            }
            /*
             * This mouse listener overwrites the mouse entered and mouse exited functions of mouse adapter to change the dimension of the icon
             * when the mouse is hovered over the label, giving the user a visual clue as to what they are about to click
               */
        });
        
        lblLibraryLink.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadLibraryComponents();
            }
        });
        /*
         * Works similar to the home label, but redirects user to their library
           */
        lblLibraryLink.setBounds(15, 125, 175, 48);
        sidebarPanel.add(lblLibraryLink);
        
        lblHelpLink.setIcon(new ImageIcon("assets/Help.png"));
        
        lblHelpLink.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
                lblHelpLink.setIcon(new ImageIcon(new ImageIcon("assets/Help.png").getImage().getScaledInstance(49, 45, Image.SCALE_SMOOTH)));
                lblHelpLink.setSize(49, 45);
            }
            public void mouseExited(MouseEvent e)
            {
                lblHelpLink.setIcon(new ImageIcon("assets/Help.png"));
                lblHelpLink.setSize(39, 35);
                /*
                   Once user stops hovering over the label, all sizes are reset to the default once
                   */
            }
            /*
             * This mouse listener overwrites the mouse entered and mouse exited functions of mouse adapter to change the dimension of the icon
             * when the mouse is hovered over the label, giving the user a visual clue as to what they are about to click
               */
        });
        
        lblHelpLink.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                giveHelp();
            }
        });
        lblHelpLink.setBounds(200, 600, 39, 35);
        sidebarPanel.add(lblHelpLink);
        
        
        sidebarPanel.setBackground(new Color(91, 124, 112));
    }
    
    /**
     * Clears the panel from any previously loaded components. Initialises
     * and locates all of the components relevant to the account page. Adds all the components to
     * main panel. Sets all the relevant text into the text field (first name and last name). 
     * Sets the combo box selected element to which ever element is saved as user's prefered call
     * in the all users file
       */
    public void redirectAccountPage()
    {
        mainPanel.removeAll();
        currentPage = "Account Page";
        
        JLabel lblName = new JLabel();
        lblName.setForeground(new Color(255, 255, 255));
        lblName.setFont(kronaFont);
        lblName.setText("First Name:");
        lblName.setBounds(40, 150, 190, 18);
        mainPanel.add(lblName);
        
        tfFirstName.setText(currentSessionUser.firstName);
        tfFirstName.setBounds(40, 190, 455, 65);
        mainPanel.add(tfFirstName);
        
        lblInvalidFN.setForeground(new Color(255, 0, 0));
        lblInvalidFN.setBounds(40, 260, 190, 18);
        lblInvalidFN.setText("");
        mainPanel.add(lblInvalidFN);
        
        JLabel lblLastName = new JLabel();
        lblLastName.setForeground(new Color(255, 255, 255));
        lblLastName.setFont(kronaFont);
        lblLastName.setText("Last Name:");
        lblLastName.setBounds(40, 295, 190, 18);
        mainPanel.add(lblLastName);
        
        tfLastName.setText(currentSessionUser.lastName);
        tfLastName.setBounds(40, 333, 455, 65);
        mainPanel.add(tfLastName);
        
        lblInvalidLN.setForeground(new Color(255, 0, 0));
        lblInvalidLN.setText("");
        lblInvalidLN.setBounds(40, 410, 190, 18);
        mainPanel.add(lblInvalidLN);
        
        JLabel lblGoBy = new JLabel();
        lblGoBy.setForeground(new Color(255, 255, 255));
        lblGoBy.setFont(kronaFont);
        lblGoBy.setText("Prefer to be ");
        lblGoBy.setBounds(40, 470, 213, 18);
        mainPanel.add(lblGoBy);
        
        JLabel lblGoBy2 = new JLabel();
        lblGoBy2.setForeground(new Color(255, 255, 255));
        lblGoBy2.setFont(kronaFont);
        lblGoBy2.setText("called by:");
        lblGoBy2.setBounds(40, 485, 213, 18);
        mainPanel.add(lblGoBy2);
        
        int indexForCBX;
        if(currentSessionUser.prefferGoBy == 'N')
        {
            indexForCBX = 0;
        } else if(currentSessionUser.prefferGoBy == 'F')
        {
            indexForCBX = 1;
        } else 
        {
            indexForCBX = 2;
        }
        cbxPrefferedGoBy.setSelectedIndex(indexForCBX);
        cbxPrefferedGoBy.setBounds(300, 470, 175, 55);
        mainPanel.add(cbxPrefferedGoBy);
        
        JButton btnSave = new JButton();
        btnSave.setText("Save changes");
        btnSave.setBounds(660, 330, 195, 60);
        btnSave.addActionListener( AL->saveAccountChangesClicked() );
        mainPanel.add(btnSave);
        
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }
    
    /**
     * Once the button to save the changes is clicked, the function validates all the fields to 
     * ensure the user has not provided any empty fields in which case a relevant label will appear
     * under the field with a missing value. If all the fields have passed validation, a confirmation
     * pop up appears asking user to confirm their action, to ensure the user attributes are not 
     * overwritten by accident. If the user response is 0 (clicked 'yes' on the pop up window) then
     * all the values are assigned to temporary variable and the method changeUnessentialAttributes
     * of the object currentSessionUser is used to overwrite the attributes of that instance of the user
     * then using the method of user list the current user is overwritten in the array with all the existing
     * users and this new data is saved to the file that holds the data about all the users
       */
    public void saveAccountChangesClicked()
    {
        
        boolean valid = true;
        if(tfFirstName.getText().equals(""))
        {
            valid = false;
            lblInvalidFN.setText("Name is required");
        } else
        {
            lblInvalidFN.setText("");
        }
            
        if(tfLastName.getText().equals(""))
        {
            valid = false;
            lblInvalidLN.setText("Last name is required");
        } else
        {
            lblInvalidLN.setText("");
        }
            
        if(valid)
        {
            int response = JOptionPane.showConfirmDialog(null, "You are about to rewrite your account details. Is that alright?", "Confirmation required", JOptionPane.YES_NO_OPTION);
            if(response == 0)
            {
                String name = tfFirstName.getText();
                String surname = tfLastName.getText();
                char addressBy;
                if(cbxPrefferedGoBy.getSelectedIndex() == 0)
                {
                    addressBy = 'N';
                } else if(cbxPrefferedGoBy.getSelectedIndex() == 1)
                {
                    addressBy = 'F';
                } else
                {
                    addressBy = 'L';
                }
                    
                currentSessionUser.changeUnessentialAttributes(name, surname, addressBy);
                    
                UserList allUsers = new UserList();
                allUsers.readUsersFromFile();
                allUsers.changeUserAttributesAndSave(currentSessionUser);
                allUsers = new UserList();
                currentSessionUser.decryptUserAttributes();
                    
                JOptionPane.showMessageDialog(null, "Successfully changed your attributes!", "Success", JOptionPane.PLAIN_MESSAGE);
                loadHomeComponents();
                
            }
            
        }
        
    }
    
    /**
     * This function works the same way as loadTutorial of the class AuthenticationGUI. It compares
     * the value of the global currentPage variable that is updated whenever new components are
     * load to ensure it always represents the correct page. Relevant message is then saved to the
     * message variable which is then used to open up a pop up window with a relevant tutorial explaining
     * how to navigate and use the page the user is on.
       */
    public void giveHelp()
    {
        String message = "";
        if(currentPage.equals("Home Page"))
        {
            message = "This is a home page, if you have any playlists - the most recent one is going to be displayed at the middle. \nClick on it to go directly to that playlist. If you don't have a playlist yet, press create playlist (big plus sign) \nto make a new playlist. If you press log out the system will automatically shut down and you'll have to log back in. \nIf you don't like the way we address you, you can change it by going to your account setting (circle icon with a person top left of the sidebar).";
        } else if (currentPage.equals("Playlist Page"))
        {
            message = "This is the playlist you created. You can press the play button to play the song or skip to the next or previous if you have more than one song. \nDown on the page you will find the table with all your songs, double click it to play any song from that table or select the song by clicking once and press 'backspace' \nto permanently delete the song from the playlist. Pressing the sort button will sort your playlist by the rating of each song, refreshing your playlist will cause to \nappear the same way it is stored in our database. Press the big plus button to add a song to the playlist, for now you can only add one song at a time.\nIf your library has more than 3 playlists than you will see the covers of 4 other playlists alongside your current playlist. Click on the cover to switch to that playlist. \nIf your find yourself in the need to search the playlist use the search bar located at the top. It allows you to search by: \ntitle of the song, artist name, genre and album the song is from. \nJust start typing and the table will automatically refresh when a match is found (search is case sensetive).";
        } else if(currentPage.equals("Playlist Create Page"))
        {
            message = "This page allows you to create your personally unique playlist. Fields marked by '*' are required fields, you cannot create a playlist without filling them first. \nWhen you fill all the data for the song press add song and it will be added to the temporary array that you can see on the right. \nBefore exiting do not forget to save your playlist. \nNotice that your library can't have 2 playlist of the same name as it would be quite confusing. \nDon't worry about filling all the data correctly, if it's wrong the system will tell you so before saving the playlist.";
        } else if(currentPage.equals("Add Song Page"))
        {
            message = "This page allows you to add a new song to your existing palylist. Right now only one song can be added at a time. Similar to playlist creation, \nrequired fields are marked with '*'. Before quiting do not forget to press add to playlist to save your new song to the playlist.";
        } else if(currentPage.equals("Library Page"))
        {
            message = "This is your personal library, no one else can see it exept you. If you have any playlists they will be displayed here, click on the cover to go to that playlist. \nIf you do not have any playlists yet, try clicking the big plus button to make a new one.";
        } else if(currentPage.equals("Account Page"))
        {
            message = "This is your personal account page. It allows you to modify some of the details about yourself, including how you want your \n system to call you, e.g. your first name, full name or your login. Before quiting this page do not forget to press the save chnages button \nfor any modifications you've made to take place.";
        }
        JOptionPane.showMessageDialog(null, message, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void loadMainPanel()
    {
        loadHomeComponents();
        /*
         * This will save all the components to the main panel before displaying it to the user in the frame
           */
    }
    
    /**
     * This function takes an integer parameter which allows the function to load the components
     * of the playlist object that is located at the index of allPlaylist array passed to the function
     * It gets the object of current playlist from the array and saves it to the global instance of current
     * playlist. It will use the attributes of the current playlist to display the correct playlist cover
     * and load the correct file with the songs of the playlist. Function will then calculate the array 
     * positions of next and previous playlists and if the user has at least 4 playlists in their library
     * the covers of the other playlists will appear to the sided of the cover of current playlist with
     * mouse listener assigned on each cover to load the playlist components of that particular playlist
     * (similar to iTunes). Mouse listeners are then assigned to media control buttons to load the relevant 
     * songs once pressed or play/pause. The table is then initialised with all the songs from the current
     * playlist loaded in. Labels with mouse listeners are added to the side of the table to allow
     * sorting, refreshing and adding of songs to the current playlist
       */
    public void loadPlaylistComponents(int indexOfPlaylist)
    {
        mainPanel.removeAll();
        clearInPlaylistSongsTable();
        
        currentPage = "Playlist Page";
        currentlyPlayingSongSource = "";
        currentSong = new CustomSong();
        indexOfCurrentSong = 0;
        
        
        currentPlaylist = allPlaylists.allPlaylists[indexOfPlaylist];
        
        CustomPlaylist nextAlbum = new CustomPlaylist();
        CustomPlaylist nextAlbum2 = new CustomPlaylist();
        CustomPlaylist previousAlbum = new CustomPlaylist();
        CustomPlaylist previousAlbum2 = new CustomPlaylist();
        int nextPosition = 0;
        int nextPosition2 = 0;
        int previousPosition = 0;
        int previousPosition2 = 0;
        
        tfSearchBar.setBounds(177, 9, 360, 42);
        tfSearchBar.setForeground(new Color(67, 67, 67));
        tfSearchBar.setText("Search the playlist...");

        tfSearchBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if(tfSearchBar.getText().equals("Search the playlist..."))
                {
                    tfSearchBar.setText("");
                    tfSearchBar.setForeground(Color.black);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (tfSearchBar.getText().isEmpty()){
                    tfSearchBar.setForeground(new Color(67, 67, 67));
                    tfSearchBar.setText("Search the playlist...");
                }
            }
        });
        /*
        * Adds a focus listener, when the search bar is clicked, it will remove the text if the text is a placeholder.
        * If the search bar has been clicked off it will add a placeholder text back. If the text is not placeholder it will
        * remain whatever text was set to be as it is a search term.*/

        tfSearchBar.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e) {
                searchPlaylist();
            }
        });
        tfSearchBar.setBackground(new Color(91, 124, 112));
        mainPanel.add(tfSearchBar);
        
        btnSearch.setIcon(new ImageIcon("assets/Search.png"));
        btnSearch.setBackground(new Color(91, 124, 112));
        btnSearch.setOpaque(false);
        btnSearch.setBorder(null);
        btnSearch.setContentAreaFilled(false);
        /*
           By setting opaque to true, the button will definetly change a 
           background colour no matter what system is the program is going to 
           be executed on
           */
        btnSearch.setBounds(538, 9, 44, 42);
        
        mainPanel.add(btnSearch);
        
        ImageIcon currentAlbum = new ImageIcon(new ImageIcon(currentPlaylist.coverSource).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
        JLabel lblCurrentAlbum = new JLabel();
        lblCurrentAlbum.setIcon(currentAlbum);
        lblCurrentAlbum.setBounds(290, 82, 180, 180);
        /*
           Currently playing cover of the track is pulled from the assests directory. getImage and getScaledInstance allows to change the dimensions of the 
           image before parsing it back as an icon. New local label is then created which is assigned with an icon (previously pulled and resized album cover) 
           */
        mainPanel.add(lblCurrentAlbum);
        
        
        
        
        
        if(allPlaylists.position >= 4)
        {
            if(indexOfPlaylist == 0)
            {
                previousPosition = allPlaylists.position-1;
                previousPosition2 = allPlaylists.position-2;
            } else if(indexOfPlaylist == 1)
            {
                previousPosition = indexOfPlaylist-1;
                previousPosition2 = allPlaylists.position-1;
            } else
            {
                previousPosition = indexOfPlaylist-1;
                previousPosition2 = indexOfPlaylist-2;
            }
            
            previousAlbum = allPlaylists.allPlaylists[previousPosition];
            previousAlbum2 = allPlaylists.allPlaylists[previousPosition2];
            
            if(indexOfPlaylist == allPlaylists.position-1)
            {
                nextPosition = 0;
                nextPosition2 = 1;
            } else if(indexOfPlaylist == allPlaylists.position-2)
            {
                nextPosition = indexOfPlaylist+1;
                nextPosition2 = 0;
            } else
            {
                nextPosition = indexOfPlaylist+1;
                nextPosition2 = indexOfPlaylist+2;
            }
            nextAlbum = allPlaylists.allPlaylists[nextPosition];
            nextAlbum2 = allPlaylists.allPlaylists[nextPosition2];
            
            
            ImageIcon previousAlbumBigger = new ImageIcon(new ImageIcon(previousAlbum.coverSource).getImage().getScaledInstance(138, 138, Image.SCALE_SMOOTH));
            JLabel lblPreviousAlbumBigger = new JLabel();
            lblPreviousAlbumBigger.setIcon(previousAlbumBigger);
            lblPreviousAlbumBigger.setBounds(140, 108, 138, 138);
            int index = previousPosition;
            
            /*
             * The reason for assigning a unique index variable with the previously established position variable instead of just passing that previous variable as a parameter to 
             * the loading function is because java only allows to pass such parameters if they are final or esentially final which means they will not change after they are passed in a function
             * essentially acting as a constant, the nextAlbum and others however can't be constants as they need to change depending on the playlist and also need to be cleared (=0) at the 
             * start of this loading function
               */
            lblPreviousAlbumBigger.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e)
                {
                    lblPreviousAlbumBigger.setLocation(140, 98);
                }
                public void mouseExited(MouseEvent e)
                {
                    lblPreviousAlbumBigger.setLocation(140, 108);
                }
                /*
                 * In the following mouse listener functions of class mouse adapter are overwriten to change the y position of the label that 
                 * contains the album covers when the mouse is hovered over them. When the mouse is no longer hovering over that album, the
                 * label's position is reset to it's original
                   */
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(index);
                }
            });
            
            mainPanel.add(lblPreviousAlbumBigger);
            
            ImageIcon previousAlbumSmaller = new ImageIcon(new ImageIcon(previousAlbum2.coverSource).getImage().getScaledInstance(113, 117, Image.SCALE_SMOOTH));
            JLabel lblPreviousAlbumSmaller = new JLabel();
            lblPreviousAlbumSmaller.setIcon(previousAlbumSmaller);
            lblPreviousAlbumSmaller.setBounds(45, 118, 113, 117);
            int index1 = previousPosition2;
            lblPreviousAlbumSmaller.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e)
                {
                    lblPreviousAlbumSmaller.setLocation(45, 98);
                }
                public void mouseExited(MouseEvent e)
                {
                    lblPreviousAlbumSmaller.setLocation(45, 118);
                }
                /*
                 * In the following mouse listener functions of class mouse adapter are overwriten to change the y position of the label that 
                 * contains the album covers when the mouse is hovered over them. When the mouse is no longer hovering over that album, the
                 * label's position is reset to it's original
                   */
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(index1);
                }
            });
            
            mainPanel.add(lblPreviousAlbumSmaller);
            
            
            ImageIcon nextAlbumBigger = new ImageIcon(new ImageIcon(nextAlbum.coverSource).getImage().getScaledInstance(138, 138, Image.SCALE_SMOOTH));
            JLabel lblNextAlbumBigger = new JLabel();
            lblNextAlbumBigger.setIcon(nextAlbumBigger);
            lblNextAlbumBigger.setBounds(483, 108, 138, 138);
            int index2 = nextPosition;
            lblNextAlbumBigger.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e)
                {
                    lblNextAlbumBigger.setLocation(483, 95);
                }
                public void mouseExited(MouseEvent e)
                {
                    lblNextAlbumBigger.setLocation(483, 108);
                }
                /*
                 * In the following mouse listener functions of class mouse adapter are overwriten to change the y position of the label that 
                 * contains the album covers when the mouse is hovered over them. When the mouse is no longer hovering over that album, the
                 * label's position is reset to it's original
                   */
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(index2);
                    
                }
            });
            
            mainPanel.add(lblNextAlbumBigger);
            
            ImageIcon nextAlbumSmaller = new ImageIcon(new ImageIcon(nextAlbum2.coverSource).getImage().getScaledInstance(113, 117, Image.SCALE_SMOOTH));
            JLabel lblNextAlbumSmaller = new JLabel();
            lblNextAlbumSmaller.setIcon(nextAlbumSmaller);
            lblNextAlbumSmaller.setBounds(615, 118, 113, 117);
            int index3 = nextPosition2;
            lblNextAlbumSmaller.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e)
                {
                    lblNextAlbumSmaller.setLocation(615, 95);
                }
                public void mouseExited(MouseEvent e)
                {
                    lblNextAlbumSmaller.setLocation(615, 118);
                }
                /*
                 * In the following mouse listener functions of class mouse adapter are overwriten to change the y position of the label that 
                 * contains the album covers when the mouse is hovered over them. When the mouse is no longer hovering over that album, the
                 * label's position is reset to it's original
                   */
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(index3);
                }
            });
            
            mainPanel.add(lblNextAlbumSmaller);
            
            /*
               By default the bigger image overlaps smaller one which creates a depth effect of album covers placed in order (previous 2, current, next 2)
               */
        }
        
        currentSongList.readAllSongsFromFile(currentUserID, currentPlaylist.title);
        
        currentSong = currentSongList.allSongs[indexOfCurrentSong]; //Start with the 1st song in the playlist when it just loads
        loadSong(currentSong);
        JLabel lblAlbumTitle = new JLabel();
        lblAlbumTitle.setForeground(new Color(255, 255, 255));
        lblAlbumTitle.setText(currentPlaylist.title);
        lblAlbumTitle.setBounds(350, 287, 700, 23);
        mainPanel.add(lblAlbumTitle);
        
        
        lblArtistName.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblArtistName.setForeground(new Color(255, 255, 255));
        lblArtistName.setText(currentSong.artist);
        lblArtistName.setBounds(345, 310, 700, 26);
        mainPanel.add(lblArtistName);
        
        JLabel lblSkipBack = new JLabel();
        lblSkipBack.setIcon(new ImageIcon("assets/skip_previous.png"));
        
        lblSkipBack.setBounds(300, 360, 34, 34);
        mainPanel.add(lblSkipBack);
        lblSkipBack.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadPreviousSong();
            }
        });
        
        
        if(mediaIsPlaying){
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/play.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));    
        } else{
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/pause.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        }
        
        lblPause.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadSong(currentSong);
                pauseBtnClicked();
            }
        });
        lblPause.setBounds(355, 360, 40, 40);
        mainPanel.add(lblPause);
        
        JLabel lblSkipNext = new JLabel();
        lblSkipNext.setIcon(new ImageIcon("assets/skip_next.png"));
        lblSkipNext.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadNextSong();
            }            
        });
        lblSkipNext.setBounds(410, 360, 34, 34);
        mainPanel.add(lblSkipNext);
        
        
        lblNowPlaying.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblNowPlaying.setForeground(new Color(255, 255, 255));
        lblNowPlaying.setText(currentSong.title);
        lblNowPlaying.setBounds(340, 400, 700, 23);
        mainPanel.add(lblNowPlaying);
        
        populateInPlaylistTable();
        scrollableInPlaylistTable.setBounds(25, 450, 660, 190);
        songsInPlaylistTable.setColumnSelectionAllowed(false);
        songsInPlaylistTable.setRowSelectionAllowed(true);
        
        songsInPlaylistTable.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2)
                {
                    playSelectedSong();
                }
            }
        });
        
        songsInPlaylistTable.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                    deleteSongFromPlaylist();
                }
            }
        });
        /*
         * Adds a key listener to the table to call the deleteSongFromPlaylist
         * when the backspace is pressed on slected in table items
           */
        mainPanel.add(scrollableInPlaylistTable);
        
        lblAdd.setIcon(new ImageIcon("assets/add_circle.png"));
        
        lblAdd.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e)
            {
                lblAdd.setLocation(718, 470);
            }
            public void mouseExited(MouseEvent e)
            {
                lblAdd.setLocation(708, 470);
            }
            
        });
        lblAdd.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadAddToExistingPlaylistComponents(indexOfPlaylist);
            }
        });
        
        lblAdd.setBounds(708, 470, 166, 51);
        lblAdd.setForeground(new Color(255, 255, 255));
        lblAdd.setText("Add song");
        mainPanel.add(lblAdd);
        
        
        JLabel lblSort = new JLabel();
        lblSort.setIcon(new ImageIcon(new ImageIcon("assets/sorting.png").getImage().getScaledInstance(53, 51, Image.SCALE_SMOOTH)));
        lblSort.setForeground(new Color(255, 255, 255));
        lblSort.setText("Sort by rating");
        
        lblSort.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e)
            {
                lblSort.setLocation(718, 530);
            }
            public void mouseExited(MouseEvent e)
            {
                lblSort.setLocation(708, 530);
            }
            public void mouseClicked(MouseEvent e)
            {
                sortInPlaylistTable();
            }
        });
        
        lblSort.setBounds(708, 530, 166, 51);
        mainPanel.add(lblSort);
        
        JLabel lblRefresh = new JLabel();
        lblRefresh.setIcon(new ImageIcon(new ImageIcon("assets/refresh.png").getImage().getScaledInstance(53, 51, Image.SCALE_SMOOTH)));
        lblRefresh.setForeground(new Color(255, 255, 255));
        lblRefresh.setText("Refresh Table");
        String title = currentPlaylist.title;
        lblRefresh.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e)
            {
                lblRefresh.setLocation(718, 590);
            }
            public void mouseExited(MouseEvent e)
            {
                lblRefresh.setLocation(708, 590);
            }
            public void mouseClicked(MouseEvent e)
            {
                
                refreshInPlaylistTable(title);
            }
        });
        
        lblRefresh.setBounds(708, 590, 166, 51);
        mainPanel.add(lblRefresh);
        
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }





    /**
     * Overwrites the default cell renderer class to make the text of the currently playing song green
     * while keeping all other cells black. It uses for loop to update each cell of each column with the new overwritten
     * renderer before using repaint to apply update to the graphical table in the interface each time the function
     * is called*/
    public void highlightNowPlayingInTable(){

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected){
                    if (row == indexOfCurrentSong){
                        c.setForeground(new Color(55, 172, 17));
                    } else {
                        c.setForeground(new Color(0, 0, 0));
                    }
                    c.setBackground(new Color(255, 255, 255));
                }

                return c;
            }
        };

        for (int i=0; i<songsInPlaylistTable.getColumnCount(); i++)
        {
            songsInPlaylistTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        songsInPlaylistTable.repaint();
    }






    /**
     * Function gets the selected row from the table and pulls that song from the array
     * as it's row index is the same as it's position in the currentPlaylist array.
     * Current song holds that object from the array of class CustomSong. It will
     * pass that song to the loadSong function and will call the pauseBtnClicked to
     * change the state of the pause button
       */
    public void playSelectedSong()
    {
        int selectedRow = songsInPlaylistTable.getSelectedRow();
        currentSong = currentSongList.allSongs[selectedRow];
        loadSong(currentSong);
        mediaIsPlaying = false;
        pauseBtnClicked();
    }
    
    /**
     * Once the user has pressed the backspace on the selected rows. The function asks 
     * for confirmation before proceeding with the deletion of the songs. If the user clicked
     * 'yes' on the pop up window the array of indecies of selected rows is created. Then for
     * loop iterates through that array pulling each song from the global array of the playlist
     * and assigning the name of that song to the string variable beofre passing it to the 
     * method of class CustomSongList deleteSong that will permanently delete the
     * song from the playlist.
       */
    public void deleteSongFromPlaylist()
    {
        int choice = JOptionPane.showConfirmDialog(null, "You are about to delete selected songs from this playlist. \nIs that ok?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(choice == 0)
        {
            int[] selectedRows = songsInPlaylistTable.getSelectedRows();
            for(int i=0; i<selectedRows.length; i++)
            {
                String songName = songsInPlaylistTable.getValueAt(i, 0).toString();
                currentSongList.deleteSong(currentUserID, currentPlaylist.title, songName);
            }
        }
        refreshInPlaylistTable(currentPlaylist.title);
    }
    
    /**
     * Function ensures that the current song is not the last song in the
     * playlist. If it isn't the currentSongIndex is incremented to point
     * at the next song in the array, that song is then pulled from the array
     * and saved to current song which is passed to the loadSong function.
     * The pause button also changes the icon to represent the media state as
     * paused
       */
    public void loadNextSong()
    {
        if(indexOfCurrentSong < currentSongList.position)
        {
            indexOfCurrentSong++;
            currentSong = currentSongList.allSongs[indexOfCurrentSong];
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/pause.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            loadSong(currentSong);
            mediaIsPlaying = false;
            pauseBtnClicked();
        }
    }
    
    /**
     * Works in the same way as load next song, but checks that the song is not first in the playlist before
     * changing the state of the index of current song and current song itself to load the previous song
       */
    public void loadPreviousSong()
    {
        if(indexOfCurrentSong > 0)
        {
            indexOfCurrentSong--;
            currentSong = currentSongList.allSongs[indexOfCurrentSong];
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/pause.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            loadSong(currentSong);
            mediaIsPlaying = false;
            pauseBtnClicked();
        }
    }
    
    /**
     * This function ensures that the passed parameter of the song object is not null. Then if the
     * media player has been initialised before, it will use dispose method to remove the instance of 
     * that class before creating the new instance of the same class that will have different attributes
     * to allow control of whatever music is being passed as parameter. After that instance of media is
     * overwritten with the source attribute of the object passed as parameter which is a path to the 
     * song source that is saved in the user directory. Instance of mediaPlayer is then overwritten to
     * provide control of updated media (song). Relevant labels are also updated to show the title and
     * artist name of the current song. setOnEndOfMedia function is used to change the state of variable
     * mediaIsPlaying to false and the icon of label pause button to change it's state to pause as the
     * song has now finished playing
       */
    public void loadSong(CustomSong song)
    {
        if(song != null)
        {
            if(mediaPlayer != null)
            {
                mediaPlayer.dispose();
            }
            media = new Media(new File(song.source).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            lblArtistName.setText(song.artist);
            lblNowPlaying.setText(song.title);
            mediaPlayer.setOnEndOfMedia( () -> {
                mediaIsPlaying = false;
                currentSongList.changeListensNumOfSong(indexOfCurrentSong, currentPlaylist.title, currentUserID);
                lblPause.setIcon(new ImageIcon(new ImageIcon("assets/pause.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
                clearInPlaylistSongsTable();
                populateInPlaylistTable();
            });
            highlightNowPlayingInTable();
        }
    }
    
    /**
     * This function checks the state of global variable mediaIsPlaying
     * to correctly display the label with the pause button to show the user
     * whether the song is playing or not
       */
    public void pauseBtnClicked()
    {
        if(!mediaIsPlaying)
        {
            mediaPlayer.play();
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/play.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            mediaIsPlaying = true;
            
        } else
        {
            mediaPlayer.pause();
            lblPause.setIcon(new ImageIcon(new ImageIcon("assets/pause.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            mediaIsPlaying = false;
            
        }
    }
    
    /**
     * Whenever user interacts with the search bar inside the playlist by typing in it, this function is called.
     * It will call the method findSongByStringValues, pass the text of the searchbar to the methods and split 
     * the returned string by the comma, creating an array with all the results. Function is then called to clear
     * the table (remove all the elements from it). For loop will then iterate through the array with all the indecies
     * of the found songs. Each of these songs is then saved to the temporary song object and each attribute is assigned 
     * to the found songs array that is the same lenght as there are headings in the table, this creates a row which is
     * then added to the playlist table model
       */
    public void searchPlaylist()
    {
        String[] songIndex = currentSongList.findSongByStringValues(tfSearchBar.getText()).split(",");
        clearInPlaylistSongsTable();
        CustomSong tempSong = new CustomSong();
        String[] foundSongs = new String[7]; //Search results of actual songs objects
        try{
            for(int i=0; i<songIndex.length; i++)
            {
                int index = Integer.parseInt(songIndex[i]);
                tempSong = new CustomSong();
                tempSong = currentSongList.allSongs[index];
                foundSongs[0] = tempSong.title;
                foundSongs[1] = tempSong.duration+"";
                foundSongs[2] = tempSong.rating+"";
                foundSongs[3] = tempSong.album;
                foundSongs[4] = tempSong.artist;
                foundSongs[5] = tempSong.genre;
                foundSongs[6] = tempSong.totalListens+"";
                
                playlistTableModel.addRow(foundSongs);
            }
        } catch(Exception e)
        {
            ;
        }
    }
    
    /**
     * Loads all the components to the playlist panel, locates them around the panel and adds 
     * the required action listeners to the buttons
       */
    public void loadAddToExistingPlaylistComponents(int indexOfPlaylist)
    {
        mainPanel.removeAll();
        currentPage = "Add Song Page";
        CustomPlaylist currentPlaylist = allPlaylists.allPlaylists[indexOfPlaylist];
        singleSongOrigin = "";
        singleSongDestination = "";
        
        JLabel lblCurrentPlaylist = new JLabel();
        lblCurrentPlaylist.setFont(kronaFont);
        lblCurrentPlaylist.setForeground(new Color(255,255,255));
        lblCurrentPlaylist.setText("You're adding to: "+currentPlaylist.title);
        lblCurrentPlaylist.setBounds(280, 20, 300, 18);
        mainPanel.add(lblCurrentPlaylist);
        
        JLabel lblSongName = new JLabel();
        lblSongName.setFont(kronaFont);
        lblSongName.setForeground(new Color(255,255,255));
        lblSongName.setText("*Song name: ");
        lblSongName.setBounds(280, 116, 300, 18);
        mainPanel.add(lblSongName);
        
        tfSongName.setBounds(280, 146, 303, 42);
        tfSongName.setText("");
        mainPanel.add(tfSongName);
        
        JLabel lblArtist = new JLabel();
        lblArtist.setFont(kronaFont);
        lblArtist.setForeground(new Color(255,255,255));
        lblArtist.setText("*Artist Name: ");
        lblArtist.setBounds(280, 208, 300, 18);
        mainPanel.add(lblArtist);
        
        tfArtistName.setBounds(280, 238, 303, 42);
        tfArtistName.setText("");
        mainPanel.add(tfArtistName);
        
        JLabel lblDuration = new JLabel();
        lblDuration.setFont(kronaFont);
        lblDuration.setForeground(new Color(255,255,255));
        lblDuration.setText("*Duration:");
        lblDuration.setBounds(280, 300, 141, 18);
        mainPanel.add(lblDuration);
        
        tfDuration.setText("");
        tfDuration.setBounds(280, 330, 90, 42);
        mainPanel.add(tfDuration);
        
        JLabel lblTotalListens = new JLabel();
        lblTotalListens.setFont(kronaFont);
        lblTotalListens.setForeground(new Color(255,255,255));
        lblTotalListens.setText("*Total Listens:");
        lblTotalListens.setBounds(420, 300, 300, 18);
        mainPanel.add(lblTotalListens);
        
        tfTotalListens.setBounds(420, 330, 133, 42);
        tfTotalListens.setText("");
        mainPanel.add(tfTotalListens);
        
        JLabel lblRating = new JLabel();
        lblRating.setFont(kronaFont);
        lblRating.setForeground(new Color(255,255,255));
        lblRating.setText("Rating: ");
        lblRating.setBounds(280, 390, 150, 18);
        mainPanel.add(lblRating);
        
        cbxRating.setBounds(280, 422, 90, 42);
        cbxRating.setSelectedIndex(0);
        mainPanel.add(cbxRating);
        
        JLabel lblGenre = new JLabel();
        lblGenre.setFont(kronaFont);
        lblGenre.setForeground(new Color(255,255,255));
        lblGenre.setText("Genre: ");
        lblGenre.setBounds(420, 390, 143, 18);
        mainPanel.add(lblGenre);
        
        cbxGenre.setBounds(420, 422, 143, 42);
        cbxGenre.setSelectedIndex(0);
        mainPanel.add(cbxGenre);
        
        JLabel lblAlbum = new JLabel();
        lblAlbum.setFont(kronaFont);
        lblAlbum.setForeground(new Color(255,255,255));
        lblAlbum.setText("Album: ");
        lblAlbum.setBounds(280, 492, 141, 18);
        mainPanel.add(lblAlbum);
        
        tfAlbum.setBounds(280, 522, 303, 42);
        tfAlbum.setText("");
        mainPanel.add(tfAlbum);
        
        JButton btnAddSong = new JButton();
        btnAddSong.setFont(kronaFont);
        btnAddSong.setText("Add Song & Exit");
        btnAddSong.addActionListener( AL->addSongToExistingListClicked(currentPlaylist.title, indexOfPlaylist) );
        btnAddSong.setBounds(280, 600, 300, 44);
        mainPanel.add(btnAddSong);
        
        JLabel lblMusicFile = new JLabel();
        lblMusicFile.setForeground(new Color(255,255,255));
        lblMusicFile.setFont(kronaFont);
        lblMusicFile.setText("*Music File: ");
        lblMusicFile.setBounds(640, 125, 180, 18);
        mainPanel.add(lblMusicFile);
        
        JButton btnChooseMusic = new JButton();
        btnChooseMusic.setFont(kronaFont);
        btnChooseMusic.setText("Upload Music file");
        btnChooseMusic.setBounds(640, 155, 141, 44);
        btnChooseMusic.addActionListener( AL->chooseSingleMusicFile() );
        mainPanel.add(btnChooseMusic);
        
        lblMusicSourcePreview.setText(singleSongOrigin);
        lblMusicSourcePreview.setForeground(new Color(255,255,255));
        lblMusicSourcePreview.setBounds(640, 200, 300, 18);
        mainPanel.add(lblMusicSourcePreview);
        
        mainPanel.add(lblInvalidArtistName);
        mainPanel.add(lblInvalidDuration);
        mainPanel.add(lblInvalidMusicFile);
        mainPanel.add(lblInvalidSongName);
        mainPanel.add(lblInvalidTotalListens);
        mainPanel.add(lblInvalidPlaylistTitle);
        
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }
    
    /**
     * Initiates an object of JFileChooser which is a pop up window that allows user to select a file. setFileFilter is
     * used to limit the user choice to music file extensions (First string is the text that is displayed, the rest are 
     * allowed extensions). showOpenDialog is used to actually open the pop up window itself and safe the user's response
     * to the variable. If the user selected a file then a new instance of file is initiated which stores the file selected
     * by user from the pop up window. Then the selected file's absolute path is stored to the global variable which is going to 
     * be used later to copy the file to user directory. The destination of the song is also saved as the directory of all users
     * + user id + the name of the file. If the user has selected a music file then the full path to it on the system is displayed 
     * under the button and a tooltip text is set to show a full path when the mouse hovers over it. Otherwise no preview is displayed
       */
    public void chooseSingleMusicFile()
    {
        JFileChooser chooserMusic = new JFileChooser();
        chooserMusic.setFileFilter(new FileNameExtensionFilter("Music Only", "mp3", "wav", "m4a"));
        /*
         * Sets file extension filter for the filechooser allowing user to only open images with the extensions passed to the object 
         * of class FileNameExtensionFilter
           */
        int result = chooserMusic.showOpenDialog(chooserMusic);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = chooserMusic.getSelectedFile();
            singleSongOrigin = selectedFile.getAbsolutePath();
            singleSongDestination = "users/"+currentUserID+"/"+chooserMusic.getSelectedFile().getName();
        }
        
        if(singleSongOrigin.equals("") == false)
        {
            lblMusicSourcePreview.setText(singleSongOrigin);
            lblMusicSourcePreview.setBounds(640, 200, 180, 18);
            lblMusicSourcePreview.setToolTipText(singleSongOrigin);
            lblInvalidMusicFile.setText("");
        } else
        {
            lblMusicSourcePreview.setText("");
        }
    }
    
    /**
     * Takes 2 parameters, the playlist title and it's index location in the global array. It will set the valid variable 
     * to true. If any of the required fields are empty it will display a label asking user to fill out the fields and
     * will not proceed with adding a song. It will try to parse the numerical values like duration and total listens to
     * their intended data types if an error is encounted then the user has not provided the correct data type and will be
     * asked to re enter the data. If the global variable stroing the song source origin is empty a label will also appear
     * asking to provide a song file. If all fields have passed validation, the function tries to copy the file from it's origin
     * to its destination inside this system. It then calls setAttributes on a temporary song object to create a new song
     * that song is then passed to the addSongtoexistingPlaylist which writes the song to the playlist file. All the fields are 
     * then cleared, the user recieves a confirmation pop up to confirm all changes were successful and is redirected back to 
     * the playlist they added a song to.
       */
    public void addSongToExistingListClicked(String playlistTitle, int playlistIndex)
    {
        CustomSong tempSong = new CustomSong();
        boolean valid = true;
        String tempSongName = "";
        String tempArtistName = "";
        String tempMusicSource = "";
        double tempDuration = 0;
        int tempTotalListens = 0;
        
        if(tfSongName.getText().equals(""))
        {
            lblInvalidSongName.setForeground(new Color(255, 0, 0));
            lblInvalidSongName.setText("Song name is required!");
            lblInvalidSongName.setBounds(280, 200, 190, 18);
            valid = false;
        } else {
            tempSongName = tfSongName.getText();
            lblInvalidSongName.setText("");
        }
        
        if(tfArtistName.getText().equals(""))
        {
            lblInvalidArtistName.setForeground(new Color(255,0,0));
            lblInvalidArtistName.setText("Artist name is required!");
            lblInvalidArtistName.setBounds(280, 290, 190, 18);
            valid = false;
        } else {
            tempArtistName = tfArtistName.getText();
            lblInvalidArtistName.setText("");
        }
        
        try{
            tempDuration = Double.parseDouble(tfDuration.getText());
            lblInvalidDuration.setText("");
        } catch(Exception e)
        {
            valid = false;
            lblInvalidDuration.setForeground(new Color(255, 0, 0));
            lblInvalidDuration.setText("Must be a number!");
            lblInvalidDuration.setBounds(280, 380, 141, 18);
        }
        
        try{
            tempTotalListens = Integer.parseInt(tfTotalListens.getText());
            lblInvalidTotalListens.setText("");
        } catch(Exception e)
        {
            lblInvalidTotalListens.setForeground(new Color(255,0,0));
            lblInvalidTotalListens.setText("Must be a number!");
            lblInvalidTotalListens.setBounds(420, 380, 141, 18);
            valid = false;
        }
        
        if(singleSongOrigin.equals(""))
        {
            lblInvalidMusicFile.setForeground(new Color(255,0,0));
            lblInvalidMusicFile.setText("Music source is required");
            lblInvalidMusicFile.setBounds(640, 200, 190, 18);
            valid = false;
        } else {
            lblInvalidMusicFile.setText("");
        }
        int tempRating = Integer.parseInt(cbxRating.getSelectedItem().toString());
        String tempGenre = cbxGenre.getSelectedItem().toString();
        
        
        if(valid)
        {
            try
            {
                Path origin = Paths.get(singleSongOrigin);
                Path destination = Paths.get(singleSongDestination);
                
                Files.copy(origin, destination, REPLACE_EXISTING);
                /*
                 * Tries to copy the file from computer's directory to its destination (a directory inside this system)
                 * If the file already exists, new file will replace the existing one to avoid any errors. 
                   */
                
                tempSong.setSongAttributes(tempSongName, tempDuration, tempRating, tfAlbum.getText(), tempArtistName, tempGenre, tempTotalListens, singleSongDestination);
                songList.addSongToExistingPlaylist(currentUserID, playlistTitle, tempSong);
                
                clearSongCreationFields();
                JOptionPane.showMessageDialog(null, "Song has been added to: "+playlistTitle, "Success", JOptionPane.PLAIN_MESSAGE);
            }
            catch (Exception e)
            {
                JOptionPane.showInternalMessageDialog(null, e+"", "Error while trying to save the song file", JOptionPane.ERROR_MESSAGE);
            }
            
            loadPlaylistComponents(playlistIndex);
        }
    }
    
    /**
     * Clears the table with all the songs, uses the sortSongByRating of global instance of songList which uses bubble sort
     * to sort all songs in array by rating, repopulates the table with the songs in array in a sorted order
       */
    public void sortInPlaylistTable()
    {
        clearInPlaylistSongsTable();
        currentSongList.sortSongByRating();
        populateInPlaylistTable();
    }
    
    /**
     * Clears all the songs from the table. Reinitiates the currentSongList object to ensure it is overwritten with new data 
     * safely. Calls the readAll method from the object to repopulate the array with all songs with songs in the order they are
     * in the file. Repopulates the table with songs in the order they are in an overwritten array of object currentSongList
       */
    public void refreshInPlaylistTable(String playlistTitle)
    {
        clearInPlaylistSongsTable();
        currentSongList = new CustomSongList();
        //Refreshes the array by reseting it allowing to reread the file creating a new array with elements added in the order they are in text file
        currentSongList.readAllSongsFromFile(currentUserID, playlistTitle);
        populateInPlaylistTable();
    }
    
    /**
     * Declares 2 variables to store the total number of songs in array as well as a counter to store the current position in the 
     * array. While loop will iterate, reading each element from the song array spliting all the objects attributes separated by comma
     * creating atemporary array of same size as the headings in the table. Each element in the temporary array is assigned with the attributes
     * of the temporary song object pulled from all songs array. That temporary array (one table row) is then added to the table as a row.
     * Counter is incremented and if it's same number as the number of elements in array, that means the function has reached the end of the
     * playlist so the loop is terminated
       */
    public void populateInPlaylistTable()
    {
        int numberOfSongs = currentSongList.position;
        int currentCountPosition = 0;
        while(currentCountPosition != numberOfSongs)
        {
            CustomSong tempSong = currentSongList.allSongs[currentCountPosition];
            
            String[] tempRow = new String[7];
            tempRow[0] = tempSong.title;
            tempRow[1] = tempSong.duration+"";
            tempRow[2] = tempSong.rating+"";
            tempRow[3] = tempSong.album;
            tempRow[4] = tempSong.artist;
            tempRow[5] = tempSong.genre;
            tempRow[6] = tempSong.totalListens+"";
            
            playlistTableModel.addRow(tempRow);
            currentCountPosition++;
            
            if(currentCountPosition == numberOfSongs)
            {
                break;
            }
        }
    }
    
    /**
     * Iterates through the table in reverse removing each individual row
       */
    public void clearInPlaylistSongsTable()
    {
        int numOfRows = playlistTableModel.getRowCount()-1;
        for(int i=numOfRows; i>=0; i--)
        {
            playlistTableModel.removeRow(i);
        }
    }
    
    /**
     * Clears the main panel from previously loaded components. Places all the components for the library page in the correct places and
     * adds them to the main panel. Assigns the mouse listeners to labels such as add playlist and playlist covers to make them act
     * as links redirecting the user to the relevant pages
       */
    public void loadLibraryComponents()
    {
        mainPanel.removeAll(); // Clears all the previously added components from the panel
        
        currentPage = "Library Page";
        
        JLabel lblAddPlaylist = new JLabel();
        lblAddPlaylist.setIcon(new ImageIcon(new ImageIcon("assets/add_circle.png").getImage().getScaledInstance(134, 132, Image.SCALE_SMOOTH)));
        lblAddPlaylist.setBounds(32, 131, 134, 132);
        lblAddPlaylist.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadPlaylistCreationComponents();
            }
        });
        mainPanel.add(lblAddPlaylist);
        
        JLabel lblCreate = new JLabel();
        lblCreate.setForeground(new Color(255, 255, 255));
        lblCreate.setText("Create Playlist");
        lblCreate.setBounds(40, 275, 156, 20);
        mainPanel.add(lblCreate);
        
        
        JPanel playlistCoversPanel = new JPanel(new GridLayout(2, 3));
        JScrollPane scrollablePlaylistCovers = new JScrollPane(playlistCoversPanel);

        allPlaylists = new CustomPlaylistList(); 
        //Reset the object to ensure all of its attributes inc. the array with all playlists is cleared from an previous instantiation
        allPlaylists.readDataFromPlaylistFile(currentUserID); 

//        int tempPosition = 0;
//        while(tempPosition < allPlaylists.position){
//            for (int i = 0; i < 3; i++){
//
//            }
//        }
        for(int i=0; i<allPlaylists.position; i++)
        {
            JPanel singleSongCoversPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = createGbc(0, 0);
            singleSongCoversPanel.setBackground(new Color(48, 76, 49));
            singleSongCoversPanel.setSize(180, 300);

            CustomPlaylist tempPlaylist = new CustomPlaylist();
            tempPlaylist = allPlaylists.allPlaylists[i];
            int index = i;
            ImageIcon playlistCoverImg = new ImageIcon(new ImageIcon(tempPlaylist.coverSource).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH));
            JLabel lblPlaylistCover = new JLabel();
            lblPlaylistCover.setIcon(playlistCoverImg);
            lblPlaylistCover.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(index);
                }
            });
            lblPlaylistCover.setSize(180, 180);
            singleSongCoversPanel.add(lblPlaylistCover, createGbc(0,0));
            

            JLabel lblTitle = new JLabel();
            lblTitle.setForeground(new Color(255, 255, 255));
            lblTitle.setText(tempPlaylist.title);
            lblTitle.setSize(500, 20);
            singleSongCoversPanel.add(lblTitle, createGbc(0,1));

//            JLabel lblEmpty = new JLabel();
//            lblEmpty.setSize(200, 15);
//            lblEmpty.setText("   ");
//            singleSongCoversPanel.add(lblEmpty);

            playlistCoversPanel.add(singleSongCoversPanel);
        }
        
        //playlistCoversPanel.setLayout(new BoxLayout(playlistCoversPanel, BoxLayout.LINE_AXIS));
        playlistCoversPanel.setBackground(new Color(48, 78, 48));
        playlistCoversPanel.setBorder(null);
        
        
        scrollablePlaylistCovers.setBounds(190, 131, 700, 500);
        scrollablePlaylistCovers.setBorder(null);
        mainPanel.add(scrollablePlaylistCovers);
        
        
        SwingUtilities.updateComponentTreeUI(mainFrame); //Forces the frame to be updated so that the user did not have to resize the window to force the pane update
    }

    private GridBagConstraints createGbc(int x, int y)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int gap = 3;
        gbc.insets = new Insets(gap, gap+2*gap*x, gap, gap);
        return gbc;
    }
    
    /**
     * Clears the main panel from previously loaded components, since it is the first page to be loaded when the user logs in, sets the background 
     * and bounds of the panel. Checks what the users prefered call is to display the correct welcome message. Adds all the components in correct
     * placed and adds them to the panel. Adds the mouse listener to make add playlist label redirect to playlist creation page. If the user has
     * any playlist in the library then the most recent one is placed as a label with the playlist's cover acting as a link to the page of that
     * playlist. Adds the action listener to shut down the system when the log out button is pressed
       */
    public void loadHomeComponents()
    {
        mainPanel.removeAll();
        currentPage = "Home Page";
        mainPanel.setBounds(250, 0, 930, 680);
        mainPanel.setBackground(new Color(48, 78, 48));
        
        String userGoBy = "";
        if(currentSessionUser.prefferGoBy == 'N')
        {
            userGoBy = currentSessionUser.firstName;
        } else if(currentSessionUser.prefferGoBy == 'F')
        {
            userGoBy = currentSessionUser.firstName + " "+ currentSessionUser.lastName;
        } else
        {
            userGoBy = currentSessionUser.login;
        }
        
        JLabel lblWelcome = new JLabel();
        lblWelcome.setForeground(new Color(255, 255, 255));
        lblWelcome.setBounds(340, 60, 500, 18);
        lblWelcome.setFont(kronaFont);
        lblWelcome.setText("Welcome "+userGoBy);
        mainPanel.add(lblWelcome);
        
        JLabel lblAddPlaylist = new JLabel();
        lblAddPlaylist.setIcon(new ImageIcon(new ImageIcon("assets/add_circle.png").getImage().getScaledInstance(134, 132, Image.SCALE_SMOOTH)));
        lblAddPlaylist.setBounds(100, 260, 134, 132);
        lblAddPlaylist.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            {
                loadPlaylistCreationComponents();
            }
        });
        mainPanel.add(lblAddPlaylist);
        
        JLabel lblCreate = new JLabel();
        lblCreate.setForeground(new Color(255, 255, 255));
        lblCreate.setFont(kronaFont);
        lblCreate.setText("Create Playlist");
        lblCreate.setBounds(90, 400, 156, 20);
        mainPanel.add(lblCreate);
        
        JLabel lblLatestPlaylist = new JLabel();
        lblLatestPlaylist.setForeground(new Color(255, 255, 255));
        lblLatestPlaylist.setFont(kronaFont);
        lblLatestPlaylist.setText("Most Recent Playlist");
        lblLatestPlaylist.setBounds(335, 230, 250, 18);
        mainPanel.add(lblLatestPlaylist);
    
        allPlaylists.readDataFromPlaylistFile(currentUserID);
        JLabel lblPlaylistCover = new JLabel();
        CustomPlaylist latestPlaylist = new CustomPlaylist();
        if(allPlaylists.position>0)
        {
            latestPlaylist = allPlaylists.allPlaylists[allPlaylists.position-1];
            lblPlaylistCover.setIcon(new ImageIcon(new ImageIcon(latestPlaylist.coverSource).getImage().getScaledInstance(155, 155, Image.SCALE_SMOOTH)));
            lblPlaylistCover.setBounds(360, 270, 155, 155);
            lblPlaylistCover.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e)
                {
                    loadPlaylistComponents(allPlaylists.position-1);
                }
            });
            mainPanel.add(lblPlaylistCover);
        }
        JButton btnLogOut = new JButton();
        btnLogOut.setText("Log out");
        btnLogOut.setBounds(670, 320, 195, 60);
        btnLogOut.addActionListener( AL->logOut() );
        mainPanel.add(btnLogOut);
        
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }
    
    /**
     * Reinitialises all the valuable variables to hold no sensetive information for security reasons. Sets the main frame to being not 
     * visible and uses System.exit to exit from the system, essentially shuting it down
       */
    public void logOut()
    {
        currentSessionUser = new User();
        currentUserID = "";
        mainFrame.setVisible(false);
        System.exit(0);
    }
    
    /**
     * Clears the main panel from any previous components, places all the labels and text fields in correct places 
     * adds the event listener to the buttons.
       */
    public void loadPlaylistCreationComponents()
    {
        mainPanel.removeAll();
        currentPage = "Playlist Create Page";
        songList = new CustomSongList();
        clearSongCreationFields();
        playlistCoverOrigin="";
        
        JLabel lblPlaylistTitle = new JLabel();
        lblPlaylistTitle.setFont(kronaFont);
        lblPlaylistTitle.setForeground(new Color(255,255,255));
        lblPlaylistTitle.setText("*Playlist Title:");
        lblPlaylistTitle.setBounds(280, 23, 160, 18);
        mainPanel.add(lblPlaylistTitle);
        
        
        tfPlaylistTitle.setBounds(280, 53, 303, 42);
        mainPanel.add(tfPlaylistTitle);
        
        JLabel lblSongName = new JLabel();
        lblSongName.setFont(kronaFont);
        lblSongName.setForeground(new Color(255,255,255));
        lblSongName.setText("*Song Name:");
        lblSongName.setBounds(22, 155, 141, 18);
        mainPanel.add(lblSongName);
        
        
        tfSongName.setBounds(22, 185, 303, 42);
        mainPanel.add(tfSongName);
        
        JLabel lblArtistName = new JLabel();
        lblArtistName.setFont(kronaFont);
        lblArtistName.setForeground(new Color(255,255,255));
        lblArtistName.setText("*Artist Name:");
        lblArtistName.setBounds(22, 247, 141, 18);
        mainPanel.add(lblArtistName);
        
        
        tfArtistName.setBounds(22, 277, 303, 42);
        mainPanel.add(tfArtistName);
        
        JLabel lblDuration = new JLabel();
        lblDuration.setFont(kronaFont);
        lblDuration.setForeground(new Color(255,255,255));
        lblDuration.setText("*Duration:");
        lblDuration.setBounds(22, 339, 141, 18);
        mainPanel.add(lblDuration);
        
        
        tfDuration.setBounds(22, 369, 90, 42);
        mainPanel.add(tfDuration);
        
        JLabel lblTotalListens = new JLabel();
        lblTotalListens.setFont(kronaFont);
        lblTotalListens.setForeground(new Color(255,255,255));
        lblTotalListens.setText("*Total Listens:");
        lblTotalListens.setBounds(163, 339, 300, 18);
        mainPanel.add(lblTotalListens);
        
        
        tfTotalListens.setBounds(163, 369, 133, 42);
        mainPanel.add(tfTotalListens);
        
        JLabel lblRating = new JLabel();
        lblRating.setFont(kronaFont);
        lblRating.setForeground(new Color(255, 255, 255));
        lblRating.setText("Rating");
        lblRating.setBounds(22, 431, 141, 18);
        mainPanel.add(lblRating);
        
        
        cbxRating.setBounds(22, 461, 90, 42);
        mainPanel.add(cbxRating);
        
        JLabel lblGenre = new JLabel();
        lblGenre.setFont(kronaFont);
        lblGenre.setForeground(new Color(255,255,255));
        lblGenre.setText("Genre");
        lblGenre.setBounds(163, 431, 141, 18);
        mainPanel.add(lblGenre);
        
        
        cbxGenre.setBounds(163, 461, 143, 42);
        mainPanel.add(cbxGenre);
        
        JLabel lblAlbum = new JLabel();
        lblAlbum.setFont(kronaFont);
        lblAlbum.setForeground(new Color(255,255,255));
        lblAlbum.setText("Album:");
        lblAlbum.setBounds(22, 531, 141, 18);
        mainPanel.add(lblAlbum);
        
        
        tfAlbum.setBounds(22, 561, 303, 42);
        mainPanel.add(tfAlbum);
        
        JButton btnAddSong = new JButton();
        btnAddSong.setFont(kronaFont);
        btnAddSong.setText("Add Song to Playlist");
        btnAddSong.setBounds(22, 610, 300, 44);
        btnAddSong.addActionListener( AL->addSongClicked() );
        mainPanel.add(btnAddSong);
        
        
        JLabel lblPlaylistCover = new JLabel();
        lblPlaylistCover.setFont(kronaFont);
        lblPlaylistCover.setForeground(new Color(255,255,255));
        lblPlaylistCover.setText("Playlist Cover");
        lblPlaylistCover.setBounds(356, 155, 141, 18);
        mainPanel.add(lblPlaylistCover);
        
        JButton btnChooseImage = new JButton();
        btnChooseImage.setFont(kronaFont);
        btnChooseImage.setText("Upload an image");
        btnChooseImage.setBounds(356, 185, 180, 44);
        btnChooseImage.addActionListener( AL->chooseImg() );
        mainPanel.add(btnChooseImage);
        
        
        if(playlistCoverOrigin.equals("")==false)
        {
            lblPlaylistCoverPreview.setIcon(new ImageIcon(new ImageIcon(playlistCoverOrigin).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblPlaylistCoverPreview.setBounds(356, 247, 180, 180);
            lblPlaylistCoverPreview.setVisible(true);
        } else {
            lblPlaylistCoverPreview.setVisible(false);
        }
        mainPanel.add(lblPlaylistCoverPreview);
        
        JLabel lblMusicFile = new JLabel();
        lblMusicFile.setFont(kronaFont);
        lblMusicFile.setForeground(new Color(255, 255, 255));
        lblMusicFile.setText("Music File:");
        lblMusicFile.setBounds(356, 460, 141, 18);
        mainPanel.add(lblMusicFile);
        
        JButton btnChooseMusic = new JButton();
        btnChooseMusic.setFont(kronaFont);
        btnChooseMusic.setText("Upload Music file");
        btnChooseMusic.setBounds(356, 480, 200, 44);
        btnChooseMusic.addActionListener( AL->chooseMusic() );
        mainPanel.add(btnChooseMusic);
        
        lblMusicSourcePreview.setForeground(new Color(255,255,255));
        lblMusicSourcePreview.setVisible(false);
        mainPanel.add(lblMusicSourcePreview);
        
        
        JLabel lblAddedSongs = new JLabel();
        lblAddedSongs.setFont(kronaFont);
        lblAddedSongs.setForeground(new Color(255,255,255));
        lblAddedSongs.setText("Songs in playlist:");
        lblAddedSongs.setBounds(583, 268, 200, 18);
        mainPanel.add(lblAddedSongs);
        
        scrollableSongsAddedList.setBounds(583, 298, 310, 248);
        mainPanel.add(scrollableSongsAddedList);
        
        JButton btnSavePlaylist = new JButton();
        btnSavePlaylist.setFont(kronaFont);
        btnSavePlaylist.setText("Save Playlist & Exit");
        btnSavePlaylist.setBounds(630, 600, 250, 44);
        btnSavePlaylist.addActionListener( AL->savePlaylistClicked() );
        mainPanel.add(btnSavePlaylist);
        
        mainPanel.add(lblInvalidArtistName);
        mainPanel.add(lblInvalidDuration);
        mainPanel.add(lblInvalidMusicFile);
        mainPanel.add(lblInvalidSongName);
        mainPanel.add(lblInvalidTotalListens);
        mainPanel.add(lblInvalidPlaylistTitle);
        
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }
    
    /**
     * Called when the save playlist button is clicked. Works like other similar functions of this class. Validates the fields to ensure they 
     * are not empty, but also checks that the user does not try to create a playlists with no songs added to it by checking the position of the
     * songList. If passes validation attempts to write the playlist to file through the writeAllSongsToFile which returns true if file name is 
     * already taken in which case the user is asked to enter a new name. If file is not taken, copies every song file source from it's origin
     * on user's device to the directory inside this system. Then creates a temporary customplaylist object with all the attributes of the playlist:
     * name, cover image source. That object is passed to file writing function to save the playlist to file containing all the data about all the 
     * playlists. Clears the fields and redirects back to library.
       */
    public void savePlaylistClicked()
    {
        String tempPlaylistTitle = "";
        boolean valid = true;
        if(songList.position==0)
        {
            valid = false;
            JOptionPane.showMessageDialog(null, "Creating an empty playlist is a bit useless. \n Try adding a few songs...");
        }
        
        if(tfPlaylistTitle.getText().equals(""))
        {
            lblInvalidPlaylistTitle.setForeground(new Color(255,0,0));
            lblInvalidPlaylistTitle.setText("Name of your playlist is required!");
            lblInvalidPlaylistTitle.setBounds(280, 100, 303, 18);
            valid = false;
        } else
        {
            tempPlaylistTitle = tfPlaylistTitle.getText();
            lblInvalidPlaylistTitle.setText("");
        }
        
        if(valid)
        {
            boolean fileTaken = songList.writeAllSongsToFile(tempPlaylistTitle, currentUserID);
            if(fileTaken)
            {
                lblInvalidPlaylistTitle.setForeground(new Color(255,0,0));
                lblInvalidPlaylistTitle.setText("Playlist with such name already exists!");
                lblInvalidPlaylistTitle.setBounds(280, 100, 303, 18);
            } else {
                lblInvalidPlaylistTitle.setText("");
                CustomPlaylist tempPlaylist = new CustomPlaylist();
                tempPlaylist.setAttributes(tempPlaylistTitle, playlistCoverDestination);
                CustomPlaylistList playlistList = new CustomPlaylistList();
                playlistList.addPlaylistToArray(tempPlaylist);
                playlistList.writeAllPlaylistsToFile(currentUserID);
                
                for(int i=0; i<songSourceArraysPosition; i++)
                {
                    Path origin = Paths.get(songSourceOrigin[i]);
                    Path destination = Paths.get(songSourceDestination[i]);
                    try
                    {
                        Files.copy(origin, destination, REPLACE_EXISTING);
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showInternalMessageDialog(null, e+"", "Error while trying to save the song file", JOptionPane.ERROR_MESSAGE);
                    }
                    /*
                     * An object of class path is created one that stores the path of the file on your system and the other that stores the destination path loacted at the user specific folder.
                     * Files.copy allows to copy a file from it's origin to it's destination. REPLACE_EXISTING will prevent the function not executing if the destination file already exists by 
                     * replacing the existing file with the new file.
                       */
                }
                
                if(playlistCoverOrigin.equals("")==false && playlistCoverDestination.equals("")==false)
                {
                   Path origin = Paths.get(playlistCoverOrigin);
                   Path destination = Paths.get(playlistCoverDestination);
                   try
                   {
                       Files.copy(origin, destination, REPLACE_EXISTING);
                   } catch(Exception e)
                   {
                       JOptionPane.showInternalMessageDialog(null, e+"", "Error while trying to save the song file", JOptionPane.ERROR_MESSAGE);
                   } 
                }
                /*
                 * Selection is used to ensure both source and destination paths exist before trying to copy the file
                   */
                
                clearPlayistCreationFields();
                JOptionPane.showMessageDialog(null, "Playlist saved");
                loadLibraryComponents();
            }
            
        }
    }
    
    /**
     * Clears all the text fields and sets all invalid label text to empty strings.
       */
    public void clearPlayistCreationFields()
    {
        clearSongCreationFields();
        songSourceOrigin = new String[100];
        songSourceDestination = new String[100];
        songSourceArraysPosition = 0;
        
        lblInvalidArtistName.setText("");
        lblInvalidDuration.setText("");
        lblInvalidMusicFile.setText("");
        lblInvalidPlaylistTitle.setText("");
        lblInvalidSongName.setText("");
        lblInvalidTotalListens.setText("");
        
        tfPlaylistTitle.setText("");
        playlistCoverOrigin="";
        playlistCoverDestination="";
        songsAddedListModel.clear();
    }
    
    /**
     * Validates all song data entered by user to ensure it is not empty, and of correct data type before creating a temporary song
     * object that is saved to array of all the songs in the playlist and added to the jlist component to show the user what songs are
     * inside the playlist
       */
    public void addSongClicked()
    {
        CustomSong tempSong = new CustomSong();
        boolean valid = true;
        String tempSongName = "";
        String tempArtistName = "";
        String tempMusicSource = "";
        double tempDuration = 0;
        int tempTotalListens = 0;
        
        if(tfSongName.getText().equals(""))
        {
            lblInvalidSongName.setForeground(new Color(255, 0, 0));
            lblInvalidSongName.setText("Song name is required!");
            lblInvalidSongName.setBounds(22, 225, 190, 18);
            valid = false;
        } else {
            tempSongName = tfSongName.getText();
            lblInvalidSongName.setText("");
        }
        
        if(tfArtistName.getText().equals(""))
        {
            lblInvalidArtistName.setForeground(new Color(255,0,0));
            lblInvalidArtistName.setText("Artist name is required!");
            lblInvalidArtistName.setBounds(22, 317, 190, 18);
            valid = false;
        } else {
            tempArtistName = tfArtistName.getText();
            lblInvalidArtistName.setText("");
        }
        
        try{
            tempDuration = Double.parseDouble(tfDuration.getText());
            lblInvalidDuration.setText("");
        } catch(Exception e)
        {
            valid = false;
            lblInvalidDuration.setForeground(new Color(255, 0, 0));
            lblInvalidDuration.setText("Must be a number!");
            lblInvalidDuration.setBounds(22, 410, 141, 18);
        }
        
        try{
            tempTotalListens = Integer.parseInt(tfTotalListens.getText());
            lblInvalidTotalListens.setText("");
        } catch(Exception e)
        {
            lblInvalidTotalListens.setForeground(new Color(255,0,0));
            lblInvalidTotalListens.setText("Must be a number!");
            lblInvalidTotalListens.setBounds(163, 410, 141, 18);
            valid = false;
        }
        
        if(songSourceArraysPosition == 0)
        {
            if(songSourceOrigin[songSourceArraysPosition]==null)
            {
                lblInvalidMusicFile.setForeground(new Color(255,0,0));
                lblInvalidMusicFile.setText("Music source is required");
                lblInvalidMusicFile.setBounds(356, 532, 190, 18);
                valid = false;
            } else {
                tempMusicSource=songSourceDestination[songSourceArraysPosition];
                lblInvalidMusicFile.setText("");
            }
        } else {
            if(songSourceOrigin[songSourceArraysPosition-1]==null)
            {
                lblInvalidMusicFile.setForeground(new Color(255,0,0));
                lblInvalidMusicFile.setText("Music source is required");
                lblInvalidMusicFile.setBounds(356, 532, 190, 18);
                valid = false;
            } else {
                tempMusicSource=songSourceDestination[songSourceArraysPosition];
                lblInvalidMusicFile.setText("");
            }
        }
        int tempRating = Integer.parseInt(cbxRating.getSelectedItem().toString());
        String tempGenre = cbxGenre.getSelectedItem().toString();
        
        if(valid)
        {
            songsAddedListModel.clear();
            tempSong.setSongAttributes(tempSongName, tempDuration, tempRating, tfAlbum.getText(), tempArtistName, tempGenre, tempTotalListens, songSourceDestination[songSourceArraysPosition-1]);
            songList.addCustomSongToList(tempSong);
            for(int i=0; i<songList.position; i++)
            {
               songsAddedListModel.addElement(songList.allSongs[i]); 
            }
            clearSongCreationFields();
        }
    }
    
    /**
     * Clears all the fields that were used in the playlist creation. Sets all combo boxes to the 1st element
       */
    public void clearSongCreationFields()
    {
        tfSongName.setText("");
        tfArtistName.setText("");
        tfDuration.setText("");
        tfTotalListens.setText("");
        cbxRating.setSelectedIndex(0);
        cbxGenre.setSelectedIndex(0);
        tfAlbum.setText("");
        
        lblMusicSourcePreview.setText("");
        
        
        lblInvalidArtistName.setText("");
        lblInvalidDuration.setText("");
        lblInvalidMusicFile.setText("");
        lblInvalidSongName.setText("");
        lblInvalidTotalListens.setText("");
        
    }
    
    /**
     * Initiates an object of JFileChooser which is a pop up window that allows user to select a file. setFileFilter is
     * used to limit the user choice to music file extensions (First string is the text that is displayed, the rest are 
     * allowed extensions). showOpenDialog is used to actually open the pop up window itself and safe the user's response
     * to the variable. If the user selected a file then a new instance of file is initiated which stores the file selected
     * by user from the pop up window. Then the selected file's absolute path is stored to the global array which is going to 
     * be used later to copy the file to user directory. The destination of the song is also saved as the directory of all users
     * + user id + the name of the file. If the user has selected a music file then the full path to it on the system is displayed 
     * under the button and a tooltip text is set to show a full path when the mouse hovers over it. Otherwise no preview is displayed.
       */
    public void chooseMusic()
    {
        JFileChooser chooserMusic = new JFileChooser();
        chooserMusic.setFileFilter(new FileNameExtensionFilter("Music Only", "mp3", "wav", "m4a"));
        /*
         * Sets file extension filter for the filechooser allowing user to only open images with the extensions passed to the object 
         * of class FileNameExtensionFilter
           */
        int result = chooserMusic.showOpenDialog(chooserMusic);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = chooserMusic.getSelectedFile();
            songSourceOrigin[songSourceArraysPosition] = selectedFile.getAbsolutePath();
            songSourceDestination[songSourceArraysPosition] = "users/"+currentUserID+"/"+chooserMusic.getSelectedFile().getName();
            songSourceArraysPosition++;
        }
        
        if(songSourceArraysPosition == 0)
        {
            if(songSourceOrigin[songSourceArraysPosition]!=null)
            {
                lblMusicSourcePreview.setText(songSourceOrigin[songSourceArraysPosition]);
                lblMusicSourcePreview.setBounds(356, 530, 180, 18);
                lblMusicSourcePreview.setToolTipText(songSourceOrigin[songSourceArraysPosition]);
                lblMusicSourcePreview.setVisible(true);
            } else {
                lblMusicSourcePreview.setVisible(false);
            }
        } else
        {
            if(songSourceOrigin[songSourceArraysPosition-1]!=null)
            {
                lblMusicSourcePreview.setText(songSourceOrigin[songSourceArraysPosition-1]);
                lblMusicSourcePreview.setBounds(356, 530, 180, 18);
                lblMusicSourcePreview.setToolTipText(songSourceOrigin[songSourceArraysPosition-1]);
                lblMusicSourcePreview.setVisible(true);
            } else {
                lblMusicSourcePreview.setVisible(false);
            }
        }
        /*
         * This selection is copied from the initPlaylistCreationComponent to update the cover preview each time the user decides to go back and change the playlist cover image
           */
    }
    
    /**
       This function will create a file chooser dialog window which allows user to select an image from their system. The absolut path of this file is then stored as a string
       value which allows the system to copy it to the system's folder making it accessible on any machine this system will run on.
       */
    public void chooseImg()
    {
        JFileChooser chooserImg = new JFileChooser();
        chooserImg.setFileFilter(new FileNameExtensionFilter("Images Only", "jpg", "jpeg", "png", "raw"));
        /*
         * Sets file extension filter for the filechooser allowing user to only open images with the extensions passed to the object 
         * of class FileNameExtensionFilter
           */
        int result = chooserImg.showOpenDialog(chooserImg);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = chooserImg.getSelectedFile();
            playlistCoverOrigin = selectedFile.getAbsolutePath();
            playlistCoverDestination = "users/"+currentUserID+"/"+selectedFile.getName();
        }
        
        if(playlistCoverOrigin.equals("")==false)
        {
            lblPlaylistCoverPreview.setIcon(new ImageIcon(new ImageIcon(playlistCoverOrigin).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblPlaylistCoverPreview.setBounds(356, 247, 180, 180);
            lblPlaylistCoverPreview.setVisible(true);
        } else {
            lblPlaylistCoverPreview.setVisible(false);
        }
        /*
         * This selection is copied from the initPlaylistCreationComponent to update the cover preview each time the user decides to go back and change the playlist cover image
           */
    }
}
