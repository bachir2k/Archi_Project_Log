package al.projet.soap.service;

import al.projet.soap.model.JetonEntity;
import al.projet.soap.model.UtilisateurEntity;
import al.projet.soap.repository.JetonRepository;
import al.projet.soap.repository.UtilisateurRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final JetonRepository jetonRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UtilisateurRepository utilisateurRepository, JetonRepository jetonRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.jetonRepository = jetonRepository;
    }

    /** Verifie le couple login/mot de passe. Retourne l'utilisateur si ok. */
    public Optional<UtilisateurEntity> authentifier(String login, String motDePasse) {
        return utilisateurRepository.findByLogin(login)
                .filter(u -> passwordEncoder.matches(motDePasse, u.getMotDePasse()));
    }

    /**
     * Verifie qu'un jeton est valide (existe, actif, non expire).
     * Utilise pour proteger l'acces SOAP au CRUD utilisateurs.
     */
    public boolean jetonValide(String valeurJeton) {
        if (valeurJeton == null || valeurJeton.isBlank()) {
            return false;
        }
        return jetonRepository.findByValeur(valeurJeton)
                .map(JetonEntity::estValide)
                .orElse(false);
    }

    /** Verifie que le jeton est valide ET appartient a un utilisateur ADMIN. */
    public boolean jetonAdminValide(String valeurJeton) {
        if (valeurJeton == null || valeurJeton.isBlank()) {
            return false;
        }
        return jetonRepository.findByValeur(valeurJeton)
                .filter(JetonEntity::estValide)
                .map(j -> "ADMIN".equals(j.getUtilisateur().getRole()))
                .orElse(false);
    }
}
