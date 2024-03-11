package Model;

import View.Const;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Craft extends AItem implements Observer {
    private static final int DFLT_COMP_SIZE = 8;
    private static final double PROFIT_AFTER_TAX = 0.98;

    private int price;
    private ArrayList<Ingredient> ingredients;

    private Enums.Job job;

    public Craft() {
        this(DFLT_NAME);
    }
    public Craft(String name) {
        this(name, DFLT_PRICE);
    }
    public Craft(String name, int price) {
        this(name, price, Enums.Preference.NEUTRAL);
    }
    public Craft(String name, int price, Enums.Preference preference) {
        this(name, price, preference, Enums.ItemType.RESOURCE);
    }
    public Craft(String name, int price, Enums.Preference preference, Enums.ItemType type) {
        this(name, price, preference, type, Enums.Job.MODELER);
    }
    public Craft(String name, int price, Enums.Preference preference, Enums.ItemType type, Enums.Job job) {
        setName(name);
        setPrice(price);
        setPreference(preference);
        setType(type);
        setJob(job);
        ingredients = new ArrayList(DFLT_COMP_SIZE);
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public Enums.Job getJob() {
        return job;
    }
    public void setJob(Enums.Job job) {
        this.job = job;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
    public ArrayList<Ingredient> getCraftIngredients() {
        ArrayList<Ingredient> ingredientsToBuy = new ArrayList<>();

        for(Ingredient ingredient : getIngredients()) {
            Craft craft = CommDB.getCraft(ingredient.getItem().getName());
            // If the item is a craft and the price of crafting it is less than just buying it
            if(craft != null && craft.getCost() < craft.getPrice()) {
                // Add all its ingredients (multiplied by the craft quantity) to the ingredient list to return
                for(Ingredient craftIngredient : craft.getCraftIngredients()) {
                    Ingredient completeCraftIngredient = new Ingredient(craftIngredient.getItem(), craftIngredient.getQuantity() * ingredient.getQuantity());
                    Const.addIngredientToArray(completeCraftIngredient, ingredientsToBuy);
                }
            }
            // If the Item isn't a craft, or simply costs less to buy directly
            else {
                ingredientsToBuy.add(ingredient);
            }
        }

        return ingredientsToBuy;
    }
    public void addIngredient(Ingredient component) {
        ingredients.add(component);
        component.addObserver(this);
    }
    public void removeIngredient(Ingredient component) {
        ingredients.remove(component);
        component.deleteObserver(this);
    }

    // Takes into account the sales tax
    public int getSalesPrice() {
        return (int)((double)getPrice() * PROFIT_AFTER_TAX);
    }

    public int getCost() {
        int totalCost = 0;
        for(Ingredient ingredient : getCraftIngredients()) {
            totalCost += ingredient.getCost();
        }
        return totalCost;
    }
    public int getProfit() {
        return (getSalesPrice() - getCost());
    }
    public double getRatio() {
        int cost = getCost();
        if(cost != 0) {
            return (double)getSalesPrice() / (double)cost;
        }
        else {
            return 0;
        }
    }

    public void update(Observable o, Object arg) {
        changed(arg);
    }
}
