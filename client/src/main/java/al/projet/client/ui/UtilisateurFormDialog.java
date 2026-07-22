package al.projet.client.ui;

import al.projet.client.soap.SoapUserClient.UtilisateurDto;

import javax.swing.*;
import java.awt.*;

/**
 * Boite de dialogue modale pour creer ou modifier un utilisateur.
 * Le login n'est modifiable qu'a la creation (coherent avec le back-office
 * du site, ou le login est aussi fige apres creation).
 */
public class UtilisateurFormDialog extends JDialog {

    private static final String[] ROLES = {"VISITEUR", "EDITEUR", "ADMIN"};

    private final JTextField loginField = new JTextField(18);
    private final JTextField nomField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JComboBox<String> roleCombo = new JComboBox<>(ROLES);

    private boolean valide = false;

    public UtilisateurFormDialog(Frame parent, UtilisateurDto utilisateurExistant) {
        super(parent, utilisateurExistant == null ? "Nouvel utilisateur" : "Modifier l'utilisateur", true);

        boolean modeCreation = utilisateurExistant == null;
        if (!modeCreation) {
            loginField.setText(utilisateurExistant.login());
            loginField.setEnabled(false);
            nomField.setText(utilisateurExistant.nom());
            emailField.setText(utilisateurExistant.email());
            roleCombo.setSelectedItem(utilisateurExistant.role());
        }

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Login :"), c);
        c.gridx = 1;
        add(loginField, c);

        c.gridx = 0; c.gridy = 1;
        add(new JLabel("Nom :"), c);
        c.gridx = 1;
        add(nomField, c);

        c.gridx = 0; c.gridy = 2;
        add(new JLabel("Email :"), c);
        c.gridx = 1;
        add(emailField, c);

        c.gridx = 0; c.gridy = 3;
        add(new JLabel("Role :"), c);
        c.gridx = 1;
        add(roleCombo, c);

        if (modeCreation) {
            JLabel info = new JLabel("<html><i>Mot de passe temporaire attribue automatiquement par le serveur.</i></html>");
            info.setForeground(Color.GRAY);
            c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
            add(info, c);
        }

        JButton valider = new JButton(modeCreation ? "Creer" : "Enregistrer");
        JButton annuler = new JButton("Annuler");
        valider.addActionListener(e -> { valide = true; setVisible(false); });
        annuler.addActionListener(e -> { valide = false; setVisible(false); });

        JPanel boutons = new JPanel();
        boutons.add(valider);
        boutons.add(annuler);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2;
        add(boutons, c);

        getRootPane().setDefaultButton(valider);
        pack();
        setLocationRelativeTo(parent);
    }

    /** Affiche la boite modale et retourne le resultat, ou null si annule. */
    public Resultat afficher() {
        setVisible(true); // bloque jusqu'a la fermeture (dialogue modal)
        if (!valide) return null;
        return new Resultat(loginField.getText().trim(), nomField.getText().trim(),
                emailField.getText().trim(), (String) roleCombo.getSelectedItem());
    }

    public record Resultat(String login, String nom, String email, String role) {}
}
