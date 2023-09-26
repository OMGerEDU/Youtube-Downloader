package org.base;

import io.netty.handler.ssl.SslContext;
import org.junit.Test;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderConfig;
import org.protoc.DownloaderProto.DownloaderConfigOuterClass.DownloaderRequest;

import static org.base.AutoChannelClient.DownloaderRequest;
import static org.base.DownloaderServer.LOGGER;
import static org.base.Globals.SERVER_HOST;
import static org.base.Globals.SERVER_PORT;

import javax.net.ssl.SSLException;
import java.io.IOException;

public class Client {


    @Test
    public void mockClient() throws IOException, InterruptedException {
        SslContext sslContext = AutoChannelClient.loadTLScredentials();
        LOGGER.info("Starting client...");
        AutoChannelClient client = new AutoChannelClient(SERVER_HOST, SERVER_PORT,sslContext);
        try {
            LOGGER.info("Sending request...");
            DownloaderConfigOuterClass.DownloaderResponse response = DownloaderRequest(client,request1());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            client.shutdown();
        }
    }

    public DownloaderRequest request1() {
        DownloaderConfigOuterClass.DownloaderConfig config = DownloaderConfig.newBuilder()
                .setLink("https://www.youtube.com/watch?v=9bZkp7q19f0")
                .setPath("E:\\Resources\\New folder")
                .build();
        DownloaderRequest request = DownloaderRequest.newBuilder()
                .setConfig(config)
                .build();

        return request;
    }

}
