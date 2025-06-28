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
import java.util.Objects;
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
     * Create a new reminder.
     *
     * @param reminder the entity to create.
     * @return the persisted entity.
     */
    public ReminderDTO create(ReminderDTO reminder) {
        log.debug("Request to create a Reminder : {}", reminder);
        User authenticatedAdmin = getCurrentAuthenticatedUser();

        if (reminder.getMotif() == null) {
            throw new BadRequestAlertException(
                "The motif is required",
                ENTITY_NAME,
                ErrorConstants.MOTIF_IS_REQUIRED
            );
        }
        validateFutureDueDate(reminder.getDueAt());

        reminder.setStatus(ReminderStatus.SCHEDULED);
        reminder.setPriority(reminder.getPriority() != null ? reminder.getPriority() : ReminderPriority.LOW);
        reminder.setCreated(ZonedDateTime.now());
        reminder.setCreatedBy(authenticatedAdmin.getEmail());

        return save(reminder);
    }

    /**
     * Update an existing reminder with selective field updates.
     *
     * @param reminder the entity to update.
     * @return the persisted entity.
     */
    public ReminderDTO update(ReminderDTO reminder) throws AccessDeniedException {
        log.debug("Request to update Reminder : {}", reminder);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ReminderDTO originalReminder = getReminderOrThrow(reminder.getId());

        // Update motif if provided
        if (reminder.getMotif() != null && !Objects.equals(reminder.getMotif(), originalReminder.getMotif())) {
            originalReminder.setMotif(reminder.getMotif());
        }

        // Update due date with validation
        if (reminder.getDueAt() != null && !Objects.equals(reminder.getMotif(), originalReminder.getMotif())) {
            validateReminderNotNearExecution(reminder.getDueAt());
            validateFutureDueDate(reminder.getDueAt());
            originalReminder.setDueAt(reminder.getDueAt());
        } else {
            validateReminderNotNearExecution(reminder.getDueAt());
        }

        // Update optional fields if changed
        updateOptionalFields(originalReminder, reminder);

        originalReminder.setUpdated(ZonedDateTime.now());
        originalReminder.setUpdatedBy(authenticatedAdmin.getEmail());

        return save(originalReminder);
    }

    /**
     * Activate an existing reminder.
     *
     * @param id the ID of the reminder to activate.
     * @return the persisted reminder.
     */
    public ReminderDTO activate(Long id) throws AccessDeniedException {
        log.debug("Request to activate reminder : {}", id);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ReminderDTO reminder = getReminderOrThrow(id);

        reminder.setActive(true);
        updateAuditFields(reminder, authenticatedAdmin);

        return save(reminder);
    }

    /**
     * Deactivate an existing reminder.
     *
     * @param id the ID of the reminder to deactivate.
     * @return the persisted reminder.
     */
    public ReminderDTO deactivate(Long id) throws AccessDeniedException {
        log.debug("Request to deactivate reminder : {}", id);
        User authenticatedAdmin = getCurrentAuthenticatedUser();
        ReminderDTO reminder = getReminderOrThrow(id);

        reminder.setActive(false);
        updateAuditFields(reminder, authenticatedAdmin);

        return save(reminder);
    }

    /**
     * Resolve an existing reminder.
     *
     * @param id the ID of the reminder to resolve.
     * @return the persisted reminder.
     */
    public ReminderDTO resolve(Long id) throws AccessDeniedException {
        log.debug("Request to resolve reminder : {}", id);
        ReminderDTO reminder = getReminderOrThrow(id);

        reminder.setResolvedAt(ZonedDateTime.now());

        return save(reminder);
    }

    /**
     * Save a reminder.
     *
     * @param reminderDTO the reminder to save.
     * @return the persisted reminder.
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

    // Gets current authenticated user or throws if not found
    private User getCurrentAuthenticatedUser() {
        return userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();
    }

    // Retrieves reminder by ID or throws AccessDeniedException if not found
    private ReminderDTO getReminderOrThrow(Long id) throws AccessDeniedException {
        return findOne(id).orElseThrow(() -> new AccessDeniedException("Reminder not found"));
    }

    // Validates that due date is in the future
    private void validateFutureDueDate(ZonedDateTime dueAt) {
        if (dueAt == null || !dueAt.isAfter(ZonedDateTime.now())) {
            throw new BadRequestAlertException(
                "The due date must be in the future",
                ENTITY_NAME,
                ErrorConstants.DUE_DATE_MUST_BE_IN_FUTURE
            );
        }
    }

    // Validates that reminder is not too close to execution time
    private void validateReminderNotNearExecution(ZonedDateTime dueAt) {
        if (dueAt.isBefore(ZonedDateTime.now().plusMinutes(1))) {
            throw new BadRequestAlertException(
                "Cannot update reminder due in 1 minutes or less",
                ENTITY_NAME,
                ErrorConstants.REMINDER_NEAR_EXECUTION_CANNOT_BE_MODIFIED
            );
        }
    }

    // Updates optional fields from source to target reminder
    private void updateOptionalFields(ReminderDTO target, ReminderDTO source) {
        if (source.getNote() != null && !Objects.equals(target.getNote(), source.getNote())) {
            target.setNote(source.getNote());
        }
        if (source.getRepeatEvery() != null && !Objects.equals(target.getRepeatEvery(), source.getRepeatEvery())) {
            target.setRepeatEvery(source.getRepeatEvery());
        }
        if (source.getRepeatUnit() != null && !Objects.equals(target.getRepeatUnit(), source.getRepeatUnit())) {
            target.setRepeatUnit(source.getRepeatUnit());
        }
        if (source.getPriority() != null && !Objects.equals(target.getPriority(), source.getPriority())) {
            target.setPriority(source.getPriority());
        }
        if (source.getClientId() != null && !Objects.equals(target.getClientId(), source.getClientId())) {
            target.setClientId(source.getClientId());
        }
    }

    // Updates audit fields for reminder modifications
    private void updateAuditFields(ReminderDTO reminder, User user) {
        reminder.setUpdated(ZonedDateTime.now());
        reminder.setUpdatedBy(user.getEmail());
    }
}
