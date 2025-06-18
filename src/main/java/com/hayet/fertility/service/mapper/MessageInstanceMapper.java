package com.hayet.fertility.service.mapper;

import com.hayet.fertility.domain.Client;
import com.hayet.fertility.domain.Message;
import com.hayet.fertility.domain.MessageInstance;
import com.hayet.fertility.service.dto.ClientDTO;
import com.hayet.fertility.service.dto.MessageDTO;
import com.hayet.fertility.service.dto.MessageInstanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MessageInstance} and its DTO {@link MessageInstanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageInstanceMapper extends EntityMapper<MessageInstanceDTO, MessageInstance> {
    @Mapping(target = "client", source = "client", qualifiedByName = "clientId")
    @Mapping(target = "message", source = "message", qualifiedByName = "messageId")
    MessageInstanceDTO toDto(MessageInstance s);

    @Named("clientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientDTO toDtoClientId(Client client);

    @Named("messageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MessageDTO toDtoMessageId(Message message);
}
