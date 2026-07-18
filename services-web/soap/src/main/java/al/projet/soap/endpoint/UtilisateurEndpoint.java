package al.projet.soap.endpoint;

import al.projet.soap.generated.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Endpoint SOAP : authentification + CRUD utilisateurs.
 * Les classes AuthentifierRequest, ListerUtilisateursRequest, etc. sont
 * generees automatiquement a la compilation (mvn generate-sources) a
 * partir de utilisateurs.xsd -> ne pas les creer a la main.
 *
 * TODO Personne B :
 *  - brancher un vrai UtilisateurRepository (JPA) + verification du jeton
 *  - hasher/verifier le mot de passe (BCrypt)
 *  - retourner succes=false + message clair en cas d'erreur / jeton invalide
 */
@Endpoint
public class UtilisateurEndpoint {

    private static final String NAMESPACE = "http://al.projet/soap/utilisateurs";

    @PayloadRoot(namespace = NAMESPACE, localPart = "authentifierRequest")
    @ResponsePayload
    public AuthentifierResponse authentifier(@RequestPayload AuthentifierRequest request) {
        AuthentifierResponse response = new AuthentifierResponse();
        // TODO: verifier login/motDePasse en base (mot de passe hashe)
        response.setSucces(false);
        response.setMessage("Non implemente");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "listerUtilisateursRequest")
    @ResponsePayload
    public ListerUtilisateursResponse lister(@RequestPayload ListerUtilisateursRequest request) {
        ListerUtilisateursResponse response = new ListerUtilisateursResponse();
        // TODO: verifier le jeton (table `jeton`, actif=true), sinon lever une SOAP fault
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ajouterUtilisateurRequest")
    @ResponsePayload
    public AjouterUtilisateurResponse ajouter(@RequestPayload AjouterUtilisateurRequest request) {
        AjouterUtilisateurResponse response = new AjouterUtilisateurResponse();
        // TODO: verifier jeton + persister l'utilisateur
        response.setSucces(false);
        response.setMessage("Non implemente");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "modifierUtilisateurRequest")
    @ResponsePayload
    public ModifierUtilisateurResponse modifier(@RequestPayload ModifierUtilisateurRequest request) {
        ModifierUtilisateurResponse response = new ModifierUtilisateurResponse();
        response.setSucces(false);
        response.setMessage("Non implemente");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "supprimerUtilisateurRequest")
    @ResponsePayload
    public SupprimerUtilisateurResponse supprimer(@RequestPayload SupprimerUtilisateurRequest request) {
        SupprimerUtilisateurResponse response = new SupprimerUtilisateurResponse();
        response.setSucces(false);
        response.setMessage("Non implemente");
        return response;
    }
}
