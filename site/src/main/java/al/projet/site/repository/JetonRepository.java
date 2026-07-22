package al.projet.site.repository;

import al.projet.site.model.Jeton;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JetonRepository extends JpaRepository<Jeton, Long> {
    Optional<Jeton> findByValeurAndActifTrue(String valeur);
    List<Jeton> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);
}
