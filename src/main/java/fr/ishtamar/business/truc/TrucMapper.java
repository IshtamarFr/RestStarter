package fr.ishtamar.business.truc;

import fr.ishtamar.starter.util.EntityMapper;
import fr.ishtamar.starter.user.UserInfoServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "Spring")
public interface TrucMapper extends EntityMapper<TrucDto, Truc> {
    @Mappings({
            @Mapping(source= "truc.user.id",target="user_id")
    })
    TrucDto toDto(Truc truc);
}
