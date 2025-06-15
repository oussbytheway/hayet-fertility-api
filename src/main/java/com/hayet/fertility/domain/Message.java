package com.hayet.fertility.domain;

import com.hayet.fertility.domain.enumeration.MessageAudience;
import com.hayet.fertility.domain.enumeration.MessageStatus;
import com.hayet.fertility.domain.enumeration.NotificationChannel;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience")
    private MessageAudience audience;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @Column(name = "scheduled_at")
    private ZonedDateTime scheduledAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "tag")
    private String tag;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated")
    private ZonedDateTime updated;

    @Column(name = "updated_by")
    private String updatedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Message content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageAudience getAudience() {
        return this.audience;
    }

    public Message audience(MessageAudience audience) {
        this.setAudience(audience);
        return this;
    }

    public void setAudience(MessageAudience audience) {
        this.audience = audience;
    }

    public NotificationChannel getChannel() {
        return this.channel;
    }

    public Message channel(NotificationChannel channel) {
        this.setChannel(channel);
        return this;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public Message status(MessageStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public ZonedDateTime getScheduledAt() {
        return this.scheduledAt;
    }

    public Message scheduledAt(ZonedDateTime scheduledAt) {
        this.setScheduledAt(scheduledAt);
        return this;
    }

    public void setScheduledAt(ZonedDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public Message sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getTag() {
        return this.tag;
    }

    public Message tag(String tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Message errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Message created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Message createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public Message updated(ZonedDateTime updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Message updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", audience='" + getAudience() + "'" +
            ", channel='" + getChannel() + "'" +
            ", status='" + getStatus() + "'" +
            ", scheduledAt='" + getScheduledAt() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", tag='" + getTag() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", created='" + getCreated() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            "}";
    }
}
