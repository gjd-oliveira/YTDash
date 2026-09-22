package br.ytdash;

import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class VideoService {

    // Consulta o yt-dlp e transforma os dados do vídeo em um objeto VideoInfo.
    public VideoInfo getVideoInfo(String url) throws Exception {

        ProcessBuilder videoProcess = new ProcessBuilder(
            "yt-dlp",
            "--dump-single-json",
            url
        );

        videoProcess.redirectErrorStream(true);

        Process process = videoProcess.start();

        Scanner videoOutput =
            new Scanner(process.getInputStream());

        StringBuilder json = new StringBuilder();

        while (videoOutput.hasNextLine()) {
            json.append(videoOutput.nextLine());
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {

            Scanner errorOutput =
                new Scanner(process.getErrorStream());

            StringBuilder error = new StringBuilder();

            while (errorOutput.hasNextLine()) {
                error.append(errorOutput.nextLine());
            }

            throw new Exception(error.toString());
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.setPropertyNamingStrategy(
            PropertyNamingStrategies.SNAKE_CASE
        );

        JsonNode root = mapper.readTree(json.toString());

        VideoInfo videoInfo = mapper.readValue(
            json.toString(),
            VideoInfo.class
        );

        videoInfo.setThumbnail(
            getThumbnailJPG(root.get("thumbnails"))
        );

        return videoInfo;
    }

    // Seleciona a maior thumbnail JPG disponível seguindo critérios de maior height e maior widht
    // SE o URL for jpg e não conter "?""
    // Mantém o formato 16:9
public String getThumbnailJPG(JsonNode thumbnails) {

    String thumbURL = null;
    int sizeThumbnail = 0;

    for (JsonNode thumbnail : thumbnails) {

        if (thumbnail.has("url") &&
            thumbnail.has("width") &&
            thumbnail.has("height")) {

            String url = thumbnail.get("url").asText();

            if (url.contains("jpg") && !url.contains("?")) {

                int width = thumbnail.get("width").asInt();
                int height = thumbnail.get("height").asInt();

                double ratio = (double) width / height;

                if (Math.abs(ratio - (16.0 / 9.0)) < 0.01) {

                    if (width * height > sizeThumbnail) {

                        thumbURL = url;
                        sizeThumbnail = width * height;
                    }
                }
            }
        }
    }

    return thumbURL;
}
}