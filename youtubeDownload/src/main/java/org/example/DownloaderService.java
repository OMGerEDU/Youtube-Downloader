package org.example;

import io.grpc.stub.StreamObserver;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass;
import org.protoc.DownloaderProto.DownloaderServiceGrpc;

import java.io.File;
import java.util.logging.Logger;

import static org.example.Downloader.downloadVideo;
import static org.example.FileUtils.runScript;

public class DownloaderService extends DownloaderServiceGrpc.DownloaderServiceImplBase {

    Logger logger = Logger.getLogger(DownloaderService.class.getName());
    @Override
    public void executeCommand(DownloaderConfigOuterClass.DownloaderConfig request, StreamObserver<DownloaderConfigOuterClass.DownloaderConfig> responseObserver) {
        try {
            File result = downloadVideo(request.getLink(),new File(request.getPath()));
            if (result != null) {
                responseObserver.onNext(request);
                responseObserver.onCompleted();
            }
            else {
                responseObserver.onError(new Exception("File not found"));
            }
        }
        catch (Exception e){
            logger.warning(e.getMessage());
        }
    }
}