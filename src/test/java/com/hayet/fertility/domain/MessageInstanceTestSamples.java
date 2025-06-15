package com.hayet.fertility.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MessageInstanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MessageInstance getMessageInstanceSample1() {
        return new MessageInstance()
            .id(1L)
            .content("content1")
            .deliveryAttempts(1)
            .errorMessage("errorMessage1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1");
    }

    public static MessageInstance getMessageInstanceSample2() {
        return new MessageInstance()
            .id(2L)
            .content("content2")
            .deliveryAttempts(2)
            .errorMessage("errorMessage2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2");
    }

    public static MessageInstance getMessageInstanceRandomSampleGenerator() {
        return new MessageInstance()
            .id(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .deliveryAttempts(intCount.incrementAndGet())
            .errorMessage(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
