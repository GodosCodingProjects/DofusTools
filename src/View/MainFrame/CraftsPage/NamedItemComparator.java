package View.MainFrame.CraftsPage;

import Model.Craft;
import Model.Item;

import java.util.Comparator;

public abstract class NamedItemComparator implements Comparator<Item> {
    public String name;

    public NamedItemComparator(String name) {
        this.name = name;
    }

    public int comparePreference(Item o1, Item o2) {
        if(o1.getPreference() == o2.getPreference()) {
            return 0;
        }
        else if(o1.getPreference().ordinal() > o2.getPreference().ordinal()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
