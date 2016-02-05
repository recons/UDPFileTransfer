package com.recons.udp.client;

import com.recons.udp.lib.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class Client implements Cancable {
    private final ClientSender clientSender;
    private final FileReader reader;
    private final Receiver receiver;

    private final PartOfFileSlidingWindow slidingWindow;
    private final Channel<byte[]> readingBuffer; // reader invokes get, put is invoked when on get moving sindow

    private volatile int readingIndex = 1; // 0 - for init package
    private Timer timer = new Timer();

    private static final long TIME_OUT = 1500;

    public Client(String fileName, int slidingWindowSize, int packageSize, InetAddress address, int serverPort) throws IOException {
        slidingWindow =  new PartOfFileSlidingWindow(slidingWindowSize);
        readingBuffer = new Channel<>(slidingWindowSize);

        for (int i = 0; i < slidingWindowSize; i++)
            readingBuffer.put(new byte[packageSize]);

        File file = new File(fileName);
        DatagramSocket socket = new DatagramSocket();


        long numberOfPackages = roundedNatural(file.length(), packageSize);
        long numberOfPackagesPlusInit = numberOfPackages + 1;

        byte[] initPackageByte = new InitPackage(file.length(), fileName, numberOfPackagesPlusInit).toBytes();
        TimedPartOfFile init = new TimedPartOfFile(initPackageByte, 0);

        slidingWindow.read(init);

        clientSender = new ClientSender(
                slidingWindowSize,
                packageSize,
                packageNumber -> slidingWindow.setSendingTime(packageNumber, System.currentTimeMillis()),
                socket,
                address,
                serverPort
        );

        clientSender.sent(init);

        reader = new FileReader(file, this::getReadingArray, this::onPartRead, this::onEnd);
        receiver = new Receiver(socket, this::onPackageConfirm);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               resend();
            }
        }, TIME_OUT, TIME_OUT);
    }


    private void resend() {
        slidingWindow.getNotConfirmedParts(TIME_OUT).forEach(clientSender::sent);
    }

    private void onEnd() {
        System.out.println("File has sent");
        reader.cancel();
    }

    private void onPartRead(byte[] bytes, int realSize) {
        if (bytes.length != realSize) {
            byte [] newB = new byte[realSize];
            System.arraycopy(bytes, 0, newB, 0, realSize);
            bytes = newB;
        }

        TimedPartOfFile partOfFile = new TimedPartOfFile(bytes, slidingWindow.getCurrentEnd());
        slidingWindow.read(partOfFile);
        clientSender.sent(getDataGram());

    }

    private long roundedNatural(long a, long b) {
        return (a+(b-1)) / b;
    }

    private byte[] getReadingArray() {
        try {
            return readingBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onPackageConfirm(int pNubmer) {
        if (slidingWindow.getCurrentStart() > pNubmer)
            return;

        slidingWindow.setConfirm(pNubmer);
        slidingWindow.moveWindow().stream().filter(p -> p.number != 0).forEach(p -> readingBuffer.put(p.data));
    }

    private PartOfFile getDataGram() {
       return slidingWindow.get(readingIndex++);
    }


    @Override
    public void cancel() {
        clientSender.cancel();
        reader.cancel();
        receiver.cancel();
        timer.cancel();
    }
}
