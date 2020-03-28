package main.studySpace;

import main.ConfigFile;
import main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class StudySpace extends JPanel {
    private ConfigFile myConfig;
    private MainFrame frame;
    private ArrayList<String> filePaths;
    private int side = 5;
    private ArrayList<JPanel> sideWindows;
    private int selectedLanguage;
    private int numberOfRows;


    private int leftColumnWidth;
    private int rightColumnWidth;

    private float sizeX;
    private float sizeY;

    //swing components holding the dynamic text in the study space
    JLabel counterLabel;
    JLabel mainWindowLabel;
    JLabel mainWindowSolutionLabel;
    JLabel auxLabel;

    //flags the current status of the solution: is it displayed or not
    boolean solutionDisplayed = false;


    //storage for the data processed from the selected files
    private boolean hasAux = false;     //whether an aux window is needed
    private int maxSideWindows = 0;     //how many side windows are needed
    private ArrayList<DataRow> data = new ArrayList<>();   //each row of data split into seperate strings
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
     * a {@link DataRow} for each row read from the file. Each line is split inside of the {@link DataRow} constructor
     * and gets stored there.
     */
    public void processFiles(){

        for(String path : filePaths){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));

                String thisLine;
                while ((thisLine = reader.readLine()) != null) {
                    //data is split in the DataRow constructor
                    DataRow thisRow = new DataRow(thisLine, this);
                    data.add(thisRow);
                }
                //shuffle the rows
                Collections.shuffle(data);
                reader.close();
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
            DataRow thisRow = data.get(index);

            counterLabel.setText((index + 1) + "/" + numberOfRows);

            //update the main window
            switch(selectedLanguage){
                case 0:     //mixed language
                    //display the main language
                    if(thisRow.getRandomNumber() == 0){
                        mainWindowLabel.setText(thisRow.getLanguageA());
                    } else if(thisRow.getRandomNumber() == 1){
                        mainWindowLabel.setText(thisRow.getLanguageB());
                    }

                    //display the solution if required
                    if(displaySolution){
                        if(thisRow.getRandomNumber() == 0){
                            mainWindowLabel.setText(thisRow.getLanguageB());
                        } else if(thisRow.getRandomNumber() == 1){
                            mainWindowLabel.setText(thisRow.getLanguageA());
                        }
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
                case 1:     //language A
                    mainWindowLabel.setText(thisRow.getLanguageA());
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(thisRow.getLanguageB());
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
                case 2:     //language B
                    mainWindowLabel.setText(thisRow.getLanguageB());
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(thisRow.getLanguageA());
                    } else{
                        mainWindowSolutionLabel.setText("");
                    }
                    break;
            }

            auxLabel.setText(thisRow.getAux());

            //loop through the available side windows
            for(int i = 0; i < sideWindows.size(); i++){
                for(Component object: sideWindows.get(i).getComponents()){
                    if(object instanceof JLabel){
                        if(thisRow.getSideWindows() != null && i < thisRow.getSideWindows().size()){
                            ((JLabel) object).setText(thisRow.getSideWindows().get(i));
                        } else{
                            //in case this row does not have enough side windows, make the windows blank
                            ((JLabel) object).setText(null);
                        }
                    }
                }
            }
        }
    }

    public int getMaxSideWindows(){
        return maxSideWindows;
    }

    public void setMaxSideWindows(int maxSideWindows){
        this.maxSideWindows = maxSideWindows;
    }

    public void setHasAux(boolean hasAux){
        this.hasAux = hasAux;
    }


    private class ChangeContentActionListener implements ActionListener{
        int direction;
        ChangeContentActionListener(int direction){
            this.direction = direction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if(myConfig.isDisplayInstantSolution()){
                if(direction == 1){
                    if(solutionDisplayed){
                        if(index < numberOfRows -1) {
                            index++;
                            updateWindows(false);
                            solutionDisplayed = false;
                        }
                    } else {
                        updateWindows(true);
                        solutionDisplayed = true;
                    }
                } else if(direction == -1){
                    if(solutionDisplayed){
                        updateWindows(false);
                        solutionDisplayed = false;
                    } else{
                        if(index > 0){
                            index--;
                            updateWindows(true);
                            solutionDisplayed = true;
                        }
                    }
                }
            } else {
                if(direction == 1 && index < numberOfRows -1){
                        index++;
                        updateWindows(false);
                        //update solution displayed in case the displayInstantSolution
                        //property is changed while in study space
                        solutionDisplayed = false;
                } else if(direction == -1 && index > 0){
                        index--;
                        updateWindows(false);
                        //update solution displayed in case the displayInstantSolution
                        //property is changed while in study space
                        solutionDisplayed = true;
                }
            }
        }
    }
}