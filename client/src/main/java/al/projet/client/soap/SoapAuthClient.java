package al.projet.client.soap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Client SOAP "maison" pour l'operation authentifier : construit l'enveloppe
 * XML a la main et l'envoie en HTTP POST (voir SoapHttpUtil), plutot que de
 * dependre de classes generees par wsimport.
 *
 * Namespace et noms d'elements = ceux definis dans
 * services-web/soap/src/main/resources/utilisateurs.xsd
 */
public class SoapAuthClient {

    private static final String NAMESPACE = "http://al.projet/soap/utilisateurs";

    private final String endpointUrl;

    public SoapAuthClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    /**
     * Appelle l'operation SOAP "authentifier".
     * @throws SoapClientException si le serveur est injoignable ou renvoie une erreur inattendue
     */
    public AuthResult authentifier(String login, String motDePasse) throws SoapClientException {
        String requestXml = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <authentifierRequest xmlns="%s">
                      <login>%s</login>
                      <motDePasse>%s</motDePasse>
                    </authentifierRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(NAMESPACE, SoapHttpUtil.escapeXml(login), SoapHttpUtil.escapeXml(motDePasse));

        Document reponse = SoapHttpUtil.envoyer(endpointUrl, requestXml);

        NodeList succesNodes = reponse.getElementsByTagNameNS(NAMESPACE, "succes");
        NodeList roleNodes = reponse.getElementsByTagNameNS(NAMESPACE, "role");
        NodeList messageNodes = reponse.getElementsByTagNameNS(NAMESPACE, "message");

        boolean succes = succesNodes.getLength() > 0 && Boolean.parseBoolean(succesNodes.item(0).getTextContent());
        String role = roleNodes.getLength() > 0 ? roleNodes.item(0).getTextContent() : null;
        String message = messageNodes.getLength() > 0 ? messageNodes.item(0).getTextContent() : null;

        return new AuthResult(succes, role, message);
    }

    /** Resultat de l'appel authentifier(login, motDePasse). */
    public record AuthResult(boolean succes, String role, String message) {
        public boolean estAdmin() {
            return succes && "ADMIN".equals(role);
        }
    }
}
