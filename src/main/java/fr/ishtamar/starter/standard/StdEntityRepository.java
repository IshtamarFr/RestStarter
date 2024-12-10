package fr.ishtamar.starter.standard;

import fr.ishtamar.starter.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface StdEntityRepository<T extends StdEntity> extends JpaRepository<T,Long> {
    List<T> findByUser(UserInfo user);
    List<T> findByNameAndUser(String name,UserInfo user);
}
