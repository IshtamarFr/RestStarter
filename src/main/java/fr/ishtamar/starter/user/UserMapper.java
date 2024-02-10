package fr.ishtamar.starter.user;

import fr.ishtamar.starter.util.EntityMapper;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, UserInfo> {
}
