package fr.ishtamar.starter.standard;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.user.UserInfo;

import java.util.List;

public interface StdEntityService<T extends StdEntity> {
    T createEntity(T entity) throws GenericException;
    List<T> getEntitiesForUser(UserInfo user);
    T getEntityById(Long id) throws EntityNotFoundException;
    void deleteEntity(T entity);
}
