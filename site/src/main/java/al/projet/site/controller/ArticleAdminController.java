package al.projet.site.controller;

import al.projet.site.dto.ArticleDto;
import al.projet.site.dto.ArticleRequest;
import al.projet.site.security.UtilisateurPrincipal;
import al.projet.site.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * API interne de gestion des articles, reservee aux editeurs/admins
 * (voir SecurityConfig : "/editeur/**" -> hasAnyRole("EDITEUR", "ADMIN")).
 */
@RestController
@RequestMapping("/editeur/api/articles")
public class ArticleAdminController {

    private final ArticleService articleService;

    public ArticleAdminController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public Page<ArticleDto> lister(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int taille) {
        Pageable pageable = PageRequest.of(page, taille);
        return articleService.lister(pageable).map(ArticleDto::from);
    }

    @GetMapping("/{id}")
    public ArticleDto trouver(@PathVariable Long id) {
        return ArticleDto.from(articleService.trouver(id));
    }

    @PostMapping
    public ResponseEntity<ArticleDto> creer(@Valid @RequestBody ArticleRequest request,
                                              @AuthenticationPrincipal UtilisateurPrincipal principal) {
        ArticleDto dto = ArticleDto.from(articleService.creer(request, principal.getUtilisateur()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ArticleDto modifier(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        return ArticleDto.from(articleService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        articleService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
