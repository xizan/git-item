package com.ztesoft.sca.util.seq;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tian.xubo
 * @created 2017 - 10 - 18 15:17
 */
public class SequenceThreadFactory implements ThreadFactory {
    private static AtomicLong threadCount = new AtomicLong(0L);
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        threadCount.incrementAndGet();
        thread.setName("SequenceCreatorThread-"+threadCount.get());
        return thread;
    }
}
