package fr.ishtamar.starter.standard;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.user.UserInfo;

import java.util.List;

public class StdEntityServiceImpl<T extends StdEntity> {
    protected StdEntityRepository<T> repository;

    public StdEntityServiceImpl(StdEntityRepository<T> repository) {
        this.repository = repository;
    }

    public T createEntity(T entity) throws GenericException {
        List<T> candidate=repository.findByNameAndUser(entity.getName(),entity.getUser());

        if (candidate.isEmpty()) {
            return repository.save(entity);
        } else {
            throw new GenericException("This entity already exists for this user");
        }
    }

    public List<T> getEntitiesForUser(UserInfo user) {
        return repository.findByUser(user);
    }

    public T getEntityById(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("id",id.toString()));
    }

    public void deleteEntity(T entity) {
        repository.delete(entity);
    }
}
