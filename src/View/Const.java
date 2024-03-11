package View;

import Model.Ingredient;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
 * Constants defined for use in the view package.
 */
public final class Const {
    public static final Dimension FULL_WINDOW_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int WINDOW_HEIGHT = (int)FULL_WINDOW_SIZE.getHeight();
    public static final int WINDOW_WIDTH = (int)FULL_WINDOW_SIZE.getWidth();
    public static final int TASKBAR_HEIGHT = 40;

    public static final int ROW_HEIGHT = WINDOW_HEIGHT / 24;
    public static final Dimension ROW_SIZE = new Dimension(WINDOW_WIDTH, ROW_HEIGHT);
    public static final Dimension BTN_SIZE = new Dimension(ROW_HEIGHT, ROW_HEIGHT);
    public static Dimension COLUMN_SIZE(float nColumnsInRow) {
        return new Dimension((int)((float)WINDOW_WIDTH / nColumnsInRow), ROW_HEIGHT);
    }
    public static void SET_SIZE(Component component, Dimension dimension) {
        component.setMaximumSize(dimension);
        component.setPreferredSize(dimension);
        component.setMinimumSize(dimension);
    }

    public static final int FONT_SIZE = ROW_HEIGHT / 2;
    public static final String FONT_NAME = "Tw Cen MT";

    public static final Color COLOR_BG_SELECTED = new Color(79, 108, 176);

    public static void initGlobalSettings() {
        // Set L&F
        FlatDarkLaf.setup();

        // Set the global font
        setUIFont(new javax.swing.plaf.FontUIResource(Const.FONT_NAME, Font.PLAIN, Const.FONT_SIZE));
    }

    /*
     * @author	EM-Creations
     * @ref		https://stackoverflow.com/questions/5824342/setting-the-global-font-for-a-java-application
     *
     * Sets the font for the whole application
     * Call at the start of the program to make all fonts the same
     */
    private static void setUIFont(javax.swing.plaf.FontUIResource f)
    {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
            {
                UIManager.put(key, f);
            }
        }
    }

    public static final String MSG_IN_USE =
            "Impossible de supprimer la ressource, "
                    + "elle est utilisée par des équipements.";

    public static void message(String message) {
        JOptionPane msg = new JOptionPane();
        msg.showMessageDialog(null, message);
    }

    private static final int MAX_MISMATCH = 0;

    public static ArrayList<String> findAllSimilar(String userInput, ArrayList<String> dictionary) {
        ArrayList<String> similarDictionary = new ArrayList<>();

        for(String fromDictionary : dictionary) {
            if(isSimilar(userInput, fromDictionary)) {
                similarDictionary.add(fromDictionary);
            }
        }

        return similarDictionary;
    }

    private static boolean isSimilar(String userInput, String fromDictionary) {
        if(userInput.length() > fromDictionary.length()) {
            return false;
        }

        return (minSubstringMismatch(userInput, fromDictionary) <= MAX_MISMATCH);
    }

    private static int minSubstringMismatch(String shorter, String longer) {
        int minMismatch = Integer.MAX_VALUE;

        for(int i = 0; i < longer.length() - shorter.length(); ++i) {
            int currMismatch = 0;

            for(int j = 0; j < shorter.length(); ++j) {
                if(shorter.charAt(j) != longer.charAt(i + j)) {
                    ++currMismatch;
                }
            }

            minMismatch = Math.min(minMismatch, currMismatch);
        }

        return minMismatch;
    }

    public static void addIngredientToArray(Ingredient newIngredient, ArrayList<Ingredient> ingredients) {
        boolean isAlreadyListed = false;

        for(Ingredient currIngredient : ingredients) {
            if(currIngredient.equals(newIngredient)) {
                isAlreadyListed = true;
                currIngredient.setQuantity(currIngredient.getQuantity() + newIngredient.getQuantity());
                break;
            }
        }

        if(!isAlreadyListed) {
            ingredients.add(new Ingredient(newIngredient.getItem(), newIngredient.getQuantity()));
        }
    }
}
