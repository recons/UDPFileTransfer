package com.recons.udp.client;

import com.recons.udp.lib.Cancable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class FileReader implements Cancable {
    private Thread thread;
    private volatile boolean active = true;

    public FileReader(File file, Supplier<byte[]> sourceArray, Callback confirmation, Runnable onEnd) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        thread = new Thread(() -> {
            while (true) {
                if (!active)
                    break;
                try {
                    byte[] bytes = sourceArray.get();
                    int read = fileInputStream.read(bytes);
                    if (read == -1) {
                        onEnd.run();
                        break;
                    } else
                        confirmation.onRead(bytes, read);
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

    public interface Callback {
        void onRead(byte[] data, int size);
    }
}
