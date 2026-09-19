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
            throw new Exception(
                "Falha ao obter informações do vídeo."
            );
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.setPropertyNamingStrategy(
            PropertyNamingStrategies.SNAKE_CASE
        );

        JsonNode root = mapper.readTree(json.toString());

        JsonNode thumbnails = root.get("thumbnails");

        System.out.println(thumbnails);

        VideoInfo videoInfo = mapper.readValue(
            json.toString(),
            VideoInfo.class
        );

        return videoInfo;
    }
}