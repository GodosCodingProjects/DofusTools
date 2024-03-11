package View.PriceUpdateFrame;

import Model.AItem;
import Model.Enums;
import View.Const;
import com.github.kwhat.jnativehook.GlobalScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class PriceUpdateFrame extends JFrame implements Runnable {
    private ItemsUpdateGlobalListener itemsListener;
    private EquipmentsUpdateGlobalListener equipmentsListener;

    private JTextArea textArea;
    private String currentHeader;

    private ArrayList<ArrayList<AItem>> items;

    private int currentStep;

    private JFrame callingFrame;
    private JPanel callingPanel;

    public PriceUpdateFrame(ArrayList<ArrayList<AItem>> items, JFrame callingFrame, JPanel callingPanel) {
        this.items = items;

        init();
        this.callingFrame = callingFrame;
        this.callingFrame.setState(Frame.ICONIFIED);
        this.callingPanel = callingPanel;

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentStep = -1;
        nextStep();
    }

    public void run() {
        setVisible(true);
    }

    public void nextStep() {
        ++currentStep;

        if(currentStep < Enums.ItemType.N_TYPES.ordinal()) {
            if(items.get(currentStep).size() == 0) {
                nextStep();
                return;
            }

            itemsListener = null;
            equipmentsListener = null;

            currentHeader = "MàJ HDV des " + Enums.itemTypeNames[currentStep] + "s";

            if(currentStep == Enums.ItemType.EQUIPMENT.ordinal()) {
                equipmentsListener = new EquipmentsUpdateGlobalListener(items.get(currentStep), this);
            }
            else {
                itemsListener = new ItemsUpdateGlobalListener(items.get(currentStep), this);
            }
        }
        else {
            callingPanel.updateUI();
            callingFrame.setState(Frame.NORMAL);
            callingFrame.setAlwaysOnTop(true);
            callingFrame.requestFocus();
            callingFrame.setAlwaysOnTop(false);
            dispose();
        }
    }

    /*
     * Sets the values of the frame and creates its content pane
     */
    private void init() {
        // Frame settings
        Const.SET_SIZE(this, new Dimension(300, 100));
        setLocation(0, Const.WINDOW_HEIGHT - getHeight() - Const.TASKBAR_HEIGHT);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
        setOpacity(0.90f);
        addWindowListener(new ExitListener());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);

        textArea = new JTextArea("Overlay");
        textArea.setEditable(false);
        contentPanel.add(textArea);
    }

    private class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            // Save the current model
            callingFrame.setState(JFrame.NORMAL);
        }
    }

    public void display(String message) {
        textArea.setText(currentHeader + '\n' + message);
    }
}
