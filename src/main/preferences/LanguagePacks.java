package main.preferences;

import main.ConfigFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.ToDoubleBiFunction;

public class LanguagePacks extends JPanel implements ActionListener {
    public ConfigFile myConfig;
    private ArrayList<SpecialCheckBoxes> allCheckBoxes;
    private JPanel enableDisablePack;

    ActionListener checkBoxAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SpecialCheckBoxes boxTriggered = (SpecialCheckBoxes)e.getSource();
            boxTriggered.setSelected();
        }
    };

    public LanguagePacks(ConfigFile myConfig){
        super();
        this.myConfig = myConfig;
        allCheckBoxes = new ArrayList<>();
        setBackground(Color.GREEN);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel addPack = new JPanel();
        addAddPackComponents(addPack);  //add all the components to the panel
        gbc.weightx = 0.6;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(addPack, gbc);


        JPanel removePack = new JPanel();
        addRemovePackComponents(removePack);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(removePack, gbc);

        enableDisablePack = new JPanel();

        gbc.weightx = 0.4;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 1;
        add(enableDisablePack, gbc);
        addEnableDisableComponents(enableDisablePack); //add all the components to the panel
    }

    private JComboBox categoryDropDown;
    JTextField displayNameTextField;
    JLabel filePathLabel;
    JCheckBox auxWindowCheckbox;
    JComboBox sideWindowDropDown;
    JCheckBox showItemsLeft;
    JButton submitButton;


    private void addAddPackComponents(JPanel section){
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        section.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        //--header--
        JLabel sectionHeading = new JLabel("Add New Language Pack", SwingConstants.CENTER);
        //set font and size
        sectionHeading.setFont(new Font(sectionHeading.getFont().getFontName(),Font.BOLD, 15));
        //sectionHeading.setMinimumSize(new Dimension(section.getWidth(), 40));
        //sectionHeading.setPreferredSize(new Dimension(section.getWidth(), 40));
        //sectionHeading.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        sectionHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        section.add(sectionHeading, gbc);



        //--category row--
        JLabel categoryLabel = new JLabel("Category");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        section.add(categoryLabel, gbc);

        String categoryDropDownItems[] = {"Words", "Phrases", "Special"};
        categoryDropDown = new JComboBox(categoryDropDownItems);
        gbc.gridx = 1;
        section.add(categoryDropDown, gbc);

        //--display name row--
        JLabel displayNameLabel = new JLabel("Display Name");
        gbc.gridx = 0;
        gbc.gridy = 2;
        section.add(displayNameLabel, gbc);

        displayNameTextField = new JTextField(15);
        gbc.gridx = 1;
        section.add(displayNameTextField, gbc);

        //--file path row--
        JButton addFilePathButton = new JButton("Add File Path");
        gbc.gridx = 0;
        gbc.gridy = 3;
        section.add(addFilePathButton, gbc);

        //ToDo Add handling for this button

        filePathLabel = new JLabel("test.txt");
        gbc.gridx = 1;
        section.add(filePathLabel, gbc);

        //--aux window row--
        auxWindowCheckbox = new JCheckBox("Enable Aux Window");
        auxWindowCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        section.add(auxWindowCheckbox, gbc);

        //--side windows row--
        JLabel sideWindowsLabel = new JLabel("Number of Side Windows");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        section.add(sideWindowsLabel, gbc);

        String dropDownItems[] = {"0","1","2","3","4","5"};
        sideWindowDropDown = new JComboBox(dropDownItems);
        gbc.gridx = 1;
        section.add(sideWindowDropDown, gbc);

        //--show items left row--
        showItemsLeft = new JCheckBox("Show Items Left");
        showItemsLeft.setHorizontalTextPosition(SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        section.add(showItemsLeft, gbc);

        //-- Add button--
        submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 7;
        submitButton.addActionListener(this);
        section.add(submitButton);

        //add form
        //category -> dropdown
        //display name -> Textfield
        //filepath -> button + JLabel

        //aux window ->checkbox
        //side windows -> dropdown
        //display how many left -> checkbox

        //Add button

    }

    public void actionPerformed(ActionEvent e){
        int category;
        String desiredDisplayName;
        String filePath;
        if(e.getSource() == submitButton){


            //store the category
            category = categoryDropDown.getSelectedIndex() + 1; //add 1 because index starts at 0 but first category is 1


            //check if display name is valid (only a-z, A-Z, 0-9, '(', ')', '-' and '_')
            //ToDo add support for different languages
            System.out.println(displayNameTextField.getText());
            if(displayNameTextField.getText().matches("^[\\s\\p{Alnum}-_()]+$")){
                desiredDisplayName = displayNameTextField.getText();
            } else{
                //ToDo display error message
                System.out.println("Invalid Display Name!");
                return;
            }

            //store the desired file path
            if(filePathLabel.getText() == ""){
                //ToDo display error message
                System.out.println("Please specify a file path");
                return;
            }
            filePath = filePathLabel.getText();

            System.out.println("Called addLanguagePack");
            //ToDo display error message
            switch(myConfig.addLanguagePack(category, true, desiredDisplayName, filePath)){
                case 0:
                    System.out.println("IO Error");
                    break;
                case 1:
                    //ToDo update the checkboxes on the right hand side
                    break;
                case -1:
                    System.out.println("file path invalid");
                    break;
                case -2:
                    System.out.println("displa name already in use");
                    break;
                case -3:
                    System.out.println("object path already in properties file");
                    break;
                case -4:
                    System.out.println("file already exists on computer");
                    break;


            }



        }
    }

    private void addRemovePackComponents(JPanel section){
        section.setBackground(Color.WHITE);
    }

    private void addEnableDisableComponents(JPanel section){
        section.setLayout(new BoxLayout(section, BoxLayout.PAGE_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createMatteBorder(0,1,0,0,Color.BLACK));

        //---create section heading---
        JLabel sectionHeading = new JLabel("Enable / Disable Language Pack", SwingConstants.CENTER);
        //set font and size
        sectionHeading.setFont(new Font(sectionHeading.getFont().getFontName(),Font.BOLD, 15));
        sectionHeading.setMinimumSize(new Dimension(section.getWidth(), 40));
        sectionHeading.setPreferredSize(new Dimension(section.getWidth(), 40));
        sectionHeading.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        sectionHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionHeading);

        //---create section description---
        JLabel sectionDescription = new JLabel("Select language packs to enable them.", SwingConstants.CENTER);
        //set size
        sectionDescription.setMinimumSize(new Dimension(section.getWidth(), 40));
        sectionDescription.setPreferredSize(new Dimension(section.getWidth(), 40));
        sectionDescription.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        sectionDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionDescription);


        JTabbedPane packsTabbedPane = new JTabbedPane();
        packsTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        packsTabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        //add words tab
        //ToDo make scrollpane
        JPanel wordsTab = new JPanel();
        wordsTab.setLayout(new BoxLayout(wordsTab, BoxLayout.PAGE_AXIS));
        packsTabbedPane.add("Words", wordsTab);
        packsTabbedPane.setToolTipTextAt(0, "Packs in the \"Words\" category.");

        //add phrases tab
        JPanel phrasesTab = new JPanel();
        packsTabbedPane.add("Phrases", phrasesTab);
        packsTabbedPane.setToolTipTextAt(0, "Packs in the \"Phrases\" category.");

        //add phrases tab
        JPanel specialTab = new JPanel();
        packsTabbedPane.add("Special", specialTab);
        packsTabbedPane.setToolTipTextAt(0, "Packs in the \"Special\" category.");

        myConfig.readLanguagePacks();
        myConfig.getFilePaths();

        for(String[] thisData : myConfig.getFilePaths()){
            SpecialCheckBoxes temp = new SpecialCheckBoxes(Integer.parseInt(thisData[0]), Boolean.parseBoolean(thisData[1]), thisData[2], thisData[3], thisData[4], this);
            temp.addActionListener(checkBoxAction);
            switch(Integer.parseInt(thisData[0])){
                case 1:
                    //add the new checkbox to the
                    wordsTab.add(temp);

                    break;
                case 2:
                    phrasesTab.add(temp);

                    break;
                case 3:
                    specialTab.add(temp);

                    break;
                default:
                    //Type data was not an int between 1 and 3
                    //ToDo handle this case
            }
            allCheckBoxes.add(temp);
        }

        section.add(packsTabbedPane);
        System.out.println(Arrays.toString(allCheckBoxes.toArray()));
    }


    /**
     * JCheckBoxes that also hold the values of the configuration for the data represented by the checkbox
     */
    private class SpecialCheckBoxes extends JCheckBox{
        String configKey;
        String name;
        String filePath;
        int category;
        boolean selected;
        LanguagePacks parent;

        SpecialCheckBoxes(int category, boolean selected, String name, String filePath, String configKey, LanguagePacks parent){
            super(name, selected);
            this.configKey = configKey;
            this.name = name;
            this.filePath = filePath;
            this.category = category;
            this.selected = selected;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return configKey;
        }

        public void setSelected(){
            System.out.println("Triggered checkbox");
            selected = isSelected();
            parent.myConfig.updateLanguagePackSelected(category, selected, name, filePath, configKey);
        }
    }




}
