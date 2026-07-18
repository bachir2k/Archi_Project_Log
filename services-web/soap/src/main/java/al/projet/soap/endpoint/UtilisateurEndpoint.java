package al.projet.soap.endpoint;

import al.projet.soap.generated.*;
import al.projet.soap.model.UtilisateurEntity;
import al.projet.soap.service.AuthService;
import al.projet.soap.service.JetonInvalideException;
import al.projet.soap.service.UtilisateurService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

/**
 * Endpoint SOAP : authentification + CRUD utilisateurs.
 *
 * - authentifier(login, motDePasse) : public, pas de jeton requis.
 * - lister/ajouter/modifier/supprimer : proteges par jeton (verifie via
 *   AuthService.jetonAdminValide -> seul un admin peut gerer les utilisateurs,
 *   conformement a l'enonce).
 *
 * Les classes AuthentifierRequest, ListerUtilisateursRequest, etc. sont
 * generees automatiquement a la compilation (mvn generate-sources) a partir
 * de utilisateurs.xsd -> ne pas les creer/editer a la main.
 */
@Endpoint
public class UtilisateurEndpoint {

    private static final String NAMESPACE = "http://al.projet/soap/utilisateurs";

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    public UtilisateurEndpoint(AuthService authService, UtilisateurService utilisateurService) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "authentifierRequest")
    @ResponsePayload
    public AuthentifierResponse authentifier(@RequestPayload AuthentifierRequest request) {
        AuthentifierResponse response = new AuthentifierResponse();

        authService.authentifier(request.getLogin(), request.getMotDePasse())
                .ifPresentOrElse(
                        u -> {
                            response.setSucces(true);
                            response.setRole(u.getRole());
                        },
                        () -> {
                            response.setSucces(false);
                            response.setMessage("Login ou mot de passe incorrect");
                        }
                );

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "listerUtilisateursRequest")
    @ResponsePayload
    public ListerUtilisateursResponse lister(@RequestPayload ListerUtilisateursRequest request) {
        verifierJetonAdmin(request.getJeton());

        ListerUtilisateursResponse response = new ListerUtilisateursResponse();
        List<UtilisateurEntity> utilisateurs = utilisateurService.lister();
        for (UtilisateurEntity u : utilisateurs) {
            response.getUtilisateur().add(versDto(u));
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ajouterUtilisateurRequest")
    @ResponsePayload
    public AjouterUtilisateurResponse ajouter(@RequestPayload AjouterUtilisateurRequest request) {
        AjouterUtilisateurResponse response = new AjouterUtilisateurResponse();
        verifierJetonAdmin(request.getJeton());

        try {
            Utilisateur dto = request.getUtilisateur();
            // Mot de passe temporaire en dur (voir UtilisateurService.ajouter)
            UtilisateurEntity cree = utilisateurService.ajouter(
                    dto.getLogin(), "changeMe123", dto.getNom(), dto.getEmail(), dto.getRole());
            response.setSucces(true);
            response.setId(cree.getId());
        } catch (IllegalArgumentException e) {
            response.setSucces(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "modifierUtilisateurRequest")
    @ResponsePayload
    public ModifierUtilisateurResponse modifier(@RequestPayload ModifierUtilisateurRequest request) {
        ModifierUtilisateurResponse response = new ModifierUtilisateurResponse();
        verifierJetonAdmin(request.getJeton());

        try {
            Utilisateur dto = request.getUtilisateur();
            utilisateurService.modifier(dto.getId(), dto.getNom(), dto.getEmail(), dto.getRole());
            response.setSucces(true);
        } catch (IllegalArgumentException e) {
            response.setSucces(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "supprimerUtilisateurRequest")
    @ResponsePayload
    public SupprimerUtilisateurResponse supprimer(@RequestPayload SupprimerUtilisateurRequest request) {
        SupprimerUtilisateurResponse response = new SupprimerUtilisateurResponse();
        verifierJetonAdmin(request.getJeton());

        try {
            utilisateurService.supprimer(request.getId());
            response.setSucces(true);
        } catch (IllegalArgumentException e) {
            response.setSucces(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    private void verifierJetonAdmin(String jeton) {
        if (!authService.jetonAdminValide(jeton)) {
            throw new JetonInvalideException();
        }
    }

    private Utilisateur versDto(UtilisateurEntity u) {
        Utilisateur dto = new Utilisateur();
        dto.setId(u.getId());
        dto.setLogin(u.getLogin());
        dto.setNom(u.getNom());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        return dto;
    }
}
