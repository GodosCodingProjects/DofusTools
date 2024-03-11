package View.MainFrame;

import Model.CommDB;
import View.Const;
import View.MainFrame.CraftsPage.CraftsPage;
import View.MainFrame.CreationPage.CraftCreator;
import View.MainFrame.CreationPage.ItemCreator;

import javax.swing.*;
import java.awt.event.*;

public class MainFrame extends JFrame implements Runnable {
    public void run() {
        init();
        setVisible(true);
    }

    /*
     * Sets the values of the frame and creates its content pane
     */
    private void init() {
        // Frame settings
        setTitle("Dofus : Outil de craft");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Const.SET_SIZE(this, Const.FULL_WINDOW_SIZE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new ExitListener());

        // Set the content pane
        JTabbedPane tabManager = new JTabbedPane();
        setContentPane(tabManager);
        tabManager.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                ((JPanel)(tabManager.getSelectedComponent())).updateUI();
            }
        });

        tabManager.add(new CraftsPage(this), "Crafts");

        JPanel creationPage = new JPanel();
        creationPage.setLayout(new BoxLayout(creationPage, BoxLayout.PAGE_AXIS));
        creationPage.add(new ItemCreator());
        creationPage.add(new CraftCreator());
        tabManager.add(creationPage, "Ajouts");
    }

    private class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            // Save the current model
            CommDB.saveAll();
        }
    }
}
