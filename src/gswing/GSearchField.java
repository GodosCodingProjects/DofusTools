package gswing;

import View.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public abstract class GSearchField extends JTextField {
    private ArrayList<String> currDisplay;

    private JPopupMenu suggestionDropdown;
    private int selected_i;
    private boolean isSelectEnabled;

    public GSearchField(boolean isSelectEnabled) {
        currDisplay = new ArrayList<>();
        selected_i = 0;
        this.isSelectEnabled = isSelectEnabled;

        init();
    }

    public abstract ArrayList<String> getDictionary();

    private void init() {
        addKeyListener(new SearchKeyListener());
    }

    private void initSuggestionDropdown() {
        suggestionDropdown = new JPopupMenu();
        Const.SET_SIZE(suggestionDropdown, new Dimension(getWidth(), Const.ROW_HEIGHT * currDisplay.size()));
        suggestionDropdown.setLayout(new BoxLayout(suggestionDropdown, BoxLayout.PAGE_AXIS));
        suggestionDropdown.setFocusable(false);
        suggestionDropdown.setVisible(true);

        selected_i = selected_i >= currDisplay.size() ? currDisplay.size() - 1 : selected_i;

        for(int i = 0; i < currDisplay.size(); ++i) {
            JPanel suggestionItemPanel = new JPanel();
            Const.SET_SIZE(suggestionItemPanel, Const.ROW_SIZE);
            suggestionItemPanel.setFocusable(false);

            JMenuItem suggestionItem = new JMenuItem(currDisplay.get(i));
            suggestionItemPanel.add(suggestionItem);

            if(isSelectEnabled && i == selected_i) {
                suggestionItemPanel.setBackground(Const.COLOR_BG_SELECTED);
            }

            suggestionDropdown.add(suggestionItemPanel);
        }

        suggestionDropdown.show(this, 0, getHeight());
    }

    private class SearchKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && getText().length() > 0) {
                updateSuggestions(getText().substring(0, getText().length() - 1));
            }
            else if((e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z') || (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == ' ' || e.getKeyChar() == '-') {
                updateSuggestions(getText() + e.getKeyChar());
            }
            else if(isSelectEnabled) {
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    selected_i = selected_i > 0 ? selected_i - 1 : 0;
                    updateSuggestions(getText());
                }
                else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    ++selected_i;
                    updateSuggestions(getText());
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setText(Const.findAllSimilar(getText(), getDictionary()).get(selected_i));
                    updateSuggestions(getText());
                }
            }
        }

        private void updateSuggestions(String userInput) {
            if(userInput.length() < 3) {
                currDisplay.clear();
                if(suggestionDropdown != null) {
                    suggestionDropdown.setVisible(false);
                }
            }
            else {
                currDisplay = Const.findAllSimilar(userInput, getDictionary());
                initSuggestionDropdown();
            }
        }
    }
}
