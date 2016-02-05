package com.recons.udp.client;

import com.recons.udp.lib.Cancable;
import com.recons.udp.lib.DatagramTranslator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class Receiver implements Cancable {
    private final Thread thread;
    private volatile boolean active = true;

    public Receiver(DatagramSocket socket, Consumer<Integer> confirm) {
        thread = new Thread(() -> {
            while (active) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[4], 4);
                    socket.receive(packet);
                    Integer integer = DatagramTranslator.packageToConfirmation.apply(packet);

                    confirm.accept(integer);

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
