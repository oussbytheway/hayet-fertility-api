package com.hayet.fertility.domain;

import static com.hayet.fertility.domain.ClientTestSamples.*;
import static com.hayet.fertility.domain.MessageInstanceTestSamples.*;
import static com.hayet.fertility.domain.MessageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hayet.fertility.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageInstanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MessageInstance.class);
        MessageInstance messageInstance1 = getMessageInstanceSample1();
        MessageInstance messageInstance2 = new MessageInstance();
        assertThat(messageInstance1).isNotEqualTo(messageInstance2);

        messageInstance2.setId(messageInstance1.getId());
        assertThat(messageInstance1).isEqualTo(messageInstance2);

        messageInstance2 = getMessageInstanceSample2();
        assertThat(messageInstance1).isNotEqualTo(messageInstance2);
    }

    @Test
    void clientTest() {
        MessageInstance messageInstance = getMessageInstanceRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        messageInstance.setClient(clientBack);
        assertThat(messageInstance.getClient()).isEqualTo(clientBack);

        messageInstance.client(null);
        assertThat(messageInstance.getClient()).isNull();
    }

    @Test
    void messageTest() {
        MessageInstance messageInstance = getMessageInstanceRandomSampleGenerator();
        Message messageBack = getMessageRandomSampleGenerator();

        messageInstance.setMessage(messageBack);
        assertThat(messageInstance.getMessage()).isEqualTo(messageBack);

        messageInstance.message(null);
        assertThat(messageInstance.getMessage()).isNull();
    }
}
