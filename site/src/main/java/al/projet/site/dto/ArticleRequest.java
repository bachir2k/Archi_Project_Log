package al.projet.site.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArticleRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200)
    private String titre;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    /** Optionnel : un article peut ne pas avoir de categorie. */
    private Long categorieId;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }
}
