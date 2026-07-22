package al.projet.site.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UtilisateurForm {

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 50)
    private String login;

    /**
     * Obligatoire a la creation. Optionnel en modification :
     * laisser vide pour ne pas changer le mot de passe existant.
     */
    private String motDePasse;

    @Size(max = 100)
    private String nom;

    @Size(max = 150)
    private String email;

    @NotBlank(message = "Le role est obligatoire")
    private String role;

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
}
