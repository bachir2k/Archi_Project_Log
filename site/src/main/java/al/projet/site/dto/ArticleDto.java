package al.projet.site.dto;

import al.projet.site.model.Article;

import java.time.LocalDateTime;

public class ArticleDto {

    private Long id;
    private String titre;
    private String description;
    private String contenu;
    private LocalDateTime datePublication;
    private Long categorieId;
    private String categorieNom;
    private String auteurLogin;

    public static ArticleDto from(Article a) {
        ArticleDto dto = new ArticleDto();
        dto.id = a.getId();
        dto.titre = a.getTitre();
        dto.description = a.getDescription();
        dto.contenu = a.getContenu();
        dto.datePublication = a.getDatePublication();
        if (a.getCategorie() != null) {
            dto.categorieId = a.getCategorie().getId();
            dto.categorieNom = a.getCategorie().getNom();
        }
        if (a.getAuteur() != null) {
            dto.auteurLogin = a.getAuteur().getLogin();
        }
        return dto;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getContenu() { return contenu; }
    public LocalDateTime getDatePublication() { return datePublication; }
    public Long getCategorieId() { return categorieId; }
    public String getCategorieNom() { return categorieNom; }
    public String getAuteurLogin() { return auteurLogin; }
}
