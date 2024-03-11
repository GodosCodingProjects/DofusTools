package View.MainFrame.CreationPage;

import Model.CommDB;
import Model.Enums;
import Model.Item;
import View.Const;
import gswing.GSearchField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ItemCreator extends JPanel {
    private static final int N_COLUMNS = 3;

    JTextField nameField;
    JTextField priceField;
    JComboBox typeSelector;

    public ItemCreator() {
        init();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMaximumSize(new Dimension(Const.WINDOW_WIDTH, Const.ROW_HEIGHT * 5));

        JPanel nameRowPanel = new JPanel();
        nameRowPanel.setMaximumSize(Const.ROW_SIZE);
        nameRowPanel.setLayout(new BoxLayout(nameRowPanel, BoxLayout.LINE_AXIS));
        add(nameRowPanel);

        JLabel panelName = new JLabel("Créer une nouvelle ressource", JLabel.CENTER);
        panelName.setMaximumSize(Const.ROW_SIZE);
        nameRowPanel.add(panelName);

        JPanel mainRowPanel = new JPanel();
        mainRowPanel.setMaximumSize(Const.ROW_SIZE);
        mainRowPanel.setLayout(new BoxLayout(mainRowPanel, BoxLayout.LINE_AXIS));
        add(mainRowPanel);

        nameField = new GSearchField(false) {
            public ArrayList<String> getDictionary() {
                ArrayList<String> dictionary = new ArrayList<>();
                dictionary.addAll(CommDB.getItemNames());
                dictionary.addAll(CommDB.getCraftNames());
                return dictionary;
            }
        };
        nameField.setHorizontalAlignment(JTextField.CENTER);
        Const.SET_SIZE(nameField, Const.COLUMN_SIZE(N_COLUMNS));
        mainRowPanel.add(nameField);

        nameField.setLayout(new BoxLayout(nameField, BoxLayout.LINE_AXIS));
        JLabel nameLabel = new JLabel("Nom: ");
        nameField.add(nameLabel);

        priceField = new JTextField("0");
        priceField.setHorizontalAlignment(JTextField.CENTER);
        Const.SET_SIZE(priceField, Const.COLUMN_SIZE(N_COLUMNS));
        mainRowPanel.add(priceField);

        priceField.setLayout(new BoxLayout(priceField, BoxLayout.LINE_AXIS));
        JLabel priceLabel = new JLabel("Prix: ");
        priceField.add(priceLabel);

        typeSelector = new JComboBox(Enums.itemTypeNames);
        typeSelector.setSelectedIndex(Enums.ItemType.RESOURCE.ordinal());
        Const.SET_SIZE(typeSelector, Const.COLUMN_SIZE(N_COLUMNS));
        mainRowPanel.add(typeSelector);

        JButton btnCreate = new JButton("Créer");
        btnCreate.setMaximumSize(Const.COLUMN_SIZE(6));
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createItem();
            }
        });
        btnCreate.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        add(btnCreate);
    }

    private void createItem() {
        if(nameField.getText().trim().equals("")) {
            return;
        }

        Item newItem = null;
        try {
            newItem = new Item(
                nameField.getText(),
                Integer.parseInt(priceField.getText()), Integer.parseInt(priceField.getText()), Integer.parseInt(priceField.getText()),
                Enums.Preference.NEUTRAL,
                Enums.ItemType.values()[typeSelector.getSelectedIndex()]
            );
        } catch(NumberFormatException e) {
            return;
        }

        nameField.setText("");
        priceField.setText("0");

        if(CommDB.getItem(newItem.getName()) == null) {
            CommDB.getItems().add(newItem);
        }
        else {
            Const.message("Cette ressource existe déjà");
        }
    }
}
