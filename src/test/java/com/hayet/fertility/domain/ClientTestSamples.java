package com.hayet.fertility.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Client getClientSample1() {
        return new Client()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phone("phone1")
            .whatsapp("whatsapp1")
            .note("note1")
            .reminderCount(1)
            .tags("tags1")
            .createdBy("createdBy1")
            .updatedBy("updatedBy1");
    }

    public static Client getClientSample2() {
        return new Client()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phone("phone2")
            .whatsapp("whatsapp2")
            .note("note2")
            .reminderCount(2)
            .tags("tags2")
            .createdBy("createdBy2")
            .updatedBy("updatedBy2");
    }

    public static Client getClientRandomSampleGenerator() {
        return new Client()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .whatsapp(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString())
            .reminderCount(intCount.incrementAndGet())
            .tags(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .updatedBy(UUID.randomUUID().toString());
    }
}
