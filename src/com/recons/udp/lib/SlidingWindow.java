package com.recons.udp.lib;

/**
 * Created by Sergey Gorodnichev on 03.02.16.
 * https://pkasko.com/
 */
public class SlidingWindow<T> {
    private final CircularFixedBlockingDeque<T> deque;
    private int size;

    public SlidingWindow(int size) {
        deque = new CircularFixedBlockingDeque<>(size);
        this.size = size;
    }

    public T move() {
        T result = deque.remove();
        return result;
    }

    public void read(T data) {
        deque.push(data);
    }

    public T get(int i) {
        return deque.get(i);
    }


    public int getCurrentStart() {
        return deque.head();
    }

    public int getCurrentEnd() {
        return deque.tail();
    }
}
