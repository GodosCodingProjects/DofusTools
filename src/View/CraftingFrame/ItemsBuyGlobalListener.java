package View.CraftingFrame;

import Model.Ingredient;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

public class ItemsBuyGlobalListener implements NativeKeyListener, NativeMouseListener {
    private CraftingFrame updateFrame;

    private ArrayList<Ingredient> ingredients;
    private int currItem_i;

    public ItemsBuyGlobalListener(ArrayList<Ingredient> ingredients, CraftingFrame updateFrame) {
        try {
            GlobalScreen.addNativeKeyListener(this);

            this.updateFrame = updateFrame;

            this.ingredients = ingredients;
            currItem_i = -1;

            readyNextItem();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readyNextItem() {
        ++currItem_i;

        // If current item index is out of range
        if(currItem_i >= ingredients.size()) {
            GlobalScreen.removeNativeKeyListener(this);
            updateFrame.nextStep();
            return;
        }

        // If item's price is already updated, skip it
        if(ingredients.get(currItem_i).getItem().isUpdated()) {
            readyNextItem();
            return;
        }

        // After potential errors have been considered, it is time to update
        copyToClipboard(ingredients.get(currItem_i).getItem().getName());
        updateFrame.display(ingredients.get(currItem_i).getItem().getName() + " x" + ingredients.get(currItem_i).getQuantity());
    }

    private static void copyToClipboard(String content) {
        StringSelection selection = new StringSelection(content);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    // Key Listener
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Never use this event, the key code is always "Undefined"
    }
    public void nativeKeyPressed(NativeKeyEvent e) {

    }
    public void nativeKeyReleased(NativeKeyEvent e) {
        if(e.getKeyCode() == NativeKeyEvent.VC_F8) {
            try {
                readyNextItem();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
