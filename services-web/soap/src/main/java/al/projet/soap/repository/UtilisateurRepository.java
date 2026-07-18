package al.projet.soap.repository;

import al.projet.soap.model.UtilisateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<UtilisateurEntity, Long> {
    Optional<UtilisateurEntity> findByLogin(String login);
}
