package fr.ishtamar.business.truc;

import fr.ishtamar.starter.standard.StdEntityRepository;
import fr.ishtamar.starter.standard.StdEntityServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TrucServiceImpl extends StdEntityServiceImpl<Truc> implements TrucService {
    public TrucServiceImpl(StdEntityRepository<Truc> repository) {
        super(repository);
    }
}
