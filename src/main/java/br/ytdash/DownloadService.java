package br.ytdash;

import java.io.File;
import java.util.Scanner;
import java.util.function.Consumer;

public class DownloadService {

// Executa o yt-dlp com os parâmetros escolhidos e acompanha o progresso do download.
public void downloadCommand(
    String url,
    String format,
    String quality,
    Consumer<Double> progressCallback
) throws Exception {

    ProcessBuilder download;

    if (format.equals("mp3")) {

        download = new ProcessBuilder(
            "yt-dlp",
            "-x",
            "--audio-format",
            "mp3",
            "--audio-quality",
            "0",
            url
        );

    } else {

        download = new ProcessBuilder(
            "yt-dlp",
            "-f",
            "bv*+ba/b",
            "-S",
            "res:" + quality + ",fps:60,+codec:avc:m4a,br",
            "--merge-output-format",
            format,
            url
        );
    }

    download.redirectErrorStream(true);

    String downloads =
        System.getProperty("user.home") + "\\Downloads";

    download.directory(new File(downloads));

    Process ytdlpProcess = download.start();

    Scanner ytdlpInput =
        new Scanner(ytdlpProcess.getInputStream());

    boolean existingFile = false;

    while (ytdlpInput.hasNextLine()) {

        String line = ytdlpInput.nextLine();

        System.out.println(line);

        if (line.contains("has already been downloaded")) {
            existingFile = true;
        }

        if (line.contains("%")) {

            String percent = extractPercent(line);

            double progress =
                Double.parseDouble(percent) / 100;

            progressCallback.accept(progress);
        }
    }

    int exitCode = ytdlpProcess.waitFor();

    System.out.println("Exit code: " + exitCode);

    if (existingFile) {
        System.out.println("Arquivo existente!");
    } else if (exitCode == 0) {
        System.out.println("Download Concluído!");
    } else {
        System.out.println("ERROR");
    }
}

// Extrai a porcentagem de progresso exibida pelo yt-dlp.
public String extractPercent(String line) {

    int startPercent = line.indexOf("]") + 1;
    int endPercent = line.indexOf("%");

    String percent =
        line.substring(startPercent, endPercent).trim();

    return percent;

}
}