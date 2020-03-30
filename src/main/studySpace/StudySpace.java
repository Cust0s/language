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
    private ArrayList<SidePanel> sideWindows;
    private int selectedLanguage;
    private int numberOfRows;


    private float sizeX;
    private float sizeY;

    //swing components holding the dynamic text in the study space
    JLabel counterLabel;
    JLabel mainWindowLabel;
    JLabel mainWindowSolutionLabel;
    JLabel auxMainLabel;
    JLabel auxSolutionLabel;
    JLabel auxWindowNameLabel;

    private Font titleFont;
    private Font contentFont;
    private Font mainFontPlain;
    private Font mainFontBold;

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
        myConfig.addStudySpace(this);

        titleFont = new Font("Malgun Gothic Semilight", Font.BOLD, 20);
        contentFont = new Font("Malgun Gothic Semilight", Font.PLAIN, 20);
        mainFontPlain = new Font("Malgun Gothic Semilight", Font.PLAIN, 30);
        mainFontBold = new Font("Malgun Gothic Semilight", Font.BOLD, 30);


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
        int leftColumnWidth;
        if(maxSideWindows == 0){
            leftColumnWidth = sizeX;
        } else {
            leftColumnWidth = Math.round(this.sizeX / 3 * 2);
        }
        leftColumn.setPreferredSize(new Dimension(leftColumnWidth, sizeY));
        leftColumn.setBackground(Color.DARK_GRAY);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn,BoxLayout.PAGE_AXIS));
        int rightColumnWidth = Math.round(this.sizeX / 3);
        rightColumn.setPreferredSize(new Dimension(rightColumnWidth, sizeY));
        rightColumn.setBackground(Color.lightGray);


        //-----
        //create title bar [1] that holds display name and number
        //-----
        JPanel titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 1));
        titleBar.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        JLabel headerLabel = new JLabel("Header");
        headerLabel.setFont(titleFont);
        titleBar.add(headerLabel);
        counterLabel = new JLabel("x/n");
        counterLabel.setFont(contentFont);
        titleBar.add(counterLabel);
        leftColumn.add(titleBar);


        //-----
        //create a new main window [2] that holds the main word
        //-----
        JPanel mainWindow = new JPanel();
        mainWindow.setLayout(new GridBagLayout());
        JPanel mainWindowContent = new JPanel();
        //mainWindowComponent is needed because the PAGE_AXIS BoxLayout only regards
        //the preferred height, not the preferred width
        mainWindowContent.setLayout(new BoxLayout(mainWindowContent, BoxLayout.PAGE_AXIS));
        if(hasAux){
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        } else {
            mainWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 8));
        }
        mainWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        mainWindowLabel = new JLabel("Main Window");
        mainWindowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainWindowLabel.setFont(mainFontBold);
        mainWindowSolutionLabel = new JLabel("Solution Label");
        mainWindowSolutionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainWindowSolutionLabel.setFont(mainFontPlain);
        mainWindowContent.add(mainWindowLabel);
        mainWindowContent.add(mainWindowSolutionLabel);
        mainWindow.add(mainWindowContent);
        leftColumn.add(mainWindow);

        //-----
        //create a new aux window [3]
        //-----
        JPanel auxWindow = new JPanel();
        JPanel auxWindowContent = new JPanel();
        //auxWindowContent is needed because the PAGE_AXIS BoxLayout only regards
        //the preferred height, not the preferred width
        auxWindowContent.setLayout(new BoxLayout(auxWindowContent, BoxLayout.PAGE_AXIS));
        auxWindow.setPreferredSize(new Dimension(leftColumnWidth, sizeY / 10 * 4));
        auxWindow.setBorder(BorderFactory.createMatteBorder(0,1,1,1,Color.BLACK));
        //label for the title of the window
        auxWindowNameLabel = new JLabel("Aux Window");
        auxWindowNameLabel.setFont(titleFont);
        auxWindowNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        auxWindowContent.add(auxWindowNameLabel);

        //label A for the content text of the window
        auxMainLabel = new JLabel("Aux A Window");
        auxMainLabel.setFont(contentFont);
        auxMainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        auxWindowContent.add(auxMainLabel);

        //label B for the content text of the window
        auxSolutionLabel = new JLabel("Aux B Window");
        auxSolutionLabel.setFont(contentFont);
        auxSolutionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        auxWindowContent.add(auxSolutionLabel);
        auxWindow.add(auxWindowContent);
        if(hasAux){
            leftColumn.add(auxWindow);
        }

        //-----
        //create a new buttonBar [4]
        //-----
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

        //-----
        //create a new sideWindow [5]
        //-----
        sideWindows = new ArrayList<>(maxSideWindows);
        for(int i = 0; i < maxSideWindows; i++){
            SidePanel sideWindowContent = new SidePanel("Side Window " + (i+1) + " title", "Side Window " + (i+1), titleFont, contentFont);
            sideWindowContent.setPreferredSize(new Dimension(rightColumnWidth, Math.round(this.sizeY / maxSideWindows)));

            if(i == 0){
                sideWindowContent.setBorder(BorderFactory.createMatteBorder(1,0,1,1,Color.BLACK));
            } else {
                sideWindowContent.setBorder(BorderFactory.createMatteBorder(0,0,1,1,Color.BLACK));
            }
            sideWindows.add(sideWindowContent);
            rightColumn.add(sideWindowContent);
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

                String firstLine = reader.readLine();
                String thisLine;
                while ((thisLine = reader.readLine()) != null) {
                    //data is split in the DataRow constructor
                    DataRow thisRow = new DataRow(thisLine, this, firstLine);
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

            //update the main window and aux window
            switch(selectedLanguage){
                case 0:     //mixed language
                    //display the main language
                    if(thisRow.getRandomNumber() == 0){
                        mainWindowLabel.setText(thisRow.getLanguageA());
                        auxMainLabel.setText(thisRow.getAuxA());
                    } else if(thisRow.getRandomNumber() == 1){
                        mainWindowLabel.setText(thisRow.getLanguageB());
                        auxMainLabel.setText(thisRow.getAuxB());
                    }

                    //display the solution if required
                    if(displaySolution){
                        if(thisRow.getRandomNumber() == 0){
                            mainWindowSolutionLabel.setText(thisRow.getLanguageB());
                            auxSolutionLabel.setText(thisRow.getAuxB());
                        } else if(thisRow.getRandomNumber() == 1){
                            mainWindowSolutionLabel.setText(thisRow.getLanguageA());
                            auxSolutionLabel.setText(thisRow.getAuxA());
                        }
                    } else{
                        mainWindowSolutionLabel.setText("");
                        auxSolutionLabel.setText("");
                    }
                    break;
                case 1:     //language A
                    mainWindowLabel.setText(thisRow.getLanguageA());
                    auxMainLabel.setText(thisRow.getAuxA());
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(thisRow.getLanguageB());
                        auxSolutionLabel.setText(thisRow.getAuxB());
                    } else{
                        mainWindowSolutionLabel.setText("");
                        auxSolutionLabel.setText("");
                    }
                    break;
                case 2:     //language B
                    mainWindowLabel.setText(thisRow.getLanguageB());
                    auxMainLabel.setText(thisRow.getAuxB());
                    if(displaySolution){
                        mainWindowSolutionLabel.setText(thisRow.getLanguageA());
                        auxSolutionLabel.setText(thisRow.getAuxA());
                    } else{
                        mainWindowSolutionLabel.setText("");
                        auxSolutionLabel.setText("");
                    }
                    break;
            }

            //set aux window title text null and then update it if isDisplayWindowTitles is turned on
            auxWindowNameLabel.setText(null);
            //get the aux window name which is always index 0
            if(myConfig.isDisplayWindowTitles() && thisRow.getWindowNames().size() != 0){
                auxWindowNameLabel.setText(thisRow.getWindowNames().get(0));
            }

            //loop through the available side windows
            int i = 0;
            for(SidePanel panel: sideWindows){
                //set title and content labels' text to null
                panel.titleLabel.setText(null);
                panel.contentLabel.setText(null);
                //check if getSideWindows() does not return null and if index i is within range of getSideWindows()
                if(thisRow.getSideWindows() != null && i < thisRow.getSideWindows().size()){
                    //content text from the current row of the file
                    String content = thisRow.getSideWindows().get(i);
                    //check if the text exists
                    if(content != null){
                        //display the text on the content label
                        panel.contentLabel.setText(content);

                        //check if a side window title exists for that side window and display that title
                        if(i+1 < thisRow.getWindowNames().size() && myConfig.isDisplayWindowTitles()){
                            //i+1 because the first entry of getWindowNames() is the aux label title
                            panel.titleLabel.setText(thisRow.getWindowNames().get(i+1));
                        }
                    }
                }
                i++;
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

    public void updateStudySpace(){
        updateWindows(solutionDisplayed);
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
                        solutionDisplayed = false;
                }
            }
        }
    }
}