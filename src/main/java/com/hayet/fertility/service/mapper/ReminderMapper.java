package com.hayet.fertility.service.mapper;

import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.Reminder;
import com.hayet.fertility.service.dto.ReminderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reminder} and its DTO {@link ReminderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReminderMapper extends EntityMapper<ReminderDTO, Reminder> {

    @Mapping(source = "reminder.id", target = "reminderId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client", target = "clientFullName", qualifiedByName = "clientToFullName")
    ReminderDTO toDto(Reminder reminder);

    @Mapping(source = "reminderId", target = "reminder")
    @Mapping(source = "clientId", target = "client")
    Reminder toEntity(ReminderDTO dto);

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
