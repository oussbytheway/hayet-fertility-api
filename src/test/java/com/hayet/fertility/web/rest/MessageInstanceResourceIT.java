package com.hayet.fertility.web.rest;

import static com.hayet.fertility.domain.MessageInstanceAsserts.*;
import static com.hayet.fertility.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hayet.fertility.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayet.fertility.IntegrationTest;
import com.hayet.fertility.domain.MessageInstance;
import com.hayet.fertility.domain.enumeration.NotificationChannel;
import com.hayet.fertility.domain.enumeration.NotificationStatus;
import com.hayet.fertility.repository.MessageInstanceRepository;
import com.hayet.fertility.service.dto.MessageInstanceDTO;
import com.hayet.fertility.service.mapper.MessageInstanceMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
 * Integration tests for the {@link MessageInstanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MessageInstanceResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final NotificationStatus DEFAULT_STATUS = NotificationStatus.PENDING;
    private static final NotificationStatus UPDATED_STATUS = NotificationStatus.SENT;

    private static final NotificationChannel DEFAULT_CHANNEL = NotificationChannel.SMS;
    private static final NotificationChannel UPDATED_CHANNEL = NotificationChannel.EMAIL;

    private static final ZonedDateTime DEFAULT_SENT_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SENT_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DELIVERED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELIVERED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_FAILED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FAILED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_DELIVERY_ATTEMPTS = 1;
    private static final Integer UPDATED_DELIVERY_ATTEMPTS = 2;

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/message-instances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MessageInstanceRepository messageInstanceRepository;

    @Autowired
    private MessageInstanceMapper messageInstanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageInstanceMockMvc;

    private MessageInstance messageInstance;

    private MessageInstance insertedMessageInstance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageInstance createEntity() {
        return new MessageInstance()
            .content(DEFAULT_CONTENT)
            .status(DEFAULT_STATUS)
            .channel(DEFAULT_CHANNEL)
            .sentAt(DEFAULT_SENT_AT)
            .deliveredAt(DEFAULT_DELIVERED_AT)
            .failedAt(DEFAULT_FAILED_AT)
            .deliveryAttempts(DEFAULT_DELIVERY_ATTEMPTS)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .created(DEFAULT_CREATED)
            .createdBy(DEFAULT_CREATED_BY)
            .updated(DEFAULT_UPDATED)
            .updatedBy(DEFAULT_UPDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageInstance createUpdatedEntity() {
        return new MessageInstance()
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .channel(UPDATED_CHANNEL)
            .sentAt(UPDATED_SENT_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .deliveryAttempts(UPDATED_DELIVERY_ATTEMPTS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .created(UPDATED_CREATED)
            .createdBy(UPDATED_CREATED_BY)
            .updated(UPDATED_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY);
    }

    @BeforeEach
    public void initTest() {
        messageInstance = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMessageInstance != null) {
            messageInstanceRepository.delete(insertedMessageInstance);
            insertedMessageInstance = null;
        }
    }

    @Test
    @Transactional
    void createMessageInstance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);
        var returnedMessageInstanceDTO = om.readValue(
            restMessageInstanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageInstanceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MessageInstanceDTO.class
        );

        // Validate the MessageInstance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMessageInstance = messageInstanceMapper.toEntity(returnedMessageInstanceDTO);
        assertMessageInstanceUpdatableFieldsEquals(returnedMessageInstance, getPersistedMessageInstance(returnedMessageInstance));

        insertedMessageInstance = returnedMessageInstance;
    }

    @Test
    @Transactional
    void createMessageInstanceWithExistingId() throws Exception {
        // Create the MessageInstance with an existing ID
        messageInstance.setId(1L);
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageInstanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageInstanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMessageInstances() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        // Get all the messageInstanceList
        restMessageInstanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(messageInstance.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(sameInstant(DEFAULT_SENT_AT))))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(sameInstant(DEFAULT_DELIVERED_AT))))
            .andExpect(jsonPath("$.[*].failedAt").value(hasItem(sameInstant(DEFAULT_FAILED_AT))))
            .andExpect(jsonPath("$.[*].deliveryAttempts").value(hasItem(DEFAULT_DELIVERY_ATTEMPTS)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)));
    }

    @Test
    @Transactional
    void getMessageInstance() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        // Get the messageInstance
        restMessageInstanceMockMvc
            .perform(get(ENTITY_API_URL_ID, messageInstance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(messageInstance.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.sentAt").value(sameInstant(DEFAULT_SENT_AT)))
            .andExpect(jsonPath("$.deliveredAt").value(sameInstant(DEFAULT_DELIVERED_AT)))
            .andExpect(jsonPath("$.failedAt").value(sameInstant(DEFAULT_FAILED_AT)))
            .andExpect(jsonPath("$.deliveryAttempts").value(DEFAULT_DELIVERY_ATTEMPTS))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingMessageInstance() throws Exception {
        // Get the messageInstance
        restMessageInstanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMessageInstance() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageInstance
        MessageInstance updatedMessageInstance = messageInstanceRepository.findById(messageInstance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMessageInstance are not directly saved in db
        em.detach(updatedMessageInstance);
        updatedMessageInstance
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .channel(UPDATED_CHANNEL)
            .sentAt(UPDATED_SENT_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .deliveryAttempts(UPDATED_DELIVERY_ATTEMPTS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .created(UPDATED_CREATED)
            .createdBy(UPDATED_CREATED_BY)
            .updated(UPDATED_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY);
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(updatedMessageInstance);

        restMessageInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageInstanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageInstanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMessageInstanceToMatchAllProperties(updatedMessageInstance);
    }

    @Test
    @Transactional
    void putNonExistingMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageInstanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageInstanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMessageInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageInstance using partial update
        MessageInstance partialUpdatedMessageInstance = new MessageInstance();
        partialUpdatedMessageInstance.setId(messageInstance.getId());

        partialUpdatedMessageInstance
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .channel(UPDATED_CHANNEL)
            .sentAt(UPDATED_SENT_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .created(UPDATED_CREATED);

        restMessageInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessageInstance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessageInstance))
            )
            .andExpect(status().isOk());

        // Validate the MessageInstance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageInstanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMessageInstance, messageInstance),
            getPersistedMessageInstance(messageInstance)
        );
    }

    @Test
    @Transactional
    void fullUpdateMessageInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageInstance using partial update
        MessageInstance partialUpdatedMessageInstance = new MessageInstance();
        partialUpdatedMessageInstance.setId(messageInstance.getId());

        partialUpdatedMessageInstance
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .channel(UPDATED_CHANNEL)
            .sentAt(UPDATED_SENT_AT)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .deliveryAttempts(UPDATED_DELIVERY_ATTEMPTS)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .created(UPDATED_CREATED)
            .createdBy(UPDATED_CREATED_BY)
            .updated(UPDATED_UPDATED)
            .updatedBy(UPDATED_UPDATED_BY);

        restMessageInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessageInstance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessageInstance))
            )
            .andExpect(status().isOk());

        // Validate the MessageInstance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageInstanceUpdatableFieldsEquals(
            partialUpdatedMessageInstance,
            getPersistedMessageInstance(partialUpdatedMessageInstance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, messageInstanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessageInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageInstance.setId(longCount.incrementAndGet());

        // Create the MessageInstance
        MessageInstanceDTO messageInstanceDTO = messageInstanceMapper.toDto(messageInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageInstanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(messageInstanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MessageInstance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMessageInstance() throws Exception {
        // Initialize the database
        insertedMessageInstance = messageInstanceRepository.saveAndFlush(messageInstance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the messageInstance
        restMessageInstanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, messageInstance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return messageInstanceRepository.count();
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

    protected MessageInstance getPersistedMessageInstance(MessageInstance messageInstance) {
        return messageInstanceRepository.findById(messageInstance.getId()).orElseThrow();
    }

    protected void assertPersistedMessageInstanceToMatchAllProperties(MessageInstance expectedMessageInstance) {
        assertMessageInstanceAllPropertiesEquals(expectedMessageInstance, getPersistedMessageInstance(expectedMessageInstance));
    }

    protected void assertPersistedMessageInstanceToMatchUpdatableProperties(MessageInstance expectedMessageInstance) {
        assertMessageInstanceAllUpdatablePropertiesEquals(expectedMessageInstance, getPersistedMessageInstance(expectedMessageInstance));
    }
}
