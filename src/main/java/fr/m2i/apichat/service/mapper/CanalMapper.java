package fr.m2i.apichat.service.mapper;

import fr.m2i.apichat.domain.Canal;
import fr.m2i.apichat.domain.User;
import fr.m2i.apichat.service.dto.CanalDTO;
import fr.m2i.apichat.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Canal} and its DTO {@link CanalDTO}.
 */
@Mapper(componentModel = "spring")
public interface CanalMapper extends EntityMapper<CanalDTO, Canal> {
    @Mapping(target = "users", source = "users", qualifiedByName = "userLoginSet")
    CanalDTO toDto(Canal s);

    @Mapping(target = "removeUser", ignore = true)
    Canal toEntity(CanalDTO canalDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("userLoginSet")
    default Set<UserDTO> toDtoUserLoginSet(Set<User> user) {
        return user.stream().map(this::toDtoUserLogin).collect(Collectors.toSet());
    }
}
