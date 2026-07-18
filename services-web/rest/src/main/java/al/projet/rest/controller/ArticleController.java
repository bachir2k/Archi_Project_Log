package al.projet.rest.controller;

import al.projet.rest.dto.ArticleDto;
import al.projet.rest.model.Article;
import al.projet.rest.model.Categorie;
import al.projet.rest.repository.ArticleRepository;
import al.projet.rest.repository.CategorieRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Negociation de contenu : le client choisit le format via
 * le header "Accept: application/json" ou "Accept: application/xml".
 * (Spring gere automatiquement JSON/XML des lors que
 * jackson-dataformat-xml est sur le classpath.)
 */
@RestController
@RequestMapping("/api")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;

    public ArticleController(ArticleRepository articleRepository, CategorieRepository categorieRepository) {
        this.articleRepository = articleRepository;
        this.categorieRepository = categorieRepository;
    }

    @GetMapping(value = "/articles", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<ArticleDto> listerArticles() {
        return articleRepository.findAll().stream().map(ArticleDto::from).collect(Collectors.toList());
    }

    @GetMapping(value = "/articles/par-categorie", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public Map<String, List<ArticleDto>> articlesParCategorie() {
        Map<String, List<ArticleDto>> resultat = new LinkedHashMap<>();
        for (Categorie c : categorieRepository.findAll()) {
            List<ArticleDto> articles = articleRepository.findByCategorieId(c.getId())
                    .stream().map(ArticleDto::from).collect(Collectors.toList());
            resultat.put(c.getNom(), articles);
        }
        return resultat;
    }

    @GetMapping(value = "/categories/{id}/articles", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public List<ArticleDto> articlesDeLaCategorie(@PathVariable Long id) {
        return articleRepository.findByCategorieId(id).stream().map(ArticleDto::from).collect(Collectors.toList());
    }
}
