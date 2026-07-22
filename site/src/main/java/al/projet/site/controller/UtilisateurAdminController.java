package al.projet.site.controller;

import al.projet.site.dto.UtilisateurForm;
import al.projet.site.model.Utilisateur;
import al.projet.site.service.JetonService;
import al.projet.site.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Back-office admin : gestion des utilisateurs et des jetons d'authentification.
 * Reserve au role ADMIN (cf. SecurityConfig : "/admin/**" -> hasRole("ADMIN")).
 */
@Controller
@RequestMapping("/admin/utilisateurs")
public class UtilisateurAdminController {

    private final UtilisateurService utilisateurService;
    private final JetonService jetonService;

    public UtilisateurAdminController(UtilisateurService utilisateurService, JetonService jetonService) {
        this.utilisateurService = utilisateurService;
        this.jetonService = jetonService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.lister());
        return "admin/utilisateurs";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("form", new UtilisateurForm());
        model.addAttribute("roles", Utilisateur.Role.values());
        model.addAttribute("modeCreation", true);
        return "admin/utilisateur_form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("form") UtilisateurForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return reafficherFormulaireCreation(model, null);
        }
        try {
            utilisateurService.creer(form);
        } catch (IllegalArgumentException e) {
            return reafficherFormulaireCreation(model, e.getMessage());
        }
        redirect.addFlashAttribute("succes", "Utilisateur cree avec succes.");
        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        Utilisateur u = utilisateurService.trouver(id);
        UtilisateurForm form = new UtilisateurForm();
        form.setLogin(u.getLogin());
        form.setNom(u.getNom());
        form.setEmail(u.getEmail());
        form.setRole(u.getRole().name());

        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("roles", Utilisateur.Role.values());
        model.addAttribute("modeCreation", false);
        model.addAttribute("jetons", jetonService.listerPour(id));
        return "admin/utilisateur_form";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id,
                            @Valid @ModelAttribute("form") UtilisateurForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return reafficherFormulaireEdition(id, model, null);
        }
        try {
            utilisateurService.modifier(id, form);
        } catch (IllegalArgumentException e) {
            return reafficherFormulaireEdition(id, model, e.getMessage());
        }
        redirect.addFlashAttribute("succes", "Utilisateur modifie avec succes.");
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirect) {
        utilisateurService.supprimer(id);
        redirect.addFlashAttribute("succes", "Utilisateur supprime.");
        return "redirect:/admin/utilisateurs";
    }

    /** Genere un nouveau jeton d'authentification pour cet utilisateur. */
    @PostMapping("/{id}/jetons")
    public String genererJeton(@PathVariable Long id, RedirectAttributes redirect) {
        jetonService.generer(id);
        redirect.addFlashAttribute("succes", "Jeton genere.");
        return "redirect:/admin/utilisateurs/" + id + "/modifier";
    }

    /** Revoque (supprime) un jeton existant. */
    @PostMapping("/{id}/jetons/{jetonId}/supprimer")
    public String supprimerJeton(@PathVariable Long id, @PathVariable Long jetonId, RedirectAttributes redirect) {
        jetonService.supprimer(jetonId);
        redirect.addFlashAttribute("succes", "Jeton revoque.");
        return "redirect:/admin/utilisateurs/" + id + "/modifier";
    }

    private String reafficherFormulaireCreation(Model model, String erreur) {
        model.addAttribute("roles", Utilisateur.Role.values());
        model.addAttribute("modeCreation", true);
        if (erreur != null) model.addAttribute("erreur", erreur);
        return "admin/utilisateur_form";
    }

    private String reafficherFormulaireEdition(Long id, Model model, String erreur) {
        model.addAttribute("id", id);
        model.addAttribute("roles", Utilisateur.Role.values());
        model.addAttribute("modeCreation", false);
        model.addAttribute("jetons", jetonService.listerPour(id));
        if (erreur != null) model.addAttribute("erreur", erreur);
        return "admin/utilisateur_form";
    }
}
