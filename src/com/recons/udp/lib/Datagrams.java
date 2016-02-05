package com.recons.udp.lib;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class Datagrams {
    public static DatagramPacket buildFirstDatagram(InitPackage initPackage, InetAddress address, int port) throws IOException {
        byte bytes[] = Util.toBytes(initPackage);
        return new DatagramPacket(bytes, 0, bytes.length, address, port);
    }
}
