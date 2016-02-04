package com.recons.udp.lib;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class SlidingWindow<T> {
    private final CircularFifoQueue<T> queue;
    private int size;

    public SlidingWindow(int size) {
        deque = new CircularFixedBlockingDeque<>(size);
        this.size = size;
    }

    public T move() throws InterruptedException {
        return deque.remove();
    }

    public void read(T data) throws InterruptedException {
        deque.push(data);
    }


}
