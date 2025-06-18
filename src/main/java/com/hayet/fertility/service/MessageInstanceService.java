package com.hayet.fertility.service;

import com.hayet.fertility.domain.MessageInstance;
import com.hayet.fertility.repository.MessageInstanceRepository;
import com.hayet.fertility.service.dto.MessageInstanceDTO;
import com.hayet.fertility.service.mapper.MessageInstanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hayet.fertility.domain.MessageInstance}.
 */
@Service
@Transactional
public class MessageInstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageInstanceService.class);

    private final MessageInstanceRepository messageInstanceRepository;

    private final MessageInstanceMapper messageInstanceMapper;

    public MessageInstanceService(MessageInstanceRepository messageInstanceRepository, MessageInstanceMapper messageInstanceMapper) {
        this.messageInstanceRepository = messageInstanceRepository;
        this.messageInstanceMapper = messageInstanceMapper;
    }

    /**
     * Save a messageInstance.
     *
     * @param messageInstanceDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageInstanceDTO save(MessageInstanceDTO messageInstanceDTO) {
        LOG.debug("Request to save MessageInstance : {}", messageInstanceDTO);
        MessageInstance messageInstance = messageInstanceMapper.toEntity(messageInstanceDTO);
        messageInstance = messageInstanceRepository.save(messageInstance);
        return messageInstanceMapper.toDto(messageInstance);
    }

    /**
     * Update a messageInstance.
     *
     * @param messageInstanceDTO the entity to save.
     * @return the persisted entity.
     */
    public MessageInstanceDTO update(MessageInstanceDTO messageInstanceDTO) {
        LOG.debug("Request to update MessageInstance : {}", messageInstanceDTO);
        MessageInstance messageInstance = messageInstanceMapper.toEntity(messageInstanceDTO);
        messageInstance = messageInstanceRepository.save(messageInstance);
        return messageInstanceMapper.toDto(messageInstance);
    }

    /**
     * Partially update a messageInstance.
     *
     * @param messageInstanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MessageInstanceDTO> partialUpdate(MessageInstanceDTO messageInstanceDTO) {
        LOG.debug("Request to partially update MessageInstance : {}", messageInstanceDTO);

        return messageInstanceRepository
            .findById(messageInstanceDTO.getId())
            .map(existingMessageInstance -> {
                messageInstanceMapper.partialUpdate(existingMessageInstance, messageInstanceDTO);

                return existingMessageInstance;
            })
            .map(messageInstanceRepository::save)
            .map(messageInstanceMapper::toDto);
    }

    /**
     * Get all the messageInstances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MessageInstanceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MessageInstances");
        return messageInstanceRepository.findAll(pageable).map(messageInstanceMapper::toDto);
    }

    /**
     * Get one messageInstance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MessageInstanceDTO> findOne(Long id) {
        LOG.debug("Request to get MessageInstance : {}", id);
        return messageInstanceRepository.findById(id).map(messageInstanceMapper::toDto);
    }

    /**
     * Delete the messageInstance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MessageInstance : {}", id);
        messageInstanceRepository.deleteById(id);
    }
}
