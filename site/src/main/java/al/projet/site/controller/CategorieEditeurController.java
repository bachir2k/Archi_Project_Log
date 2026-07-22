package al.projet.site.controller;

import al.projet.site.dto.CategorieRequest;
import al.projet.site.model.Categorie;
import al.projet.site.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Back-office editeur : gestion des categories (lister/ajouter/modifier/supprimer).
 * Reserve aux editeurs et admins (cf. SecurityConfig : "/editeur/**").
 */
@Controller
@RequestMapping("/editeur/categories")
public class CategorieEditeurController {

    private final CategorieService categorieService;

    public CategorieEditeurController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("categories", categorieService.lister());
        return "editeur/categories";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("form", new CategorieRequest());
        model.addAttribute("modeCreation", true);
        return "editeur/categorie_form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("form") CategorieRequest form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("modeCreation", true);
            return "editeur/categorie_form";
        }
        categorieService.creer(form);
        redirect.addFlashAttribute("succes", "Categorie creee avec succes.");
        return "redirect:/editeur/categories";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        Categorie c = categorieService.trouver(id);
        CategorieRequest form = new CategorieRequest();
        form.setNom(c.getNom());
        form.setDescription(c.getDescription());

        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("modeCreation", false);
        return "editeur/categorie_form";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                            @Valid @ModelAttribute("form") CategorieRequest form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("modeCreation", false);
            return "editeur/categorie_form";
        }
        categorieService.modifier(id, form);
        redirect.addFlashAttribute("succes", "Categorie modifiee avec succes.");
        return "redirect:/editeur/categories";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirect) {
        categorieService.supprimer(id);
        redirect.addFlashAttribute("succes", "Categorie supprimee.");
        return "redirect:/editeur/categories";
    }
}
