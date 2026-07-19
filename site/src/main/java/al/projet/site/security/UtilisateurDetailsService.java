package al.projet.site.security;

import al.projet.site.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Branche Spring Security sur la table "utilisateur" : au login, Spring
 * appelle loadUserByUsername(login), recupere le hash BCrypt stocke en base
 * et compare avec le mot de passe saisi (via le PasswordEncoder de SecurityConfig).
 */
@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu : " + login));
    }
}
