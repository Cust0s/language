package main.studySpace;

import main.ConfigFile;
import main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class StudySpace extends JPanel {
    private ConfigFile myConfig;
    private MainFrame frame;
    private ArrayList<String> filePaths;
    private int side = 5;
    private ArrayList<JPanel> sideWindows;
    private int selectedLanguage;
    private int numberOfRows;

    private ArrayList<ArrayList<String>> currentSessionData;

    private int leftColumnWidth;
    private int rightColumnWidth;

    private float sizeX;
    private float sizeY;

    //counter in the header section
    JLabel counterLabel;
    //main window label
    JLabel mainWindowLabel;
    JLabel mainWindowSolutionLabel;
    JLabel auxLabel;


    //storage for the data processed from the selected files
    private boolean hasAux = false;     //whether an aux window is needed
    private int maxSideWindows = 0;     //how many side windows are needed
    private ArrayList<String[]> data = new ArrayList<>();   //each row of data split into seperate strings
    private int index = 0;

    public StudySpace(ConfigFile myConfig, MainFrame frame, int sizeX, int sizeY, ArrayList<String> filePaths, int language, int numberOfRows){
        this.myConfig = myConfig;
        this.frame = frame;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.filePaths = filePaths;
        this.selectedLanguage = language;
        processFiles();
        //set the number of items that should be queried.
        if(numberOfRows < data.size()){
            this.numberOfRows = numberOfRows;
        } else{
            this.numberOfRows = data.size();
        }
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
        }
        leftColumn.setPreferredSize(new Dimension(leftColumnWidth, sizeY));
        leftColumn.setBackground(Color.DARK_GRAY);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn,BoxLayout.PAGE_AXIS));
        rightColumnWidth = Math.round(this.sizeX / 3);
        rightColumn.setPreferredSize(new Dimension(rightColumnWidth, sizeY));
        rightColumn.setBackground(Color.lightGray);


        //create title bar [1] that holds display name and number
        JPanel titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 1));
        titleBar.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        titleBar.add(new JLabel("Header"));
        counterLabel = new JLabel("x/n");
        titleBar.add(counterLabel);
        leftColumn.add(titleBar);


        //create a new main window [2] that holds the main word
        JPanel mainWindow = new JPanel();
        mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.PAGE_AXIS));
        if(hasAux){
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        } else {
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 8));
        }
        mainWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        mainWindowLabel = new JLabel("Main Window");
        mainWindowSolutionLabel = new JLabel("Solution Label");
        mainWindow.add(mainWindowLabel);
        mainWindow.add(mainWindowSolutionLabel);
        leftColumn.add(mainWindow);

        //create a new aux window [3]
        JPanel auxWindow = new JPanel();
        auxWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        auxWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        auxLabel = new JLabel("Aux Window");
        auxWindow.add(auxLabel);
        if(hasAux){
            leftColumn.add(auxWindow);
        }

        //create a new buttonBar [4]
        JPanel buttonBar = new JPanel();
        buttonBar.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 1));
        buttonBar.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        JButton prevButton = new JButton("Previous");
        prevButton.addActionListener(new ChangeContentActionListener(-1));
        buttonBar.add(prevButton);
        JButton endButton = new JButton("End");
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Endbutton");

                frame.startMainMenu();
                frame.revalidate();
                frame.repaint();
            }
        });
        buttonBar.add(endButton);
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ChangeContentActionListener(1));
        buttonBar.add(nextButton);

        leftColumn.add(buttonBar);

        //create a new sideWindow [5]
        sideWindows = new ArrayList<>(maxSideWindows);
        for(int i = 0; i < maxSideWindows; i++){
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
        if(maxSideWindows != 0){
            add(rightColumn);
        }
        updateWindows(false);
    }

    /**
     * Processes the given file paths by opening each file and reading the data into an ArrayList containing
     * an Array for each row. Each line is split at the following points:
     * <p>[Main]<br>
     * [Aux]<br>
     * [Side x] where x is the number of the side window</p>
     */
    public void processFiles(){

        for(String path : filePaths){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));

                String thisLine;
                while ((thisLine = reader.readLine()) != null) {
                    String[] temp = thisLine.split("\\[Language A\\]|\\[Language B\\]|\\[Aux\\]|\\[Side \\d\\]");

                    //anything < 3 will not have at least two strings for language A and B
                    if(temp.length >= 3){    //temp will contain at least [, language A, language B, ...] >= 3
                        String[] rowContents = Arrays.copyOfRange(temp, 1, temp.length);

                        //case: languageA, languageB, aux, side x
                        //      [languageA, languageB, aux, side x, ...]
                        //array.length >= 4

                        //case: languageA, languageB, side x
                        //      [languageA, languageB, , side x, ...]
                        //array.length >= 4

                        //case: languageA, languageB, aux
                        //      [languageA, languageB, aux]
                        //array.length = 3

                        //case: languageA, languageB
                        //      [language A, languageB]
                        //array.length = 2


                        data.add(rowContents);
                        if((rowContents.length >= 4 && !rowContents[2].equals("")) || rowContents.length == 3){
                            //if there are 3 entries in the array, temp[1] (aux) is in bounds. If the string
                            //is not empty, then aux content exists

                            //aux needed
                            hasAux = true;
                        }

                        if(rowContents.length - 3 > maxSideWindows){
                            maxSideWindows = rowContents.length -3;
                        }
                        System.out.println(Arrays.toString(rowContents));
                        System.out.println("Max side windows: " + maxSideWindows + " aux: " + hasAux);
                    }
                }
                Collections.shuffle(data);
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //inform the user if there was no data to collect from the selected files
        if(data.isEmpty()){
            //ToDo inform the user that there is no valid data in all the selected files
            System.out.println("Selected files have no valid data");
        }
    }

    //updates all the windows with the data from the currently selected row
    private void updateWindows(boolean displaySolution){
        if (index < data.size() && index >= 0){
            String temp[] = data.get(index);


            counterLabel.setText((index + 1) + "/" + numberOfRows);

            //update the main window
            switch(selectedLanguage){
                case 0:     //mixed language
                    Random rnd = new Random();
                    int rndNumber = rnd.nextInt((1 - 0) + 1) + 0;
                    mainWindowLabel.setText(temp[rndNumber]);
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(temp[1-rndNumber]);
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
                case 1:     //language A
                    mainWindowLabel.setText(temp[0]);
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(temp[1]);
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
                case 2:     //language B
                    mainWindowLabel.setText(temp[1]);
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(temp[0]);
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
            }




            //update the aux window
            if(temp.length > 3){
                auxLabel.setText(temp[3]);
            } else {
                //in case this row does not have any aux or side windows, make aux blank
                auxLabel.setText("");
            }

            //loop through the available side windows
            for(int i = 0; i < sideWindows.size(); i++){
                for(Component object: sideWindows.get(i).getComponents()){
                    if(object instanceof JLabel){
                        if((i + 3) < temp.length) {
                            ((JLabel) object).setText(temp[i + 3]);
                        } else{
                            //in case this row does not have enough side windows, make the windows blank
                            ((JLabel) object).setText("");
                        }
                    }
                }
            }
        }
    }


    private class ChangeContentActionListener implements ActionListener{
        int direction;
        //whether to advance to the next item or display the solution
        boolean displayNextSolution = false;
        boolean displayPreviousSolution = true;

        ChangeContentActionListener(int direction){
            this.direction = direction;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
//            if(myConfig.isDisplayInstantSolution() && !displayNextSolution && index <= numberOfRows - 1){
//                    updateWindows(true);
//                    displayNextSolution = true;
//                displayPreviousSolution = true;
//            } else if(myConfig.isDisplayInstantSolution() && !displayPreviousSolution && index >= 0){
//                updateWindows(false);
//                displayPreviousSolution = true;
//                displayNextSolution = true;
//
//            } else {

                if(direction == 1/* && index < numberOfRows - 1 */) { //next
                    if(displayNextSolution){
                        updateWindows(true);
                        displayNextSolution = false;
                    } else {
                        if(index < numberOfRows - 1){
                            index++;
                        }
                        displayNextSolution = true;
                        updateWindows(false);
                    }
                } else if (direction == -1/* && index > 0*/){    //previous


                    if(displayNextSolution){
                        updateWindows(false);
                        displayNextSolution = false;
                    } else {
                        if(index > 0){
                            index--;
                        }
                        displayNextSolution = true;
                        updateWindows(true);
                    }
                }

                //displayNextSolution = false;

            //}
        }
    }
}
