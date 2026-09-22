package br.ytdash;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
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
private Label debugMessage;

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
    Label videoDuration,
    Label debugMessage
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
    this.debugMessage = debugMessage;

    verifyURLButton.setVisible(false);
    downloadBar.setVisible(false);

    // URL
    ytURL.textProperty().addListener((obs, oldValue, newValue) -> {

        if (!newValue.equals(verifiedURL)) {
            videoInfo = null;

            dropdownFormat.getItems().clear();
            dropdownQuality.getItems().clear();

            ControlsManager.setDisabled(true, dropdownFormat, dropdownQuality);
        }

        verifyDownload();
        verifyURLButton.setVisible(verifyURL());
    });

    // Format
    dropdownFormat.valueProperty().addListener((obs, oldValue, newValue) -> {

        if (newValue == null) {
            dropdownQuality.getItems().clear();

            ControlsManager.setDisabled(true, dropdownQuality);

        } else {

            ControlsManager.setDisabled(false, dropdownQuality);

            populateQualityMenu(videoInfo);
        }

        verifyDownload();
    });

    // Quality
    dropdownQuality.valueProperty().addListener((obs, oldValue, newValue) -> {
        verifyDownload();
    });

    // Initial state
    ControlsManager.setDisabled(true, dropdownQuality, download);

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
        showDebugMessage("URL Inválida");
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

        ControlsManager.setDisabled(true,ytURL,verifyURLButton);
    });

    verifyTask.setOnSucceeded(event -> {

        ControlsManager.setDisabled(false, ytURL, verifyURLButton);

        videoInfo = verifyTask.getValue();

        verifiedURL = ytURL.getText();

        videoTitle.setText("Título: " + videoInfo.getTitle());

        int totalSeconds = (int) videoInfo.getDuration();

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String duration = String.format("%d:%02d", minutes, seconds);

        videoDuration.setText("Duração: " + duration);

        String thumbURL = videoInfo.getThumbnail();

        if (thumbURL == null) {
            thumbnail.setImage(null); // ou uma imagem placeholder
            showDebugMessage("Prévia indisponível");
        } else {
        Image image = new Image(videoInfo.getThumbnail(), true);
        
        image.errorProperty().addListener((obs, oldValue, newValue) -> {
    if (newValue) {
        System.out.println("ERRO AO CARREGAR THUMBNAIL");
        System.out.println(image.getException());
        }
    });
        thumbnail.setImage(image);

    }
        System.out.println(videoInfo.getThumbnail());

        populateFormatMenu(videoInfo);
    });

    verifyTask.setOnFailed(event -> {

        verifyTask.getException().printStackTrace();
        String error = verifyTask.getException().getMessage();

        if (error.contains("This video is unavailable")) {
            showDebugMessage("Vídeo não encontrado \n       ou indisponível");

        } else if (
            error.contains("Unable to download API page")
            && error.contains("Failed to resolve")
        ) {
            showDebugMessage("Sem conexão com a internet");

        } else {
            showDebugMessage("Falha ao verificar vídeo");
        }

        ControlsManager.setDisabled(false, ytURL, verifyURLButton);
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
        ControlsManager.setDisabled(true, download);

    } else {
        ControlsManager.setDisabled(false, download);
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

    ControlsManager.setDisabled(
        dropdownFormat.getItems().isEmpty(),
        dropdownFormat
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
                progress -> {
                    updateProgress(progress, 1);
                },
                () -> {
                    updateMessage("has already been downloaded");
                }
            );

            return null;
        }
    };

    downloadTask.setOnRunning(event -> {

        ControlsManager.setDisabled(
            true,
            download,
            ytURL,
            dropdownFormat,
            dropdownQuality,
            verifyURLButton
        );
    });

    downloadTask.setOnSucceeded(event -> {

        if ("has already been downloaded".equals(downloadTask.getMessage())) {
            showDebugMessage("Arquivo já existe");
        } else {
            showDebugMessage("Download Concluído");
        }

        ControlsManager.setDisabled(
            false,
            download,
            ytURL,
            dropdownFormat,
            dropdownQuality,
            verifyURLButton
        );

        downloadBar.setVisible(false);
        downloadBar.progressProperty().unbind();
        downloadBar.setProgress(0);
    });

    downloadTask.setOnFailed(event -> {

        downloadTask.getException().printStackTrace();
        String error = downloadTask.getException().getMessage();

        if (error.contains("This video is unavailable")) {
            showDebugMessage("Vídeo ficou indisponível");

        } else if (
            error.contains("Unable to download API page")
            && error.contains("Failed to resolve")
        ) {
            showDebugMessage("Sem conexão com a internet");

        } else {
            showDebugMessage("Falha no download");
        }

        ControlsManager.setDisabled(
            false,
            download,
            ytURL,
            dropdownFormat,
            dropdownQuality,
            verifyURLButton
        );

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

    return ytURL.getText().contains("youtube.com/watch?v=")
        || ytURL.getText().contains("youtube.com/shorts/")
        || ytURL.getText().contains("youtube.com/embed/")
        || ytURL.getText().contains("youtube.com/v/")
        || ytURL.getText().contains("youtube.com/e/")
        || ytURL.getText().contains("youtube.com/live/")
        || ytURL.getText().contains("youtu.be/");
}

public void showDebugMessage(String message) {
    debugMessage.setText(message);
    debugMessage.setVisible(true);

    PauseTransition pause = new PauseTransition(
        Duration.seconds(5)
    );

    pause.setOnFinished(event -> {
        debugMessage.setVisible(false);
    });
    pause.play();

}
}