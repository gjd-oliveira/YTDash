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

    // Seleciona a maior thumbnail JPG disponível.
    public String getThumbnailJPG(JsonNode thumbnails) {

        String thumbURL = null;
        int sizeThumbnail = 0;

        for (JsonNode thumbnail : thumbnails) {
            if(thumbnail.has("url")) {
                if (thumbnail.get("url").asText().contains("jpg")) {
                    if (thumbnail.has("height")) {
                        if (sizeThumbnail < thumbnail.get("height").asInt()) {
                            thumbURL = thumbnail.get("url").asText();
                            sizeThumbnail = thumbnail.get("height").asInt();
                        }
                    }
                }
            }
        }
        return thumbURL;
    }
}