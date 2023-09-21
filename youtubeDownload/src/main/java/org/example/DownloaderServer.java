package org.example;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.example.Globals.SERVER_HOST;
import static org.example.Globals.SERVER_PORT;

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

    public void buildServer() throws SSLException {
        sslContext = loadTLSCredentials();
        downloaderService = new DownloaderService();
        server = NettyServerBuilder.forPort(this.port).sslContext(sslContext)
                .addService(downloaderService)
                //.addService(ProtoReflectionService.newInstance())
                .maxInboundMessageSize(100 * 1024 * 1024) // 100 MiB
                .keepAliveTime(15, TimeUnit.MINUTES)
                .maxConcurrentCallsPerConnection(1000)
                .build();
    }

    public void start() throws IOException {
        server.start();
        LOGGER.info("API Gateway Server started, listening on port " + server.getPort()+" with the host of "+host);
        serverStartedLatch.countDown();
    }


    public static SslContext loadTLSCredentials() throws SSLException {
        File serverCertFile = new File("cert/server-cert.pem");
        File serverKeyFile = new File("cert/server-key.pem");
        File clientCACertFile = new File("cert/ca-cert.pem");

        SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
                .clientAuth(ClientAuth.NONE)
                .trustManager(clientCACertFile);
        return GrpcSslContexts.configure(ctxBuilder).build();
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
