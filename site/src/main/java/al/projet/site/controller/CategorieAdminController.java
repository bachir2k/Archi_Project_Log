package al.projet.site.controller;

import al.projet.site.dto.CategorieDto;
import al.projet.site.dto.CategorieRequest;
import al.projet.site.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API interne de gestion des categories, reservee aux editeurs/admins
 * (voir SecurityConfig : "/editeur/**" -> hasAnyRole("EDITEUR", "ADMIN")).
 */
@RestController
@RequestMapping("/editeur/api/categories")
public class CategorieAdminController {

    private final CategorieService categorieService;

    public CategorieAdminController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping
    public List<CategorieDto> lister() {
        return categorieService.lister().stream().map(CategorieDto::from).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategorieDto trouver(@PathVariable Long id) {
        return CategorieDto.from(categorieService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<CategorieDto> creer(@Valid @RequestBody CategorieRequest request) {
        CategorieDto dto = CategorieDto.from(categorieService.creer(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public CategorieDto modifier(@PathVariable Long id, @Valid @RequestBody CategorieRequest request) {
        return CategorieDto.from(categorieService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        categorieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
