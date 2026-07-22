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
 * Fonctions communes utilisees par tous les clients SOAP "maison"
 * (SoapAuthClient, SoapUserClient) : envoi HTTP, parsing XML, gestion
 * des SOAP Faults et echappement des valeurs inserees dans le XML.
 */
final class SoapHttpUtil {

    private SoapHttpUtil() {}

    static Document envoyer(String endpointUrl, String requestXml) throws SoapClientException {
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
            Document reponse = builder.parse(flux);

            verifierFault(reponse);
            return reponse;

        } catch (SoapClientException e) {
            throw e;
        } catch (IOException e) {
            throw new SoapClientException(
                    "Impossible de contacter le service SOAP (" + endpointUrl + "). "
                            + "Verifiez qu'il est bien lance. Detail : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SoapClientException("Reponse SOAP illisible : " + e.getMessage(), e);
        }
    }

    private static void verifierFault(Document reponse) throws SoapClientException {
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

    static String texteEnfant(Element parent, String tagLocal) {
        NodeList nodes = parent.getElementsByTagNameNS("*", tagLocal);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
    }

    static String escapeXml(String valeur) {
        if (valeur == null) return "";
        return valeur.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
