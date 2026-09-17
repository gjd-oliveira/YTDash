package br.ytdash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // =========================
        // 1. COMPONENTES
        // =========================

        // TÍTULO
        Label ytdashTitle = new Label();
        ytdashTitle.setText("YTDash");
        ytdashTitle.getStyleClass().add("title");

        // SUBTÍTULO
        Label ytdashSubtitle = new Label();
        ytdashSubtitle.setText("Downloader");
        ytdashSubtitle.getStyleClass().add("subtitle");

        // LABEL URL
        Label ytURLplaceholder = new Label();
        ytURLplaceholder.setText("URL:");
        ytURLplaceholder.getStyleClass().add("label-text");

        // LABEL FORMATO
        Label dropdownFormatLabel = new Label();
        dropdownFormatLabel.setText("Formato:");
        dropdownFormatLabel.getStyleClass().add("label-text");

        // LABEL QUALIDADE
        Label dropdownQualityLabel = new Label();
        dropdownQualityLabel.setText("Qualidade:");
        dropdownQualityLabel.getStyleClass().add("label-text");

        // TEXT FIELD
        TextField ytURL = new TextField();
        ytURL.setPrefSize(230, 25);
        ytURL.setPromptText("Cole a URL do vídeo aqui...");

        // DROPDOWN FORMATO
        ComboBox<String> dropdownFormat = new ComboBox<>();
        dropdownFormat.setPrefSize(75, 25);
        dropdownFormat.setPromptText("Formato");

        dropdownFormat.getItems().add("MP4");
        dropdownFormat.getItems().add("MP3");
        dropdownFormat.getItems().add("MKV");

        // DROPDOWN QUALIDADE
        ComboBox<String> dropdownQuality = new ComboBox<>();
        dropdownQuality.setPrefSize(150, 25);
        dropdownQuality.setPromptText("Qualidade");

        // BOTÃO DOWNLOAD
        Button download = new Button();
        download.setPrefSize(250, 30);
        download.setText("Download");

        // BOTÃO VERIFICAR URL
        Button verifyURLButton = new Button("✓");
        verifyURLButton.setPrefSize(50, 50);

        // PROGRESS BAR
        ProgressBar downloadBar = new ProgressBar(0);
        downloadBar.setPrefSize(300, 20);

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
        videoService
        );


        // =========================
        // 2. CONTAINER
        // =========================

        Pane box = new Pane();


        // =========================
        // 3. POSIÇÃO DOS COMPONENTES
        // =========================

        ytdashTitle.setLayoutX(110);
        ytdashTitle.setLayoutY(55);

        ytdashSubtitle.setLayoutX(150);
        ytdashSubtitle.setLayoutY(110);

        ytURLplaceholder.setLayoutX(25);
        ytURLplaceholder.setLayoutY(150);

        ytURL.setLayoutX(80);
        ytURL.setLayoutY(152);

        dropdownFormatLabel.setLayoutX(50);
        dropdownFormatLabel.setLayoutY(225);

        dropdownFormat.setLayoutX(45);
        dropdownFormat.setLayoutY(250);

        dropdownQualityLabel.setLayoutX(225);
        dropdownQualityLabel.setLayoutY(225);

        dropdownQuality.setLayoutX(190);
        dropdownQuality.setLayoutY(250);

        download.setLayoutX(65);
        download.setLayoutY(375);

        verifyURLButton.setLayoutX(330);
        verifyURLButton.setLayoutY(147);

        downloadBar.setLayoutX(40);
        downloadBar.setLayoutY(500);


        // =========================
        // 4. ADICIONAR COMPONENTES
        // =========================

        box.getChildren().addAll(
            ytdashTitle,
            ytdashSubtitle,
            ytURLplaceholder,
            dropdownFormatLabel,
            dropdownQualityLabel,
            ytURL,
            dropdownFormat,
            dropdownQuality,
            download,
            verifyURLButton,
            downloadBar
        );


        // =========================
        // 5. SCENE
        // =========================

        Scene scene = new Scene(box);

        // CSS
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );


        // =========================
        // 6. STAGE
        // =========================

        stage.setTitle("YTDash");

        stage.setX(1250);
        stage.setY(175);

        stage.setWidth(400);
        stage.setHeight(650);

        stage.setScene(scene);


        // =========================
        // 7. MOSTRAR
        // =========================

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}