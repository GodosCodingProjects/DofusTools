package View.MainFrame.CreationPage;

import Model.*;
import View.Const;
import gswing.GSearchField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CraftCreator extends JPanel {
    private static final int N_COLUMNS = 4;

    JTextField nameField;
    JTextField priceField;
    JComboBox typeSelector;
    JComboBox jobSelector;
    ArrayList<JTextField> ingredientNames;
    ArrayList<JTextField> ingredientQuantities;

    public CraftCreator() {
        init();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMaximumSize(new Dimension(Const.WINDOW_WIDTH, Const.ROW_HEIGHT * 10));

        JPanel nameRowPanel = new JPanel();
        nameRowPanel.setMaximumSize(Const.ROW_SIZE);
        nameRowPanel.setLayout(new BoxLayout(nameRowPanel, BoxLayout.LINE_AXIS));
        add(nameRowPanel);

        JLabel panelName = new JLabel("Créer un nouvel équipement", JLabel.CENTER);
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

        jobSelector = new JComboBox(Enums.jobNames);
        jobSelector.setSelectedIndex(Enums.Job.MODELER.ordinal());
        Const.SET_SIZE(jobSelector, Const.COLUMN_SIZE(N_COLUMNS));
        mainRowPanel.add(jobSelector);

        typeSelector = new JComboBox(Enums.itemTypeNames);
        typeSelector.setSelectedIndex(Enums.ItemType.EQUIPMENT.ordinal());
        Const.SET_SIZE(typeSelector, Const.COLUMN_SIZE(N_COLUMNS));
        mainRowPanel.add(typeSelector);

        ingredientNames = new ArrayList(8);
        ingredientQuantities = new ArrayList(8);

        for(int i = 0; i < 8; ++i) {
            JPanel currRowPanel = new JPanel();
            currRowPanel.setMaximumSize(Const.ROW_SIZE);
            currRowPanel.setLayout(new BoxLayout(currRowPanel, BoxLayout.LINE_AXIS));
            add(currRowPanel);

            JLabel currLabel = new JLabel("Ingrédient #" + (i + 1));
            currLabel.setHorizontalAlignment(JTextField.CENTER);
            currLabel.setMaximumSize(Const.COLUMN_SIZE(6));
            currRowPanel.add(currLabel);

            JTextField currName = new GSearchField(true) {
                public ArrayList<String> getDictionary() {
                    ArrayList<String> dictionary = new ArrayList<>();
                    dictionary.addAll(CommDB.getItemNames());
                    dictionary.addAll(CommDB.getCraftNames());
                    return dictionary;
                }
            };
            currName.setHorizontalAlignment(JTextField.CENTER);
            currName.setMaximumSize(Const.COLUMN_SIZE(2));
            currRowPanel.add(currName);
            ingredientNames.add(currName);

            currName.setLayout(new BoxLayout(currName, BoxLayout.LINE_AXIS));
            JLabel currNameLabel = new JLabel("Nom: ");
            currName.add(currNameLabel);

            JTextField currQuantity = new JTextField("1");
            currQuantity.setHorizontalAlignment(JTextField.CENTER);
            currQuantity.setMaximumSize(Const.COLUMN_SIZE(2));
            currRowPanel.add(currQuantity);
            ingredientQuantities.add(currQuantity);

            currQuantity.setLayout(new BoxLayout(currQuantity, BoxLayout.LINE_AXIS));
            JLabel currQuantityLabel = new JLabel("Quantité: ");
            currQuantity.add(currQuantityLabel);
        }

        JButton btnCreate = new JButton("Créer");
        btnCreate.setMaximumSize(Const.COLUMN_SIZE(6));
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createCraft();
            }
        });
        btnCreate.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        add(btnCreate);
    }

    private void createCraft() {
        if(nameField.getText().trim().equals("")) {
            return;
        }

        Craft newCraft;
        try {
            newCraft = new Craft(
                nameField.getText(),
                Integer.parseInt(priceField.getText()),
                Enums.Preference.NEUTRAL,
                Enums.ItemType.values()[typeSelector.getSelectedIndex()],
                Enums.Job.values()[jobSelector.getSelectedIndex()]
            );
        } catch(NumberFormatException e) {
            return;
        }

        for(int i = 0; i < 8; ++i) {
            try {
                if(!ingredientNames.get(i).getText().trim().equals("")) {
                    Item item = CommDB.getItem(ingredientNames.get(i).getText());
                    if(item == null) {
                        Craft craft = CommDB.getCraft(ingredientNames.get(i).getText());

                        if(craft == null) {
                            continue;
                        }
                        else {
                            newCraft.addIngredient(new Ingredient(
                                craft,
                                Integer.parseInt(ingredientQuantities.get(i).getText())
                            ));
                        }
                    }
                    else {
                        newCraft.addIngredient(new Ingredient(
                            item,
                            Integer.parseInt(ingredientQuantities.get(i).getText())
                        ));
                    }
                }
            } catch(NumberFormatException e) {
                continue;
            }
        }

        nameField.setText("");
        priceField.setText("0");
        for(int i = 0; i < 8; ++i) {
            ingredientNames.get(i).setText("");
            ingredientQuantities.get(i).setText("1");
        }

        CommDB.getCrafts().add(newCraft);
    }
}
