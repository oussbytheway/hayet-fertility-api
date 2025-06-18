package com.hayet.fertility.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Message getMessageSample1() {
        return new Message()
            .id(1L)
            .content("content1")
            .tag("tag1")
            .errorMessage("errorMessage1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1");
    }

    public static Message getMessageSample2() {
        return new Message()
            .id(2L)
            .content("content2")
            .tag("tag2")
            .errorMessage("errorMessage2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2");
    }

    public static Message getMessageRandomSampleGenerator() {
        return new Message()
            .id(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .tag(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
