package org.base;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderRequest;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderResponse;
import org.protoc.DownloaderProto.DownloaderManagerServiceGrpc;


import javax.net.ssl.SSLException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.base.DownloaderServer.streamToFile;


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




//    public static SslContext loadTLScredentials() throws SSLException {
//        File serverCACert = new File("resources/cert/ca-cert.pem");
//        File clientCertFile = new File("resources/cert/client-cert.pem");
//        File clientKeyFile = new File("resources/cert/client-key.pem");
//
//        if (!clientCertFile.exists()|| !clientKeyFile.exists() || !serverCACert.exists())
//            throw new SSLException("cert files not found \n" +serverCACert+" "+serverCACert.exists()+"\n "+clientCertFile+" "+clientCertFile.exists()+"\n "+clientKeyFile+" "+clientKeyFile.exists());
//
//        return GrpcSslContexts.forClient()
//                .keyManager(clientCertFile, clientKeyFile)
//                .trustManager(serverCACert)
//                .build();
//    }
    public static SslContext loadTLScredentials() throws IOException {

        InputStream serverCACert = AutoChannelClient.class.getClassLoader().getResourceAsStream("cert/ca-cert.pem");
        InputStream clientCertFile = AutoChannelClient.class.getClassLoader().getResourceAsStream("cert/client-cert.pem");
        InputStream clientKeyFile = AutoChannelClient.class.getClassLoader().getResourceAsStream("cert/client-key.pem");

        File serverCertFile = streamToFile(serverCACert, "ca-cert.pem");
        File serverKeyFile = streamToFile(clientCertFile, "client-cert.pem");
        File clientCACertFile = streamToFile(clientKeyFile, "client-key.pem");
        LOGGER.info("Checking existence:"+serverCertFile.exists()+" "+serverKeyFile.exists()+" "+clientCACertFile.exists());

        if (!serverCertFile.exists()|| !serverKeyFile.exists() || !clientCACertFile.exists())
            throw new SSLException("cert files not found \n" +serverCACert+"\n "+clientCertFile+"\n "+clientKeyFile+"\n");

        return GrpcSslContexts.forClient()
                .keyManager(clientCertFile, clientKeyFile)
                .trustManager(serverCACert)
                .build();
    }

    static File streamToFile(InputStream in, String fileName) throws IOException {
        Path tempPath = Files.createTempFile(fileName, null);
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit(); // The file will be deleted when the JVM terminates.

        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
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
