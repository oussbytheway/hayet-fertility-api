package com.hayet.fertility.domain;

import com.hayet.fertility.domain.enumeration.ReminderMotif;
import com.hayet.fertility.domain.enumeration.ReminderPriority;
import com.hayet.fertility.domain.enumeration.ReminderStatus;
import com.hayet.fertility.domain.enumeration.RepeatUnit;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Reminder.
 */
@Entity
@Table(name = "reminder")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif")
    private ReminderMotif motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReminderStatus status;

    @Column(name = "note")
    private String note;

    @Column(name = "due_at")
    private ZonedDateTime dueAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @Column(name = "repeat_every")
    private Integer repeatEvery;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_unit")
    private RepeatUnit repeatUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ReminderPriority priority;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated")
    private ZonedDateTime updated;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reminder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReminderMotif getMotif() {
        return this.motif;
    }

    public Reminder motif(ReminderMotif motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(ReminderMotif motif) {
        this.motif = motif;
    }

    public ReminderStatus getStatus() {
        return this.status;
    }

    public Reminder status(ReminderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public Reminder note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ZonedDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(ZonedDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public Reminder sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getResolvedAt() {
        return this.resolvedAt;
    }

    public Reminder resolvedAt(ZonedDateTime resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Integer getRepeatEvery() {
        return this.repeatEvery;
    }

    public Reminder repeatEvery(Integer repeatEvery) {
        this.setRepeatEvery(repeatEvery);
        return this;
    }

    public void setRepeatEvery(Integer repeatEvery) {
        this.repeatEvery = repeatEvery;
    }

    public RepeatUnit getRepeatUnit() {
        return this.repeatUnit;
    }

    public Reminder repeatUnit(RepeatUnit repeatUnit) {
        this.setRepeatUnit(repeatUnit);
        return this;
    }

    public void setRepeatUnit(RepeatUnit repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    public ReminderPriority getPriority() {
        return priority;
    }

    public void setPriority(ReminderPriority priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Reminder created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Reminder createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public Reminder updated(ZonedDateTime updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Reminder updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Reminder client(Client client) {
        this.setClient(client);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reminder)) {
            return false;
        }
        return getId() != null && getId().equals(((Reminder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reminder{" +
            "id=" + id +
            ", motif=" + motif +
            ", status=" + status +
            ", note='" + note + '\'' +
            ", dueAt=" + dueAt +
            ", sentAt=" + sentAt +
            ", resolvedAt=" + resolvedAt +
            ", repeatEvery=" + repeatEvery +
            ", repeatUnit=" + repeatUnit +
            ", priority=" + priority +
            ", active=" + active +
            ", created=" + created +
            ", createdBy='" + createdBy + '\'' +
            ", updated=" + updated +
            ", updatedBy='" + updatedBy + '\'' +
            ", client=" + client +
            '}';
    }
}
