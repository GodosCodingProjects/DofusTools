package View.PriceUpdateFrame;

import Model.AItem;
import Model.Craft;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import java.io.File;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EquipmentsUpdateGlobalListener implements NativeKeyListener {
    private static final int SHOP_LOAD_TIME = 300; // In ms

    private ImgToStringParser parser;
    private PriceUpdateFrame updateFrame;

    private ArrayList<AItem> items;
    private int currItem_i;

    private enum State {
        PROCESSING,
        WAITING_FOR_CTRL,
        WAITING_FOR_PASTE,
        WAITING_FOR_UPDATE
    };
    private State state;

    public EquipmentsUpdateGlobalListener(ArrayList<AItem> items, PriceUpdateFrame updateFrame) {
        try {
            GlobalScreen.addNativeKeyListener(this);

            parser = new ImgToStringParser();
            this.updateFrame = updateFrame;
            state = State.PROCESSING;

            this.items = items;
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
        if(currItem_i >= items.size()) {
            GlobalScreen.removeNativeKeyListener(this);
            updateFrame.nextStep();
            return;
        }

        // If item's price is already updated, skip it
        if(items.get(currItem_i).isUpdated()) {
            readyNextItem();
            return;
        }

        // After potential errors have been considered, it is time to update
        copyToClipboard(items.get(currItem_i).getName());
        state = State.WAITING_FOR_CTRL;
        updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de CTRL");
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
        if(state == State.WAITING_FOR_CTRL && e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            state = State.WAITING_FOR_PASTE;
            updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de V");
        }
        else if(state == State.WAITING_FOR_PASTE && e.getKeyCode() == NativeKeyEvent.VC_V) {
            state = State.WAITING_FOR_UPDATE;
            updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de F8");
        }
        else if(state == State.WAITING_FOR_UPDATE && e.getKeyCode() == NativeKeyEvent.VC_F8) {
            state = State.PROCESSING;
            try {
                Thread.currentThread().sleep(SHOP_LOAD_TIME);

                updateFrame.display(items.get(currItem_i).getName()+"\nMise à jour du prix...");
                updatePrice((Craft) items.get(currItem_i), MouseInfo.getPointerInfo().getLocation().y);
                items.get(currItem_i).setUpdated(true);
                readyNextItem();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public void nativeKeyReleased(NativeKeyEvent e) {
        if(state == State.WAITING_FOR_PASTE && e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            state = State.WAITING_FOR_CTRL;
            updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de CTRL");
        }
    }

    private static final int PRICES_TOP = 252;
    private static final int PRICES_LEFT = 900;
    private static final int PRICES_ROW_HEIGHT = 46;
    private static final int PRICES_WIDTH = 127;

    private static final int CUTOUT_LOW = 120;
    private static final int CUTOUT_HIGH = 150;

    private void updatePrice(Craft craft, int mouseY) throws Exception {
        //BufferedImage img = ImageIO.read(new File("resources/dofus3.png"));
        BufferedImage img = parser.takeScreenshot(); //ImageIO.write(img, "png", new File("resources/dofus.png"));

        int mouseDelta = mouseY - PRICES_TOP;
        int row = mouseDelta / PRICES_ROW_HEIGHT;
        int targetRowTop = PRICES_TOP + row * PRICES_ROW_HEIGHT;

        // Acquire the subimage for the price
        BufferedImage croppedPriceImg = img.getSubimage(PRICES_LEFT, targetRowTop, PRICES_WIDTH, PRICES_ROW_HEIGHT);
        BufferedImage imgPriceLow = parser.processImage(croppedPriceImg, CUTOUT_LOW);
        BufferedImage imgPriceHigh = parser.processImage(croppedPriceImg, CUTOUT_HIGH);

        // Parse the image and update the price
        String strPriceLow = parser.getTextFromImage(imgPriceLow, "0123456789");
        if(strPriceLow.equals("")) return;

        String strFinalPrice = parser.processEdgeCases(imgPriceHigh, strPriceLow);
        craft.setPrice(Integer.parseInt(strFinalPrice));

        //ImageIO.write(imgPriceLow, "png", new File("resources/testCraft.png"));

        System.out.println("Updated price for: "+craft.getName()+"\nPrice is: "+strFinalPrice);
    }
}
