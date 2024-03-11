package View.MainFrame.CraftsPage;

import Model.*;
import View.Const;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class CraftRow extends JPanel implements Observer {
    private static final int COLUMN_NB = 10;
    private static final int SMALL_COLUMN_NB = 2 * COLUMN_NB;
    private static final float LARGE_COLUMN_NB = COLUMN_NB / 2;

    private JPanel parent;
    private Craft craft;

    private JButton btnName;
    private JTextField priceField;
    private JTextField costField;
    private JTextField ratioField;

    private JTextField craftAmountField;
    private int craftAmount;

    private JComboBox jobSelector;
    private JComboBox typeSelector;

    private JPanel recipePanel;

    public CraftRow(Craft craft, JPanel parent) {
        this.craft = craft;
        this.craft.addObserver(this);
        this.parent = parent;

        craftAmount = 0;

        init();
        initRecipe();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel mainRowPanel = new JPanel();
        mainRowPanel.setLayout(new BoxLayout(mainRowPanel, BoxLayout.LINE_AXIS));
        Const.SET_SIZE(mainRowPanel, Const.ROW_SIZE);
        setBorder(new EmptyBorder(10, 0, 0, 0));
        add(mainRowPanel);

        JButton btnShowRecipe = new JButton("+");
        btnShowRecipe.setMaximumSize(Const.BTN_SIZE);
        btnShowRecipe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(recipePanel != null) {
                    if (recipePanel.isVisible()) {
                        recipePanel.setVisible(false);
                        btnShowRecipe.setText("+");
                    }
                    else {
                        recipePanel.setVisible(true);
                        btnShowRecipe.setText("\u2013");
                    }
                }
            }
        });
        Const.SET_SIZE(btnShowRecipe, Const.BTN_SIZE);
        mainRowPanel.add(btnShowRecipe);

        btnName = new JButton();
        btnName.addKeyListener(new SaveKeyListener(this));
        switch(craft.getPreference()) {
            case FAVORED:
                btnName.setForeground(Color.GREEN);
                break;
            case UNFAVORED:
                btnName.setForeground(Color.RED);
                break;
            default:
        }
        btnName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch(craft.getPreference()) {
                    case FAVORED:
                        craft.setPreference(Enums.Preference.UNFAVORED);
                        btnName.setForeground(Color.RED);
                        break;
                    case UNFAVORED:
                        craft.setPreference(Enums.Preference.NEUTRAL);
                        btnName.setForeground(getForeground());
                        break;
                    default:
                        craft.setPreference(Enums.Preference.FAVORED);
                        btnName.setForeground(Color.GREEN);
                        break;
                }
            }
        });
        Const.SET_SIZE(btnName, Const.COLUMN_SIZE(LARGE_COLUMN_NB));
        mainRowPanel.add(btnName);

        priceField = new JTextField();
        priceField.setHorizontalAlignment(JTextField.CENTER);
        priceField.addFocusListener(new EditableFieldListener());
        priceField.addKeyListener(new SaveKeyListener(this));
        Const.SET_SIZE(priceField, Const.COLUMN_SIZE(COLUMN_NB));
        mainRowPanel.add(priceField);

        costField = new JTextField();
        costField.setHorizontalAlignment(JTextField.CENTER);
        costField.setEditable(false);
        Const.SET_SIZE(costField, Const.COLUMN_SIZE(COLUMN_NB));
        mainRowPanel.add(costField);

        ratioField = new JTextField();
        ratioField.setHorizontalAlignment(JTextField.CENTER);
        ratioField.setEditable(false);
        Const.SET_SIZE(ratioField, Const.COLUMN_SIZE(COLUMN_NB));
        ratioField.setLayout(new BoxLayout(ratioField, BoxLayout.LINE_AXIS));
        ratioField.add(new JLabel("ratio: "));
        mainRowPanel.add(ratioField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        Const.SET_SIZE(buttonPanel, Const.COLUMN_SIZE(COLUMN_NB / 4));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        mainRowPanel.add(buttonPanel);

        JButton btnPlus1 = new JButton("+1");
        Const.SET_SIZE(btnPlus1, Const.COLUMN_SIZE(SMALL_COLUMN_NB));
        btnPlus1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCraftAmount(1);
            }
        });
        buttonPanel.add(btnPlus1);

        JButton btnPlus2 = new JButton("+2");
        Const.SET_SIZE(btnPlus2, Const.COLUMN_SIZE(SMALL_COLUMN_NB));
        btnPlus2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCraftAmount(2);
            }
        });
        buttonPanel.add(btnPlus2);

        JButton btnPlus5 = new JButton("+5");
        Const.SET_SIZE(btnPlus5, Const.COLUMN_SIZE(SMALL_COLUMN_NB));
        btnPlus5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCraftAmount(5);
            }
        });
        buttonPanel.add(btnPlus5);

        craftAmountField = new JTextField(Integer.toString(craftAmount));
        Const.SET_SIZE(craftAmountField, Const.COLUMN_SIZE(COLUMN_NB));
        buttonPanel.add(craftAmountField);

        jobSelector = new JComboBox(Enums.jobNames);
        jobSelector.setSelectedIndex(craft.getJob().ordinal());
        jobSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                craft.setJob(Enums.Job.values()[jobSelector.getSelectedIndex()]);
                updateUI();
            }
        });
        buttonPanel.add(jobSelector);

        typeSelector = new JComboBox(Enums.itemTypeNames);
        typeSelector.setSelectedIndex(craft.getType().ordinal());
        typeSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                craft.setType(Enums.ItemType.values()[typeSelector.getSelectedIndex()]);
                updateUI();
            }
        });
        buttonPanel.add(typeSelector);

        updateText();

        // Make a right-click context menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteOption = new JMenuItem("Supprimer");
        deleteOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        popupMenu.add(deleteOption);
        add(popupMenu);

        btnName.addMouseListener(new PopupMenuListener(popupMenu));
        priceField.addMouseListener(new PopupMenuListener(popupMenu));
        costField.addMouseListener(new PopupMenuListener(popupMenu));
        ratioField.addMouseListener(new PopupMenuListener(popupMenu));
        addMouseListener(new PopupMenuListener(popupMenu));
    }

    private void initRecipe() {
        recipePanel = new JPanel();
        recipePanel.setVisible(false);
        recipePanel.setLayout(new BoxLayout(recipePanel, BoxLayout.PAGE_AXIS));
        add(recipePanel);

        JPanel labelPanel = new JPanel();
        labelPanel.setMaximumSize(new Dimension(Const.WINDOW_WIDTH, Const.ROW_HEIGHT/2));
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
        recipePanel.add(labelPanel);

        JPanel buffer = new JPanel();
        buffer.setMaximumSize(Const.BTN_SIZE);
        labelPanel.add(buffer);

        JLabel nameLabel = new JLabel("Nom", JLabel.CENTER);
        nameLabel.setMaximumSize(Const.COLUMN_SIZE(4));
        labelPanel.add(nameLabel);

        JLabel priceLabel = new JLabel("Prix", JLabel.CENTER);
        priceLabel.setMaximumSize(Const.COLUMN_SIZE(4));
        labelPanel.add(priceLabel);

        JLabel quantityLabel = new JLabel("Quantité", JLabel.CENTER);
        quantityLabel.setMaximumSize(Const.COLUMN_SIZE(4));
        labelPanel.add(quantityLabel);

        JLabel costLabel = new JLabel("Coût", JLabel.CENTER);
        costLabel.setMaximumSize(Const.COLUMN_SIZE(4));
        labelPanel.add(costLabel);

        for(Ingredient ingredient : craft.getCraftIngredients()) {
            recipePanel.add(new IngredientRow(ingredient, getBackground(), craft, this));
        }
    }

    private void save() {
        craft.setPrice(Integer.parseInt(priceField.getText()));
    }

    private void delete() {
        CommDB.remove(craft);
        parent.updateUI();
    }

    public void updateText() {
        btnName.setText(craft.getName());
        priceField.setText(Integer.toString(craft.getPrice()));
        costField.setText(Integer.toString(craft.getCost()));
        ratioField.setText(String.format("%.2f", craft.getRatio()));
    }

    public void update(Observable o, Object arg) {
        updateText();
    }

    public void addCraftAmount(int amount) {
        craftAmount += amount;
        craftAmountField.setText(Integer.toString(craftAmount));
    }

    public Craft getCraft() {
        return craft;
    };

    public int getAmount() {
        return Integer.parseInt(craftAmountField.getText());
    }

    class PopupMenuListener extends MouseAdapter {
        private JPopupMenu popupMenu;

        public PopupMenuListener(JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }

        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private class EditableFieldListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            save();
        }
    }

    private class SaveKeyListener extends KeyAdapter {
        private JComponent focusGrabber;

        public SaveKeyListener(JComponent focusGrabber) {
            this.focusGrabber = focusGrabber;
        }

        public void keyReleased(KeyEvent e) {
            switch(e.getKeyChar()) {
                case KeyEvent.VK_ENTER:
                    focusGrabber.requestFocus();
                    break;
                case KeyEvent.VK_ESCAPE:
                    updateText();
                    focusGrabber.requestFocus();
                default:
            }
        }
    }
}
