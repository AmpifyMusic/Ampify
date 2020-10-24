package client;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RegexValidator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.*;
import java.util.List ;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    String currentPlaylist , currentlyPlayingPlaylist , currentSong , currentUser;
    FileChooser fileChooser = new FileChooser();
    private MediaPlayer mediaPlayer;
    int trackNo = 0;
    Boolean status = false;
    //private ObservableList<String> list = FXCollections.observableArrayList();
    private List<File> fileList;
   // private HashMap<String , List<File>> playlists = new HashMap<>();
    int currentSelectedPane=3;
    private ObservableList<String> playlistNames = FXCollections.observableArrayList();   //to store the names of playlists locally
    private ObservableList<String> queue = FXCollections.observableArrayList();
    private ObservableList<String> listOfSongsInPlaylist = FXCollections.observableArrayList();



    @FXML
    private AnchorPane mediaViewPanel;
    @FXML
    private Label totalSongNumber;
    @FXML
    private Label libraryNameLabel;
    @FXML
    private HBox selectPlaylistHbox;
    @FXML
    private JFXListView<String> playlistNameListView;
    @FXML
    private MediaView mediaView;
    @FXML
    private AnchorPane sidePanel;
    @FXML
    private AnchorPane rootPanel;
    @FXML
    private AnchorPane homePagePane;
    @FXML
    private AnchorPane playlistPane;
    @FXML
    private AnchorPane lyricsPane;
    @FXML
    private AnchorPane queuePane;
    @FXML
    private VBox playlistPaneVbox;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private AnchorPane songPane;
    @FXML
    private JFXListView<String> songsListView;
    @FXML
    private JFXListView<String> playlistListView;
    @FXML
    private  JFXListView<String> libraryListView;
    @FXML
    private AnchorPane libraryPane;
    @FXML
    private VBox libraryPaneVbox;
    @FXML
    private ScrollPane playlistsScrollPane;
    @FXML
    private JFXMasonryPane playlistsMasonryPane;
    @FXML
    private AnchorPane playerControl;
    @FXML
    private JFXSlider progressBar;
    @FXML
    private ImageView importButton;
    @FXML
    private JFXButton deletePlaylistButton;
    @FXML
    private ImageView playSongsFromCurrentPlaylistButton;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXListView queueListView;
    @FXML
    private Label currentTime;
    @FXML
    private Label trackLength;
    @FXML
    private ImageView volumeImage;
    @FXML
    private JFXButton openButton;
    @FXML
    private JFXButton stopButton;
    @FXML
    private JFXButton playlistPlayButton;
    @FXML
    private Label songName;
    @FXML
    private JFXButton playButton;
    @FXML
    private ImageView playImage;
    @FXML
    private HBox createPlaylistHbox;
    @FXML
    private JFXButton createPlaylistcloseButton;
    @FXML
    private JFXTextField createPlaylistTextField;
    @FXML
    private JFXButton createPlaylistButton ;
    @FXML
    private Label warningLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePagePane.toFront();
        progressBar.setValue(0);
        playlistListView.setDisable(true);
        if(LoginController.getCurrentUser().equals(null))
            currentUser = SignUpController.getCurrentUser();
        else
            currentUser = LoginController.getCurrentUser();
       loadPlaylists();
        playlistNameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //currentPlaylist = playlistNames.get(playlistNameListView.getSelectionModel().getSelectedIndex());
               showPlaylist(playlistNameListView.getSelectionModel().getSelectedIndex());
            }
        });
    }

    public void playpause() {
        if (status) {
            mediaPlayer.pause();
            status = false;
            try {
                playImage.setImage(new Image(new FileInputStream("src/client/Icons/playIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.play();
            status = true;
            try {
                playImage.setImage(new Image(new FileInputStream("src/client/Icons/pauseIcon.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void nextButtonPressed(){
        if(currentlyPlayingPlaylist == "local"){
            System.out.println(currentSong);
            int trackno = queue.indexOf(currentSong) + 1;
            if(trackno==queue.size()){  trackno = 0;  }
            System.out.println(trackno);
            jumpTrack(trackno);
            queueListView.getSelectionModel().select(trackno);
        }
    }

    public void previousButtonPressed(){
        if(currentlyPlayingPlaylist == "local"){
            System.out.println(currentSong);
            int trackno = queue.indexOf(currentSong) - 1;
            if(trackno<0){  trackno = 0;  }
            System.out.println(trackno);
            jumpTrack(trackno);
            queueListView.getSelectionModel().select(trackno);
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        status = false;
        try {
            playImage.setImage(new Image(new FileInputStream("src/client/Icons/playIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        songName.setText("");
        trackLength.setText("");
        currentTime.setText("");
        mediaViewPanel.toBack();
    }


    public String getSecondsToSimpleString(double userSeconds) {
        double mins = userSeconds / 60;
        String minsStr = mins + "";
        int index = minsStr.indexOf('.');
        String str1 = minsStr.substring(0, index);
        String minsStr2 = minsStr.substring(index + 1);
        double secs = Double.parseDouble("0." + minsStr2) * 60;
        String str2 = (int) secs + "";
        if (secs < 10)
            str2 = 0 + str2;
        return str1 + ":" + str2;
    }


    void playLocalSong(File f) {
        String fileName, path, fileExtension;
        path = f.toURI().toString();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        try {
            playImage.setImage(new Image(new FileInputStream("src/client/Icons/pauseIcon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = true;
        fileName = f.getName();
        songName.setText("Now Playing-\n" + fileName);
        progressBar.setValue(0.0);
        fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, f.getName().length());
        if (fileExtension.equals("mp4")) {
            mediaView.setMediaPlayer(mediaPlayer);
            DoubleProperty widthProp = mediaView.fitWidthProperty();
            DoubleProperty heightProp = mediaView.fitHeightProperty();
            //creating bindings
            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            mediaView.setPreserveRatio(true);
            mediaViewPanel.toFront();
            playerControl.toFront();
        } else if (fileExtension.equals("mp3") || fileExtension.equals("wav") && status) {
            mediaViewPanel.toBack();
        }


        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressBar.setValue(newValue.toSeconds());
                    currentTime.setText("" + getSecondsToSimpleString(newValue.toSeconds()));
                }
        );

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = media.getDuration();
                trackLength.setText(getSecondsToSimpleString(total.toSeconds()));
                progressBar.setMax(total.toSeconds());

            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                nextButtonPressed();
            }
        });
    }//handlePlay method ends here

    public  void handlePlay(String songName){
        currentSong =songName;
        System.out.println(songName);
                try {
                    AppData playSong = new AppData("playSong" , songName);
                    Main.clientOutputStream.writeObject(playSong);
                    byte[] buffer = new byte[1024];
                    int bytesRead = Main.clientInputStream.read(buffer);
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try {
                        System.out.println("here 17");
                        while (bytesRead != -1)
                        {
                            output.write(buffer, 0, bytesRead);
                            bytesRead = Main.clientInputStream.read(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] tuneAsBytes = output.toByteArray();
                    File tempMp3 = File.createTempFile("music", ".mp3");
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(tuneAsBytes);
                    System.out.println(tempMp3.getAbsolutePath());
                    System.out.println(tempMp3.toURI().toURL().toString());
                    final Media media = new Media(tempMp3.toURI().toURL().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    tempMp3.deleteOnExit();
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                    volumeSlider.setValue(mediaPlayer.getVolume() * 100);
                    volumeSlider.valueProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                        }
                    });

                    mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                                progressBar.setValue(newValue.toSeconds());
                                currentTime.setText("" + getSecondsToSimpleString(newValue.toSeconds()));
                            }
                    );

                    progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));

                    progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                        }

                    });

                    mediaPlayer.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            Duration total = media.getDuration();
                            trackLength.setText(getSecondsToSimpleString(total.toSeconds()));
                            progressBar.setMax(total.toSeconds());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


    public void jumpTrack(int index) {
        File file = null;
        System.out.println(index);
        if (status) {
            mediaPlayer.stop();
        }
        try {
            trackNo = index;
            file = fileList.get(trackNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            playLocalSong(file);
            currentlyPlayingPlaylist = "local";
            currentSong = file.getName();
        }
    }//jumpTrack() method closed here


    public void openSong() {
        if (fileList != null) {
            if (!fileList.isEmpty()) {
                File existDirectory = fileList.get(0).getParentFile();
                fileChooser.setInitialDirectory(existDirectory);
            }
        }
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select files", "*.mp3", "*.mp4", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
        fileList = fileChooser.showOpenMultipleDialog(null);
        for (File value : fileList) {
            queue.add(value.getName());
        }
        queuePane.toFront();
        queueListView.setItems(queue);
        queueListView.getSelectionModel().select(0);
        queueListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                jumpTrack(queueListView.getSelectionModel().getSelectedIndex());
            }
        });
    }//openFile() method closed here

   public void createPlaylist(){
      createPlaylistHbox.toFront();
      createPlaylistTextField.setText(null);
      warningLabel.setText(null);
      createPlaylistButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (createPlaylistTextField.getText().isEmpty() || createPlaylistTextField.getText().isBlank()) {
                    warningLabel.setText("Playlist name cannot be empty");
                }
                else if (playlistNames.contains(createPlaylistTextField.getText())) {
                    warningLabel.setText("Playlist name already taken");
                }
                else {
                    playlistNames.add(createPlaylistTextField.getText());
                   try{
                       AppData createPlaylistData = new AppData("CreatePlaylist" , currentUser , createPlaylistTextField.getText());
                       Main.clientOutputStream.writeObject(createPlaylistData);
                   }catch(Exception e){
                       e.printStackTrace();
                       System.out.println("Playlist not created ERROR!");
                   }
                   playlistListView.setItems(null);
                   currentPlaylist = createPlaylistTextField.getText();
                    createPlaylistHbox.toBack();
                    playlistPane.toFront();
                    playlistNameLabel.setText(createPlaylistTextField.getText());
                    totalSongNumber.setText("it is lonely here!");
                    playlistNameListView.setItems(playlistNames);
                }
            }
      });
    }//CreatePlaylist method ends here

    public void loadPlaylists(){
        try{
            System.out.println("Sending username to server to get playlist names");
            AppData loadPlaylistData = new AppData("loadPlaylists",currentUser);
            Main.clientOutputStream.writeObject(loadPlaylistData);
            System.out.println("reading playlist names from server");
            String playlistName;
            while (!"".equals(playlistName = Main.clientInputStream.readUTF())){
                System.out.println("playlist Name : "+ playlistName);
                playlistNames.add(playlistName);
            }
            playlistNameListView.setItems(playlistNames);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error loading playlists");
        }

    }//loadPlaylists method ends here


    public void OnCreatePlaylistcloseButtonPressed(){
        createPlaylistHbox.toBack();
    }

    public void onAddPlaylistcloseButtonPressed(){
        selectPlaylistHbox.toBack();
    }

    public void addToPlaylistMenuButtonPressed(){
        selectPlaylistHbox.toFront();
    }

    public void showPlaylist(int i){
        listOfSongsInPlaylist.clear();
        playlistListView.setDisable(false);
        playlistPane.toFront();
        if(currentPlaylist != playlistNames.get(i))
        {
            currentPlaylist = playlistNames.get(i);
            playlistNameLabel.setText(currentPlaylist);
            try {
                AppData showPlaylistData = new AppData("showPlaylist", currentUser , currentPlaylist);
                Main.clientOutputStream.writeObject(showPlaylistData);
                System.out.println("Reading list of songs in playlist from server");
                String songName;
                while (!"".equals(songName = Main.clientInputStream.readUTF())){
                    System.out.println("Song Name : "+ songName);
                    listOfSongsInPlaylist.add(songName);
                }
                if(listOfSongsInPlaylist.size()>0)
                    totalSongNumber.setText("Number of songs : " + listOfSongsInPlaylist.size());
                else
                    totalSongNumber.setText("It is lonely here!");
                playlistListView.setItems(listOfSongsInPlaylist);
                playlistListView.getSelectionModel().select(0);
                playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        handlePlay(playlistListView.getSelectionModel().getSelectedItem());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to open playlist");
            }
        }
    }//showPlaylists method ends here

   public void deletePlaylist(){
       try {
           AppData deletePlaylist = new AppData("deletePlaylist",currentUser , currentPlaylist);
           Main.clientOutputStream.writeObject(deletePlaylist);
           System.out.println("delete data sent successfully");
           playlistNames.remove(currentPlaylist);
           homePagePane.toFront();
       }catch(Exception e){
           e.printStackTrace();
           System.out.println("error deleting playlist");
       }
   }

    public void clearQueueButtonPressed(){
        queue.clear();
        stop();
       // fileList.clear();
    }

    public void playlistPlayButtonPressed(){
        System.out.println("list of songs" + listOfSongsInPlaylist);
        queue.clear();
        queue.addAll(listOfSongsInPlaylist) ;
       currentlyPlayingPlaylist = currentPlaylist ;
       playlistListView.getSelectionModel().select(0);
       handlePlay(playlistListView.getSelectionModel().getSelectedItem());
        System.out.println("queue:"+ queue);
    }

    public void homeButtonPressed(){
        homePagePane.toFront();
    }
    public void queueButtonPressed(){
        queuePane.toFront();
        queueListView.setItems(queue);
    }

    public void artistButtonPressed() {
        System.out.println("entered artist fuction");
        ObservableList<String> artists = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Artists");
        System.out.println("sending request to get artist data");
        try {
            AppData artistData = new AppData("getArtistData");
            Main.clientOutputStream.writeObject(artistData);
            String artistName;
            while (!"".equals(artistName = Main.clientInputStream.readUTF())) {
                System.out.println("Artist name: " + artistName);
                artists.add(artistName);
            }
            libraryListView.setItems(artists);
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getSongs(libraryListView.getSelectionModel().getSelectedItem() , "artist");
                }
            });
        }catch(Exception e){
            System.out.println("Error getting artist data");
        }
    }


    public void genreButtonPressed(){
        ObservableList<String> genres = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Genres");
        System.out.println("sending request to get genre data");
        try {
            AppData languageData = new AppData("getGenreData");
            Main.clientOutputStream.writeObject(languageData);
            String genre;
            while (!"".equals(genre = Main.clientInputStream.readUTF())) {
                System.out.println("genre : " + genre);
                genres.add(genre);
            }
            libraryListView.setItems(genres);
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getSongs(libraryListView.getSelectionModel().getSelectedItem() , "genre");
                }
            });
        }catch(Exception e){
            System.out.println("Error getting genre data");
        }
    }

    public void languagesButtonPressed(){
        ObservableList<String> languages = FXCollections.observableArrayList();
        libraryPane.toFront();
        libraryNameLabel.setText("Languages");
        System.out.println("sending request to get language data");
        try {
            AppData languageData = new AppData("getLanguageData");
            Main.clientOutputStream.writeObject(languageData);
            String language;
            while (!"".equals(language = Main.clientInputStream.readUTF())) {
                System.out.println("Language : " + language);
                languages.add(language);
            }
            libraryListView.setItems(languages);
            libraryListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getSongs(libraryListView.getSelectionModel().getSelectedItem() , "language");
                }
            });
        }catch(Exception e){
            System.out.println("Error getting language data");
        }
    }

    public void likedSongsButtonPressed(){
        libraryPane.toFront();
        libraryNameLabel.setText("Liked Songs");
        libraryListView.setItems(null);
    }

    public void getSongs(String name , String type){
        ObservableList<String> songList = FXCollections.observableArrayList();
        songPane.toFront();
        songNameLabel.setText(name);
        try {
            AppData getSongsByArtist = new AppData("getSongs", name , type);
            Main.clientOutputStream.writeObject(getSongsByArtist);
            System.out.println("Reading list of song  from server");
            String songName ;
            while (!"".equals(songName = Main.clientInputStream.readUTF())){
                System.out.println("Song Name : "+ songName);
                songList.add(songName);
            }
            songsListView.setItems(songList);
            songsListView.getSelectionModel().select(0);
            songsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    handlePlay(songsListView.getSelectionModel().getSelectedItem());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to get songs");
        }
    }


    public void historyButtonPressed(){
        libraryPane.toFront();
        libraryNameLabel.setText("Recently Played");
    }

    public void lyricsButtonPressed(){
        lyricsPane.toFront();
    }


    private void switchPane(int paneNo)
    {
        // 1 : home 2 : Library, 3: playlists
        if(currentSelectedPane!=paneNo)
        {
            if(paneNo == 1)
            {
               // homePane.toFront();
            }
            else if(paneNo == 2)
            {
                rootPanel.toFront();
                libraryPane.toFront();

            }
            else if(paneNo == 3)
            {
                rootPanel.toFront();
                playlistPane.toFront();
            }
            currentSelectedPane = paneNo;
        }
    }
}



