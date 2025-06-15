package com.hayet.fertility.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hayet.fertility.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageInstanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MessageInstanceDTO.class);
        MessageInstanceDTO messageInstanceDTO1 = new MessageInstanceDTO();
        messageInstanceDTO1.setId(1L);
        MessageInstanceDTO messageInstanceDTO2 = new MessageInstanceDTO();
        assertThat(messageInstanceDTO1).isNotEqualTo(messageInstanceDTO2);
        messageInstanceDTO2.setId(messageInstanceDTO1.getId());
        assertThat(messageInstanceDTO1).isEqualTo(messageInstanceDTO2);
        messageInstanceDTO2.setId(2L);
        assertThat(messageInstanceDTO1).isNotEqualTo(messageInstanceDTO2);
        messageInstanceDTO1.setId(null);
        assertThat(messageInstanceDTO1).isNotEqualTo(messageInstanceDTO2);
    }
}
