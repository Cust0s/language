package main;

import mainMenu.MainMenuContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    public Settings currentSettings;
    final int sizeX = 1280;
    final int sizeY = 720;

    MainFrame(Settings currentSettings){
        super("Language Learning");
        this.currentSettings = currentSettings;
        getContentPane().setPreferredSize(new Dimension(sizeX, sizeY));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        getContentPane().add(new MainMenuContent(currentSettings));

        addMenu();


        pack();
        setVisible(true);
    }

    public void addMenu(){
        JMenuBar menuBar = new JMenuBar();

        //---File Menu---
        JMenu fileMenu = new JMenu("File");

        //- - Preferences Menu Item - -
        JMenuItem preferencesItem = new JMenuItem(new AbstractAction("Preferences") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PreferencesFrame(getThisParent(), currentSettings);
                System.out.println("PREFERENCES WINDOW");
            }
        });



        fileMenu.add(preferencesItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

    }

    JFrame getThisParent(){
        return this;
    }
}
