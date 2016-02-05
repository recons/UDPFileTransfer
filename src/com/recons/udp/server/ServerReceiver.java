package com.recons.udp.server;

import com.recons.udp.lib.Cancable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class ServerReceiver implements Cancable {
    private final Thread thread;
    private volatile boolean active = true;

    public ServerReceiver(DatagramSocket socket, Consumer<DatagramPacket> datagramPacketConsumer, Supplier<byte[]> supplier) {
        thread = new Thread(() -> {
            while (active) {
                try {
                    byte[] bytes = supplier.get();
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                    socket.receive(datagramPacket);
                    datagramPacketConsumer.accept(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void cancel() {
        active = false;
        thread.interrupt();
    }
}
