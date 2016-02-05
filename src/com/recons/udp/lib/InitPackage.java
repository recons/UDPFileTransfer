package com.recons.udp.lib;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class InitPackage implements Serializable {
    public long fileSize;
    public String fileName;
    public long totalPackageCount;

    public InitPackage() {
    }

    public InitPackage(long fileSize, String fileName, long totalPackageCount) {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.totalPackageCount = totalPackageCount;
    }

    public static InitPackage fromBytes(byte[] bytes) throws IOException {
        return Util.fromBytes(bytes);
    }

    public static byte[] toBytes(InitPackage initPackage) throws IOException {
        return Util.toBytes(initPackage);
    }

    public byte[] toBytes() throws IOException {
        return toBytes(this);
    }
}
