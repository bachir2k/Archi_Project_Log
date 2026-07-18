package al.projet.soap.service;

import al.projet.soap.model.UtilisateurEntity;
import al.projet.soap.repository.UtilisateurRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<UtilisateurEntity> lister() {
        return utilisateurRepository.findAll();
    }

    /**
     * Ajoute un utilisateur. Mot de passe temporaire en dur pour l'instant
     * (le XSD ne porte pas de champ mot de passe a la creation) : l'utilisateur
     * devra le changer a la premiere connexion si vous ajoutez cette fonctionnalite.
     */
    @Transactional
    public UtilisateurEntity ajouter(String login, String motDePasseClair, String nom, String email, String role) {
        if (utilisateurRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Ce login existe deja");
        }
        UtilisateurEntity u = new UtilisateurEntity();
        u.setLogin(login);
        u.setMotDePasse(passwordEncoder.encode(motDePasseClair));
        u.setNom(nom);
        u.setEmail(email);
        u.setRole(role != null ? role : "VISITEUR");
        return utilisateurRepository.save(u);
    }

    @Transactional
    public void modifier(Long id, String nom, String email, String role) {
        UtilisateurEntity u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        if (nom != null) u.setNom(nom);
        if (email != null) u.setEmail(email);
        if (role != null) u.setRole(role);
        utilisateurRepository.save(u);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur introuvable : " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    public Optional<UtilisateurEntity> trouver(Long id) {
        return utilisateurRepository.findById(id);
    }
}
