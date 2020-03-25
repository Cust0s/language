package main.studySpace;

import main.ConfigFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StudySpace extends JPanel {
    private ConfigFile myConfig;
    private int side = 5;
    private ArrayList<JPanel> sideWindows;

    private int leftColumnWidth;
    private int rightColumnWidth;

    private float sizeX;
    private float sizeY;

    public StudySpace(ConfigFile myConfig, int sizeX, int sizeY){
        this.myConfig = myConfig;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        setPreferredSize(new Dimension(sizeX, sizeY));
        setMaximumSize(new Dimension(sizeX, sizeY));
        setBackground(Color.GREEN);
        setLayout(new GridBagLayout());

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.PAGE_AXIS));
        if(side == 0){
            leftColumnWidth = sizeX;
        } else {
            leftColumnWidth = Math.round(this.sizeX / 3 * 2);
            System.out.println("/3*2: " + Math.round(this.sizeX / 3 * 2));
        }
        leftColumn.setPreferredSize(new Dimension(leftColumnWidth, sizeY));
        leftColumn.setBackground(Color.DARK_GRAY);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn,BoxLayout.PAGE_AXIS));
        rightColumnWidth = Math.round(this.sizeX / 3);
        System.out.println("/3: " + Math.round(this.sizeX /3));
        rightColumn.setPreferredSize(new Dimension(rightColumnWidth, sizeY));
        rightColumn.setBackground(Color.lightGray);

        //create title bar [1] that holds display name and number
        JPanel titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 1));
        titleBar.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        titleBar.add(new JLabel("Header"));
        titleBar.add(new JLabel("x/N"));
        leftColumn.add(titleBar);

        //create a new main window [2] that holds the main word
        JPanel mainWindow = new JPanel();
        if(myConfig.isAuxWindowEnabled()){
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        } else {
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 8));
        }
        mainWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        mainWindow.add(new JLabel("Main Window"));
        leftColumn.add(mainWindow);

        //create a new aux window [3]
        JPanel auxWindow = new JPanel();
        auxWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        auxWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        auxWindow.add(new JLabel("Aux Window"));
        if(myConfig.isAuxWindowEnabled()){
            leftColumn.add(auxWindow);
        }

        //create a new buttonBar [4]
        JPanel buttonBar = new JPanel();
        buttonBar.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 1));
        buttonBar.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        buttonBar.add(new JButton("Previous"));
        buttonBar.add(new JButton("End"));
        buttonBar.add(new JButton("Next"));
        leftColumn.add(buttonBar);

        //create a new sideWindow [5]
        sideWindows = new ArrayList<>(side);
        for(int i = 0; i < side; i++){
            JPanel sideWindow = new JPanel();
            sideWindow.setPreferredSize(new Dimension(rightColumnWidth, Math.round(this.sizeY / side)));
            //sideWindow.setBackground(new Color(255 - i*20,0,0));
            sideWindows.add(sideWindow);
            if(i == 0){
                sideWindow.setBorder(BorderFactory.createMatteBorder(1,0,1,1,Color.BLACK));
            } else {
                sideWindow.setBorder(BorderFactory.createMatteBorder(0,0,1,1,Color.BLACK));
            }
            sideWindow.add(new JLabel("Side Window " + (i+1)));
            rightColumn.add(sideWindow);
        }
        add(leftColumn);
        if(side != 0){
            add(rightColumn);
        }
    }
}
