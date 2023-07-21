package org.example;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        String arg = "https://www.youtube.com/watch?v=pXRviuL6vMY&ab_channel=FueledByRamen";
        if (args.length != 1) {
            System.err.println("Found no single argument to download, defaulting to " + arg);
        }
        else {
            arg = args[0];
        }
        File outputFolder = new File("resources").getAbsoluteFile();
        outputFolder.mkdir();
        Downloader.extractYtDlp();
        //Download audio from YouTube
        File audio = Downloader.downloadAudio(arg, outputFolder);
        System.out.println(audio);
        //Download video from Youtube
        File video = Downloader.downloadVideo(arg, outputFolder);
        System.out.println(video);

    }
}