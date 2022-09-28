package fr.m2i.apichat.service.mapper;

import fr.m2i.apichat.domain.Canal;
import fr.m2i.apichat.domain.Message;
import fr.m2i.apichat.domain.User;
import fr.m2i.apichat.service.dto.CanalDTO;
import fr.m2i.apichat.service.dto.MessageDTO;
import fr.m2i.apichat.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "canal", source = "canal", qualifiedByName = "canalId")
    MessageDTO toDto(Message s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("canalId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CanalDTO toDtoCanalId(Canal canal);
}
