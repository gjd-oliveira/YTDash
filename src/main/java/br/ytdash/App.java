package br.ytdash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // =========================
        // 1. COMPONENTES
        // =========================

        // TÍTULO
        Label ytdashTitle = new Label("YTDash");
        ytdashTitle.getStyleClass().add("title");

        // SUBTÍTULO
        Label ytdashSubtitle = new Label("Downloader");
        ytdashSubtitle.getStyleClass().add("subtitle");

        // LABEL URL
        Label ytURLplaceholder = new Label("URL:");
        ytURLplaceholder.getStyleClass().add("label-text");

        // LABEL FORMATO
        Label dropdownFormatLabel = new Label("Formato:");
        dropdownFormatLabel.getStyleClass().add("label-text");

        // LABEL QUALIDADE
        Label dropdownQualityLabel = new Label("Qualidade:");
        dropdownQualityLabel.getStyleClass().add("label-text");

        // TEXT FIELD
        TextField ytURL = new TextField();
        ytURL.setPrefSize(230, 25);
        ytURL.setPromptText("Cole a URL do vídeo aqui...");

        // DROPDOWN FORMATO
        ComboBox<String> dropdownFormat = new ComboBox<>();
        dropdownFormat.setPrefSize(90, 25);
        dropdownFormat.setPromptText("Formato");
        dropdownFormat.setDisable(true);

        // DROPDOWN QUALIDADE
        ComboBox<String> dropdownQuality = new ComboBox<>();
        dropdownQuality.setPrefSize(150, 25);
        dropdownQuality.setPromptText("Qualidade");

        // VIDEO PLACEHOLDER
        StackPane videoPlaceholder = new StackPane();
        videoPlaceholder.setPrefSize(311, 175);
        videoPlaceholder.getStyleClass().add("glass-placeholder");

        // CONTEÚDO DO VÍDEO
        ImageView thumbnail = new ImageView();
        thumbnail.setFitWidth(311);
        thumbnail.setFitHeight(175);
        thumbnail.setPreserveRatio(false);

        Rectangle thumbnailClip = new Rectangle(311, 175);
        thumbnailClip.setArcWidth(24);
        thumbnailClip.setArcHeight(24);

        thumbnail.setClip(thumbnailClip);

        videoPlaceholder.getChildren().add(thumbnail);

        // TÍTULO DO VÍDEO
        Label videoTitle = new Label();
        videoTitle.getStyleClass().add("video-info");
        videoTitle.setLayoutY(187);
        videoTitle.setLayoutX(5);
        videoTitle.setVisible(true);

        // DURAÇÃO DO VÍDEO
        Label videoDuration = new Label();
        videoDuration.getStyleClass().add("video-info");
        videoDuration.setLayoutY(213);
        videoDuration.setLayoutX(5);
        videoDuration.setVisible(true);

        // PRIMEIRA BARRA DO TÍTULO
        Rectangle titleBar = new Rectangle(230, 20);
        titleBar.getStyleClass().add("title-placeholder");
        titleBar.setLayoutY(185);

        // SEGUNDA BARRA DO TÍTULO
        Rectangle titleBarLine2 = new Rectangle(175, 20);
        titleBarLine2.getStyleClass().add("title-placeholder");
        titleBarLine2.setLayoutY(210);

        // BOTÃO DOWNLOAD
        Button download = new Button("Download");
        download.setPrefSize(250, 30);

        // BOTÃO VERIFICAR URL
        Button verifyURLButton = new Button("x");
        verifyURLButton.setPrefSize(20, 20);
        verifyURLButton.setTranslateY(-3);

        // PROGRESS BAR
        ProgressBar downloadBar = new ProgressBar(0);
        downloadBar.setPrefSize(300, 20);

        // =========================
        // 2. PAINÉIS
        // =========================

        // HEADER
        VBox header = new VBox();

        header.getChildren().addAll(
            ytdashTitle,
            ytdashSubtitle
        );

        header.setSpacing(-15);
        ytdashSubtitle.setTranslateX(40);


        // URL
        HBox urlPanel = new HBox();

        urlPanel.getChildren().addAll(
            ytURLplaceholder,
            ytURL,
            verifyURLButton
        );

        urlPanel.setSpacing(15);
        ytURLplaceholder.setTranslateY(3);


        // FORMATO
        VBox formatPanel = new VBox();

        formatPanel.getChildren().addAll(
            dropdownFormatLabel,
            dropdownFormat
        );

        dropdownFormatLabel.setTranslateX(15);


        // QUALIDADE
        VBox qualityPanel = new VBox();

        qualityPanel.getChildren().addAll(
            dropdownQualityLabel,
            dropdownQuality
        );

        dropdownQualityLabel.setTranslateX(35);


        // OPÇÕES
        HBox optionsPanel = new HBox();

        optionsPanel.getChildren().addAll(
            formatPanel,
            qualityPanel
        );

        optionsPanel.setSpacing(50);


        // DOWNLOAD
        VBox downloadPanel = new VBox();

        downloadPanel.getChildren().addAll(
            download,
            downloadBar
        );

        downloadBar.setTranslateX(-25);
        downloadBar.setTranslateY(30);


        // PLACEHOLDERS DO VÍDEO
        Pane videoPlaceholderPanel = new Pane();

        videoPlaceholderPanel.getChildren().addAll(
            videoPlaceholder,
            titleBar,
            titleBarLine2,
            videoTitle,
            videoDuration
        );
        
        // CRIAR O DOWNLOAD SERVICE
        DownloadService downloadService = new DownloadService();
        VideoService videoService = new VideoService();

        // CRIAR O CONTROLADOR  
        Controller controller = new Controller(
            ytURL,
            dropdownFormat,
            dropdownQuality,
            verifyURLButton,
            download,
            downloadBar,
            downloadService,
            videoService,
            thumbnail,
            videoTitle,
            videoDuration
        );


        // =========================
        // 3. CONTAINER
        // =========================

        Pane box = new Pane();


        // =========================
        // 4. POSIÇÃO DOS PAINÉIS
        // =========================

        header.setLayoutX(110);
        header.setLayoutY(25);

        urlPanel.setLayoutX(35);
        urlPanel.setLayoutY(120);

        optionsPanel.setLayoutX(45);
        optionsPanel.setLayoutY(170);

        downloadPanel.setLayoutX(65);
        downloadPanel.setLayoutY(500);

        videoPlaceholderPanel.setLayoutX(35);
        videoPlaceholderPanel.setLayoutY(250);


        // =========================
        // 5. ADICIONAR PAINÉIS
        // =========================

        box.getChildren().addAll(
            header,
            urlPanel,
            optionsPanel,
            downloadPanel,
            videoPlaceholderPanel
        );


        // =========================
        // 6. SCENE
        // =========================

        Scene scene = new Scene(box);

        // CSS
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );


        // =========================
        // 7. STAGE
        // =========================

        stage.setTitle("YTDash");

        stage.setX(1250);
        stage.setY(175);

        stage.setWidth(400);
        stage.setHeight(650);

        stage.setScene(scene);


        // =========================
        // 8. MOSTRAR
        // =========================

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}