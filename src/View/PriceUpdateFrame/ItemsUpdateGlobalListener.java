package View.PriceUpdateFrame;

import Model.AItem;
import Model.Item;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.imageio.ImageIO;
import java.io.File;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ItemsUpdateGlobalListener implements NativeKeyListener, NativeMouseListener {
    private static final int SHOP_LOAD_TIME = 300; // In ms

    private ImgToStringParser parser;
    private PriceUpdateFrame updateFrame;

    private ArrayList<AItem> items;
    private int currItem_i;

    private enum State {
        PROCESSING,
        WAITING_FOR_CTRL,
        WAITING_FOR_PASTE,
        WAITING_FOR_CLICK
    };
    private State state;

    public ItemsUpdateGlobalListener(ArrayList<AItem> items, PriceUpdateFrame updateFrame) {
        try {
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);

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
            GlobalScreen.removeNativeMouseListener(this);
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

    private static final int PRICES_TOP = 206;
    private static final int PRICES_LEFT = 900;
    private static final int PRICES_ROW_HEIGHT = 46;
    private static final int PRICES_WIDTH = 127;

    private static final int LOTS_TOP = PRICES_TOP;
    private static final int LOTS_LEFT = 830;
    private static final int LOTS_ROW_HEIGHT = PRICES_ROW_HEIGHT;
    private static final int LOTS_WIDTH = PRICES_LEFT - LOTS_LEFT;

    private static final int CUTOUT_LOW = 110;
    private static final int CUTOUT_HIGH = 140;

    private void updatePrice(Item item, int mouseY) throws Exception {
        //BufferedImage img = ImageIO.read(new File("resources/dofus3.png"));
        BufferedImage img = parser.takeScreenshot(); //ImageIO.write(img, "png", new File("resources/dofus.png"));

        int prices[] = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

        int mouseDelta = mouseY - PRICES_TOP;
        int row = mouseDelta / PRICES_ROW_HEIGHT;
        int targetRowTop = PRICES_TOP + row * PRICES_ROW_HEIGHT;

        System.out.println("MouseY: " + mouseY + ", Delta: " + mouseDelta);
        System.out.println("Row: " + row + ", Top at: " + targetRowTop);

        for(int i = 0; i < 3; ++i) {
            BufferedImage croppedLotImg = img.getSubimage(LOTS_LEFT, targetRowTop + LOTS_ROW_HEIGHT * (i + 1), LOTS_WIDTH, LOTS_ROW_HEIGHT);
            BufferedImage imgLot = parser.processImage(croppedLotImg, CUTOUT_LOW);

            BufferedImage croppedPriceImg = img.getSubimage(PRICES_LEFT, targetRowTop + PRICES_ROW_HEIGHT * (i + 1), PRICES_WIDTH, PRICES_ROW_HEIGHT);
            BufferedImage imgPriceLow = parser.processImage(croppedPriceImg, CUTOUT_LOW);
            BufferedImage imgPriceHigh = parser.processImage(croppedPriceImg, CUTOUT_HIGH);

            String strPriceLow = parser.getTextFromImage(imgPriceLow, "0123456789");
            if(strPriceLow.equals("")) continue;

            String strFinalPrice = parser.processEdgeCases(imgPriceHigh, strPriceLow);
            double finalPrice = Integer.parseInt(strFinalPrice);

            String strLot = parser.getTextFromImage(imgLot, "0123456789lot de").substring(5);
            double quantity = Integer.parseInt(strLot);

            prices[i] = (int) Math.round(finalPrice/quantity);

            // Outputs for testing purposes
            System.out.println("Prix: "+(int)finalPrice+", Qtt: "+(int)quantity+", Prix unitaire:"+(int)finalPrice/quantity);
            //ImageIO.write(imgLot, "png", new File("resources/testLot"+i+".png"));
            //ImageIO.write(imgPriceLow, "png", new File("resources/test"+i+".png"));

            //if(i==1) saveNthCharacterImage(imgLow, 0, "large_five");
        }

        item.setPrice(prices[0], prices[1], prices[2]);

        System.out.println("");
        System.out.println("Updated price for: " + item.getName() + "\nPrice is: " + item.getPrice());
        System.out.println("-------------------------------------------");
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
            state = State.WAITING_FOR_CLICK;
            updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de CLICK");
        }
    }
    public void nativeKeyReleased(NativeKeyEvent e) {
        if(state == State.WAITING_FOR_PASTE && e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            state = State.WAITING_FOR_CTRL;
            updateFrame.display(items.get(currItem_i).getName()+"\nEn attente de CTRL");
        }
    }

    // Mouse Listener
    public void nativeMouseClicked(NativeMouseEvent e) {}
    public void nativeMousePressed(NativeMouseEvent e) {}
        public void nativeMouseReleased(NativeMouseEvent e) {
            if(state == State.WAITING_FOR_CLICK) {
                state = State.PROCESSING;
                try {
                    Thread.currentThread().sleep(SHOP_LOAD_TIME);

                    updateFrame.display(items.get(currItem_i).getName()+"\nMise à jour du prix...");
                    updatePrice((Item) items.get(currItem_i), e.getY());
                    items.get(currItem_i).setUpdated(true);
                    readyNextItem();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
    }
}
