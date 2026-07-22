package al.projet.client.soap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Client SOAP "maison" pour le CRUD utilisateurs (lister/ajouter/modifier/
 * supprimer), protege par jeton d'authentification (genere par un admin
 * depuis le back-office du site, cf. /admin/utilisateurs).
 *
 * Namespace et noms d'elements = ceux definis dans
 * services-web/soap/src/main/resources/utilisateurs.xsd
 */
public class SoapUserClient {

    private static final String NAMESPACE = "http://al.projet/soap/utilisateurs";

    private final String endpointUrl;

    public SoapUserClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public List<UtilisateurDto> lister(String jeton) throws SoapClientException {
        String requestXml = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <listerUtilisateursRequest xmlns="%s">
                      <jeton>%s</jeton>
                    </listerUtilisateursRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(NAMESPACE, SoapHttpUtil.escapeXml(jeton));

        Document reponse = SoapHttpUtil.envoyer(endpointUrl, requestXml);
        NodeList utilisateurs = reponse.getElementsByTagNameNS(NAMESPACE, "utilisateur");

        List<UtilisateurDto> resultat = new ArrayList<>();
        for (int i = 0; i < utilisateurs.getLength(); i++) {
            Element u = (Element) utilisateurs.item(i);
            resultat.add(new UtilisateurDto(
                    parseLong(SoapHttpUtil.texteEnfant(u, "id")),
                    SoapHttpUtil.texteEnfant(u, "login"),
                    SoapHttpUtil.texteEnfant(u, "nom"),
                    SoapHttpUtil.texteEnfant(u, "email"),
                    SoapHttpUtil.texteEnfant(u, "role")
            ));
        }
        return resultat;
    }

    public OperationResult ajouter(String jeton, String login, String nom, String email, String role)
            throws SoapClientException {
        String requestXml = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <ajouterUtilisateurRequest xmlns="%s">
                      <jeton>%s</jeton>
                      <utilisateur>
                        <login>%s</login>
                        <nom>%s</nom>
                        <email>%s</email>
                        <role>%s</role>
                      </utilisateur>
                    </ajouterUtilisateurRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(NAMESPACE, SoapHttpUtil.escapeXml(jeton),
                SoapHttpUtil.escapeXml(login), SoapHttpUtil.escapeXml(nom),
                SoapHttpUtil.escapeXml(email), SoapHttpUtil.escapeXml(role));

        Document reponse = SoapHttpUtil.envoyer(endpointUrl, requestXml);
        return lireResultat(reponse);
    }

    /** login = login actuel de l'utilisateur (champ obligatoire du contrat XSD, non utilise pour la modif). */
    public OperationResult modifier(String jeton, Long id, String login, String nom, String email, String role)
            throws SoapClientException {
        String requestXml = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <modifierUtilisateurRequest xmlns="%s">
                      <jeton>%s</jeton>
                      <utilisateur>
                        <id>%d</id>
                        <login>%s</login>
                        <nom>%s</nom>
                        <email>%s</email>
                        <role>%s</role>
                      </utilisateur>
                    </modifierUtilisateurRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(NAMESPACE, SoapHttpUtil.escapeXml(jeton), id,
                SoapHttpUtil.escapeXml(login), SoapHttpUtil.escapeXml(nom),
                SoapHttpUtil.escapeXml(email), SoapHttpUtil.escapeXml(role));

        Document reponse = SoapHttpUtil.envoyer(endpointUrl, requestXml);
        return lireResultat(reponse);
    }

    public OperationResult supprimer(String jeton, Long id) throws SoapClientException {
        String requestXml = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <supprimerUtilisateurRequest xmlns="%s">
                      <jeton>%s</jeton>
                      <id>%d</id>
                    </supprimerUtilisateurRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(NAMESPACE, SoapHttpUtil.escapeXml(jeton), id);

        Document reponse = SoapHttpUtil.envoyer(endpointUrl, requestXml);
        return lireResultat(reponse);
    }

    private OperationResult lireResultat(Document reponse) {
        NodeList succesNodes = reponse.getElementsByTagNameNS(NAMESPACE, "succes");
        NodeList idNodes = reponse.getElementsByTagNameNS(NAMESPACE, "id");
        NodeList messageNodes = reponse.getElementsByTagNameNS(NAMESPACE, "message");

        boolean succes = succesNodes.getLength() > 0 && Boolean.parseBoolean(succesNodes.item(0).getTextContent());
        Long id = idNodes.getLength() > 0 ? parseLong(idNodes.item(0).getTextContent()) : null;
        String message = messageNodes.getLength() > 0 ? messageNodes.item(0).getTextContent() : null;

        return new OperationResult(succes, id, message);
    }

    private static Long parseLong(String valeur) {
        try {
            return valeur != null ? Long.valueOf(valeur) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Utilisateur tel que renvoye par le service SOAP. */
    public record UtilisateurDto(Long id, String login, String nom, String email, String role) {}

    /** Resultat commun aux operations ajouter/modifier/supprimer. */
    public record OperationResult(boolean succes, Long id, String message) {}
}
