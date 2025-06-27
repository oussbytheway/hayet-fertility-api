package com.hayet.fertility.web.rest;

import com.hayet.fertility.repository.ClientRepository;
import com.hayet.fertility.service.ClientService;
import com.hayet.fertility.service.dto.ClientDTO;
import com.hayet.fertility.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hayet.fertility.domain.Client}.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientResource {

    private static final Logger log = LoggerFactory.getLogger(ClientResource.class);

    private static final String ENTITY_NAME = "client";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    public ClientResource(ClientService clientService, ClientRepository clientRepository) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
    }

    /**
     * {@code POST /clients} : Create a new client.
     *
     * @param clientDTO the client data to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and body of the created clientDTO,
     *         or status {@code 400 (Bad Request)} if the client already has an ID.
     * @throws URISyntaxException if the URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
        log.debug("REST request to save Client : {}", clientDTO);
        if (clientDTO.getId() != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientDTO = clientService.create(clientDTO);
        return ResponseEntity.created(new URI("/api/clients/" + clientDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, clientDTO.getId().toString()))
            .body(clientDTO);
    }

    /**
     * {@code PUT /clients/:id} : Update an existing client.
     *
     * @param clientDTO the client data to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body of the updated clientDTO,
     *         or status {@code 400 (Bad Request)} if the ID is missing,
     *         or status {@code 500 (Internal Server Error)} if the client cannot be updated.
     * @throws URISyntaxException if the URI syntax is incorrect.
     * @throws AccessDeniedException if access is denied.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> update(
        @Valid @RequestBody ClientDTO clientDTO
    ) throws URISyntaxException, AccessDeniedException {
        log.debug("REST request to update Client : {}", clientDTO.getId());
        if (clientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        validateClientExists(clientDTO.getId());

        clientDTO = clientService.update(clientDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientDTO.getId().toString()))
            .body(clientDTO);
    }

    /**
     * {@code POST /clients/:id/archive} : Archive a client.
     *
     * @param id the ID of the client to archive.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body of the archived client.
     * @throws AccessDeniedException if access is denied.
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<ClientDTO> archiveClient(@PathVariable("id") Long id) throws AccessDeniedException {
        log.debug("REST request to archive Client : {}", id);
        validateClientExists(id);

        ClientDTO clientDTO = clientService.archive(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientDTO.getId().toString()))
            .body(clientDTO);
    }

    /**
     * {@code POST /clients/:id/restore} : Restore an archived client.
     *
     * @param id the ID of the client to restore.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body of the restored client.
     * @throws AccessDeniedException if access is denied.
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ClientDTO> restoreClient(@PathVariable("id") Long id) throws AccessDeniedException {
        log.debug("REST request to restore Client : {}", id);
        validateClientExists(id);

        ClientDTO clientDTO = clientService.restore(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientDTO.getId().toString()))
            .body(clientDTO);
    }

    /**
     * {@code GET /clients} : Get all clients with pagination.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a list of clients.
     */
    @GetMapping("")
    public ResponseEntity<List<ClientDTO>> getAllClients(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Clients");
        Page<ClientDTO> page = clientService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /clients/:id} : Get a single client by ID.
     *
     * @param id the ID of the client to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the client data,
     *         or status {@code 404 (Not Found)} if the client is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable("id") Long id) {
        log.debug("REST request to get Client : {}", id);
        Optional<ClientDTO> clientDTO = clientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientDTO);
    }

    /**
     * {@code DELETE /clients/:id} : Delete a client by ID.
     *
     * @param id the ID of the client to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable("id") Long id) {
        log.debug("REST request to delete Client : {}", id);
        clientService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * Validates that a client with the given ID exists.
     *
     * @param id the ID to check.
     * @throws BadRequestAlertException if the client does not exist.
     */
    private void validateClientExists(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }
}

