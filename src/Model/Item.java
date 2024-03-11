package Model;

public class Item extends AItem {
    private int price1;
    private int price10;
    private int price100;

    public Item() {
        this(DFLT_NAME);
    }
    public Item(String name) {
        this(name, DFLT_PRICE, DFLT_PRICE, DFLT_PRICE);
    }
    public Item(String name, int price1, int price10, int price100) {
        this(name, price1, price10, price100, Enums.Preference.NEUTRAL);
    }
    public Item(String name, int price1, int price10, int price100, Enums.Preference preference) {
        this(name, price1, price10, price100, preference, Enums.ItemType.RESOURCE);
    }
    public Item(String name, int price1, int price10, int price100, Enums.Preference preference, Enums.ItemType type) {
        setName(name);
        setPrice(price1, price10, price100);
        setPreference(preference);
        setType(type);

        isUpdated = false;
    }

    public int getPrice() {
        return Math.min(price1, Math.min(price10, price100));
    }
    public int getPrice1() {
        return price1;
    }
    public int getPrice10() {
        return price10;
    }
    public int getPrice100() {
        return price100;
    }

    public double getRatio() {
        return Math.max((double) getPrice1() / (double) getPrice(), Math.max((double) getPrice10() / (double) getPrice(), (double) getPrice100() / (double) getPrice()));
    }

    public void setPrice(int price1, int price10, int price100) {
        this.price1 = price1;
        this.price10 = price10;
        this.price100 = price100;
    }
}
