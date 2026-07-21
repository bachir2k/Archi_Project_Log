package al.projet.client.soap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Client SOAP "maison" : construit l'enveloppe XML a la main et l'envoie en
 * HTTP POST, plutot que de dependre de classes generees par wsimport.
 *
 * Avantage pour ce projet : le module client peut se compiler independamment,
 * sans que le service SOAP (module soap/) ait besoin de tourner au moment du
 * build. Il doit juste tourner au moment de l'EXECUTION (pour que le login
 * fonctionne reellement).
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
                """.formatted(NAMESPACE, escapeXml(login), escapeXml(motDePasse));

        Document reponse = envoyer(requestXml);
        verifierFault(reponse);

        NodeList succesNodes = reponse.getElementsByTagNameNS(NAMESPACE, "succes");
        NodeList roleNodes = reponse.getElementsByTagNameNS(NAMESPACE, "role");
        NodeList messageNodes = reponse.getElementsByTagNameNS(NAMESPACE, "message");

        boolean succes = succesNodes.getLength() > 0 && Boolean.parseBoolean(succesNodes.item(0).getTextContent());
        String role = roleNodes.getLength() > 0 ? roleNodes.item(0).getTextContent() : null;
        String message = messageNodes.getLength() > 0 ? messageNodes.item(0).getTextContent() : null;

        return new AuthResult(succes, role, message);
    }

    private Document envoyer(String requestXml) throws SoapClientException {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.setRequestMethod("POST");
            connexion.setDoOutput(true);
            connexion.setConnectTimeout(5000);
            connexion.setReadTimeout(5000);
            connexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connexion.setRequestProperty("SOAPAction", "\"\"");

            try (OutputStream out = connexion.getOutputStream()) {
                out.write(requestXml.getBytes(StandardCharsets.UTF_8));
            }

            int code = connexion.getResponseCode();
            // En cas de SOAP Fault, Spring-WS repond avec un code 500 mais un
            // corps XML exploitable -> on lit le flux d'erreur dans ce cas.
            InputStream flux = (code >= 200 && code < 300)
                    ? connexion.getInputStream()
                    : connexion.getErrorStream();

            if (flux == null) {
                throw new SoapClientException("Le serveur SOAP a repondu sans contenu (code " + code + ")");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(flux);

        } catch (IOException e) {
            throw new SoapClientException(
                    "Impossible de contacter le service SOAP (" + endpointUrl + "). "
                            + "Verifiez qu'il est bien lance. Detail : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SoapClientException("Reponse SOAP illisible : " + e.getMessage(), e);
        }
    }

    private void verifierFault(Document reponse) throws SoapClientException {
        NodeList faults = reponse.getElementsByTagNameNS("*", "Fault");
        if (faults.getLength() > 0) {
            Element fault = (Element) faults.item(0);
            NodeList reasons = fault.getElementsByTagNameNS("*", "Reason");
            NodeList faultstrings = fault.getElementsByTagNameNS("*", "faultstring");
            String texte = reasons.getLength() > 0 ? reasons.item(0).getTextContent()
                    : faultstrings.getLength() > 0 ? faultstrings.item(0).getTextContent()
                    : "Erreur SOAP inconnue";
            throw new SoapClientException(texte.trim());
        }
    }

    private static String escapeXml(String valeur) {
        if (valeur == null) return "";
        return valeur.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /** Resultat de l'appel authentifier(login, motDePasse). */
    public record AuthResult(boolean succes, String role, String message) {
        public boolean estAdmin() {
            return succes && "ADMIN".equals(role);
        }
    }
}
