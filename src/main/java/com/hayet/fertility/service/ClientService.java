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
import java.util.Objects;
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
    private final UserService userService;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, UserService userService, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.clientMapper = clientMapper;
    }

    /**
     * Create a new client with validation and default values.
     *
     * @param client the entity to create.
     * @return the persisted entity.
     */
    public ClientDTO create(ClientDTO client) {
        log.debug("Request to create a Client : {}", client);
        User authenticatedAdmin = getCurrentAuthenticatedUser();

        if (client.getNotificationPreference() == null || client.getNotificationPreference().isEmpty()) {
            throw new BadRequestAlertException(
                "Please pick at least one notification preference",
                ENTITY_NAME,
                ErrorConstants.AT_LEAST_ONE_NOTIFICATION_PREFERENCE_IS_REQUIRED
            );
        }
        validateLastName(client.getLastName());

        client.setStatus(ClientStatus.ACTIVE);
        client.setLanguage(client.getLanguage() != null ? client.getLanguage() : Language.FR);
        client.setCreated(ZonedDateTime.now());
        client.setCreatedBy(authenticatedAdmin.getEmail());

        return save(client);
    }

    /**
     * Update an existing client with selective field updates.
     *
     * @param client the entity to update.
     * @return the persisted entity.
     */
    public ClientDTO update(ClientDTO client) throws AccessDeniedException {
        log.debug("Request to update Client : {}", client);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ClientDTO originalClient = getClientOrThrow(client.getId());

        // Update notification preferences with validation
        if (client.getNotificationPreference() != null) {
            if (client.getNotificationPreference().isEmpty()) {
                throw new BadRequestAlertException(
                    "Please pick at least one notification preference",
                    ENTITY_NAME,
                    ErrorConstants.AT_LEAST_ONE_NOTIFICATION_PREFERENCE_IS_REQUIRED
                );
            }
            originalClient.setNotificationPreference(client.getNotificationPreference());
        }

        // Update last name with validation
        if (client.getLastName() != null && !Objects.equals(client.getLastName(), originalClient.getLastName())) {
            validateLastName(client.getLastName());
            originalClient.setLastName(client.getLastName());
        }

        // Update other fields if changed
        updateClientFields(originalClient, client);

        updateAuditFields(originalClient, authenticatedAdmin);

        return save(originalClient);
    }

    /**
     * Archive a client by setting status to 'ARCHIVED'.
     *
     * @param id the id of the client to archive.
     * @return the archived client.
     */
    public ClientDTO archive(Long id) throws AccessDeniedException {
        log.debug("Request to archive Client : {}", id);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ClientDTO client = getClientOrThrow(id);

        client.setStatus(ClientStatus.ARCHIVED);
        updateAuditFields(client, authenticatedAdmin);

        return save(client);
    }

    /**
     * Restore an archived client by setting status to 'ACTIVE'.
     *
     * @param id the id of the client to restore.
     * @return the restored client.
     */
    public ClientDTO restore(Long id) throws AccessDeniedException {
        log.debug("Request to restore Client : {}", id);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ClientDTO client = getClientOrThrow(id);

        client.setStatus(ClientStatus.ACTIVE);
        updateAuditFields(client, authenticatedAdmin);

        return save(client);
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

    // Gets current authenticated user or throws if not found
    private User getCurrentAuthenticatedUser() {
        return userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();
    }

    // Retrieves client by ID or throws AccessDeniedException if not found
    private ClientDTO getClientOrThrow(Long id) throws AccessDeniedException {
        return findOne(id).orElseThrow(() -> new AccessDeniedException("Client not found"));
    }

    // Validates that last name is not null or blank
    private void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new BadRequestAlertException(
                "The last name is required",
                ENTITY_NAME,
                ErrorConstants.LAST_NAME_IS_REQUIRED
            );
        }
    }

    // Updates client fields only if they have changed
    private void updateClientFields(ClientDTO target, ClientDTO source) {
        if (source.getFirstName() != null && !Objects.equals(source.getFirstName(), target.getFirstName())) {
            target.setFirstName(source.getFirstName());
        }
        if (source.getEmail() != null && !Objects.equals(source.getEmail(), target.getEmail())) {
            target.setEmail(source.getEmail());
        }
        if (source.getPhone() != null && !Objects.equals(source.getPhone(), target.getPhone())) {
            target.setPhone(source.getPhone());
        }
        if (source.getWhatsapp() != null && !Objects.equals(source.getWhatsapp(), target.getWhatsapp())) {
            target.setWhatsapp(source.getWhatsapp());
        }
        if (source.getNote() != null && !Objects.equals(source.getNote(), target.getNote())) {
            target.setNote(source.getNote());
        }
        if (source.getGender() != null && !Objects.equals(source.getGender(), target.getGender())) {
            target.setGender(source.getGender());
        }
        if (source.getBirthDate() != null && !Objects.equals(source.getBirthDate(), target.getBirthDate())) {
            target.setBirthDate(source.getBirthDate());
        }
        if (source.getLanguage() != null && !Objects.equals(source.getLanguage(), target.getLanguage())) {
            target.setLanguage(source.getLanguage());
        }
        if (source.getTags() != null && !source.getTags().isEmpty() && !Objects.equals(source.getTags(), target.getTags())) {
            target.setTags(source.getTags());
        }
    }

    // Updates audit fields for client modifications
    private void updateAuditFields(ClientDTO client, User user) {
        client.setUpdated(ZonedDateTime.now());
        client.setUpdatedBy(user.getEmail());
    }
}
