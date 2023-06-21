package org.example;

import java.io.File;
import java.io.IOException;
import static org.example.FileUtils.runScript;
public class Downloader {

    //path to the yt-dlp executable, download yt-dlp from GitHub.
    public static final String pathToDLP = new File("resources/yt-dlp.exe").getAbsolutePath();
    public static final String pathToDlpParent = new File(pathToDLP).getParent();

    public static File downloadVideo(String link, File outputPath) throws IOException, InterruptedException {
        String cmd = "yt-dlp.exe \"" + link+"\" -P \""+outputPath+"\"";
        runScript(cmd, pathToDlpParent);
        return checkDownloaded(outputPath.getAbsolutePath());
    }

    public static File downloadAudio(String link, File outputPath) throws IOException, InterruptedException {
        String cmd = "yt-dlp.exe -x --audio-format mp3 \"" + link+"\" -P \""+outputPath+"\"";
        runScript(cmd, pathToDlpParent);
        return checkDownloaded(outputPath.getAbsolutePath());

    }


    public static File checkDownloaded(String outputLocation) {
        File toCheck = new File(outputLocation);
        if (toCheck.exists())
            return toCheck;
        return null;
    }





}
