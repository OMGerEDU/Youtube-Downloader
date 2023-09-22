package org.base;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderRequest;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderResponse;
import org.protoc.DownloaderProto.DownloaderManagerServiceGrpc;


import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class AutoChannelClient {
    String host;
    int port;

    static final Logger LOGGER = Logger.getLogger(AutoChannelClient.class.getName());

    private final ManagedChannel channel;

    DownloaderManagerServiceGrpc.DownloaderManagerServiceBlockingStub blockingStub;

    DownloaderManagerServiceGrpc.DownloaderManagerServiceStub serviceStub;



    SslContext sslContext;



    // plaintext
//    public AutoChannelClient(String host, int port) {
//        channel = ManagedChannelBuilder.forAddress(host,port)
//                .usePlaintext().build();
//        serviceStub = ApiServiceGrpc.newStub(channel);
//        blockingStub = ApiServiceGrpc.newBlockingStub(channel);
//        asyncStub = ApiServiceGrpc.newStub(channel);
//        filesAsyncStub = StorageHandlerGrpc.newStub(channel);
//    }



    // SSL
    public AutoChannelClient(String host, int port, SslContext sslContext) {
        channel = NettyChannelBuilder.forAddress(host,port)
                .sslContext(sslContext)
                .build();
        blockingStub = DownloaderManagerServiceGrpc.newBlockingStub(channel);
        serviceStub = DownloaderManagerServiceGrpc.newStub(channel);
    }



    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public static DownloaderResponse DownloaderRequest(AutoChannelClient client,DownloaderRequest request) {
        return client.blockingStub.executeCommand(request);
    }




    public static SslContext loadTLScredentials() throws SSLException {
        File serverCACert = new File("cert/ca-cert.pem");
        File clientCertFile = new File("cert/client-cert.pem");
        File clientKeyFile = new File("cert/client-key.pem");

        if (!clientCertFile.exists()|| !clientKeyFile.exists() || !serverCACert.exists())
            throw new SSLException("cert files not found \n" +serverCACert+"\n "+clientCertFile+"\n "+clientKeyFile+"\n");

        return GrpcSslContexts.forClient()
                .keyManager(clientCertFile, clientKeyFile)
                .trustManager(serverCACert)
                .build();
    }






//    public static void main(String[] args) throws InterruptedException, SSLException {
//        SslContext sslContext = AutoChannelClient.loadTLScredentials();
//        LOGGER.info("Starting client...");
//        AutoChannelClient client = new AutoChannelClient(SERVER_HOST, SERVER_PORT,sslContext);
//        try {
//            LOGGER.info("Sending request...");
//            DownloaderResponse response = DownloaderRequest(client);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            client.shutdown();
//        }
//    }

}
