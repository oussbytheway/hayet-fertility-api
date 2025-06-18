package com.hayet.fertility.domain;

import static com.hayet.fertility.domain.ClientTestSamples.*;
import static com.hayet.fertility.domain.ReminderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hayet.fertility.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReminderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reminder.class);
        Reminder reminder1 = getReminderSample1();
        Reminder reminder2 = new Reminder();
        assertThat(reminder1).isNotEqualTo(reminder2);

        reminder2.setId(reminder1.getId());
        assertThat(reminder1).isEqualTo(reminder2);

        reminder2 = getReminderSample2();
        assertThat(reminder1).isNotEqualTo(reminder2);
    }

    @Test
    void clientTest() {
        Reminder reminder = getReminderRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        reminder.setClient(clientBack);
        assertThat(reminder.getClient()).isEqualTo(clientBack);

        reminder.client(null);
        assertThat(reminder.getClient()).isNull();
    }
}
