package com.recons.udp.lib;

import java.io.*;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class Util {
    public static byte[] toBytes(Serializable serializable) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(serializable);
            return bos.toByteArray();
        }
    }

    public static <T extends Serializable> T fromBytes(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (T) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
