package com.recons.udp.lib;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class TimedPartOfFile extends PartOfFile {
    public transient volatile long timeOfSanding;
    public transient volatile boolean confirm;

    public TimedPartOfFile(byte[] data, int number) {
        super(data, number);
    }
}
