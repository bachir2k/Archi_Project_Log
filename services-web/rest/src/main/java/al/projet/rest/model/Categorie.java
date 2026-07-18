package al.projet.rest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    private Long id;

    private String nom;
    private String description;

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
}
