package mainMenu;

import main.ConfigFile;
import main.Main;
import main.MainFrame;
import main.studySpace.StudySpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainMenuContent extends JPanel{
    ConfigFile myConfig;
    MainFrame frame;

    CheckBoxScrollPane wordsCheckBoxPane;
    CheckBoxScrollPane phrasesCheckBoxPane;
    CheckBoxScrollPane specialCheckBoxPane;
    GridBagConstraints gbc;



    public MainMenuContent(ConfigFile myConfig, Dimension panelSize, MainFrame frame){
        this.myConfig = myConfig;
        this.frame = frame;

        //add reference to myConfig so that it can update the displayed checkboxes
        myConfig.addMainMenuContentReference(this);
        setPreferredSize(panelSize);
        setBackground(Color.RED);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();

        //create the three scroll panes to hold the available language packs
        wordsCheckBoxPane = new CheckBoxScrollPane("Words");
        phrasesCheckBoxPane = new CheckBoxScrollPane("Phrases");
        specialCheckBoxPane = new CheckBoxScrollPane("Special");

        //add the language packs to the three scroll panes
        populateCheckBoxPanes();

        //add the three scroll panes to the MainMenuContent
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        add(wordsCheckBoxPane, gbc);
        gbc.gridx = 2;
        add(phrasesCheckBoxPane, gbc);
        gbc.gridx = 3;
        add(specialCheckBoxPane, gbc);


        //=== Display Language ===
        gbc.gridx = 1;
        gbc.gridy = 2;
        JLabel languageLabel = new JLabel("Display Language");
        add(languageLabel, gbc);

        JRadioButton checkboxLanguageA = new JRadioButton(myConfig.getLanguageA(), true);
        gbc.gridy = 3;
        add(checkboxLanguageA, gbc);
        JRadioButton checkboxLanguageB = new JRadioButton(myConfig.getLanguageB(), false);
        gbc.gridy = 4;
        add(checkboxLanguageB, gbc);
        JRadioButton checkboxLanguageAB = new JRadioButton("Mixed", false);
        gbc.gridy = 5;
        add(checkboxLanguageAB, gbc);

        ButtonGroup languageSelect = new ButtonGroup();
        languageSelect.add(checkboxLanguageA);
        languageSelect.add(checkboxLanguageB);
        languageSelect.add(checkboxLanguageAB);

        //=== Amount Section ===
        JLabel amountLabel = new JLabel("Repetitions");
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(amountLabel, gbc);

        JTextField amountTextField = new JTextField(3);
        gbc.gridy = 3;
        add(amountTextField, gbc);



        //=== Buttons Section ===

        JButton deselectButton = new JButton("Deselect All");
        deselectButton.addActionListener(new SelectionActionListener(false));
        gbc.gridx = 3;
        gbc.gridy = 2;
        add(deselectButton, gbc);


        JButton selectButton = new JButton("Select All");
        selectButton.addActionListener(new SelectionActionListener(true));
        gbc.gridx = 3;
        gbc.gridy = 3;
        add(selectButton, gbc);



        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> filePaths = new ArrayList<>();
                filePaths.addAll(wordsCheckBoxPane.getSelectedFilePaths());
                filePaths.addAll(phrasesCheckBoxPane.getSelectedFilePaths());
                filePaths.addAll(specialCheckBoxPane.getSelectedFilePaths());

                if(filePaths.isEmpty()){
                    //custom title, warning icon
                    JOptionPane.showMessageDialog(frame,
                            "Please select at least one language pack!",
                            "No Language Pack Selected",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int numberOfItems;
                try {
                    numberOfItems = Integer.parseInt(amountTextField.getText());
                    if(numberOfItems < 1){
                        throw new NumberFormatException();
                    }
                }catch (NumberFormatException err){
                    amountTextField.setBackground(Color.RED);
                    JOptionPane.showMessageDialog(frame,
                            "Please enter a valid integer larger than 0 in the repetitions field!",
                            "Invalid Number",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int selectedLanguage = Main.MIXED;
                if(checkboxLanguageA.isSelected()){
                    selectedLanguage = Main.LANGUAGE_A;
                } else if(checkboxLanguageB.isSelected()){
                    selectedLanguage = Main.LANGUAGE_B;
                } else if(checkboxLanguageAB.isSelected()){
                    selectedLanguage = Main.MIXED;
                }


                frame.startStudySpace(filePaths, selectedLanguage, numberOfItems);
                frame.revalidate();
                frame.repaint();
            }
        });
        gbc.gridy = 4;
        add(startButton, gbc);



        revalidate();
        repaint();
    }

    public void populateCheckBoxPanes(){
        wordsCheckBoxPane.removeAllCheckBoxes();
        phrasesCheckBoxPane.removeAllCheckBoxes();
        specialCheckBoxPane.removeAllCheckBoxes();

        myConfig.readLanguagePacks();
        for(String[] languagePack : myConfig.getFilePaths()){
            //[0] = category
            //[1] = language pack shown
            //[2] = display name
            //[3] = file path
            //[4] = properties key

            //only proceed with the language packs that are enabled (shown)
            if(Boolean.parseBoolean(languagePack[1])){
                switch (Integer.parseInt(languagePack[0])){
                    case 1:
                        //category words
                        wordsCheckBoxPane.addCheckBox(languagePack[2], languagePack[3], languagePack[4]);
                        break;
                    case 2:
                        //category phrases
                        phrasesCheckBoxPane.addCheckBox(languagePack[2], languagePack[3], languagePack[4]);
                        break;
                    case 3:
                        //category special
                        specialCheckBoxPane.addCheckBox(languagePack[2], languagePack[3], languagePack[4]);
                        break;
                    default :
                        //ToDo print error
                        break;
                }
            }
        }
        revalidate();
        repaint();
    }

    /**
     * A scrollable pane with a heading that can hold and display checkboxes of type {@link SpecialCheckBox}.
     * <p>
     * The checkboxes can be updated by calling the {@link #removeAllCheckBoxes()} method and then repopulating
     * the entire pane with new checkboxes.</p>
     */
    class CheckBoxScrollPane extends JPanel{
        /**
         * A scrollable JPanel that displays the checkboxes of type {@link SpecialCheckBox}.
         */
        private JPanel scrollContent;
        /**
         * Holds all the {@link SpecialCheckBox} for that {@link #scrollContent}.
         * <p>Each SpecialCheckBox holds displayName, filePath and propertiesKey.</p>
         */
        private ArrayList<SpecialCheckBox> myCheckBoxes;


        /**
         * Constructor that creates the scroll pane that holds the checkboxes.
         * Only the heading for the pane is added but no checkboxes.
         * @param heading   The heading to be displayed above the pane
         */
        CheckBoxScrollPane(String heading){
            myCheckBoxes = new ArrayList<>();
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            JLabel name = new JLabel(heading);    //add heading for the list
            add(name);

            //JLabel to populate with checkboxes
            scrollContent = new JPanel();
            scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.PAGE_AXIS));
            scrollContent.setPreferredSize(new Dimension(350, 450));

            //make JLabel scrollable
            JScrollPane scrollPane = new JScrollPane(scrollContent);
            scrollPane.setPreferredSize(new Dimension(375,450));
            add(scrollPane);
        }

        /**
         * Selects or deselects all the checkboxes
         * @param state     Desired selection state of the checkboxes
         */
        public void selectDeselectAllCheckboxes(boolean state){
            for(SpecialCheckBox checkBox : myCheckBoxes){
                checkBox.setSelected(state);
            }
        }

        /**
         * Adds a new {@link SpecialCheckBox} to the scrollContent and also stores it in the {@link #myCheckBoxes} array.
         * @param displayName       The name shown next to the checkbox
         * @param filePath          The file path to the file that the checkbox represents
         * @param propertiesKey     The properties key under which the language pack is stored in the {@link #myConfig} file
         */
        void addCheckBox(String displayName, String filePath, String propertiesKey){
            SpecialCheckBox tempBox = new SpecialCheckBox(displayName, filePath, propertiesKey);
            myCheckBoxes.add(tempBox);      //save reference for later use
            scrollContent.add(tempBox);     //add to scrollable panel
        }

        /**
         * Removes all {@link SpecialCheckBox} from the {@link #scrollContent} pane and also empties the checkbox array
         */
        void removeAllCheckBoxes(){
            myCheckBoxes.clear();
            scrollContent.removeAll();
        }

        public ArrayList<String> getSelectedFilePaths(){
            ArrayList<String> filePaths = new ArrayList<>();
            for(SpecialCheckBox thisCheckBox : myCheckBoxes){
                if(thisCheckBox.isSelected()) {
                    filePaths.add(thisCheckBox.filePath);
                }
            }
            return filePaths;
        }
    }

    /**
     * Extends {@link JCheckBox}. Stores {@link #displayName}, {@link #filePath}, {@link #propertiesKey} for each checkbox.
     */
    class SpecialCheckBox extends JCheckBox{
        /**
         * The name that is to be displayed next to the checkbox
         */
        String displayName;
        /**
         * The file path to the file linked with this checkbox
         */
        String filePath;
        /**
         * The property key with which to find the language pack information in the {@link #myConfig} file
         */
        String propertiesKey;

        /**
         * Constructor that creates the checkbox and stores the {@link #displayName}, {@link #filePath} and {@link #propertiesKey}.
         * @param displayName       The name shown next to the checkbox
         * @param filePath          The file path to the file that the checkbox represents
         * @param propertiesKey     The properties key under which the language pack is stored in the {@link #myConfig} file
         */
        SpecialCheckBox(String displayName, String filePath, String propertiesKey){
            super(displayName, false);
            this.displayName = displayName;
            this.filePath = filePath;
            this.propertiesKey = propertiesKey;
        }
    }

    private class SelectionActionListener implements ActionListener{
        private boolean selectionStatus;
        SelectionActionListener(boolean selectionStatus){
            this.selectionStatus = selectionStatus;
        }
        public void actionPerformed(ActionEvent e){
            System.out.println("Clicked " + selectionStatus);
            wordsCheckBoxPane.selectDeselectAllCheckboxes(selectionStatus);
            phrasesCheckBoxPane.selectDeselectAllCheckboxes(selectionStatus);
            specialCheckBoxPane.selectDeselectAllCheckboxes(selectionStatus);
        }
    }

    private class SpecialRadioButton extends JRadioButton{
        int id;
        SpecialRadioButton(String text, boolean selected, int id){
            super(text, selected);
            this.id = id;
        }
    }
}

