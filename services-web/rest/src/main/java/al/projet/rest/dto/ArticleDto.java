package al.projet.rest.dto;

import al.projet.rest.model.Article;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

@JacksonXmlRootElement(localName = "article")
public class ArticleDto {

    @JacksonXmlProperty(isAttribute = true)
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime datePublication;
    private String categorie;

    public static ArticleDto from(Article a) {
        ArticleDto dto = new ArticleDto();
        dto.id = a.getId();
        dto.titre = a.getTitre();
        dto.description = a.getDescription();
        dto.datePublication = a.getDatePublication();
        dto.categorie = a.getCategorie() != null ? a.getCategorie().getNom() : null;
        return dto;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDateTime getDatePublication() { return datePublication; }
    public String getCategorie() { return categorie; }
}
