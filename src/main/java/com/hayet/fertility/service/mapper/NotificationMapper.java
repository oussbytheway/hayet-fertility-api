package com.hayet.fertility.service.mapper;

import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.Notification;
import com.hayet.fertility.domain.Reminder;
import com.hayet.fertility.service.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link com.hayet.fertility.domain.Notification} and its DTO {@link com.hayet.fertility.domain.Notification}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {

    @Mapping(source = "reminder.id", target = "reminderId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client", target = "clientFullName", qualifiedByName = "clientToFullName")
    NotificationDTO toDto(Notification notification);

    @Mapping(source = "reminderId", target = "reminder")
    @Mapping(source = "clientId", target = "client")
    Notification toEntity(NotificationDTO dto);

    default Reminder fromReminderId(Long id) {
        if (id == null) {
            return null;
        }
        Reminder reminder = new Reminder();
        reminder.setId(id);
        return reminder;
    }

    default Client fromClientId(Long id) {
        if (id == null) {
            return null;
        }
        Client client = new Client();
        client.setId(id);
        return client;
    }

    @Named("clientToFullName")
    default String clientToFullName(Client client) {
        if (client == null) {
            return null;
        }
        return (client.getFirstName() != null ? client.getFirstName() + " " : "") + client.getLastName();
    }
}
