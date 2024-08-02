package org.cis1200;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

public class RunCheckers implements Runnable {

    /**
     * Game class to run Checkers as a stand-alone
     * application.
     */
    public void run() {

        JFrame window = new JFrame("Checkers App");
        CheckersMainPanel content = new CheckersMainPanel();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(
                (screensize.width - window.getWidth()) / 2,
                (screensize.height - window.getHeight()) / 2
        );
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
    }
}
