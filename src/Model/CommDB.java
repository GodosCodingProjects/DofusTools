package Model;

import java.io.*;
import java.util.ArrayList;

public class CommDB {
    private final static String FOLDER = "resources/";
    private final static String EXTENSION = ".txt";

    private final static String ITEMS_FILE_NAME = "items";
    private final static String ITEMS_FILE_PATH = FOLDER+ITEMS_FILE_NAME+EXTENSION;

    private final static String CRAFTS_FILE_NAME = "crafts";
    private final static String CRAFTS_FILE_PATH = FOLDER+CRAFTS_FILE_NAME+EXTENSION;

    private static CommDB instance = null;

    private ArrayList<Item> items;
    private ArrayList<Craft> crafts;

    private static CommDB get() {
        if(instance == null) {
            instance = new CommDB();
        }
        return instance;
    }
    private CommDB() {
        instance = this;

        items = new ArrayList();
        crafts = new ArrayList();

        loadItems();
        loadCrafts();
    }

    public static void add(Item item) {
        getItems().add(item);
    }
    public static void add(Craft craft) {
        getCrafts().add(craft);
    }
    public static void remove(Item item) {
        getItems().remove(item);

        for(Craft craft : getCrafts()) {
            if(craft.getIngredients().contains(new Ingredient(item, 0))) {
                craft.update(null, null);
            }
        }
    }
    public static void remove(Craft craft) {
        getCrafts().remove(craft);
    }

    public static Item getItem(String name) {
        for(Item item : getItems()) {
            if(item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
    public static ArrayList<Item> getItems() {
        return get().items;
    }
    public static ArrayList<String> getItemNames() {
        ArrayList<String> names = new ArrayList<>();
        for(Item item : getItems()) {
            names.add(item.getName());
        }
        return names;
    }

    public static ArrayList<Craft> getCrafts() {
        return get().crafts;
    }
    public static Craft getCraft(String name) {
        for(Craft craft : getCrafts()) {
            if(craft.getName().equals(name)) {
                return craft;
            }
        }
        return null;
    }
    public static ArrayList<String> getCraftNames() {
        ArrayList<String> names = new ArrayList<>();
        for(Craft craft : getCrafts()) {
            names.add(craft.getName());
        }
        return names;
    }

    public static void saveAll() {
        get().saveItems();
        get().saveCrafts();
    }

    private void loadItems() {
        try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ITEMS_FILE_PATH)))) {

            int nItems = in.readInt();
            for(int i = 0; i < nItems; ++i) {
                String name = (String) in.readObject();
                int price1 = in.readInt();
                int price10 = in.readInt();
                int price100 = in.readInt();
                Enums.Preference preference = (Enums.Preference) in.readObject();
                Enums.ItemType type = (Enums.ItemType) in.readObject();

                items.add(new Item(name, price1, price10, price100, preference, type));
            }

        } catch(Exception e) {}
    }

    private void saveItems() {
        try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(ITEMS_FILE_PATH)))) {

            out.writeInt(items.size());
            for(Item item : items) {
                out.writeObject(item.getName());
                out.writeInt(item.getPrice1());
                out.writeInt(item.getPrice10());
                out.writeInt(item.getPrice100());
                out.writeObject(item.getPreference());
                out.writeObject(item.getType());
            }

        } catch(IOException e) {}
    }

    private void loadCrafts() {
        try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(CRAFTS_FILE_PATH)))) {

            // Preload all crafts, giving them fake lists of ingredients
            int nCrafts = in.readInt();
            for(int i = 0; i < nCrafts; ++i) {
                String name = (String) in.readObject();
                int price = in.readInt();
                Enums.Preference preference = (Enums.Preference) in.readObject();
                Enums.ItemType type = (Enums.ItemType) in.readObject();
                Enums.Job job = (Enums.Job) in.readObject();

                Craft craft = new Craft(name, price, preference, type, job);
                crafts.add(craft);

                int nIngredients = in.readInt();
                for(int j = 0; j < nIngredients; ++j) {
                    String ingredient = (String) in.readObject();
                    int quantity = in.readInt();
                    craft.addIngredient(new Ingredient(new Item(ingredient), quantity));
                }
            }

            // Make sure to go over all ingredients and tying them to the real Item/Craft
            for(Craft craft : crafts) {
                ArrayList<Ingredient> realIngredients = new ArrayList<>();

                for(Ingredient ingredient : craft.getIngredients()) {
                    Item itemIngredient = getItem(ingredient.getItem().getName());

                    if(itemIngredient == null) {
                        Craft craftIngredient = getCraft(ingredient.getItem().getName());

                        if(craftIngredient == null) {
                            continue;
                        }
                        else {
                            realIngredients.add(new Ingredient(craftIngredient, ingredient.getQuantity()));
                        }
                    }
                    else {
                        realIngredients.add(new Ingredient(itemIngredient, ingredient.getQuantity()));
                    }
                }

                craft.getIngredients().clear();

                for(Ingredient realIngredient : realIngredients) {
                    craft.addIngredient(realIngredient);
                }
            }

        } catch(Exception e) {}
    }

    private void saveCrafts() {
        try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(CRAFTS_FILE_PATH)))) {

            out.writeInt(crafts.size());
            for(Craft craft : crafts) {
                out.writeObject(craft.getName());
                out.writeInt(craft.getPrice());
                out.writeObject(craft.getPreference());
                out.writeObject(craft.getType());
                out.writeObject(craft.getJob());

                out.writeInt(craft.getIngredients().size());
                for(Ingredient ingredient : craft.getIngredients()) {
                    out.writeObject(ingredient.getItem().getName());
                    out.writeInt(ingredient.getQuantity());
                }
            }

        } catch(IOException e) {}
    }
}
