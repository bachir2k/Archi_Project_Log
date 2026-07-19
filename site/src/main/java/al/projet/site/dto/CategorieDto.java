package al.projet.site.dto;

import al.projet.site.model.Categorie;

public class CategorieDto {

    private Long id;
    private String nom;
    private String description;

    public static CategorieDto from(Categorie c) {
        CategorieDto dto = new CategorieDto();
        dto.id = c.getId();
        dto.nom = c.getNom();
        dto.description = c.getDescription();
        return dto;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
}
