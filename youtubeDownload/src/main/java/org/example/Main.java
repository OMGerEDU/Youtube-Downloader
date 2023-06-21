package org.example;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        File outputFolder = new File("resources").getAbsoluteFile();
        //Download audio from YouTube
        File audio = Downloader.downloadAudio("https://www.youtube.com/watch?v=pXRviuL6vMY&ab_channel=FueledByRamen", outputFolder);
        System.out.println(audio);
        //Download video from Youtube
        File video = Downloader.downloadVideo("https://www.youtube.com/watch?v=pXRviuL6vMY&ab_channel=FueledByRamen", outputFolder);
        System.out.println(video);

    }
}