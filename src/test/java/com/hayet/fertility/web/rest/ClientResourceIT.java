package com.hayet.fertility.web.rest;

import static com.hayet.fertility.domain.ClientAsserts.*;
import static com.hayet.fertility.web.rest.TestUtil.createUpdateProxyForBean;
import static com.hayet.fertility.web.rest.TestUtil.sameInstant;
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
import com.hayet.fertility.service.dto.ClientDTO;
import com.hayet.fertility.service.mapper.ClientMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.Set;
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
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    public static Client createEntity() {
        return new Client()
            .lastName("Doe")
            .firstName("John")
            .email("john.doe@example.com")
            .phone("0123456789")
            .whatsapp("0123456789")
            .gender(Gender.MALE)
            .status(ClientStatus.ACTIVE)
            .notificationPreference(Set.of(NotificationChannel.EMAIL))
            .birthDate(LocalDate.of(2000,5, 25))
            .note("this is a note")
            .tags("nouveau,test")
            .language(Language.FR);
    }

    @BeforeEach
    public void initTest() {
        client = createEntity();
        client = clientRepository.saveAndFlush(client);
    }

    @AfterEach
    public void cleanup() {
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();

        Client newClient = createEntity();
        ClientDTO clientDTO = clientMapper.toDto(newClient);
        clientDTO.setId(null);

        var returnedClientDTO = om.readValue(
            restClientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientDTO.class
        );

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertThat(returnedClientDTO.getLastName()).isEqualTo("Doe");
        assertThat(returnedClientDTO.getStatus()).isEqualTo(ClientStatus.ACTIVE);
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        ClientDTO clientDTO = clientMapper.toDto(client);
        clientDTO.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.idexists"));

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update existing client
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        em.detach(updatedClient);
        updatedClient
            .lastName("Updated")
            .firstName("Jane")
            .email("jane.updated@example.com")
            .phone("0987654321")
            .whatsapp("0987654321")
            .gender(Gender.FEMALE)
            .notificationPreference(Set.of(NotificationChannel.SMS, NotificationChannel.EMAIL))
            .birthDate(LocalDate.of(1995, 8, 15))
            .tags("updated,test")
            .language(Language.EN);

        ClientDTO clientDTO = clientMapper.toDto(updatedClient);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);

        Client persistedClient = getPersistedClient(client);
        assertThat(persistedClient.getLastName()).isEqualTo("Updated");
        assertThat(persistedClient.getFirstName()).isEqualTo("Jane");
        assertThat(persistedClient.getEmail()).isEqualTo("jane.updated@example.com");
        assertThat(persistedClient.getPhone()).isEqualTo("0987654321");
        assertThat(persistedClient.getWhatsapp()).isEqualTo("0987654321");
        assertThat(persistedClient.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(persistedClient.getNotificationPreference()).containsExactlyInAnyOrder(NotificationChannel.SMS, NotificationChannel.EMAIL);
        assertThat(persistedClient.getBirthDate()).isEqualTo(LocalDate.of(1995, 8, 15));
        assertThat(persistedClient.getTags()).isEqualTo("updated,test");
        assertThat(persistedClient.getLanguage()).isEqualTo(Language.EN);
        assertThat(persistedClient.getUpdated()).isNotNull();
        assertThat(persistedClient.getUpdatedBy()).isNotNull();
    }

    @Test
    @Transactional
    void updateClientWithNonExistingId() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();

        ClientDTO clientDTO = clientMapper.toDto(createEntity());
        clientDTO.setId(Long.MAX_VALUE); // Non-existing ID

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientDTO))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("error.idnotfound"));

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void archiveClient() throws Exception {
        restClientMockMvc
            .perform(post(ENTITY_API_URL_ID + "/archive", client.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ClientStatus.ARCHIVED.name()));
    }

    @Test
    @Transactional
    void restoreClient() throws Exception {
        Client archivedClient = clientRepository.saveAndFlush(createEntity().status(ClientStatus.ARCHIVED));

        restClientMockMvc
            .perform(post(ENTITY_API_URL_ID + "/restore", archivedClient.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ClientStatus.ACTIVE.name()));
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem("Doe")))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem("John")))
            .andExpect(jsonPath("$.[*].email").value(hasItem("john.doe@example.com")))
            .andExpect(jsonPath("$.[*].phone").value(hasItem("0123456789")))
            .andExpect(jsonPath("$.[*].whatsapp").value(hasItem("0123456789")))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(Gender.MALE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(ClientStatus.ACTIVE.toString())))
            .andExpect(jsonPath("$.[*].notificationPreference[*]").value(hasItem(NotificationChannel.EMAIL.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem("2000-05-25")))
            .andExpect(jsonPath("$.[*].tags").value(hasItem("nouveau,test")))
            .andExpect(jsonPath("$.[*].language").value(hasItem(Language.FR.toString())));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.phone").value("0123456789"))
            .andExpect(jsonPath("$.whatsapp").value("0123456789"))
            .andExpect(jsonPath("$.gender").value(Gender.MALE.toString()))
            .andExpect(jsonPath("$.status").value(ClientStatus.ACTIVE.toString()))
            .andExpect(jsonPath("$.notificationPreference[*]").value(hasItem(NotificationChannel.EMAIL.toString())))
            .andExpect(jsonPath("$.birthDate").value("2000-05-25"))
            .andExpect(jsonPath("$.tags").value("nouveau,test"))
            .andExpect(jsonPath("$.language").value(Language.FR.toString()));
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        long databaseSizeBeforeDelete = getRepositoryCount();

        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isNoContent());

        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(getRepositoryCount()).isEqualTo(countBefore + 1);
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(getRepositoryCount()).isEqualTo(countBefore - 1);
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(getRepositoryCount()).isEqualTo(countBefore);
    }

    protected Client getPersistedClient(Client client) {
        return clientRepository.findById(client.getId()).orElseThrow();
    }
}
