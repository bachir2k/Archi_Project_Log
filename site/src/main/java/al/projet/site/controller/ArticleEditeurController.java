package al.projet.site.controller;

import al.projet.site.dto.ArticleRequest;
import al.projet.site.model.Article;
import al.projet.site.security.UtilisateurPrincipal;
import al.projet.site.service.ArticleService;
import al.projet.site.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Back-office editeur : gestion des articles (lister/ajouter/modifier/supprimer).
 * Reserve aux editeurs et admins (cf. SecurityConfig : "/editeur/**").
 */
@Controller
@RequestMapping("/editeur/articles")
public class ArticleEditeurController {

    private static final int TAILLE_PAGE = 10;

    private final ArticleService articleService;
    private final CategorieService categorieService;

    public ArticleEditeurController(ArticleService articleService, CategorieService categorieService) {
        this.articleService = articleService;
        this.categorieService = categorieService;
    }

    @GetMapping
    public String liste(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, TAILLE_PAGE);
        Page<Article> resultats = articleService.lister(pageable);
        model.addAttribute("articles", resultats.getContent());
        model.addAttribute("page", page);
        model.addAttribute("hasNext", resultats.hasNext());
        return "editeur/articles";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("form", new ArticleRequest());
        model.addAttribute("categories", categorieService.lister());
        model.addAttribute("modeCreation", true);
        return "editeur/article_form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("form") ArticleRequest form,
                         BindingResult result, Model model,
                         @AuthenticationPrincipal UtilisateurPrincipal principal,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return reafficherCreation(model, null);
        }
        try {
            articleService.creer(form, principal.getUtilisateur());
        } catch (IllegalArgumentException e) {
            return reafficherCreation(model, e.getMessage());
        }
        redirect.addFlashAttribute("succes", "Article cree avec succes.");
        return "redirect:/editeur/articles";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        Article a = articleService.trouver(id);

        ArticleRequest form = new ArticleRequest();
        form.setTitre(a.getTitre());
        form.setDescription(a.getDescription());
        form.setContenu(a.getContenu());
        form.setCategorieId(a.getCategorie() != null ? a.getCategorie().getId() : null);

        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("categories", categorieService.lister());
        model.addAttribute("modeCreation", false);
        return "editeur/article_form";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ArticleRequest form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return reafficherEdition(id, model, null);
        }
        try {
            articleService.modifier(id, form);
        } catch (IllegalArgumentException e) {
            return reafficherEdition(id, model, e.getMessage());
        }
        redirect.addFlashAttribute("succes", "Article modifie avec succes.");
        return "redirect:/editeur/articles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirect) {
        articleService.supprimer(id);
        redirect.addFlashAttribute("succes", "Article supprime.");
        return "redirect:/editeur/articles";
    }

    private String reafficherCreation(Model model, String erreur) {
        model.addAttribute("categories", categorieService.lister());
        model.addAttribute("modeCreation", true);
        if (erreur != null) model.addAttribute("erreur", erreur);
        return "editeur/article_form";
    }

    private String reafficherEdition(Long id, Model model, String erreur) {
        model.addAttribute("id", id);
        model.addAttribute("categories", categorieService.lister());
        model.addAttribute("modeCreation", false);
        if (erreur != null) model.addAttribute("erreur", erreur);
        return "editeur/article_form";
    }
}
