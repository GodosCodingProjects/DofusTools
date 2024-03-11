package View.MainFrame.CraftsPage;

import Model.*;
import View.Const;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class ItemRow extends JPanel implements Observer {
    private static final int COLUMN_NB = 8;

    private JPanel parent;
    private Item item;

    private JButton btnName;
    private JTextField price1Field;
    private JTextField price10Field;
    private JTextField price100Field;
    private JTextField ratioField;
    private JLabel ratioLabel;

    private JComboBox typeSelector;

    public ItemRow(Item item, JPanel parent) {
        this.item = item;
        this.item.addObserver(this);
        this.parent = parent;

        init();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel mainRowPanel = new JPanel();
        mainRowPanel.setLayout(new BoxLayout(mainRowPanel, BoxLayout.LINE_AXIS));
        Const.SET_SIZE(mainRowPanel, Const.ROW_SIZE);
        setBorder(new EmptyBorder(10, 0, 0, 0));
        add(mainRowPanel);

        btnName = new JButton();
        btnName.addKeyListener(new SaveKeyListener(this));
        switch(item.getPreference()) {
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
                switch(item.getPreference()) {
                    case FAVORED:
                        item.setPreference(Enums.Preference.UNFAVORED);
                        btnName.setForeground(Color.RED);
                        break;
                    case UNFAVORED:
                        item.setPreference(Enums.Preference.NEUTRAL);
                        btnName.setForeground(getForeground());
                        break;
                    default:
                        item.setPreference(Enums.Preference.FAVORED);
                        btnName.setForeground(Color.GREEN);
                        break;
                }
            }
        });
        Const.SET_SIZE(btnName, Const.COLUMN_SIZE(4));
        mainRowPanel.add(btnName);

        price1Field = new JTextField();
        price1Field.setHorizontalAlignment(JTextField.CENTER);
        price1Field.addFocusListener(new EditableFieldListener());
        price1Field.addKeyListener(new SaveKeyListener(this));
        Const.SET_SIZE(price1Field, Const.COLUMN_SIZE(COLUMN_NB));
        mainRowPanel.add(price1Field);

        price10Field = new JTextField();
        price10Field.setHorizontalAlignment(JTextField.CENTER);
        price10Field.addFocusListener(new EditableFieldListener());
        price10Field.addKeyListener(new SaveKeyListener(this));
        Const.SET_SIZE(price10Field, Const.COLUMN_SIZE(COLUMN_NB));
        mainRowPanel.add(price10Field);

        price100Field = new JTextField();
        price100Field.setHorizontalAlignment(JTextField.CENTER);
        price100Field.addFocusListener(new EditableFieldListener());
        price100Field.addKeyListener(new SaveKeyListener(this));
        Const.SET_SIZE(price100Field, Const.COLUMN_SIZE(COLUMN_NB));
        mainRowPanel.add(price100Field);

        ratioField = new JTextField();
        ratioField.setHorizontalAlignment(JTextField.RIGHT);
        ratioField.setEditable(false);
        Const.SET_SIZE(ratioField, Const.COLUMN_SIZE(COLUMN_NB));
        ratioField.setLayout(new BoxLayout(ratioField, BoxLayout.LINE_AXIS));
        ratioLabel = new JLabel();
        ratioField.add(ratioLabel);
        mainRowPanel.add(ratioField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        Const.SET_SIZE(buttonPanel, Const.COLUMN_SIZE((float) (COLUMN_NB / 2)));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        mainRowPanel.add(buttonPanel);

        typeSelector = new JComboBox(Enums.itemTypeNames);
        typeSelector.setSelectedIndex(item.getType().ordinal());
        typeSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                item.setType(Enums.ItemType.values()[typeSelector.getSelectedIndex()]);
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
        price1Field.addMouseListener(new PopupMenuListener(popupMenu));
        price10Field.addMouseListener(new PopupMenuListener(popupMenu));
        price100Field.addMouseListener(new PopupMenuListener(popupMenu));
        addMouseListener(new PopupMenuListener(popupMenu));
    }

    private void save() {
        item.setPrice(Integer.parseInt(price1Field.getText()), Integer.parseInt(price10Field.getText()), Integer.parseInt(price100Field.getText()));
    }

    private void delete() {
        CommDB.remove(item);
        parent.updateUI();
    }

    public void updateText() {
        btnName.setText(item.getName());
        price1Field.setText(Integer.toString(item.getPrice1()));
        price10Field.setText(Integer.toString(item.getPrice10()));
        price100Field.setText(Integer.toString(item.getPrice100()));

        int packs[] = { 1, 10, 100 };
        double ratios[] = {
            (double) item.getPrice1() / (double) item.getPrice(),
            (double) item.getPrice10() / (double) item.getPrice(),
            (double) item.getPrice100() / (double) item.getPrice()
        };

        int from = 1;
        int to = 1;
        for(int i = 0; i < 3; ++i) {
            if(ratios[i] == 1.0) {
                from = packs[i];
            }
            if(ratios[i] == item.getRatio()) {
                to = packs[i];
            }
        }

        ratioField.setText(String.format("%.2f", item.getRatio()));
        ratioLabel.setText("(" + from + " => " + to + ")");
    }

    public void update(Observable o, Object arg) {
        updateText();
    }

    public Item getItem() {
        return item;
    };

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
