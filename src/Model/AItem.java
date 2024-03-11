package Model;

import java.util.Observable;

public abstract class AItem extends Observable {
    protected static final String DFLT_NAME = "";
    protected static final int DFLT_PRICE = 0;

    protected String name;
    protected boolean isUpdated;

    protected Enums.Preference preference;
    protected Enums.ItemType type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract int getPrice();

    public Enums.Preference getPreference() {
        return preference;
    }
    public void setPreference(Enums.Preference preference) {
        this.preference = preference;
    }

    public Enums.ItemType getType() {
        return type;
    }
    public void setType(Enums.ItemType type) {
        this.type = type;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    protected void changed(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
    protected void changed() {
        setChanged();
        notifyObservers();
    }

    public boolean equals(Object o) {
        AItem obj = (AItem) o;
        if(obj != null) {
            if(getName().equals(obj.getName())) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Name: " + getName();
    }
}
