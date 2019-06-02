package com.common;

import static com.common.Common.sleep;


//A method return value that can be awaited from another thread.
public class ReturnValue {
    private final Object lock = new Object();
    private int returnValue = 0;
    private boolean hasReturned = false;

    public void reset() {
        this.returnValue = -1;
        this.hasReturned = false;
    }


    public void doReturn(int value) {
        synchronized (lock) {
            hasReturned = true;
            returnValue = value;
        }
    }

    public int await(long timeout) {
        for (long i = System.currentTimeMillis(); (System.currentTimeMillis() - i) < timeout || timeout == 0; ) {
            synchronized (lock) {
                if (hasReturned) {
                    return returnValue;
                }
            }
            sleep(10);
        }

        return -1;
    }

}
