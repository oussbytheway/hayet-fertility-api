package com.hayet.fertility.service.mapper;

import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.Reminder;
import com.hayet.fertility.service.dto.ClientDTO;
import com.hayet.fertility.service.dto.ReminderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reminder} and its DTO {@link ReminderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReminderMapper extends EntityMapper<ReminderDTO, Reminder> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientId")
    ReminderDTO toDto(Reminder s);

    @Named("clientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientDTO toDtoClientId(Client client);
}
