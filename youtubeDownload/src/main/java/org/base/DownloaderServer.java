package org.base;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.base.Globals.SERVER_HOST;
import static org.base.Globals.SERVER_PORT;

public class DownloaderServer {

    DownloaderService downloaderService;

    static final Logger LOGGER = Logger.getLogger(DownloaderServer.class.getName());
    private String host;
    private  int port;
    private Server server;
    SslContext sslContext;
//    DownloaderManagerServiceGrpc.DownloaderManagerServiceBlockingStub downloaderServiceBlockingStub;
//    DownloaderManagerServiceGrpc.DownloaderManagerServiceStub downloaderServiceStub;
    private static final CountDownLatch serverStartedLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        startServer(SERVER_HOST, SERVER_PORT);
    }

    public static void startServer(String host,int port) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                DownloaderServer server = new DownloaderServer(host,port);
                server.buildServer();
                server.start();
                server.blockUntilShutdown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void buildServer() throws IOException {
        LOGGER.severe("TEST1");
        sslContext = loadTLSCredentials();
        LOGGER.severe("TEST2");
        downloaderService = new DownloaderService();
        server = NettyServerBuilder.forPort(this.port).sslContext(sslContext)
                .addService(downloaderService)
                //.addService(ProtoReflectionService.newInstance())
                .maxInboundMessageSize(100 * 1024 * 1024) // 100 MiB
                .keepAliveTime(15, TimeUnit.MINUTES)
                .maxConcurrentCallsPerConnection(1000)
                .build();
        LOGGER.severe("TEST3");

    }

    public void start() throws IOException {
        server.start();
        LOGGER.info("API Gateway Server started, listening on port " + server.getPort()+" with the host of "+host);
        serverStartedLatch.countDown();
    }


    public static SslContext loadTLSCredentials() throws IOException {
        InputStream serverCertStream = DownloaderServer.class.getClassLoader().getResourceAsStream("cert/server-cert.pem");
        InputStream serverKeyStream = DownloaderServer.class.getClassLoader().getResourceAsStream("cert/server-key.pem");
        InputStream clientCACertStream = DownloaderServer.class.getClassLoader().getResourceAsStream("cert/ca-cert.pem");

        File serverCertFile = streamToFile(serverCertStream, "server-cert.pem");
        File serverKeyFile = streamToFile(serverKeyStream, "server-key.pem");
        File clientCACertFile = streamToFile(clientCACertStream, "ca-cert.pem");

        SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
                .clientAuth(ClientAuth.NONE)
                .trustManager(clientCACertFile);
        return GrpcSslContexts.configure(ctxBuilder).build();
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


//    public DownloaderServer(ServerBuilder serverBuilder,String host, int port, DownloaderService downloaderService) {
//        ManagedChannel channel;
//        this.port = port;
//        this.host = host;
//        server = serverBuilder
//                .addService(downloaderService)
//                .addService(ProtoReflectionService.newInstance())
//                .build();
//
//
//        channel = NettyChannelBuilder.forAddress(host, this.port)
//                .usePlaintext()
//                .build();
//        downloaderServiceBlockingStub = DownloaderManagerServiceGrpc.newBlockingStub(channel);
//        downloaderServiceStub = DownloaderManagerServiceGrpc.newStub(channel);
//    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DownloaderServer(int port) {
        this.port = port;
    }

    public DownloaderServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
