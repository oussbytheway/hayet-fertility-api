package com.hayet.fertility.service;

import com.hayet.fertility.domain.Reminder;
import com.hayet.fertility.domain.User;
import com.hayet.fertility.domain.enumeration.ReminderPriority;
import com.hayet.fertility.domain.enumeration.ReminderStatus;
import com.hayet.fertility.repository.ReminderRepository;
import com.hayet.fertility.security.SecurityUtils;
import com.hayet.fertility.service.dto.ReminderDTO;
import com.hayet.fertility.service.mapper.ReminderMapper;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.hayet.fertility.web.rest.errors.BadRequestAlertException;
import com.hayet.fertility.web.rest.errors.ErrorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hayet.fertility.domain.Reminder}.
 */
@Service
@Transactional
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);
    private static final String ENTITY_NAME = "Reminder";

    private final ReminderRepository reminderRepository;
    private final UserService userService;
    private final ReminderMapper reminderMapper;

    public ReminderService(ReminderRepository reminderRepository, UserService userService, ReminderMapper reminderMapper) {
        this.reminderRepository = reminderRepository;
        this.userService = userService;
        this.reminderMapper = reminderMapper;
    }

    /**
     * Save a reminder.
     *
     * @param reminder the entity to save.
     * @return the persisted entity.
     */
    public ReminderDTO create(ReminderDTO reminder) {
        log.debug("Request to create a Reminder : {}", reminder);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        if (reminder.getMotif() == null) {
            throw new BadRequestAlertException(
                "The motif is required",
                ENTITY_NAME,
                ErrorConstants.MOTIF_IS_REQUIRED
            );
        }
        if (reminder.getDueAt() == null || !reminder.getDueAt().isAfter(ZonedDateTime.now())) {
            throw new BadRequestAlertException(
                "The due date must be in the future",
                ENTITY_NAME,
                ErrorConstants.DUE_DATE_MUST_BE_IN_FUTURE
            );
        }

        reminder.setStatus(ReminderStatus.SCHEDULED);
        reminder.setPriority(reminder.getPriority() != null ? reminder.getPriority() : ReminderPriority.LOW);
        reminder.setCreated(ZonedDateTime.now());
        reminder.setCreatedBy(authenticatedAdmin.getEmail());

        return save(reminder);
    }

    /**
     * Update a reminder.
     *
     * @param reminder the entity to update.
     * @return the persisted entity.
     */
    public ReminderDTO update(ReminderDTO reminder) throws AccessDeniedException {
        log.debug("Request to update Reminder : {}", reminder);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        ReminderDTO originalReminder = findOne(reminder.getId()).orElseThrow(() -> new AccessDeniedException("Reminder not found"));

        // Validate and update fields if provided
        if (reminder.getMotif() != null) {
            originalReminder.setMotif(reminder.getMotif());
        }

        if (reminder.getDueAt() != null) {
            if (!reminder.getDueAt().isAfter(ZonedDateTime.now())) {
                throw new BadRequestAlertException(
                    "The due date must be in the future",
                    ENTITY_NAME,
                    ErrorConstants.DUE_DATE_MUST_BE_IN_FUTURE
                );
            }
            if (reminder.getDueAt().isBefore(ZonedDateTime.now().plusMinutes(1))) {
                throw new BadRequestAlertException(
                    "Cannot update reminder due in 1 minutes or less",
                    ENTITY_NAME,
                    ErrorConstants.REMINDER_NEAR_EXECUTION_CANNOT_BE_MODIFIED
                );
            }
            originalReminder.setDueAt(reminder.getDueAt());
        }

        if (reminder.getNote() != null) {
            originalReminder.setNote(reminder.getNote());
        }
        if (reminder.getSentAt() != null) {
            originalReminder.setSentAt(reminder.getSentAt());
        }
        if (reminder.getRepeatEvery() != null) {
            originalReminder.setRepeatEvery(reminder.getRepeatEvery());
        }
        if (reminder.getRepeatUnit() != null) {
            originalReminder.setRepeatUnit(reminder.getRepeatUnit());
        }
        if (reminder.getPriority() != null) {
            originalReminder.setPriority(reminder.getPriority());
        }
        if (reminder.getClient() != null) {
            originalReminder.setClient(reminder.getClient());
        }

        originalReminder.setUpdated(ZonedDateTime.now());
        originalReminder.setUpdatedBy(authenticatedAdmin.getEmail());

        return save(originalReminder);
    }

    public ReminderDTO activate(Long id) throws AccessDeniedException {
        log.debug("Request to activate reminder : {}", id);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        ReminderDTO originalReminder = findOne(id).orElseThrow(() -> new AccessDeniedException("Reminder not found"));
        originalReminder.setCanceled(true);
        originalReminder.setUpdated(ZonedDateTime.now());
        originalReminder.setUpdatedBy(authenticatedAdmin.getEmail());

        return save(originalReminder);
    }

    public ReminderDTO deactivate(Long id) throws AccessDeniedException {
        log.debug("Request to deactivate reminder : {}", id);
        User authenticatedAdmin = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();

        ReminderDTO originalReminder = findOne(id).orElseThrow(() -> new AccessDeniedException("Reminder not found"));
        originalReminder.setCanceled(false);
        originalReminder.setUpdated(ZonedDateTime.now());
        originalReminder.setUpdatedBy(authenticatedAdmin.getEmail());

        return save(originalReminder);
    }

    public ReminderDTO resolve(Long id) throws AccessDeniedException {
        log.debug("Request to resolve reminder : {}", id);

        ReminderDTO originalReminder = findOne(id).orElseThrow(() -> new AccessDeniedException("Reminder not found"));
        originalReminder.setResolvedAt(ZonedDateTime.now());

        return save(originalReminder);
    }

    /**
     * Save a reminder.
     *
     * @param reminderDTO the entity to save.
     * @return the persisted entity.
     */
    public ReminderDTO save(ReminderDTO reminderDTO) {
        log.debug("Request to save Reminder : {}", reminderDTO);
        Reminder reminder = reminderMapper.toEntity(reminderDTO);
        reminder = reminderRepository.save(reminder);
        return reminderMapper.toDto(reminder);
    }

    /**
     * Get all the reminders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReminderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reminders");
        return reminderRepository.findAll(pageable).map(reminderMapper::toDto);
    }

    /**
     * Get one reminder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReminderDTO> findOne(Long id) {
        log.debug("Request to get Reminder : {}", id);
        return reminderRepository.findById(id).map(reminderMapper::toDto);
    }

    /**
     * Delete the reminder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reminder : {}", id);
        reminderRepository.deleteById(id);
    }
}
