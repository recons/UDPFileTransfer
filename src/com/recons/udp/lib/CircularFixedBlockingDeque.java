package com.recons.udp.lib;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class CircularFixedBlockingDeque<T> {
    private Queue<T> deque;
    private final int limit;

    public CircularFixedBlockingDeque(int limit) {
        this.deque = new LinkedList<>();
        this.limit = limit;
    }

    public synchronized void push(T item) throws InterruptedException {
        while (this.deque.size() == this.limit) {
            wait();
        }
        if (this.deque.size() == 0) {
            notifyAll();
        }
        this.deque.add(item);
    }

    public synchronized T remove() throws InterruptedException {
        while (this.deque.size() == 0) {
            wait();
        }
        if (this.deque.size() == this.limit) {
            notifyAll();
        }
        return this.deque.remove();
    }

    public T get(int index) {
        return deque.
    }
}
