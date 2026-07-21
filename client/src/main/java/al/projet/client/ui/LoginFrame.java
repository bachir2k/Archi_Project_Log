package al.projet.client.ui;

import al.projet.client.soap.SoapAuthClient;
import al.projet.client.soap.SoapClientException;

import javax.swing.*;
import java.awt.*;

/**
 * Ecran de login de l'application client.
 * Appelle reellement le service SOAP (operation authentifier) et verifie
 * que l'utilisateur a le role ADMIN avant de donner acces a la gestion
 * des utilisateurs (ecran complet prevu samedi, cf. roadmap).
 */
public class LoginFrame extends JFrame {

    // Adresse du service SOAP (module services-web/soap, port 8082 par defaut).
    private static final String SOAP_ENDPOINT = "http://localhost:8082/ws";

    private final JTextField loginField = new JTextField(15);
    private final JPasswordField motDePasseField = new JPasswordField(15);
    private final JButton connexionBtn = new JButton("Se connecter");
    private final JLabel statutLabel = new JLabel(" ");

    private final SoapAuthClient soapClient = new SoapAuthClient(SOAP_ENDPOINT);

    public LoginFrame() {
        super("Connexion - Gestion des utilisateurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Login :"), c);
        c.gridx = 1;
        add(loginField, c);

        c.gridx = 0; c.gridy = 1;
        add(new JLabel("Mot de passe :"), c);
        c.gridx = 1;
        add(motDePasseField, c);

        connexionBtn.addActionListener(e -> authentifier());
        c.gridx = 1; c.gridy = 2;
        add(connexionBtn, c);

        statutLabel.setForeground(Color.RED);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        add(statutLabel, c);

        // Entree valide le formulaire
        getRootPane().setDefaultButton(connexionBtn);

        pack();
        setMinimumSize(new Dimension(350, getHeight()));
        setLocationRelativeTo(null);
    }

    private void authentifier() {
        String login = loginField.getText().trim();
        String motDePasse = new String(motDePasseField.getPassword());

        if (login.isEmpty() || motDePasse.isEmpty()) {
            statutLabel.setText("Login et mot de passe obligatoires");
            return;
        }

        setFormulaireActif(false);
        statutLabel.setForeground(Color.DARK_GRAY);
        statutLabel.setText("Connexion en cours...");

        // Appel reseau -> hors de l'EDT (Event Dispatch Thread) via SwingWorker,
        // pour ne pas geler l'interface pendant l'appel SOAP.
        new SwingWorker<SoapAuthClient.AuthResult, Void>() {
            private SoapClientException erreur;

            @Override
            protected SoapAuthClient.AuthResult doInBackground() {
                try {
                    return soapClient.authentifier(login, motDePasse);
                } catch (SoapClientException e) {
                    erreur = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                setFormulaireActif(true);

                if (erreur != null) {
                    statutLabel.setForeground(Color.RED);
                    statutLabel.setText(erreur.getMessage());
                    return;
                }

                try {
                    SoapAuthClient.AuthResult resultat = get();
                    traiterResultat(resultat);
                } catch (Exception e) {
                    statutLabel.setForeground(Color.RED);
                    statutLabel.setText("Erreur inattendue : " + e.getMessage());
                }
            }
        }.execute();
    }

    private void traiterResultat(SoapAuthClient.AuthResult resultat) {
        if (!resultat.succes()) {
            statutLabel.setForeground(Color.RED);
            statutLabel.setText(resultat.message() != null ? resultat.message() : "Echec de connexion");
            return;
        }

        if (!resultat.estAdmin()) {
            statutLabel.setForeground(Color.RED);
            statutLabel.setText("Acces refuse : reserve aux administrateurs (role actuel : " + resultat.role() + ")");
            return;
        }

        // TODO (samedi) : ouvrir le veritable ecran de gestion des utilisateurs
        // (CRUD complet via SOAP, avec le jeton d'authentification).
        JOptionPane.showMessageDialog(this,
                "Connexion reussie en tant qu'ADMIN.\n"
                        + "L'ecran de gestion des utilisateurs sera branche samedi.",
                "Connexion reussie", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setFormulaireActif(boolean actif) {
        loginField.setEnabled(actif);
        motDePasseField.setEnabled(actif);
        connexionBtn.setEnabled(actif);
    }
}
