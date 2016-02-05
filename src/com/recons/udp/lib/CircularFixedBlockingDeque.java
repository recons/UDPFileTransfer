package com.recons.udp.lib;

import java.util.Arrays;

/**
 * Created by Sergey Gorodnichev on 04.02.16.
 * https://pkasko.com/
 */
public class CircularFixedBlockingDeque<T> {
    private int head;
    private int tail;
    private final Object[] data;
    protected final Object lock = new Object();

    public CircularFixedBlockingDeque(int length) {
        this.data = new Object[length];
    }

    public void push(T elem) {
        synchronized (lock) {
            while (tail - head >= maxSize()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            data[(tail++) % maxSize()] = elem;
            lock.notify();
        }
    }

    public T remove() {
        synchronized (lock) {
            while (head == tail) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int index = (head++) % data.length;
            final T result =  (T) data[index];
            data[index] = null;
            lock.notify();
            return result;
        }
    }

    public T get(int i) {
        synchronized (lock) {
            while (i - head >= size()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return (T) data[i % maxSize()];
        }
    }

    public int size() {
        synchronized (lock) {
            int size = 0;
            for (Object item : data) {
                if (item != null) size++;
            }
            return size;
        }
    }

    public int maxSize() {
        return data.length;
    }

    public int head() {
        synchronized (lock) {
            return head;
        }
    }

    public int tail() {
        synchronized (lock) {
            return tail;
        }
    }

    @Override
    public String toString() {
        return "CyclingFixedSizeDeque{" +
                "head=" + head +
                ", tail=" + tail +
                ", data=" + Arrays.toString(data) +
                ", length=" + data.length +
                '}';
    }
}
