package View.MainFrame.CraftsPage;

import Model.Craft;
import Model.Ingredient;
import Model.Item;
import View.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class IngredientRow extends JPanel implements Observer {
    private JPanel parent;
    private Ingredient ingredient;
    private Craft craft;

    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField costField;

    public IngredientRow(Ingredient ingredient, Color color, Craft craft, JPanel parent) {
        this.ingredient = ingredient;
        this.ingredient.addObserver(this);

        this.parent = parent;

        init(color);
    }

    private void init(Color color) {
        setMaximumSize(Const.ROW_SIZE);
        setBackground(color);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel buffer = new JPanel();
        buffer.setMaximumSize(Const.BTN_SIZE);
        add(buffer);

        JPanel row = new JPanel();
        row.setMaximumSize(Const.ROW_SIZE);
        row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
        add(row);

        nameField = new JTextField();
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setEditable(false);
        nameField.setMaximumSize(Const.COLUMN_SIZE(4));
        row.add(nameField);

        priceField = new JTextField();
        priceField.setHorizontalAlignment(JTextField.CENTER);
        priceField.setEditable(false);
        priceField.setMaximumSize(Const.COLUMN_SIZE(4));
        row.add(priceField);

        quantityField = new JTextField();
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.setEditable(false);
        quantityField.setMaximumSize(Const.COLUMN_SIZE(4));
        row.add(quantityField);

        costField = new JTextField();
        costField.setHorizontalAlignment(JTextField.CENTER);
        costField.setEditable(false);
        costField.setMaximumSize(Const.COLUMN_SIZE(4));
        row.add(costField);

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
        row.add(popupMenu);

        nameField.addMouseListener(new PopupMenuListener(popupMenu));
        priceField.addMouseListener(new PopupMenuListener(popupMenu));
        quantityField.addMouseListener(new PopupMenuListener(popupMenu));
        costField.addMouseListener(new PopupMenuListener(popupMenu));
        row.addMouseListener(new PopupMenuListener(popupMenu));
    }

    private void save() {
        ((Item) ingredient.getItem()).setPrice(Integer.parseInt(priceField.getText()), Integer.parseInt(priceField.getText()), Integer.parseInt(priceField.getText()));
        ingredient.setQuantity(Integer.parseInt(quantityField.getText()));
    }

    private void delete() {
        craft.removeIngredient(ingredient);
        parent.updateUI();
    }

    public void updateText() {
        nameField.setText(ingredient.getItem().getName());
        priceField.setText(Integer.toString(ingredient.getItem().getPrice()));
        quantityField.setText(Integer.toString(ingredient.getQuantity()));
        costField.setText(Integer.toString(ingredient.getCost()));
    }

    public void update(Observable o, Object arg) {
        updateText();
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
