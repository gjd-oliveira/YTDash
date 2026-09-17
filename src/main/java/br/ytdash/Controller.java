package br.ytdash;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

public class Controller {

    private TextField ytURL;
    private ComboBox<String> dropdownFormat;
    private ComboBox<String> dropdownQuality;
    private Button download;
    private ProgressBar downloadBar;
    private DownloadService downloadService;

    public Controller(
        TextField ytURL,
        ComboBox<String> dropdownFormat,
        ComboBox<String> dropdownQuality,
        Button download,
        ProgressBar downloadBar,
        DownloadService downloadService
    ) {

        this.ytURL = ytURL;
        this.dropdownFormat = dropdownFormat;
        this.dropdownQuality = dropdownQuality;
        this.download = download;
        this.downloadBar = downloadBar;
        this.downloadService = downloadService;

            ytURL.textProperty().addListener((obs, oldValue, newValue) -> {
            verifyDownload();
            });

            dropdownFormat.valueProperty().addListener((obs, oldValue, newValue) -> {
            verifyIfFormat();
            verifyFormat();
            verifyDownload();
            });

            dropdownQuality.valueProperty().addListener((obs, oldValue, newValue) -> {
            verifyDownload();

            });

            verifyIfFormat();
            verifyFormat();
            verifyDownload();

            download.setOnAction(event -> {
            startDownload();
            });
            
    }

    public void verifyDownload() {

        if (
            ytURL.getText().isEmpty() ||
            dropdownFormat.getValue() == null ||
            dropdownQuality.getValue() == null
        ) {
            download.setDisable(true);
        } else {
            download.setDisable(false);
        }
    }

    public void verifyFormat() {
        /////////////////////////////////////////////////////
        /// MP4 VIDEO
        if ("MP4".equals(dropdownFormat.getValue())) {

            dropdownQuality.getItems().add("MP4 4K");
            dropdownQuality.getItems().add("MP4 1080P");

            if (dropdownQuality.getItems().contains("MP3 320KBPS")) {
                dropdownQuality.getItems().remove("MP3 320KBPS");
            }
        }
        /////////////////////////////////////////////////////
        /// MP3 AUDIO
        else if ("MP3".equals(dropdownFormat.getValue())) {

            dropdownQuality.getItems().add("MP3 320KBPS");

                if (dropdownQuality.getItems().contains("MP4 1080P")) {
                    dropdownQuality.getItems().remove("MP4 1080P");
                }

                if (dropdownQuality.getItems().contains("MP4 4K")) {
                    dropdownQuality.getItems().remove("MP4 4K");
                }     
        }
        ////////////////////////////////////////////////////
        /// SE QUISER IMPLEMENTAR MAIS FORMATOS FUTURAMENTE COMO MKV SEGUIR MESMA LÓGICA DE CIMA
    }

    public void verifyIfFormat() {
        if(dropdownFormat.getValue() == null) {
            dropdownQuality.setDisable(true);
        } else {
            dropdownQuality.setDisable(false);
        }
    }

    public String getFormat() {
        String format = dropdownQuality.getValue().substring(0,3).toLowerCase();
        return format;
    }

    public String getQuality() {
        String quality = "";
        if (dropdownQuality.getValue().equals("MP4 1080P")) {
            quality = "1080";
        }

        if (dropdownQuality.getValue().equals("MP4 4K")) {
            quality = "2160";
        }

        if (dropdownQuality.getValue().equals("MP3 320KBPS")) {
            quality = "320";
        }
        return quality;
    }

    public void startDownload() {

        String url = ytURL.getText();
        String format = getFormat();
        String quality = getQuality();

        Task<Void> task = new Task<>() {

        @Override
        protected Void call() throws Exception {

            downloadService.downloadCommand(
                url,
                format,
                quality,
                progresso -> {
                    updateProgress(progresso, 1);
                }
            );

            return null;
        }
        };

        downloadBar.progressProperty().bind(task.progressProperty());

        Thread thread = new Thread(task);
        thread.start();
    }
}