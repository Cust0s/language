package main;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final int WORDS = 1;
    public static final int PHRASES = 2;
    public static final int SPECIAL = 3;

    public static final int LANGUAGE_A = 1;
    public static final int LANGUAGE_B = 2;
    public static final int MIXED = 0;

    //public static String fontName =
    public static ConfigFile myConfig;

    public static void main(String[] args) {
        UIManager.getLookAndFeelDefaults()
                .put("defaultFont", new Font("Malgun Gothic", Font.PLAIN, 20));
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        myConfig = new ConfigFile("config.properties");

        MainFrame frame = new MainFrame(myConfig);

    }
}
