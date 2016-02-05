package com.recons.udp.client;

import com.recons.udp.lib.SlidingWindow;
import com.recons.udp.lib.TimedPartOfFile;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Sergey Gorodnichev on 04.02.16.
 * https://pkasko.com/
 */
public class PartOfFileSlidingWindow extends SlidingWindow<TimedPartOfFile> {

    public PartOfFileSlidingWindow(int size) {
        super(size);
    }

    /**
     * return stream of package without confirmation
     *
     * @param timeOutInMilliseconds timeout after it package should have confirm
     * @return Stream of timeout packages
     */
    public Stream<TimedPartOfFile> getNotConfirmedParts(long timeOutInMilliseconds) {
        long now = System.currentTimeMillis();
        return IntStream.range(getCurrentStart(), getCurrentEnd())
                .mapToObj(this::get)
                .filter(f -> f != null && !f.confirm && f.timeOfSanding != 0 && now - f.timeOfSanding > timeOutInMilliseconds);
    }

    /**
     * move window while number of packages in the front of window follow each other
     *
     * @return List of free packages after moving. Reuse data from them
     */
    public List<TimedPartOfFile> moveWindow() {
        LinkedList<TimedPartOfFile> result = new LinkedList<>();
        while (get(getCurrentStart()).confirm) {
            result.add(move());
        }
        return result;
    }

    /**
     * set sending time
     *
     * @param number  package number
     * @param sending time of sending
     */
    public void setSendingTime(int number, long sending) {
        if (number >= getCurrentStart()) {
            try {
                get(number).timeOfSanding = sending;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set some package confirm
     *
     * @param number number of package
     */
    public void setConfirm(int number) {
        if (number >= getCurrentStart())
            get(number).confirm = true;
    }
}
