package com.recons.udp.lib;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class PartOfFile {
    public final byte[] data;
    public final int number;

    public PartOfFile(byte[] data, int number) {
        this.data = data;
        this.number = number;
    }

    @Override
    public String toString() {
        return "PartOfFile{" +
                "number=" + number +
                '}';
    }
}

