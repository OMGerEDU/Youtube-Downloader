package org.base;

import org.protoc.DownloaderProto.DownloaderConfigOuterClass;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.logging.Logger;

import static org.base.FileUtils.runScript;

public class Downloader {
    static final Logger LOGGER = Logger.getLogger(Downloader.class.getName());

    //path to the yt-dlp executable, download yt-dlp from GitHub.
    // we moved to enviroment yt-dlp, add it to your PATH and install it correctly in order to use yt-dlp.
//    public static final String pathToDLP = new File("resources/yt-dlp.exe").getAbsolutePath();
//    public static final String pathToDlpParent = new File(pathToDLP).getParent();

    public static File executeDownloadRequest(DownloaderRequest request) {
        File downloadedFile = null;
        switch (request.getConfig().getDownloadType()) {
            case "Audio" ->
                    downloadedFile = downloadAudio(request.getConfig().getLink(), new File(request.getConfig().getPath()), request.getConfig().getIsPlaylist());
            case "Video" ->
                    downloadedFile =downloadVideo(request.getConfig().getLink(), new File(request.getConfig().getPath()), request.getConfig().getIsPlaylist());
        }
        return downloadedFile;
    }


    public static File downloadVideo(String link, File outputPath, Boolean ... isPlaylist)  {
        try {
            String cmd = "yt-dlp " + (isPlaylist[0] ? "" : "--no-playlist ") + "\""+link+"\" -P \""+outputPath+"\"";
            runScript(cmd, System.getProperty("user.home"));
        }
        catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
        return checkDownloaded(outputPath.getAbsolutePath());
    }

    public static File downloadAudio(String link, File outputPath, Boolean ... isPlaylist)  {
        isPlaylist = (isPlaylist.length > 0) ? isPlaylist : new Boolean[]{false};
        try {
            String cmd = "yt-dlp " + (isPlaylist[0] ? "" : "--no-playlist ") + "-x --audio-format mp3 \"" + link + "\" -P \"" + outputPath + "\"";
            runScript(cmd, System.getProperty("user.home"));
        }
        catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
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