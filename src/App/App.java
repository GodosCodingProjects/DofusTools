package App;

import View.Const;
import View.MainFrame.MainFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        Const.initGlobalSettings();
        SwingUtilities.invokeLater(new MainFrame());
    }

    /*
     * TODO: ULTIMATE EDITION
     *
     * - Include buy-resell calculation module for different quantities of an item.
     *
     * TODO: Would be nice (AKA, might never implement)
     *
     * - Replace wasModified boolean to a timestamp for each price (update anything after x amount of time).
     *   - Keep in memory prices for a certain amount of time, as a histogram. Maybe display it in a details window for items.
     *     - Implement inflation calculator using average data for all items.
     *   - Have a way to see which items are way bellow normal prices, and possibly estimate when the price will be back up.
     *
     * - Implement a price fluctuation calculator, that keeps track of price augmentation through a series of buy (actual final price vs start price, given x items bought).
     *   - Take this into account when calculating profits.
     *
     * - Improve the CraftsPage's searchbar to interactively display all items that match the search term.
     *
     * - Fix panels contained in a ScrollingPanel (the width seems way too large and popups are de-centered).
     *
     */
}
