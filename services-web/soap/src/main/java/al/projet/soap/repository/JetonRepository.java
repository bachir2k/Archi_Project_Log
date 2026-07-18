package al.projet.soap.repository;

import al.projet.soap.model.JetonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JetonRepository extends JpaRepository<JetonEntity, Long> {
    Optional<JetonEntity> findByValeur(String valeur);
}
