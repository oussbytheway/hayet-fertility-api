package com.hayet.fertility.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReminderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Reminder getReminderSample1() {
        return new Reminder().id(1L).note("note1").createdBy("createdBy1").updatedBy("updatedBy1").repeatEvery(1);
    }

    public static Reminder getReminderSample2() {
        return new Reminder().id(2L).note("note2").createdBy("createdBy2").updatedBy("updatedBy2").repeatEvery(2);
    }

    public static Reminder getReminderRandomSampleGenerator() {
        return new Reminder()
            .id(longCount.incrementAndGet())
            .note(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString())
            .repeatEvery(intCount.incrementAndGet());
    }
}
