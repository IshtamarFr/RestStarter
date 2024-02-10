package fr.ishtamar.starter.truc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrucRepository extends JpaRepository<Truc, Long> {
}
