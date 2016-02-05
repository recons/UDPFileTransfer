package com.recons.udp.lib;

import java.util.function.Function;
import java.net.DatagramPacket;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class DatagramTranslator {
    public static Function<Integer, DatagramPacket> confirmationToPackage = packageNumber -> new DatagramPacket(ByteConverter.intToBytes(packageNumber), 4);
    public static Function<DatagramPacket, Integer> packageToConfirmation = datagram -> ByteConverter.bytesToInt(datagram.getData());

    public static PartOfFile unWrap(DatagramPacket packet) {
        //get number from 0 - 4 bytes
        byte numberInBytes[] = new byte[4];
        System.arraycopy(packet.getData(), 0, numberInBytes, 0, 4);
        int number = ByteConverter.bytesToInt(numberInBytes);

        int dataSize = packet.getLength() - 4;
        byte data[] = new byte[dataSize];
        System.arraycopy(packet.getData(), 4, data, 0, dataSize);
        return new PartOfFile(data, number);
    }
}
