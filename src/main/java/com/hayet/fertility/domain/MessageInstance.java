package com.hayet.fertility.domain;

import com.hayet.fertility.domain.enumeration.NotificationChannel;
import com.hayet.fertility.domain.enumeration.NotificationStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MessageInstance.
 */
@Entity
@Table(name = "message_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "failed_at")
    private ZonedDateTime failedAt;

    @Column(name = "delivery_attempts")
    private Integer deliveryAttempts;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message message;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MessageInstance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public MessageInstance content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationStatus getStatus() {
        return this.status;
    }

    public MessageInstance status(NotificationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public NotificationChannel getChannel() {
        return this.channel;
    }

    public MessageInstance channel(NotificationChannel channel) {
        this.setChannel(channel);
        return this;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public ZonedDateTime getSentAt() {
        return this.sentAt;
    }

    public MessageInstance sentAt(ZonedDateTime sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public MessageInstance deliveredAt(ZonedDateTime deliveredAt) {
        this.setDeliveredAt(deliveredAt);
        return this;
    }

    public void setDeliveredAt(ZonedDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public ZonedDateTime getFailedAt() {
        return this.failedAt;
    }

    public MessageInstance failedAt(ZonedDateTime failedAt) {
        this.setFailedAt(failedAt);
        return this;
    }

    public void setFailedAt(ZonedDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public Integer getDeliveryAttempts() {
        return this.deliveryAttempts;
    }

    public MessageInstance deliveryAttempts(Integer deliveryAttempts) {
        this.setDeliveryAttempts(deliveryAttempts);
        return this;
    }

    public void setDeliveryAttempts(Integer deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public MessageInstance errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public MessageInstance created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public MessageInstance createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public MessageInstance updated(ZonedDateTime updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public MessageInstance updatedBy(String updatedBy) {
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

    public MessageInstance client(Client client) {
        this.setClient(client);
        return this;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageInstance message(Message message) {
        this.setMessage(message);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageInstance)) {
            return false;
        }
        return getId() != null && getId().equals(((MessageInstance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageInstance{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", status='" + getStatus() + "'" +
            ", channel='" + getChannel() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", failedAt='" + getFailedAt() + "'" +
            ", deliveryAttempts=" + getDeliveryAttempts() +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", created='" + getCreated() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            "}";
    }
}
