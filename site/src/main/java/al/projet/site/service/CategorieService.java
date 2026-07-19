package al.projet.site.service;

import al.projet.site.dto.CategorieRequest;
import al.projet.site.model.Categorie;
import al.projet.site.repository.CategorieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    public List<Categorie> lister() {
        return categorieRepository.findAll();
    }

    public Categorie trouver(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categorie introuvable : " + id));
    }

    @Transactional
    public Categorie creer(CategorieRequest request) {
        Categorie c = new Categorie();
        c.setNom(request.getNom());
        c.setDescription(request.getDescription());
        return categorieRepository.save(c);
    }

    @Transactional
    public Categorie modifier(Long id, CategorieRequest request) {
        Categorie c = trouver(id);
        c.setNom(request.getNom());
        c.setDescription(request.getDescription());
        return categorieRepository.save(c);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new IllegalArgumentException("Categorie introuvable : " + id);
        }
        // Les articles de cette categorie restent (categorie_id passe a NULL,
        // cf. ON DELETE SET NULL dans schema.sql).
        categorieRepository.deleteById(id);
    }
}
