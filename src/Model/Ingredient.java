package Model;

import java.util.Observable;
import java.util.Observer;

public class Ingredient extends Observable implements Observer {
    private static final Item DFLT_ITEM = null;
    private static final int DFLT_QTT = 0;

    private AItem item;
    private int quantity;

    public Ingredient() {
        this(DFLT_ITEM, DFLT_QTT);
    }

    public Ingredient(AItem item, int quantity) {
        setItem(item);
        setQuantity(quantity);
    }

    public AItem getItem() {
        return item;
    }
    private void setItem(AItem item) {
        this.item = item;
        this.item.addObserver(this);
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCost() {
        return item.getPrice() * getQuantity();
    }

    protected void changed(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
    private void changed() {
        setChanged();
        notifyObservers();
    }

    public void update(Observable o, Object arg) {
        changed(arg);
    }

    public boolean equals(Object o) {
        if(o.getClass().equals(this.getClass())) {
            Ingredient obj = (Ingredient) o;
            if(obj != null && getItem().getName().equals(obj.getItem().getName())) {
                return true;
            }
        }
        return false;
    }
}
