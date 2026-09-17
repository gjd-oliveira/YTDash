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
    private Button verifyURLButton;
    private Button download;
    private ProgressBar downloadBar;
    private DownloadService downloadService;
    private VideoService videoService;

    public Controller(
        TextField ytURL,
        ComboBox<String> dropdownFormat,
        ComboBox<String> dropdownQuality,
        Button verifyURLButton,
        Button download,
        ProgressBar downloadBar,
        DownloadService downloadService,
        VideoService videoService
    ) {

        this.ytURL = ytURL;
        this.dropdownFormat = dropdownFormat;
        this.dropdownQuality = dropdownQuality;
        this.verifyURLButton = verifyURLButton;
        this.download = download;
        this.downloadBar = downloadBar;
        this.downloadService = downloadService;
        this.videoService = videoService;

        verifyURLButton.setVisible(false);

        // URL

        ytURL.textProperty().addListener((obs, oldValue, newValue) -> {
            verifyDownload();
            verifyURLButton.setVisible(verifyURL());
        });


        // Format

        dropdownFormat.valueProperty().addListener((obs, oldValue, newValue) -> {
            verifyIfFormat();
            verifyFormat();
            verifyDownload();
        });


        // Quality

        dropdownQuality.valueProperty().addListener((obs, oldValue, newValue) -> {
            verifyDownload();
        });


        // Initial state

        verifyIfFormat();
        verifyFormat();
        verifyDownload();


        // Verify URL

        verifyURLButton.setOnAction(event -> {
            verifyVideo();
        });


        // Download

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


    public void verifyVideo() {

        if (!verifyURL()) {
            System.out.println("URL inválida");
            return;
        }

        String url = ytURL.getText();

        Task<VideoInfo> verifyTask = new Task<>() {

            @Override
            protected VideoInfo call() throws Exception {

                return videoService.getVideoInfo(url);
            }
        };

        verifyTask.setOnRunning(event -> {
            ytURL.setDisable(true);
            verifyURLButton.setDisable(true);
        });

        verifyTask.setOnSucceeded(event -> {

            ytURL.setDisable(false);
            verifyURLButton.setDisable(false);

            VideoInfo videoInfo = verifyTask.getValue();

            System.out.println(videoInfo.getTitle());

            System.out.println(
                "Quantidade de formatos: "
                + videoInfo.getFormats().size()
            );
            
        });

        verifyTask.setOnFailed(event -> {

            System.out.println("Erro ao verificar vídeo");
            verifyTask.getException().printStackTrace();

            ytURL.setDisable(false);
            verifyURLButton.setDisable(false);
        });

        Thread verifyThread = new Thread(verifyTask);

        verifyThread.start();
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


        /////////////////////////////////////////////////////
        /// SE QUISER IMPLEMENTAR MAIS FORMATOS FUTURAMENTE
        /// COMO MKV, SEGUIR MESMA LÓGICA DE CIMA
    }


    public void verifyIfFormat() {

        if (dropdownFormat.getValue() == null) {
            dropdownQuality.setDisable(true);
        } else {
            dropdownQuality.setDisable(false);
        }
    }


    public String getFormat() {

        String format = dropdownQuality.getValue()
            .substring(0, 3)
            .toLowerCase();

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

        Task<Void> downloadTask = new Task<>() {

            @Override
            protected Void call() throws Exception {

                updateProgress(0, 1);

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


        downloadTask.setOnRunning(event -> {
            download.setDisable(true);
        });


        downloadTask.setOnSucceeded(event -> {
            download.setDisable(false);
            downloadBar.progressProperty().unbind();
            downloadBar.setProgress(0);
        });


        downloadTask.setOnFailed(event -> {
            download.setDisable(false);
            downloadBar.progressProperty().unbind();
            downloadBar.setProgress(0);
        });


        downloadBar.progressProperty().bind(
            downloadTask.progressProperty()
        );


        Thread downloadThread = new Thread(downloadTask);
        downloadThread.start();
    }


    public boolean verifyURL() {

        return ytURL.getText().contains(
            "www.youtube.com/watch?v="
        );
    }
}