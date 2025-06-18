package com.hayet.fertility.service;

import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.User;
import com.hayet.fertility.domain.enumeration.ClientStatus;
import com.hayet.fertility.domain.enumeration.Language;
import com.hayet.fertility.repository.ClientRepository;
import com.hayet.fertility.security.SecurityUtils;
import com.hayet.fertility.service.dto.ClientDTO;
import com.hayet.fertility.service.mapper.ClientMapper;

import java.nio.file.AccessDeniedException;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.hayet.fertility.web.rest.errors.BadRequestAlertException;
import com.hayet.fertility.web.rest.errors.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hayet.fertility.domain.Client}.
 */
@Service
@Transactional
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    private static final String ENTITY_NAME = "Client";

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserService userService;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, UserService userService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.userService = userService;
    }

    /**
     * Save a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public ClientDTO create(ClientDTO client) {
        log.debug("Request to create a Client : {}", client);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        if (client.getNotificationPreference() == null || client.getNotificationPreference().isEmpty()) {
            throw new BadRequestAlertException(
                "Please pick at least one notification preference",
                ENTITY_NAME,
                ErrorConstants.AT_LEAST_ONE_NOTIFICATION_PREFERENCE_IS_REQUIRED
            );
        }
        if (client.getLastName() == null || client.getLastName().isBlank()) {
            throw new BadRequestAlertException(
                "The last name is required",
                ENTITY_NAME,
                ErrorConstants.LAST_NAME_IS_REQUIRED
            );
        }

        client.setStatus(ClientStatus.ACTIVE);
        client.setLanguage(client.getLanguage() != null ? client.getLanguage() : Language.FR);
        client.setCreated(ZonedDateTime.now());
        client.setCreatedBy(authenticatedAdmin.getEmail());

        return save(client);
    }

    /**
     * Update a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public ClientDTO update(ClientDTO client) throws AccessDeniedException {
        log.debug("Request to update Client : {}", client);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        ClientDTO originalClient = findOne(client.getId()).orElseThrow(() -> new AccessDeniedException("Not found"));

        // Validate notification preference if provided
        if (client.getNotificationPreference() != null && !client.getNotificationPreference().isEmpty()) {
            originalClient.setNotificationPreference(client.getNotificationPreference());
        } else if (client.getNotificationPreference() != null && client.getNotificationPreference().isEmpty()) {
            throw new BadRequestAlertException(
                "Please pick at least one notification preference",
                ENTITY_NAME,
                ErrorConstants.AT_LEAST_ONE_NOTIFICATION_PREFERENCE_IS_REQUIRED
            );
        }

        // Validate and update last name if provided
        if (client.getLastName() != null) {
            if (client.getLastName().isBlank()) {
                throw new BadRequestAlertException(
                    "The last name is required",
                    ENTITY_NAME,
                    ErrorConstants.LAST_NAME_IS_REQUIRED
                );
            }
            originalClient.setLastName(client.getLastName());
        }

        // Update other fields if provided
        if (client.getFirstName() != null) {
            originalClient.setFirstName(client.getFirstName());
        }
        if (client.getEmail() != null) {
            originalClient.setEmail(client.getEmail());
        }
        if (client.getPhone() != null) {
            originalClient.setPhone(client.getPhone());
        }
        if (client.getWhatsapp() != null) {
            originalClient.setWhatsapp(client.getWhatsapp());
        }
        if (client.getNote() != null) {
            originalClient.setNote(client.getNote());
        }
        if (client.getGender() != null) {
            originalClient.setGender(client.getGender());
        }
        if (client.getBirthDate() != null) {
            originalClient.setBirthDate(client.getBirthDate());
        }
        if (client.getLanguage() != null) {
            originalClient.setLanguage(client.getLanguage());
        }
        if (client.getStatus() != null) {
            originalClient.setStatus(client.getStatus());
        }
        if (client.getReminderCount() != null) {
            originalClient.setReminderCount(client.getReminderCount());
        }
        if (client.getTags() != null && !client.getTags().isEmpty()) {
            originalClient.setTags(client.getTags());
        }

        // Set update metadata
        originalClient.setUpdated(ZonedDateTime.now());
        originalClient.setUpdatedBy(authenticatedAdmin.getEmail());

        return save(originalClient);
    }

    /**
     * Save a client.
     *
     * @param clientDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientDTO save(ClientDTO clientDTO) {
        log.debug("Request to save Client : {}", clientDTO);
        Client client = clientMapper.toEntity(clientDTO);
        client = clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    /**
     * Get all the clients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Clients");
        return clientRepository.findAll(pageable).map(clientMapper::toDto);
    }

    /**
     * Get one client by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClientDTO> findOne(Long id) {
        log.debug("Request to get Client : {}", id);
        return clientRepository.findById(id).map(clientMapper::toDto);
    }

    /**
     * Delete the client by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Client : {}", id);
        clientRepository.deleteById(id);
    }
}
