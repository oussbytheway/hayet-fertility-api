package com.hayet.fertility.service.dto;

import com.hayet.fertility.domain.enumeration.ReminderMotif;
import com.hayet.fertility.domain.enumeration.ReminderStatus;
import com.hayet.fertility.domain.enumeration.RepeatUnit;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.hayet.fertility.domain.Reminder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReminderDTO implements Serializable {

    private Long id;

    private ReminderMotif motif;

    private ReminderStatus status;

    private String note;

    private ZonedDateTime sentAt;

    private ZonedDateTime resolvedAt;

    private ZonedDateTime created;

    private String createdBy;

    private ZonedDateTime updated;

    private String updatedBy;

    private Integer repeatEvery;

    private RepeatUnit repeatUnit;

    private ClientDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReminderMotif getMotif() {
        return motif;
    }

    public void setMotif(ReminderMotif motif) {
        this.motif = motif;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(Integer repeatEvery) {
        this.repeatEvery = repeatEvery;
    }

    public RepeatUnit getRepeatPattern() {
        return repeatUnit;
    }

    public void setRepeatPattern(RepeatUnit repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReminderDTO reminderDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reminderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReminderDTO{" +
            "id=" + getId() +
            ", motif='" + getMotif() + "'" +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", created='" + getCreated() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", repeatEvery=" + getRepeatEvery() +
            ", repeatUnit='" + getRepeatPattern() + "'" +
            ", client=" + getClient() +
            "}";
    }
}
