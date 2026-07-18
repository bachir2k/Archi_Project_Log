package al.projet.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Ecran de login de l'application client.
 * TODO Personne B (vendredi) :
 *   - appeler le service SOAP genere par wsimport (operation authentifier)
 *   - si succes et role == ADMIN -> ouvrir GestionUtilisateursFrame
 *   - sinon -> afficher un message d'erreur
 */
public class LoginFrame extends JFrame {

    private final JTextField loginField = new JTextField(15);
    private final JPasswordField motDePasseField = new JPasswordField(15);

    public LoginFrame() {
        super("Connexion - Gestion des utilisateurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Login :"), c);
        c.gridx = 1;
        add(loginField, c);

        c.gridx = 0; c.gridy = 1;
        add(new JLabel("Mot de passe :"), c);
        c.gridx = 1;
        add(motDePasseField, c);

        JButton connexionBtn = new JButton("Se connecter");
        connexionBtn.addActionListener(e -> authentifier());
        c.gridx = 1; c.gridy = 2;
        add(connexionBtn, c);

        pack();
        setLocationRelativeTo(null);
    }

    private void authentifier() {
        String login = loginField.getText();
        String motDePasse = new String(motDePasseField.getPassword());

        // TODO: remplacer par l'appel reel au service SOAP (wsimport)
        JOptionPane.showMessageDialog(this,
                "TODO: appeler le service SOAP authentifier(" + login + ", ****)");
    }
}
