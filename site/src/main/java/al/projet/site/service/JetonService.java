package al.projet.site.service;

import al.projet.site.model.Jeton;
import al.projet.site.model.Utilisateur;
import al.projet.site.repository.JetonRepository;
import al.projet.site.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Gere les jetons d'authentification permettant d'acceder au service SOAP
 * (CRUD utilisateurs). Seul un admin peut en generer/supprimer depuis le
 * back-office (cf. enonce du projet).
 */
@Service
public class JetonService {

    private final JetonRepository jetonRepository;
    private final UtilisateurRepository utilisateurRepository;

    public JetonService(JetonRepository jetonRepository, UtilisateurRepository utilisateurRepository) {
        this.jetonRepository = jetonRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Jeton> listerPour(Long utilisateurId) {
        return jetonRepository.findByUtilisateurIdOrderByDateCreationDesc(utilisateurId);
    }

    @Transactional
    public Jeton generer(Long utilisateurId) {
        Utilisateur u = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId));

        Jeton jeton = new Jeton();
        jeton.setUtilisateur(u);
        jeton.setValeur(UUID.randomUUID().toString());
        return jetonRepository.save(jeton);
    }

    @Transactional
    public void supprimer(Long jetonId) {
        if (!jetonRepository.existsById(jetonId)) {
            throw new IllegalArgumentException("Jeton introuvable : " + jetonId);
        }
        jetonRepository.deleteById(jetonId);
    }
}
