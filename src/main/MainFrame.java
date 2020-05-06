package main;

import main.studySpace.StudySpace;
import mainMenu.MainMenuContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    public ConfigFile myConfig;
    private JPanel currentContent;  //holds the current displayed content e.g. Main Menu or study space
    final int sizeX = 1280;
    final int sizeY = 720;
    final Dimension size = new Dimension(sizeX,sizeY);

    MainFrame(ConfigFile myConfig){
        super("Language Learning");
        this.myConfig = myConfig;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(true);

        currentContent = new JPanel();
        currentContent.setLayout(new GridBagLayout());


        currentContent.add(new MainMenuContent(myConfig, size, this));

        getContentPane().add(currentContent);
        addMenu();

        revalidate();
        repaint();

        pack();
        setVisible(true);
    }

    public void startMainMenu(){
        currentContent.removeAll();
        currentContent.add(new MainMenuContent(myConfig, size, this));
    }

    public void startStudySpace(ArrayList<String> filePaths, int language, int numberOfItems){
        currentContent.removeAll();
        currentContent.add(new StudySpace(myConfig, this, sizeX, sizeY, filePaths, language, numberOfItems));
    }

    private void addMenu(){
        JMenuBar menuBar = new JMenuBar();

        //---File Menu---
        JMenu fileMenu = new JMenu("File");

        //- - Preferences Menu Item - -
        JMenuItem preferencesItem = new JMenuItem(new AbstractAction("Preferences") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PreferencesFrame(getThisParent(), myConfig);
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
