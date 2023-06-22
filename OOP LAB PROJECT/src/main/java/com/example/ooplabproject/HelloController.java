package com.example.ooplabproject;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.Timer;

public class HelloController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button play, pause, reset, next, previous;
    @FXML
    private ComboBox<String> SpeedBox;
    @FXML
    private ProgressBar videoprogressbar;
    @FXML
    private Slider VolumeSlider;
    @FXML
    private ListView<String> listView;
    @FXML
    private ObservableList<String> items;
    @FXML
    private MediaView mediaView;

    // Media-related variables
    private Media media;
    private MediaPlayer mediaPlayer;
    Duration mediaDuration;
    Duration pauseDuration;

    // Menu item
    @FXML
    MenuItem OpenDirectory;
    @FXML
    Menu file;

    // Directory and file-related variables
    private File directory;
    private File[] files;
    private ArrayList<File> videos;
    private int videoNumber;

    // Playback speed options
    private final int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    // Timer for updating progress bar
    private Timer timer;
    private TimerTask task;
    private boolean running;

    // Method for opening a directory and selecting video files
    @FXML
    private void openDirectoryAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Directory");

        Stage stage = (Stage) pane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            // Clear the existing video list
            videos.clear();
            items.clear();

            // Set the new directory
            directory = selectedDirectory;
            files = directory.listFiles();

            // Add video files from the selected directory to the video list
            if (files != null) {
                for (File file : files) {
                    videos.add(file);
                    items.add(file.getName());
                }
            }

            if (!videos.isEmpty()) {
                // Set the first video from the new directory as the current video
                videoNumber = 0;
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer(new Media(videos.get(videoNumber).toURI().toString()));
                mediaPlayer.setVolume(VolumeSlider.getValue() * 0.01);
                PlayMedia();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the videos list and set the default directory
        videos = new ArrayList<>();
        directory = new File("E:\\OOP lab final edit\\OOP LAB PROJECT\\src\\main\\resources\\com\\example\\ooplabproject\\MUSIC");

        // Get the list of files in the default directory
        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                videos.add(file);
                System.out.println(file);
            }
        }

        // Check if a media player exists and print the current volume
        if (mediaPlayer != null) {
            double volume = mediaPlayer.getVolume();

            System.out.println("Volume: " + volume);
        }

        if (!videos.isEmpty()) {
            // Set the first video from the default directory as the current video
            media = new Media(videos.get(videoNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            songLabel.setText(videos.get(videoNumber).getName());
        }

        // Add speed options to the ComboBox
        for (int speed : speeds) {
            SpeedBox.getItems().add(speed + "%");
        }
        SpeedBox.setOnAction(this::ChangeSpeed);

        // Set the initial volume and add a listener for volume changes
        VolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> mediaPlayer.setVolume(VolumeSlider.getValue() * 0.01));
        VolumeSlider.setValue(mediaPlayer.getVolume() * 100);
        mediaPlayer.volumeProperty().addListener((observable, oldValue, newValue) -> {
            // Update the volume slider's value
            VolumeSlider.setValue(newValue.doubleValue() * 100);
        });

        // Set the style of the progress bar
        videoprogressbar.setStyle("-fx-accent:#00FF00;");

        // Create ListView and ObservableList
        listView = new ListView<>();
        listView.setPrefSize(200, 508);
        items = FXCollections.observableArrayList();

        // Add video file names to the ObservableList
        for (File video : videos) {
            items.add(video.getName());
        }

        // Set items of the ListView to the ObservableList
        listView.setItems(items);

        // Set listener for when an item in the ListView is clicked
        listView.setOnMouseClicked(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getName().equals(selectedItem)) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                    media = new Media(videos.get(i).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.setAutoPlay(true);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    int finalI = i;
                    mediaPlayer.setOnReady(() -> {
                        songLabel.setText(videos.get(finalI).getName());
                        VolumeSlider.setValue(mediaPlayer.getVolume() * 100);
                    });
                    mediaPlayer.setOnEndOfMedia(this::NextMedia);
                    mediaPlayer.setOnError(() -> System.out.println("Error: " + mediaPlayer.getError().toString()));
                    videoNumber = i;
                    VolumeSlider.setValue(mediaPlayer.getVolume() * 100);
                    break;
                }
            }
        });

        // Add the ListView to the root VBox
        pane.getChildren().add(listView);
        OpenDirectory.setOnAction(this::openDirectoryAction);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
    }

    public void PlayMedia() {
        beginTimer();
        ChangeSpeed(null);
        mediaPlayer.setVolume(VolumeSlider.getValue() * 0.01);

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            // Resume playback from the paused position
            mediaPlayer.play();
            beginTimer();
        } else {

            if (!videos.isEmpty()) {
                media = new Media(videos.get(videoNumber).toURI().toString());
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(videos.get(videoNumber).getName()); // set the label text to the file name
                mediaView.setMediaPlayer(null);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                // Store the media duration
                mediaDuration = media.getDuration();
                videoprogressbar.setProgress(0);
            }
            if (timer != null) {
                CancelTimer();
            }
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    // Update the progress bar
                    javafx.application.Platform.runLater(() -> {
                        double progress;
                        if (mediaDuration.toMillis() > 0) {
                            progress = mediaPlayer.getCurrentTime().toMillis() / mediaDuration.toMillis();
                            videoprogressbar.setProgress(progress);
                        }
                    });
                }
            };
            timer.schedule(task, 0, 100);
            beginTimer();
        }
    }

    public void PauseMedia() {
        mediaPlayer.pause();

        // Store the current playback position
        pauseDuration = mediaPlayer.getCurrentTime();

        CancelTimer();
    }

    public void ResetMedia() {
        // Reset the progress bar and seek to the beginning of the media
        videoprogressbar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void NextMedia() {
        // Play the next video in the list if available
        if (videoNumber < videos.size() - 1) {
            videoNumber++;
            mediaPlayer.stop();
            if (running) {
                CancelTimer();
            }

            media = new Media(videos.get(videoNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(videos.get(videoNumber).getName());
        } else {
            // Restart from the first video if at the end of the list
            videoNumber = 0;
            mediaPlayer.stop();
            if (running) {
                CancelTimer();
            }

            media = new Media(videos.get(videoNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(videos.get(videoNumber).getName());
        }
        mediaPlayer.setVolume(VolumeSlider.getValue() * 0.01);
        PlayMedia();
    }

    public void PreviousMedia() {
        // Play the previous video in the list if available
        if (videoNumber > 0) {
            videoNumber--;
            mediaPlayer.stop();
            if (running) {
                CancelTimer();
            }

            media = new Media(videos.get(videoNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(videos.get(videoNumber).getName());
            mediaPlayer.setVolume(VolumeSlider.getValue() * 0.01);
            PlayMedia();
        } else {
            // Go to the last video if at the beginning of the list
            if (running) {
                CancelTimer();
            }
            videoNumber = videos.size() - 1;
            mediaPlayer.stop();
            media = new Media(videos.get(videoNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(videos.get(videoNumber).getName());
        }
    }

    public void ChangeSpeed(ActionEvent event) {
        // Change the playback speed based on the selected option in the ComboBox
        if (SpeedBox.getValue() == null) {
            mediaPlayer.setRate(1.0);
        } else {
            mediaPlayer.setRate(Integer.parseInt(SpeedBox.getValue().substring(0, SpeedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {
        // Start a timer to update the progress bar
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    Platform.runLater(() -> {
                        running = true;
                        Duration currentTime = mediaPlayer.getCurrentTime();
                        Duration duration = media.getDuration();
                        // Calculate the current and total duration in seconds
                        double current = currentTime.toSeconds();
                        double end = duration.toSeconds();

                        // Update the progress bar based on the current playback position
                        videoprogressbar.setProgress(current / end);

                        // Check if the media has reached the end
                        if (current / end == 1) {
                            CancelTimer();
                        }
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void CancelTimer() {
        // Cancel and clear the timer
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}

