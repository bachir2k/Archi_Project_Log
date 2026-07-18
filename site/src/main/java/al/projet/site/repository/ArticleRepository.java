package al.projet.site.repository;

import al.projet.site.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByOrderByDatePublicationDesc(Pageable pageable);
    Page<Article> findByCategorieIdOrderByDatePublicationDesc(Long categorieId, Pageable pageable);
}
