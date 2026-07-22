package al.projet.client.ui;

import al.projet.client.soap.SoapClientException;
import al.projet.client.soap.SoapUserClient;
import al.projet.client.soap.SoapUserClient.OperationResult;
import al.projet.client.soap.SoapUserClient.UtilisateurDto;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Ecran de gestion des utilisateurs (lister/ajouter/modifier/supprimer),
 * via le service SOAP, protege par le jeton fourni a la connexion.
 */
public class GestionUtilisateursFrame extends JFrame {

    private final SoapUserClient soapClient;
    private final String jeton;

    private final UtilisateurTableModel tableModel = new UtilisateurTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel statutLabel = new JLabel(" ");

    public GestionUtilisateursFrame(String soapEndpoint, String jeton) {
        super("Gestion des utilisateurs");
        this.soapClient = new SoapUserClient(soapEndpoint);
        this.jeton = jeton;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(600, 400));

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton rafraichirBtn = new JButton("Rafraichir");
        JButton ajouterBtn = new JButton("Ajouter");
        JButton modifierBtn = new JButton("Modifier");
        JButton supprimerBtn = new JButton("Supprimer");

        rafraichirBtn.addActionListener(e -> rafraichir());
        ajouterBtn.addActionListener(e -> ajouter());
        modifierBtn.addActionListener(e -> modifier());
        supprimerBtn.addActionListener(e -> supprimer());

        JPanel barreOutils = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barreOutils.add(rafraichirBtn);
        barreOutils.add(ajouterBtn);
        barreOutils.add(modifierBtn);
        barreOutils.add(supprimerBtn);
        add(barreOutils, BorderLayout.NORTH);

        statutLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statutLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        rafraichir();
    }

    private void rafraichir() {
        executerEnArrierePlan(
                () -> soapClient.lister(jeton),
                utilisateurs -> {
                    tableModel.setUtilisateurs(utilisateurs);
                    afficherStatut("Liste actualisee (" + utilisateurs.size() + " utilisateur(s)).", false);
                }
        );
    }

    private void ajouter() {
        UtilisateurFormDialog dialog = new UtilisateurFormDialog(this, null);
        UtilisateurFormDialog.Resultat saisie = dialog.afficher();
        if (saisie == null) return; // annule

        executerEnArrierePlan(
                () -> soapClient.ajouter(jeton, saisie.login(), saisie.nom(), saisie.email(), saisie.role()),
                resultat -> traiterResultatOperation(resultat, "Utilisateur cree.")
        );
    }

    private void modifier() {
        UtilisateurDto selection = utilisateurSelectionne();
        if (selection == null) return;

        UtilisateurFormDialog dialog = new UtilisateurFormDialog(this, selection);
        UtilisateurFormDialog.Resultat saisie = dialog.afficher();
        if (saisie == null) return;

        executerEnArrierePlan(
                () -> soapClient.modifier(jeton, selection.id(), selection.login(), saisie.nom(), saisie.email(), saisie.role()),
                resultat -> traiterResultatOperation(resultat, "Utilisateur modifie.")
        );
    }

    private void supprimer() {
        UtilisateurDto selection = utilisateurSelectionne();
        if (selection == null) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer l'utilisateur \"" + selection.login() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) return;

        executerEnArrierePlan(
                () -> soapClient.supprimer(jeton, selection.id()),
                resultat -> traiterResultatOperation(resultat, "Utilisateur supprime.")
        );
    }

    private UtilisateurDto utilisateurSelectionne() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this, "Selectionnez d'abord un utilisateur dans la liste.",
                    "Aucune selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableModel.getUtilisateurA(ligne);
    }

    private void traiterResultatOperation(OperationResult resultat, String messageSucces) {
        if (resultat.succes()) {
            afficherStatut(messageSucces, false);
            rafraichir();
        } else {
            afficherStatut("Echec : " + resultat.message(), true);
        }
    }

    /** Execute un appel SOAP bloquant hors de l'EDT, sans geler l'interface. */
    private <T> void executerEnArrierePlan(SoapCall<T> appel, java.util.function.Consumer<T> surSucces) {
        afficherStatut("Appel du service SOAP en cours...", false);
        new SwingWorker<T, Void>() {
            private SoapClientException erreur;

            @Override
            protected T doInBackground() {
                try {
                    return appel.executer();
                } catch (SoapClientException e) {
                    erreur = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (erreur != null) {
                    afficherStatut(erreur.getMessage(), true);
                    return;
                }
                try {
                    surSucces.accept(get());
                } catch (Exception e) {
                    afficherStatut("Erreur inattendue : " + e.getMessage(), true);
                }
            }
        }.execute();
    }

    private void afficherStatut(String message, boolean erreur) {
        statutLabel.setForeground(erreur ? Color.RED : Color.DARK_GRAY);
        statutLabel.setText(message);
    }

    /** Petit contrat fonctionnel pour les appels SOAP pouvant lever SoapClientException. */
    @FunctionalInterface
    private interface SoapCall<T> {
        T executer() throws SoapClientException;
    }
}
