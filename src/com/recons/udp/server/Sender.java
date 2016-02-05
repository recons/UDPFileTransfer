package com.recons.udp.server;

import com.recons.udp.lib.ByteConverter;
import com.recons.udp.lib.Cancable;
import com.recons.udp.lib.Channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
class Sender implements Cancable {
    private Thread thread;
    private volatile boolean active = true;
    private final Channel<Integer> packetChannel;
    private final DatagramSocket socket;

    public Sender(int channel,
                  DatagramSocket socket
                 ) throws SocketException {
        packetChannel = new Channel<>(channel);
        this.socket = socket;
    }

    void init( InetAddress address,
               int port) {
        if (thread == null) {
            thread = new Thread(() -> {
                while (true) {
                    if (!active || socket.isClosed())
                        break;
                    try {
                        DatagramPacket packet = null;
                        try {
                            Integer partOfFile = packetChannel.get();
                            packet = new DatagramPacket(ByteConverter.intToBytes(partOfFile), 4, address, port);
                            packet.setPort(port);
                            packet.setAddress(address);
                            socket.send(packet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    @Override
    public void cancel() {
        active = false;
        if (thread != null)
            thread.interrupt();
        System.out.println("Sender has stopped");
    }

    public void sent(Integer packet) {
        packetChannel.put(packet);
    }
}

