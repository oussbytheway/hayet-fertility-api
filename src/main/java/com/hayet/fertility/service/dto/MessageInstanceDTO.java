package com.hayet.fertility.service.dto;

import com.hayet.fertility.domain.enumeration.NotificationChannel;
import com.hayet.fertility.domain.enumeration.NotificationStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.hayet.fertility.domain.MessageInstance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageInstanceDTO implements Serializable {

    private Long id;

    private String content;

    private NotificationStatus status;

    private NotificationChannel channel;

    private ZonedDateTime sentAt;

    private ZonedDateTime deliveredAt;

    private ZonedDateTime failedAt;

    private Integer deliveryAttempts;

    private String errorMessage;

    private ZonedDateTime created;

    private String createdBy;

    private ZonedDateTime updated;

    private String updatedBy;

    private ClientDTO client;

    private MessageDTO message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(ZonedDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public ZonedDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(ZonedDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public Integer getDeliveryAttempts() {
        return deliveryAttempts;
    }

    public void setDeliveryAttempts(Integer deliveryAttempts) {
        this.deliveryAttempts = deliveryAttempts;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageInstanceDTO)) {
            return false;
        }

        MessageInstanceDTO messageInstanceDTO = (MessageInstanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageInstanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageInstanceDTO{" +
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
            ", client=" + getClient() +
            ", message=" + getMessage() +
            "}";
    }
}
