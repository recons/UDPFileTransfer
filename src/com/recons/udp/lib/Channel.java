package com.recons.udp.lib;

import java.util.LinkedList;

/**
 * Created by Sergey Gorodnichev on 04.02.2016.
 * http://pkasko.com/
 */
public class Channel<T> {
    private final int nMax;
    private final LinkedList<T> list = new LinkedList<>();
    private final Object lock = new Object();

    public Channel(int nMax) {
        this.nMax = nMax;
    }

    public T get() throws InterruptedException {
        synchronized (lock) {
            while (list.size() <= 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            lock.notify();
            return list.removeLast();
        }
    }

    public void put(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }

        synchronized (lock) {
            while (list.size() >= nMax) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            list.addFirst(obj);
            lock.notify();
        }
    }

    public int size() {
        synchronized (lock) {
            return list.size();
        }
    }
}
