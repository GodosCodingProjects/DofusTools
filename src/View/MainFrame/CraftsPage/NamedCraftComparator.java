package View.MainFrame.CraftsPage;

import Model.Craft;

import java.util.Comparator;

public abstract class NamedCraftComparator implements Comparator<Craft> {
    public String name;

    public NamedCraftComparator(String name) {
        this.name = name;
    }

    public int comparePreference(Craft o1, Craft o2) {
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
