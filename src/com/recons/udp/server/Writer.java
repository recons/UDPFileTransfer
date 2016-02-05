package com.recons.udp.server;

import com.recons.udp.lib.Cancable;
import com.recons.udp.lib.InitPackage;
import com.recons.udp.lib.Channel;
import com.recons.udp.lib.PartOfFile;

import java.io.*;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
class Writer implements Cancable {
    private Thread thread;
    private final Channel<PartOfFile> chanel;
    private volatile boolean active = true;

    public Writer(int maxChannelSize) {
        chanel = new Channel<>(maxChannelSize);
    }

    public void init(InitPackage initPackage) {
        thread = new Thread(() -> {
            try {
                File file = new File(initPackage.fileName);
                OutputStream outputStream = new FileOutputStream("send" + file.getName());
                while (active) {
                    try {
                        PartOfFile partOfFile = chanel.get();
                        outputStream.write(partOfFile.data);
                        if (initPackage.totalPackageCount - 1 == partOfFile.number) {
                            cancel();
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });
        thread.start();
    }

    @Override
    public void cancel() {
        active = false;
        thread.interrupt();
        System.out.println("Writer has stopped");
    }

    public void write(PartOfFile bytes) {
        chanel.put(bytes);
    }
}
