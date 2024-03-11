package View.MainFrame.CraftsPage;

import Model.*;
import View.Const;
import View.CraftingFrame.CraftingFrame;
import View.PriceUpdateFrame.PriceUpdateFrame;
import gswing.GScrollingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CraftsPage extends JPanel {
    private static final int COLUMN_NB = 10;
    private static final int DBL_COLUMN_NB = COLUMN_NB / 2;
    private static final int DFLT_ITEM_TYPE = 0;
    private static final int DFLT_PREFERENCE = Enums.Preference.NEUTRAL.ordinal();
    private static final int DFLT_JOB = 0;

    private ArrayList<CraftRow> craftRows;
    private String currSorter;
    private boolean isReversed;

    private JTextField searchBar;
    private String displaySearched;
    private ArrayList<String> searchResults;

    private int displayItemType;
    private int displayPreference;
    private int displayJob;
    private boolean displayCraft;

    private JFrame parent;
    private JPanel self;

    public CraftsPage(JFrame parent) {
        currSorter = "";
        isReversed = false;

        displaySearched = "";
        searchResults = new ArrayList<>();

        displayItemType = DFLT_ITEM_TYPE;
        displayPreference = DFLT_PREFERENCE;
        displayJob = DFLT_JOB;
        displayCraft = true;

        this.parent = parent;
        self = this;

        init();
    }

    private void init() {
        craftRows = new ArrayList<>();
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel topButtonsPanel = new JPanel();
        topButtonsPanel.setMaximumSize(Const.ROW_SIZE);
        add(topButtonsPanel);

        JCheckBox box = new JCheckBox("Crafts");
        box.setSelected(displayCraft);
        box.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayCraft = box.isSelected();
                updateUI();
            }
        });
        topButtonsPanel.add(box);

        ArrayList<String> itemTypesChoices = new ArrayList<>();
        itemTypesChoices.add("Tout HDV");
        itemTypesChoices.addAll(Arrays.asList(Enums.itemTypeNames));
        JComboBox itemTypeDropdown = new JComboBox(itemTypesChoices.toArray());
        itemTypeDropdown.setSelectedIndex(displayItemType);
        itemTypeDropdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayItemType = itemTypeDropdown.getSelectedIndex();
                updateUI();
            }
        });
        topButtonsPanel.add(itemTypeDropdown);

        String[] preferencesString = { "Toute préférence", "Neutres et plus", "Favoris seulement" };
        JComboBox preferenceDropdown = new JComboBox(preferencesString);
        preferenceDropdown.setSelectedIndex(displayPreference);
        preferenceDropdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayPreference = preferenceDropdown.getSelectedIndex();
                updateUI();
            }
        });
        topButtonsPanel.add(preferenceDropdown);

        ArrayList<String> jobChoices = new ArrayList<>();
        jobChoices.add("Tout métier");
        jobChoices.addAll(Arrays.asList(Enums.jobNames));
        JComboBox jobDropdown = new JComboBox(jobChoices.toArray());
        jobDropdown.setSelectedIndex(displayJob);
        jobDropdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayJob = jobDropdown.getSelectedIndex();
                updateUI();
            }
        });
        topButtonsPanel.add(jobDropdown);

        searchBar = new JTextField();
        searchBar.setText(displaySearched);
        searchBar.addKeyListener(new SearchKeyListener());
        Const.SET_SIZE(searchBar, Const.COLUMN_SIZE(8));
        topButtonsPanel.add(searchBar);

        JPanel spacingPanel = new JPanel();
        Const.SET_SIZE(spacingPanel, new Dimension(Const.WINDOW_WIDTH / 10, 1));
        topButtonsPanel.add(spacingPanel);

        JButton btnUpdate = new JButton("Mettre à jour les prix");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<ArrayList<AItem>> items = new ArrayList<>();
                for(int i = 0; i < Enums.ItemType.N_TYPES.ordinal(); ++i) {
                    items.add(new ArrayList<>());
                }

                // If current display is crafts, get those crafts and their ingredients to update
                if(displayCraft) {
                    ArrayList<Craft> craftsToGetItemsFrom = new ArrayList<>();
                    for(Craft craft : CommDB.getCrafts()) {
                        if(isToDisplay(craft)) {
                            craftsToGetItemsFrom.add(craft);
                            items.get(craft.getType().ordinal()).add(craft);
                        }
                    }

                    for(Craft craft : craftsToGetItemsFrom) {
                        for(Ingredient ingredient : craft.getCraftIngredients()) {
                            ArrayList<AItem> itemTypeItems = items.get(ingredient.getItem().getType().ordinal());
                            if(!itemTypeItems.contains(ingredient.getItem())) {
                                itemTypeItems.add(ingredient.getItem());
                            }
                        }
                    }
                }
                // If current display is non-crafts, get all displayed items, and update only those
                else {
                    for(Item item : CommDB.getItems()) {
                        if(isToDisplay(item)) {
                            items.get(item.getType().ordinal()).add(item);
                        }
                    }
                }

                SwingUtilities.invokeLater(new PriceUpdateFrame(items, parent, self));
            }
        });
        topButtonsPanel.add(btnUpdate);

        // If we're looking at crafts, we want a button that start the craft procedure
        JButton btnOpenQueue = new JButton("Commencer à craft");
        btnOpenQueue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<ArrayList<Craft>> crafts = new ArrayList<>();
                ArrayList<ArrayList<Integer>> amounts = new ArrayList<>();
                for(int i = 0; i < Enums.Job.N_JOBS.ordinal(); ++i) {
                    crafts.add(new ArrayList<>());
                    amounts.add(new ArrayList<>());
                }

                for(CraftRow row : craftRows) {
                    if(row.getAmount() > 0) {
                        crafts.get(row.getCraft().getJob().ordinal()).add(row.getCraft());
                        amounts.get(row.getCraft().getJob().ordinal()).add(row.getAmount());
                    }
                }

                SwingUtilities.invokeLater(new CraftingFrame(crafts, amounts, parent, self));
            }
        });
        if(!displayCraft) {
            btnOpenQueue.setEnabled(false);
        }
        topButtonsPanel.add(btnOpenQueue);

        if(displayCraft) {
            initCraftRows();
        }
        else {
            initItemRows();
        }
    }

    private void initCraftRows() {
        initCraftHeader();

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.PAGE_AXIS));

        int i = 0;
        for(Craft craft : CommDB.getCrafts()) {
            if(isToDisplay(craft)) {
                CraftRow row = new CraftRow(craft, this);
                if((i & 1) == 0) {
                    row.setBackground(new Color(70, 70, 100));
                }
                else {
                    row.setBackground(new Color(90, 80, 70));
                }
                rowPanel.add(row);
                craftRows.add(row);

                ++i;
            }
        }

        Const.SET_SIZE(rowPanel, new Dimension(Const.WINDOW_WIDTH, Const.ROW_HEIGHT * (i + 12)));
        add(new GScrollingPanel(rowPanel));
    }

    private void initCraftHeader() {
        JPanel listPanelTags = new JPanel();
        listPanelTags.setMaximumSize(Const.ROW_SIZE);
        listPanelTags.setLayout(new BoxLayout(listPanelTags, BoxLayout.LINE_AXIS));
        add(listPanelTags);

        JPanel buffer = new JPanel();
        buffer.setMaximumSize(Const.BTN_SIZE);
        listPanelTags.add(buffer);

        JButton tagName = new JButton("Nom");
        Const.SET_SIZE(tagName, Const.COLUMN_SIZE(DBL_COLUMN_NB));
        tagName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("name") && !isReversed) {
                    CommDB.getCrafts().sort(new NamedCraftComparator("name") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return (o1.getName().compareTo(o2.getName()) * -1);
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getCrafts().sort(new NamedCraftComparator("name") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getName().compareTo(o2.getName());
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "name";
                updateUI();
            }
        });
        listPanelTags.add(tagName);

        JButton tagPrice = new JButton("Prix");
        Const.SET_SIZE(tagPrice, Const.COLUMN_SIZE(COLUMN_NB));
        tagPrice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("price") && !isReversed) {
                    CommDB.getCrafts().sort(new NamedCraftComparator("price") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice() == o2.getPrice() ? 0 : o1.getPrice() < o2.getPrice() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getCrafts().sort(new NamedCraftComparator("price") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice() == o2.getPrice() ? 0 : o1.getPrice() < o2.getPrice() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "price";
                updateUI();
            }
        });
        listPanelTags.add(tagPrice);

        JButton tagCost = new JButton("Coût");
        Const.SET_SIZE(tagCost, Const.COLUMN_SIZE(COLUMN_NB));
        tagCost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("cost") && !isReversed) {
                    CommDB.getCrafts().sort(new NamedCraftComparator("cost") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getCost() == o2.getCost() ? 0 : o1.getCost() < o2.getCost() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getCrafts().sort(new NamedCraftComparator("cost") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getCost() == o2.getCost() ? 0 : o1.getCost() < o2.getCost() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "cost";
                updateUI();
            }
        });
        listPanelTags.add(tagCost);

        JButton tagRatio = new JButton("Ratio");
        Const.SET_SIZE(tagRatio, Const.COLUMN_SIZE(COLUMN_NB));
        tagRatio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("ratio") && !isReversed) {
                    CommDB.getCrafts().sort(new NamedCraftComparator("ratio") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getRatio() == o2.getRatio() ? 0 : o1.getRatio() < o2.getRatio() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getCrafts().sort(new NamedCraftComparator("ratio") {
                        public int compare(Craft o1, Craft o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getRatio() == o2.getRatio() ? 0 : o1.getRatio() < o2.getRatio() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "ratio";
                updateUI();
            }
        });
        listPanelTags.add(tagRatio);
    }

    private void initItemRows() {
        initItemHeader();

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.PAGE_AXIS));

        int i = 0;
        for(Item item : CommDB.getItems()) {
            if(isToDisplay(item)) {
                ItemRow row = new ItemRow(item, this);
                if((i & 1) == 0) {
                    row.setBackground(new Color(70, 70, 100));
                }
                else {
                    row.setBackground(new Color(90, 80, 70));
                }
                rowPanel.add(row);

                ++i;
            }
        }

        Const.SET_SIZE(rowPanel, new Dimension(Const.WINDOW_WIDTH, (Const.ROW_HEIGHT + 10) * (i + 1)));
        add(new GScrollingPanel(rowPanel));
    }

    private void initItemHeader() {
        JPanel listPanelTags = new JPanel();
        listPanelTags.setMaximumSize(Const.ROW_SIZE);
        listPanelTags.setLayout(new BoxLayout(listPanelTags, BoxLayout.LINE_AXIS));
        add(listPanelTags);

        JButton tagName = new JButton("Nom");
        Const.SET_SIZE(tagName, Const.COLUMN_SIZE(4));
        tagName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("name") && !isReversed) {
                    CommDB.getItems().sort(new NamedItemComparator("name") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return (o1.getName().compareTo(o2.getName()) * -1);
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getItems().sort(new NamedItemComparator("name") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getName().compareTo(o2.getName());
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "name";
                updateUI();
            }
        });
        listPanelTags.add(tagName);

        JButton tagPrice1 = new JButton("Prix unitaire (x1)");
        Const.SET_SIZE(tagPrice1, Const.COLUMN_SIZE(8));
        tagPrice1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("price1") && !isReversed) {
                    CommDB.getItems().sort(new NamedItemComparator("price1") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice1() == o2.getPrice1() ? 0 : o1.getPrice1() < o2.getPrice1() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getItems().sort(new NamedItemComparator("price1") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice1() == o2.getPrice1() ? 0 : o1.getPrice1() < o2.getPrice1() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "price1";
                updateUI();
            }
        });
        listPanelTags.add(tagPrice1);

        JButton tagPrice10 = new JButton("Prix unitaire (x10)");
        Const.SET_SIZE(tagPrice10, Const.COLUMN_SIZE(8));
        tagPrice10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("price10") && !isReversed) {
                    CommDB.getItems().sort(new NamedItemComparator("price10") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice10() == o2.getPrice10() ? 0 : o1.getPrice10() < o2.getPrice10() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getItems().sort(new NamedItemComparator("price10") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice10() == o2.getPrice10() ? 0 : o1.getPrice10() < o2.getPrice10() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "price10";
                updateUI();
            }
        });
        listPanelTags.add(tagPrice10);

        JButton tagPrice100 = new JButton("Prix unitaire (x100)");
        Const.SET_SIZE(tagPrice100, Const.COLUMN_SIZE(8));
        tagPrice100.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("price100") && !isReversed) {
                    CommDB.getItems().sort(new NamedItemComparator("price100") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice100() == o2.getPrice100() ? 0 : o1.getPrice100() < o2.getPrice100() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getItems().sort(new NamedItemComparator("price100") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getPrice100() == o2.getPrice100() ? 0 : o1.getPrice100() < o2.getPrice100() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "price100";
                updateUI();
            }
        });
        listPanelTags.add(tagPrice100);

        JButton tagRatio = new JButton("Ratio");
        Const.SET_SIZE(tagRatio, Const.COLUMN_SIZE(8));
        tagRatio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(currSorter.equals("ratio") && !isReversed) {
                    CommDB.getItems().sort(new NamedItemComparator("ratio") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getRatio() == o2.getRatio() ? 0 : o1.getRatio() < o2.getRatio() ? 1 : -1;
                            }
                        }
                    });
                    isReversed = true;
                }
                else {
                    CommDB.getItems().sort(new NamedItemComparator("ratio") {
                        public int compare(Item o1, Item o2) {
                            int result = comparePreference(o1, o2);
                            if(result != 0) {
                                return result;
                            }
                            else {
                                return o1.getRatio() == o2.getRatio() ? 0 : o1.getRatio() < o2.getRatio() ? -1 : 1;
                            }
                        }
                    });
                    isReversed = false;
                }
                currSorter = "ratio";
                updateUI();
            }
        });
        listPanelTags.add(tagRatio);
    }



    private boolean isToDisplay(Craft craft) {
        if(displaySearched != null && displaySearched.length() > 2) {
            if(searchResults.contains(craft.getName())) {
                return true;
            }
            else {
                return false;
            }
        }

        // Check craft
        boolean isCraftDisplay = displayCraft;
        // Check item type
        boolean isItemTypeToDisplay = displayItemType == 0 || craft.getType().ordinal() + 1 == displayItemType;
        // Check preference
        boolean isPreferenceToDisplay = craft.getPreference().ordinal() >= displayPreference;
        // Check job
        boolean isJobToDisplay = displayJob == 0 || craft.getJob().ordinal() + 1 == displayJob;

        return isCraftDisplay && isItemTypeToDisplay && isPreferenceToDisplay && isJobToDisplay;
    }

    private boolean isToDisplay(Item item) {
        if(displaySearched != null && displaySearched.length() > 2) {
            if(searchResults.contains(item.getName())) {
                return true;
            }
            else {
                return false;
            }
        }

        // Check craft
        boolean isCraftDisplay = !displayCraft;
        // Check item type
        boolean isItemTypeToDisplay = displayItemType == 0 || item.getType().ordinal() + 1 == displayItemType;
        // Check preference
        boolean isPreferenceToDisplay = item.getPreference().ordinal() >= displayPreference;

        return isCraftDisplay && isItemTypeToDisplay && isPreferenceToDisplay;
    }

    public void updateUI() {
        init();
        super.updateUI();
    }

    private class SearchKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && displaySearched.length() > 0) {
                updateSuggestions(displaySearched.substring(0, displaySearched.length() - 1));
            }
            else if((e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z') || (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == ' ' || e.getKeyChar() == '-') {
                updateSuggestions(displaySearched + e.getKeyChar());
            }
        }

        private void updateSuggestions(String userInput) {
            displaySearched = userInput;

            if(userInput.length() < 3) {
                searchResults.clear();
            }
            else {
                searchResults = Const.findAllSimilar(userInput, getDictionary());
            }

            updateUI();
            searchBar.requestFocus();
        }

        private ArrayList<String> getDictionary() {
            ArrayList<String> dictionary = new ArrayList<>();
            dictionary.addAll(CommDB.getItemNames());
            dictionary.addAll(CommDB.getCraftNames());
            return dictionary;
        }
    }
}