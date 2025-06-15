package com.hayet.fertility.service.mapper;

import static com.hayet.fertility.domain.MessageInstanceAsserts.*;
import static com.hayet.fertility.domain.MessageInstanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageInstanceMapperTest {

    private MessageInstanceMapper messageInstanceMapper;

    @BeforeEach
    void setUp() {
        messageInstanceMapper = new MessageInstanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMessageInstanceSample1();
        var actual = messageInstanceMapper.toEntity(messageInstanceMapper.toDto(expected));
        assertMessageInstanceAllPropertiesEquals(expected, actual);
    }
}
