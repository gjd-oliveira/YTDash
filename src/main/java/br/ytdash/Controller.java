package br.ytdash;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Label;

public class Controller {

private TextField ytURL;
private ComboBox<String> dropdownFormat;
private ComboBox<String> dropdownQuality;
private Button verifyURLButton;
private Button download;
private ProgressBar downloadBar;
private DownloadService downloadService;
private VideoService videoService;
private VideoInfo videoInfo;
private String verifiedURL;
private ImageView thumbnail;
private Label videoTitle;
private Label videoDuration;

public Controller(
    TextField ytURL,
    ComboBox<String> dropdownFormat,
    ComboBox<String> dropdownQuality,
    Button verifyURLButton,
    Button download,
    ProgressBar downloadBar,
    DownloadService downloadService,
    VideoService videoService,
    ImageView thumbnail,
    Label videoTitle,
    Label videoDuration
) {

    this.ytURL = ytURL;
    this.dropdownFormat = dropdownFormat;
    this.dropdownQuality = dropdownQuality;
    this.verifyURLButton = verifyURLButton;
    this.download = download;
    this.downloadBar = downloadBar;
    this.downloadService = downloadService;
    this.videoService = videoService;
    this.thumbnail = thumbnail;
    this.videoTitle = videoTitle;
    this.videoDuration = videoDuration;

    verifyURLButton.setVisible(false);
    downloadBar.setVisible(false);

    // URL
    ytURL.textProperty().addListener((obs, oldValue, newValue) -> {

        if (!newValue.equals(verifiedURL)) {
            videoInfo = null;

            dropdownFormat.getItems().clear();
            dropdownQuality.getItems().clear();

            dropdownFormat.setDisable(true);
            dropdownQuality.setDisable(true);
        }

        verifyDownload();
        verifyURLButton.setVisible(verifyURL());
    });

    // Format
    dropdownFormat.valueProperty().addListener((obs, oldValue, newValue) -> {

        if (newValue == null) {
            dropdownQuality.getItems().clear();
            dropdownQuality.setDisable(true);
        } else {
            dropdownQuality.setDisable(false);
            populateQualityMenu(videoInfo);
        }

        verifyDownload();
    });

    // Quality
    dropdownQuality.valueProperty().addListener((obs, oldValue, newValue) -> {
        verifyDownload();
    });

    // Initial state
    dropdownQuality.setDisable(true);
    download.setDisable(true);

    // Verify URL
    verifyURLButton.setOnAction(event -> {
        verifyVideo();
    });

    // Download
    download.setOnAction(event -> {
        startDownload();
    });
}

// Consulta o vídeo através do VideoService e armazena os dados retornados.
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

        videoInfo = verifyTask.getValue();

        verifiedURL = ytURL.getText();

        videoTitle.setText("Título: " + videoInfo.getTitle());

        int totalSeconds = (int) videoInfo.getDuration();

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String duration = String.format("%d:%02d", minutes, seconds);

        videoDuration.setText("Duração: " + duration);

        Image image = new Image(videoInfo.getThumbnail(), true);
        thumbnail.setImage(image);

        System.out.println(videoInfo.getThumbnail());

        populateFormatMenu(videoInfo);
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

// Verifica se todos os campos necessários foram preenchidos e validados para liberar o download.
public void verifyDownload() {

    if (
        ytURL.getText().isEmpty() ||
        verifiedURL == null ||
        !ytURL.getText().equals(verifiedURL) ||
        dropdownFormat.getValue() == null ||
        dropdownQuality.getValue() == null
    ) {
        download.setDisable(true);
    } else {
        download.setDisable(false);
    }
}

// Popula o menu de qualidade usando apenas as qualidades existentes no vídeo.
public void populateQualityMenu(VideoInfo videoInfo) {

    dropdownQuality.getItems().clear();

    for (VideoFormat format : videoInfo.getFormats()) {

        if ("MP4".equals(dropdownFormat.getValue())) {

            if (format.getVcodec() == null ||
                format.getVcodec().equals("none")) {
                continue;
            }

            int height = format.getHeight();

            if (height <= 0) {
                continue;
            }

            String quality = "MP4 " + height + "P";

            if (!dropdownQuality.getItems().contains(quality)) {
                dropdownQuality.getItems().add(quality);
            }

        } else if ("MP3".equals(dropdownFormat.getValue())) {

            if (format.getAcodec() == null ||
                format.getAcodec().equals("none")) {
                continue;
            }

            if (format.getAbr() == null ||
                format.getAbr() <= 0) {
                continue;
            }

            int abr = format.getAbr().intValue();

            String quality = "MP3 " + abr + "KBPS";

            if (!dropdownQuality.getItems().contains(quality)) {
                dropdownQuality.getItems().add(quality);
            }
        }
    }
}

public void populateFormatMenu(VideoInfo videoInfo) {

    dropdownFormat.getItems().clear();

    boolean hasVideo = false;
    boolean hasAudio = false;

    for (VideoFormat format : videoInfo.getFormats()) {

        if (format.getVcodec() != null &&
            !format.getVcodec().equals("none")) {
            hasVideo = true;
        }

        if (format.getAcodec() != null &&
            !format.getAcodec().equals("none")) {
            hasAudio = true;
        }
    }

    if (hasVideo) {
        dropdownFormat.getItems().add("MP4");
    }

    if (hasAudio) {
        dropdownFormat.getItems().add("MP3");
    }

    dropdownFormat.setDisable(
        dropdownFormat.getItems().isEmpty()
    );
}

// Inicia o download em uma thread separada e atualiza a barra de progresso.
public void startDownload() {

    downloadBar.setVisible(true);

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
        ytURL.setDisable(true);
        dropdownFormat.setDisable(true);
        dropdownQuality.setDisable(true);
        verifyURLButton.setDisable(true);
    });

    downloadTask.setOnSucceeded(event -> {
        download.setDisable(false);
        ytURL.setDisable(false);
        dropdownFormat.setDisable(false);
        dropdownQuality.setDisable(false);
        verifyURLButton.setDisable(false);
        downloadBar.setVisible(false);
        downloadBar.progressProperty().unbind();
        downloadBar.setProgress(0);
    });

    downloadTask.setOnFailed(event -> {
        download.setDisable(false);
        ytURL.setDisable(false);
        dropdownFormat.setDisable(false);
        dropdownQuality.setDisable(false);
        verifyURLButton.setDisable(false);
        downloadBar.setVisible(false);
        downloadBar.progressProperty().unbind();
        downloadBar.setProgress(0);
    });

    downloadBar.progressProperty().bind(
        downloadTask.progressProperty()
    );

    Thread downloadThread = new Thread(downloadTask);
    downloadThread.start();
}

// Obtém o valor numérico da qualidade escolhida pelo usuário.
public String getQuality() {

    String quality = dropdownQuality.getValue();

    int space = quality.indexOf(" ");

    String value = quality.substring(space + 1);

    value = value.replace("P", "");
    value = value.replace("KBPS", "");

    return value;
}

// Obtém o formato MP4 ou MP3 selecionado pelo usuário.
public String getFormat() {
    return dropdownFormat.getValue().toLowerCase();
}

// Verifica se a URL possui a estrutura esperada de um vídeo do YouTube.
public boolean verifyURL() {

    return ytURL.getText().contains(
        "www.youtube.com/"
    );

}
}
