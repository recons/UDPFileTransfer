package com.recons.udp;

import com.recons.udp.client.Client;
import com.recons.udp.server.Server;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class Main {
    private static final String inputFile = "1.tar.gz";
    private static final int serverPort = 8000;
    private static final int packageSize = 1500;
    private static final int slidingWindowSize = 5;

    public static void main(String[] args) throws IOException {
        new Server(packageSize, serverPort, slidingWindowSize);
        new Client(inputFile, slidingWindowSize, packageSize, InetAddress.getLocalHost(), serverPort);
    }
}
