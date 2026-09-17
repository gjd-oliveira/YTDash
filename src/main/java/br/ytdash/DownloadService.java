package br.ytdash;

import java.io.File;
import java.util.Scanner;
import java.util.function.Consumer;

public class DownloadService {

    public void downloadCommand(String url, String format, String quality, Consumer<Double> progressCallback) throws Exception {

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

        String downloads = System.getProperty("user.home") + "\\Downloads";

        download.directory(new File(downloads));

        Process processo = download.start();

        Scanner ytdlpInput =
        new Scanner(processo.getInputStream());

        while (ytdlpInput.hasNextLine()) {

            String percentString = ytdlpInput.nextLine();
            System.out.println(percentString);

            if (percentString.contains("%")) {
                String percent = extractPercent(percentString);

                double progress = Double.parseDouble(percent) / 100;

                progressCallback.accept(progress);

            }
        }

        processo.waitFor();

        }

        public String extractPercent(String linha) {

            int startPercent = linha.indexOf("]") + 1;
            int endPercent = linha.indexOf("%");

            String percent = linha.substring(startPercent, endPercent).trim();

            return percent;
        }

}