package org.base;

import io.grpc.stub.StreamObserver;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass;
import org.protoc.DownloaderProto.DownloaderManagerServiceGrpc;
import java.io.File;
import java.util.logging.Logger;
import static org.base.Downloader.executeDownloadRequest;

public class DownloaderService extends DownloaderManagerServiceGrpc.DownloaderManagerServiceImplBase {

    Logger logger = Logger.getLogger(DownloaderService.class.getName());

    @Override
    public void executeCommand(DownloaderConfigOuterClass.DownloaderRequest request, StreamObserver<DownloaderConfigOuterClass.DownloaderResponse> responseObserver) {
        logger.info("Received request with the following details: " + request);
        //System.out.println("Received request for " + request.getConfig().getLink());
        try {
            File result = executeDownloadRequest(request);
            if (result != null) {
                DownloaderConfigOuterClass.DownloaderResponse.Builder builder = DownloaderConfigOuterClass.DownloaderResponse.newBuilder();
                DownloaderConfigOuterClass.DownloaderConfig config = DownloaderConfigOuterClass.DownloaderConfig.newBuilder().setLink(request.getConfig().getLink()).setPath(result.getPath()).build();
                //
                responseObserver.onNext(builder.setConfig(config).build());
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