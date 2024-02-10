package fr.ishtamar.starter.truc;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrucServiceImpl implements TrucService {
    private final TrucRepository repository;

    public TrucServiceImpl(TrucRepository repository) {
        this.repository = repository;
    }

    public List<Truc> getAllTrucs() {
        return repository.findAll();
    }

    @Override
    public Truc getTrucById(final Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Truc.class,"id",id.toString()));
    }
}
