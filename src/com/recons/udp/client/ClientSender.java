package com.recons.udp.client;

import com.recons.udp.lib.Cancable;
import com.recons.udp.lib.ByteConverter;
import com.recons.udp.lib.Channel;
import com.recons.udp.lib.PartOfFile;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
class ClientSender implements Cancable {
    private Thread thread;
    private volatile boolean active = true;
    private final Channel<PartOfFile> packetChannel;
    private final byte[] buffer;

    public ClientSender(int chanalSize,
                        int packageSize,
                        Consumer<Integer> sendingConfirmation,
                        DatagramSocket socket,
                        InetAddress address,
                        int port) throws SocketException {
        packetChannel = new Channel<>(chanalSize);

        thread = new Thread(() -> {
            while (true) {
                if (!active || socket.isClosed())
                    break;
                try {
                    DatagramPacket packet = null;
                    try {
                        PartOfFile partOfFile = packetChannel.get();
                        packet = wrapPartOfFile(partOfFile);
                        packet.setPort(port);
                        packet.setAddress(address);
                        socket.send(packet);
                        sendingConfirmation.accept(partOfFile.number);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        buffer = new byte[packageSize + 4];
    }

    @Override
    public void cancel() {
        active = false;
        thread.interrupt();
        System.out.println("ClientSender end work");
    }

    public void sent(PartOfFile packet) {
        packetChannel.put(packet);
    }


    public DatagramPacket wrapPartOfFile(PartOfFile partOfFile) {
        byte allBytes[] = buffer;
        byte number[] = ByteConverter.intToBytes(partOfFile.number);

        //copy number to 0 - 4 bytes
        System.arraycopy(number, 0, allBytes, 0, 4);

        //copy data to 4 - last bytes
        System.arraycopy(partOfFile.data, 0, allBytes, 4, partOfFile.data.length);

        return new DatagramPacket(allBytes,  partOfFile.data.length + 4);
    }
}
