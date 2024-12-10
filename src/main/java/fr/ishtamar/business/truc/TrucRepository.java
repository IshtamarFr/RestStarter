package fr.ishtamar.business.truc;

import fr.ishtamar.starter.standard.StdEntityRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrucRepository extends StdEntityRepository<Truc> {
}
