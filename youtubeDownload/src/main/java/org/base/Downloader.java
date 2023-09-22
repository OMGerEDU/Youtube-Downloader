package org.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import static org.base.FileUtils.runScript;

public class Downloader {

    //path to the yt-dlp executable, download yt-dlp from GitHub.
    // we moved to enviroment yt-dlp, add it to your PATH and install it correctly in order to use yt-dlp.
//    public static final String pathToDLP = new File("resources/yt-dlp.exe").getAbsolutePath();
//    public static final String pathToDlpParent = new File(pathToDLP).getParent();

    public static File downloadVideo(String link, File outputPath) throws IOException, InterruptedException {
        String cmd = "yt-dlp --no-playlist \"" + link+"\" -P \""+outputPath+"\"";
        runScript(cmd, System.getProperty("user.home"));
        return checkDownloaded(outputPath.getAbsolutePath());
    }

    public static File downloadAudio(String link, File outputPath) throws IOException, InterruptedException {
        String cmd = "yt-dlp --no-playlist -x --audio-format mp3 \"" + link+"\" -P \""+outputPath+"\"";
        runScript(cmd, System.getProperty("user.home"));
        return checkDownloaded(outputPath.getAbsolutePath());
    }
    public static File checkDownloaded(String outputLocation) {
        File toCheck = new File(outputLocation);
        if (toCheck.exists()) {
            System.out.println("File " + outputLocation + " exists");
            return toCheck;
        }
        return null;
    }
    public static void extractYtDlp() throws IOException {
        Files.copy(Objects.requireNonNull(Downloader.class.getClassLoader().getResourceAsStream("yt-dlp.exe")),
                Paths.get("resources", "yt-dlp.exe"),
                StandardCopyOption.REPLACE_EXISTING);
    }
}