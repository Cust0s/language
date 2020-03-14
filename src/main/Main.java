package main;

import javax.swing.*;
import java.awt.*;

public class Main {
    static Settings currentSettings;

    public static void main(String[] args) {
        UIManager.getLookAndFeelDefaults()
                .put("defaultFont", new Font("Malgun Gothic", Font.PLAIN, 20));
        currentSettings = new Settings();
        MainFrame frame = new MainFrame(currentSettings);
    }
}
