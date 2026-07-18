package al.projet.rest.repository;

import al.projet.rest.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategorieId(Long categorieId);
}
