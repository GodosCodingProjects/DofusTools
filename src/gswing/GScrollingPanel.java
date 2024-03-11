package gswing;

import View.Const;

import java.awt.*;

import javax.swing.*;

/*
 * A "container" for a JPanel that displays it with a vertical scrollbar.
 */
public class GScrollingPanel extends JPanel {

    public GScrollingPanel(JPanel panel) {
        init(panel);
    }

    private void init(JPanel panel) {
        setLayout(new BorderLayout());

        Scrollable scrollable = new Scrollable();
        Const.SET_SIZE(scrollable, panel.getMaximumSize());
        scrollable.add(panel);

        JScrollPane scroller = new JScrollPane(scrollable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller, BorderLayout.CENTER);
    }

    private class Scrollable extends JPanel implements javax.swing.Scrollable {
        private static final int SCROLL_SPEED = Const.ROW_HEIGHT;

        public Dimension getPreferredScrollableViewportSize() {
            return null;
        }
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return SCROLL_SPEED;
        }
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return SCROLL_SPEED;
        }
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}