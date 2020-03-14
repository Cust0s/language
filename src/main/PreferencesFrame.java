package main;

import javax.swing.*;
import java.awt.*;

public class PreferencesFrame extends JDialog {

    public PreferencesFrame(JFrame parent){
        super(parent, "Title", true);
        getContentPane().setPreferredSize(new Dimension(400,200));
        getContentPane().setBackground(Color.CYAN);

        pack();
        setVisible(true);
    }
}
