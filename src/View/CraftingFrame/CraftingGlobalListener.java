package View.CraftingFrame;

import Model.Craft;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.util.ArrayList;

public class CraftingGlobalListener implements NativeKeyListener, NativeMouseListener {
    private CraftingFrame updateFrame;

    private ArrayList<Craft> crafts;
    private ArrayList<Integer> amounts;
    private int currItem_i;

    public CraftingGlobalListener(ArrayList<Craft> crafts, ArrayList<Integer> amounts, CraftingFrame updateFrame) {
        try {
            GlobalScreen.addNativeKeyListener(this);

            this.crafts = crafts;
            this.amounts = amounts;
            this.updateFrame = updateFrame;

            currItem_i = -1;
            readyNextItem();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readyNextItem() throws NativeHookException {
        ++currItem_i;

        // If current item index is out of range, close the frame
        if(currItem_i >= crafts.size()) {
            GlobalScreen.removeNativeKeyListener(this);
            updateFrame.nextStep();
            return;
        }

        // If item's price is already updated, skip it
        if(crafts.get(currItem_i).isUpdated()) {
            readyNextItem();
            return;
        }

        // After potential errors have been considered, it is time to update
        updateFrame.display(crafts.get(currItem_i).getName() + " x" + amounts.get(currItem_i)
            + "\nEn attente de F8");
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
