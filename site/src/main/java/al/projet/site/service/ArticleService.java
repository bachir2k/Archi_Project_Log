package al.projet.site.service;

import al.projet.site.dto.ArticleRequest;
import al.projet.site.model.Article;
import al.projet.site.model.Categorie;
import al.projet.site.model.Utilisateur;
import al.projet.site.repository.ArticleRepository;
import al.projet.site.repository.CategorieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;

    public ArticleService(ArticleRepository articleRepository, CategorieRepository categorieRepository) {
        this.articleRepository = articleRepository;
        this.categorieRepository = categorieRepository;
    }

    public Page<Article> lister(Pageable pageable) {
        return articleRepository.findAllByOrderByDatePublicationDesc(pageable);
    }

    public Page<Article> listerParCategorie(Long categorieId, Pageable pageable) {
        return articleRepository.findByCategorieIdOrderByDatePublicationDesc(categorieId, pageable);
    }

    public Article trouver(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable : " + id));
    }

    @Transactional
    public Article creer(ArticleRequest request, Utilisateur auteur) {
        Article a = new Article();
        a.setTitre(request.getTitre());
        a.setDescription(request.getDescription());
        a.setContenu(request.getContenu());
        a.setAuteur(auteur);
        a.setCategorie(resoudreCategorie(request.getCategorieId()));
        return articleRepository.save(a);
    }

    @Transactional
    public Article modifier(Long id, ArticleRequest request) {
        Article a = trouver(id);
        a.setTitre(request.getTitre());
        a.setDescription(request.getDescription());
        a.setContenu(request.getContenu());
        a.setCategorie(resoudreCategorie(request.getCategorieId()));
        return articleRepository.save(a);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new IllegalArgumentException("Article introuvable : " + id);
        }
        articleRepository.deleteById(id);
    }

    private Categorie resoudreCategorie(Long categorieId) {
        if (categorieId == null) {
            return null;
        }
        return categorieRepository.findById(categorieId)
                .orElseThrow(() -> new IllegalArgumentException("Categorie introuvable : " + categorieId));
    }
}
