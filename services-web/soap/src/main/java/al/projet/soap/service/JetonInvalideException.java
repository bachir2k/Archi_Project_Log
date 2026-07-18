package al.projet.soap.service;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "Jeton d'authentification invalide ou expire")
public class JetonInvalideException extends RuntimeException {
    public JetonInvalideException() {
        super("Jeton d'authentification invalide ou expire");
    }
}
