package mainMenu;

import main.ConfigFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainMenuContent extends JPanel {
    ConfigFile myConfig;

    public MainMenuContent(ConfigFile myConfig){
        this.myConfig = myConfig;
        setBackground(Color.RED);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        //=== Words Section ===
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;

        CheckBoxScrollPane testPane = new CheckBoxScrollPane("Words");
        testPane.addCheckBox("Box 1", false);
        testPane.addCheckBox("Box 2", false);
        testPane.addCheckBox("Box 3", false);
        testPane.addCheckBox("Box 4", true);
        add(testPane, gbc);

        //=== Phrases Section ===
        gbc.gridx = 2;
        CheckBoxScrollPane testPane2 = new CheckBoxScrollPane("Phrases");
        testPane2.addCheckBox("Box 1", false);
        testPane2.addCheckBox("Box 2", false);
        testPane2.addCheckBox("Box 3", false);
        testPane2.addCheckBox("Box 4", true);
        add(testPane2, gbc);

        //=== Special Section ===
        gbc.gridx = 3;
        CheckBoxScrollPane testPane3 = new CheckBoxScrollPane("Special");
        testPane3.addCheckBox("Box 1", false);
        testPane3.addCheckBox("Box 2", false);
        testPane3.addCheckBox("Box 3", false);
        testPane3.addCheckBox("Box 4", true);
        add(testPane3, gbc);


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
        gbc.gridx = 3;
        gbc.gridy = 2;
        add(deselectButton, gbc);

        JButton startButton = new JButton("Start");
        gbc.gridy = 3;
        add(startButton, gbc);



        revalidate();
        repaint();
    }

    //checkbox list including heading
    class CheckBoxScrollPane extends JPanel{
        private JPanel scrollContent;
        private ArrayList<JCheckBox> myCheckBoxes;

        CheckBoxScrollPane(String heading){
            myCheckBoxes = new ArrayList<>();
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            JLabel name = new JLabel(heading);    //add label for the list
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

        void addCheckBox(String name, boolean state){
            JCheckBox tempBox = new JCheckBox(name, state);
            myCheckBoxes.add(tempBox);      //save reference for later use
            scrollContent.add(tempBox);     //add to scrollable panel
        }
    }
}

