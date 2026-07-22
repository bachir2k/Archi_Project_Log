package al.projet.site.service;

import al.projet.site.dto.UtilisateurForm;
import al.projet.site.model.Utilisateur;
import al.projet.site.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> lister() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur trouver(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    @Transactional
    public Utilisateur creer(UtilisateurForm form) {
        if (utilisateurRepository.findByLogin(form.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Ce login existe deja");
        }
        if (form.getMotDePasse() == null || form.getMotDePasse().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire a la creation");
        }

        Utilisateur u = new Utilisateur();
        u.setLogin(form.getLogin());
        u.setMotDePasse(passwordEncoder.encode(form.getMotDePasse()));
        u.setNom(form.getNom());
        u.setEmail(form.getEmail());
        u.setRole(Utilisateur.Role.valueOf(form.getRole()));
        return utilisateurRepository.save(u);
    }

    @Transactional
    public Utilisateur modifier(Long id, UtilisateurForm form) {
        Utilisateur u = trouver(id);
        u.setNom(form.getNom());
        u.setEmail(form.getEmail());
        u.setRole(Utilisateur.Role.valueOf(form.getRole()));
        if (form.getMotDePasse() != null && !form.getMotDePasse().isBlank()) {
            u.setMotDePasse(passwordEncoder.encode(form.getMotDePasse()));
        }
        return utilisateurRepository.save(u);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur introuvable : " + id);
        }
        utilisateurRepository.deleteById(id);
    }
}
