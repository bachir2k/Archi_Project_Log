package al.projet.site.controller;

import al.projet.site.dto.CategorieDto;
import al.projet.site.service.CategorieService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ajoute automatiquement la liste des categories au modele de chaque page,
 * pour que le fragment de navigation (fragments/nav.html) puisse toujours
 * l'afficher sans que chaque controleur ait a y penser.
 */
@ControllerAdvice(basePackages = "al.projet.site.controller")
public class NavigationModelAdvice {

    private final CategorieService categorieService;

    public NavigationModelAdvice(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @ModelAttribute("listeCategoriesNav")
    public List<CategorieDto> listeCategoriesNav() {
        return categorieService.lister().stream().map(CategorieDto::from).collect(Collectors.toList());
    }
}
