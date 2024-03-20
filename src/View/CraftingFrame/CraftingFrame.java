package View.CraftingFrame;

import Model.Craft;
import Model.Enums;
import Model.Ingredient;
import View.Const;
import com.github.kwhat.jnativehook.GlobalScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class CraftingFrame extends JFrame implements Runnable {
    private ItemsBuyGlobalListener buyListener;
    private CraftingGlobalListener craftListener;

    private JTextArea textArea;
    private String currentHeader;
    private JFrame callingFrame;
    private JPanel callingPanel;

    private ArrayList<ArrayList<Craft>> crafts;
    private ArrayList<ArrayList<Integer>> amounts;
    private ArrayList<ArrayList<Ingredient>> ingredients;

    private int currentStep;

    public CraftingFrame(ArrayList<ArrayList<Craft>> crafts, ArrayList<ArrayList<Integer>> amounts, JFrame callingFrame, JPanel callingPanel) {
        this.crafts = crafts;
        this.amounts = amounts;
        this.callingFrame = callingFrame;
        this.callingFrame.setState(Frame.ICONIFIED);
        this.callingPanel = callingPanel;

        init();

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ingredients = new ArrayList<>();
        for(int i = 0; i < Enums.ItemType.N_TYPES.ordinal(); ++i) {
            ingredients.add(new ArrayList<>());
        }
        makeIngredientList();

        currentStep = -1;
        nextStep();
    }

    public void run() {
        setVisible(true);
    }

    private void makeIngredientList() {
        for(int i = 0; i < crafts.size(); ++i) {
            for(int j = 0; j < crafts.get(i).size(); ++j) {
                for(Ingredient ingredient : crafts.get(i).get(j).getCraftIngredients()) {
                    Ingredient completeIngredient = new Ingredient(ingredient.getItem(), ingredient.getQuantity() * amounts.get(i).get(j));
                    Const.addIngredientToArray(completeIngredient, ingredients.get(completeIngredient.getItem().getType().ordinal()));
                }
            }
        }
    }

    public void nextStep() {
        ++currentStep;

        if(currentStep < Enums.ItemType.N_TYPES.ordinal()) {
            if(ingredients.get(currentStep).size() == 0) {
                nextStep();
                return;
            }

            buyListener = null;

            currentHeader = "Achat HDV des " + Enums.itemTypeNames[currentStep] + "s";
            buyListener = new ItemsBuyGlobalListener(ingredients.get(currentStep), this);
        }
        else if(currentStep < Enums.ItemType.N_TYPES.ordinal() + Enums.Job.N_JOBS.ordinal()){
            int curr_i = currentStep - Enums.ItemType.N_TYPES.ordinal();
            if (crafts.get(curr_i).size() == 0) {
                nextStep();
                return;
            }

            buyListener = null;
            craftListener = null;

            currentHeader = "Craft à l'atelier des " + Enums.jobNames[currentStep - Enums.ItemType.N_TYPES.ordinal()] + "s";
            craftListener = new CraftingGlobalListener(crafts.get(curr_i), amounts.get(curr_i), this);
        }
        else {
            callingPanel.updateUI();
            callingFrame.setState(Frame.NORMAL);
            dispose();
        }
    }

    /*
     * Sets the values of the frame and creates its content pane
     */
    private void init() {
        // Frame settings
        Const.SET_SIZE(this, new Dimension(350, 100));
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
