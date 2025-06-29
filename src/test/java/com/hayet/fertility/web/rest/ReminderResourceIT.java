package com.hayet.fertility.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayet.fertility.IntegrationTest;
import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.Reminder;
import com.hayet.fertility.domain.enumeration.*;
import com.hayet.fertility.repository.ClientRepository;
import com.hayet.fertility.repository.ReminderRepository;
import com.hayet.fertility.service.dto.ReminderDTO;
import com.hayet.fertility.service.mapper.ReminderMapper;
import com.hayet.fertility.web.rest.errors.ErrorConstants;
import jakarta.persistence.EntityManager;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReminderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReminderResourceIT {

    private static final String ENTITY_API_URL = "/api/reminders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderMapper reminderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReminderMockMvc;

    private Client client;
    private Reminder reminder;

    public static Reminder createEntity() {
        return new Reminder()
            .motif(ReminderMotif.MOTIF1)
            .status(ReminderStatus.SCHEDULED)
            .priority(ReminderPriority.LOW)
            .note("Test reminder note")
            .dueAt(ZonedDateTime.now().plusDays(1))
            .active(true)
            .repeatEvery(1)
            .repeatUnit(RepeatUnit.DAY);
    }

    @BeforeEach
    public void initTest() {
        client = ClientResourceIT.createEntity();
        client = clientRepository.saveAndFlush(client);
        reminder = createEntity().client(client);
        reminder = reminderRepository.saveAndFlush(reminder);
    }

    @AfterEach
    public void cleanup() {
        reminderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    void createReminder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();

        Reminder newReminder = createEntity().client(client);
        ReminderDTO reminderDTO = reminderMapper.toDto(newReminder);
        reminderDTO.setClientId(client.getId());
        reminderDTO.setId(null);

        var returnedReminderDTO = om.readValue(
            restReminderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reminderDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReminderDTO.class
        );

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertThat(returnedReminderDTO.getStatus()).isEqualTo(ReminderStatus.SCHEDULED);
        assertThat(returnedReminderDTO.getPriority()).isEqualTo(ReminderPriority.LOW);
        assertThat(returnedReminderDTO.getCreated()).isNotNull();
        assertThat(returnedReminderDTO.getCreatedBy()).isNotNull();
    }

    @Test
    @Transactional
    void createReminderWithExistingId() throws Exception {
        Reminder newReminder = createEntity().client(client);
        ReminderDTO reminderDTO = reminderMapper.toDto(newReminder);
        reminderDTO.setClientId(client.getId());
        reminderDTO.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restReminderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reminderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.idexists"));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createReminderWithoutMotif() throws Exception {
        Reminder newReminder = createEntity().client(client);
        ReminderDTO reminderDTO = reminderMapper.toDto(newReminder);
        reminderDTO.setClientId(client.getId());
        reminderDTO.setId(null);
        reminderDTO.setMotif(null);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restReminderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reminderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error." + ErrorConstants.MOTIF_IS_REQUIRED));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createReminderWithPastDueDate() throws Exception {
        Reminder newReminder = createEntity().client(client);
        ReminderDTO reminderDTO = reminderMapper.toDto(newReminder);
        reminderDTO.setClientId(client.getId());
        reminderDTO.setId(null);
        reminderDTO.setDueAt(ZonedDateTime.now().minusHours(1));

        long databaseSizeBeforeCreate = getRepositoryCount();

        restReminderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reminderDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error." + ErrorConstants.DUE_DATE_MUST_BE_IN_FUTURE));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateReminder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();

        Client updatedClient = clientRepository.saveAndFlush(
            ClientResourceIT.createEntity().lastName("BBQ").email("example@live").gender(Gender.MALE).tags("alger,marié")
        );

        Reminder updatedReminder = reminderRepository.findById(reminder.getId()).orElseThrow();
        em.detach(updatedReminder);
        updatedReminder
            .motif(ReminderMotif.MOTIF2)
            .note("Updated reminder note")
            .dueAt(ZonedDateTime.now().plusDays(2))
            .priority(ReminderPriority.HIGH)
            .repeatEvery(2)
            .repeatUnit(RepeatUnit.WEEK)
            .client(updatedClient);

        ReminderDTO reminderDTO = reminderMapper.toDto(updatedReminder);

        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reminderDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);

        Reminder persistedReminder = getPersistedReminder(updatedReminder);
        assertThat(persistedReminder.getMotif()).isEqualTo(ReminderMotif.MOTIF2);
        assertThat(persistedReminder.getNote()).isEqualTo("Updated reminder note");
        assertThat(persistedReminder.getUpdated()).isNotNull();
        assertThat(persistedReminder.getUpdatedBy()).isNotNull();
    }

    @Test
    @Transactional
    void updateReminderNearExecution() throws Exception {
        // Set due date to less than 1 minute from now
        reminder.setDueAt(ZonedDateTime.now().plusSeconds(30));
        reminderRepository.saveAndFlush(reminder);

        Reminder updatedReminder = reminderRepository.findById(reminder.getId()).orElseThrow();
        em.detach(updatedReminder);
        updatedReminder.setNote("Should not be updated");

        ReminderDTO reminderDTO = reminderMapper.toDto(updatedReminder);

        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error." + ErrorConstants.REMINDER_NEAR_EXECUTION_CANNOT_BE_MODIFIED));
    }

    @Test
    @Transactional
    void activateReminder() throws Exception {
        restReminderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/activate", reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));

        Reminder persistedReminder = getPersistedReminder(reminder);
        assertThat(persistedReminder.getActive()).isTrue();
        assertThat(persistedReminder.getUpdated()).isNotNull();
        assertThat(persistedReminder.getUpdatedBy()).isNotNull();
    }

    @Test
    @Transactional
    void deactivateReminder() throws Exception {
        restReminderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/deactivate", reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));

        Reminder persistedReminder = getPersistedReminder(reminder);
        assertThat(persistedReminder.getActive()).isFalse();
    }

    @Test
    @Transactional
    void resolveReminder() throws Exception {
        restReminderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/resolve", reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resolvedAt").isNotEmpty());

        Reminder persistedReminder = getPersistedReminder(reminder);
        assertThat(persistedReminder.getResolvedAt()).isNotNull();
    }

    @Test
    @Transactional
    void activateNonExistingReminder() throws Exception {
        restReminderMockMvc
            .perform(post(ENTITY_API_URL_ID + "/activate", Long.MAX_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getAllReminders() throws Exception {
        restReminderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reminder.getId().intValue())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(ReminderMotif.MOTIF1.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(ReminderStatus.SCHEDULED.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(ReminderPriority.LOW.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem("Test reminder note")))
            .andExpect(jsonPath("$.[*].active").value(hasItem(true)))
            .andExpect(jsonPath("$.[*].repeatEvery").value(hasItem(1)))
            .andExpect(jsonPath("$.[*].repeatUnit").value(hasItem(RepeatUnit.DAY.toString())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(client.getId().intValue())));
    }

    @Test
    @Transactional
    void getReminder() throws Exception {
        restReminderMockMvc
            .perform(get(ENTITY_API_URL_ID, reminder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reminder.getId().intValue()))
            .andExpect(jsonPath("$.motif").value(ReminderMotif.MOTIF1.toString()))
            .andExpect(jsonPath("$.status").value(ReminderStatus.SCHEDULED.toString()))
            .andExpect(jsonPath("$.priority").value(ReminderPriority.LOW.toString()))
            .andExpect(jsonPath("$.note").value("Test reminder note"))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.repeatEvery").value(1))
            .andExpect(jsonPath("$.repeatUnit").value(RepeatUnit.DAY.toString()))
            .andExpect(jsonPath("$.clientId").value(client.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingReminder() throws Exception {
        restReminderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateNonExistingReminder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        Reminder nonExistingReminder = createEntity().id(longCount.incrementAndGet());
        ReminderDTO reminderDTO = reminderMapper.toDto(nonExistingReminder);

        restReminderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reminderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reminderDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.idnotfound"));

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReminder() throws Exception {
        long databaseSizeBeforeDelete = getRepositoryCount();

        restReminderMockMvc
            .perform(delete(ENTITY_API_URL_ID, reminder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reminderRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Reminder getPersistedReminder(Reminder reminder) {
        return reminderRepository.findById(reminder.getId()).orElseThrow();
    }
}
