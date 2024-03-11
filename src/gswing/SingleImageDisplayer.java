package gswing;

import View.Const;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SingleImageDisplayer extends JFrame implements Runnable {
    public void run() {
        setVisible(true);
    }

    public SingleImageDisplayer(BufferedImage image) {
        init(image);
    }

    private void init(BufferedImage image) {
        Const.SET_SIZE(this, Const.FULL_WINDOW_SIZE);

        JPanel pane = new JPanel();
        setContentPane(pane);

        pane.add(new JLabel(new ImageIcon(image)));
    }
}
