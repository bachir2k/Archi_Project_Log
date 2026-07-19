package al.projet.rest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "article")
public class Article {

    @Id
    private Long id;

    @Column(length = 200)
    private String titre;
    @Column(length = 500)
    private String description;

    @Lob
    @Column(length = 65535)
    private String contenu;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getContenu() { return contenu; }
    public LocalDateTime getDatePublication() { return datePublication; }
    public Categorie getCategorie() { return categorie; }
}
