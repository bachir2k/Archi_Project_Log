package al.projet.client.ui;

import al.projet.client.soap.SoapUserClient.UtilisateurDto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurTableModel extends AbstractTableModel {

    private static final String[] COLONNES = {"ID", "Login", "Nom", "Email", "Role"};

    private List<UtilisateurDto> utilisateurs = new ArrayList<>();

    public void setUtilisateurs(List<UtilisateurDto> utilisateurs) {
        this.utilisateurs = utilisateurs;
        fireTableDataChanged();
    }

    public UtilisateurDto getUtilisateurA(int ligne) {
        return utilisateurs.get(ligne);
    }

    @Override
    public int getRowCount() { return utilisateurs.size(); }

    @Override
    public int getColumnCount() { return COLONNES.length; }

    @Override
    public String getColumnName(int col) { return COLONNES[col]; }

    @Override
    public Object getValueAt(int ligne, int colonne) {
        UtilisateurDto u = utilisateurs.get(ligne);
        return switch (colonne) {
            case 0 -> u.id();
            case 1 -> u.login();
            case 2 -> u.nom();
            case 3 -> u.email();
            case 4 -> u.role();
            default -> "";
        };
    }
}
