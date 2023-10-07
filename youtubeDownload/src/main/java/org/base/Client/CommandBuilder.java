package org.base.Client;

import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderConfig;

import java.util.logging.Logger;

public class CommandBuilder {
    private DownloaderConfig config;
    static final Logger LOGGER = Logger.getLogger(CommandBuilder.class.getName());


    public CommandBuilder(DownloaderConfig config) {
        this.config = config;
    }

    public String build() {
        StringBuilder cmd = new StringBuilder("yt-dlp ");

        // If it's not a playlist, add the --no-playlist option
        if (!config.getIsPlaylist()) {
            cmd.append("--no-playlist ");
        }
        // Add the link
        cmd.append("\"").append(config.getLink()).append("\" ");
        // Add the output path option
        cmd.append("-P \"").append(config.getPath()).append("\" ");
        // Add the retries option
        if (config.getRetries() > 0) {
            cmd.append("-R ").append(config.getRetries()).append(" ");
        }
        if (config.getDownloadType().equals("Audio")) {
            // Download as audio
            cmd.append("-x --audio-format ").append(config.getOutputFormat()).append(" mp3");
            // Add the embed thumbnail option
            if (config.getEmbedThumbnail()) {
                cmd.append("--embed-thumbnail ");
            }
        } else if (config.getDownloadType().equals("Video")) {
            // Download as video
            cmd.append("-f ").append(config.getOutputFormat()).append(" mp4");
            // If resolution is specified, add the resolution option
            if (config.getResolution() != null && !config.getResolution().isEmpty()) {
                cmd.append("[height<=?").append(config.getResolution()).append("] ");
            }
            // Add the embed subtitles option
            if (config.getEmbedSubtitles()) {
                cmd.append(" --write-auto-sub ");
            }
            // Add the embed thumbnail option
            if (config.getEmbedThumbnail()) {
                cmd.append(" --write-thumbnail --embed-thumbnail ");
            }
        }
        LOGGER.info("Command: " + cmd.toString());
        return cmd.toString();
    }

}

