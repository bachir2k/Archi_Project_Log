package al.projet.site.controller;

import al.projet.site.dto.ArticleDto;
import al.projet.site.dto.CategorieDto;
import al.projet.site.model.Article;
import al.projet.site.model.Categorie;
import al.projet.site.service.ArticleService;
import al.projet.site.service.CategorieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pages publiques (visiteur simple, pas d'authentification requise) :
 * accueil avec pagination, detail d'un article, filtre par categorie.
 */
@Controller
public class PublicController {

    private static final int TAILLE_PAGE = 5;

    private final ArticleService articleService;
    private final CategorieService categorieService;

    public PublicController(ArticleService articleService, CategorieService categorieService) {
        this.articleService = articleService;
        this.categorieService = categorieService;
    }

    @GetMapping("/")
    public String accueil(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, TAILLE_PAGE);
        Page<Article> resultats = articleService.lister(pageable);
        remplirModelListe(model, resultats, page, null);
        return "accueil";
    }

    @GetMapping("/articles/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("article", ArticleDto.from(articleService.trouver(id)));
        return "detail";
    }

    @GetMapping("/categories/{id}")
    public String parCategorie(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        Categorie categorie = categorieService.trouver(id);
        Pageable pageable = PageRequest.of(page, TAILLE_PAGE);
        Page<Article> resultats = articleService.listerParCategorie(id, pageable);
        remplirModelListe(model, resultats, page, categorie);
        return "accueil";
    }

    private void remplirModelListe(Model model, Page<Article> resultats, int page, Categorie categorieActuelle) {
        List<ArticleDto> articles = resultats.getContent().stream().map(ArticleDto::from).collect(Collectors.toList());
        model.addAttribute("articles", articles);
        model.addAttribute("page", page);
        model.addAttribute("hasNext", resultats.hasNext());
        model.addAttribute("categorieActuelle", categorieActuelle != null ? CategorieDto.from(categorieActuelle) : null);
    }
}
