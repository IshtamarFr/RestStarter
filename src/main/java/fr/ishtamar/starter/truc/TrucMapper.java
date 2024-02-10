package fr.ishtamar.starter.truc;

import fr.ishtamar.starter.util.EntityMapper;
import fr.ishtamar.starter.user.UserInfoServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "Spring")
public abstract class TrucMapper implements EntityMapper<TrucDto, Truc> {

    @Autowired
    UserInfoServiceImpl userInfoService;

    @Mappings({
            @Mapping(target="user", expression="java(this.userInfoService.getUserById(trucDto.getUser_id()))")
    })
    public abstract Truc toEntity(TrucDto trucDto);

    @Mappings({
            @Mapping(source= "truc.user.id",target="user_id")
    })
    public abstract TrucDto toDto(Truc truc);
}
