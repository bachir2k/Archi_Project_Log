package al.projet.soap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite JPA mappee sur la table "utilisateur".
 * Nommee UtilisateurEntity (et pas simplement "Utilisateur") pour ne pas
 * entrer en collision avec la classe generee par JAXB a partir du XSD
 * (al.projet.soap.generated.Utilisateur), utilisee elle pour le contrat SOAP.
 */
@Entity
@Table(name = "utilisateur")
public class UtilisateurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse; // hash BCrypt

    private String nom;
    private String email;

    @Column(nullable = false)
    private String role; // VISITEUR / EDITEUR / ADMIN

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
