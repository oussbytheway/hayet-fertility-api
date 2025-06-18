package com.hayet.fertility.service.mapper;

import com.hayet.fertility.domain.Message;
import com.hayet.fertility.service.dto.MessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {}
