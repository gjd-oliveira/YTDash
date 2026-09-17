package br.ytdash;

import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class VideoService {

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

    process.waitFor();

    ObjectMapper mapper = new ObjectMapper();

    mapper.setPropertyNamingStrategy(
        PropertyNamingStrategies.SNAKE_CASE
    );

    VideoInfo videoInfo = mapper.readValue(
        json.toString(),
        VideoInfo.class
    );

    return videoInfo;

}
}