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
    private GridBagConstraints gbc;

    //action listener for the enable/disable checkboxes
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
        gbc = new GridBagConstraints();

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

    /**
     * Updates the checkboxes in the enable/disable language pack panel. Also updates the
     * main menu checkboxes.
     */
    private void updateEnableDisablePackPanel(){
        //update the enable disable panel
        this.remove(enableDisablePack);
        enableDisablePack = new JPanel();
        addEnableDisableComponents(enableDisablePack);
        gbc.weightx = 0.4;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 1;
        add(enableDisablePack, gbc);
        revalidate();
        repaint();
        //update the main menu
        myConfig.updateMainMenuCheckBoxes();
    }

    private JComboBox categoryDropDown;
    JTextField displayNameTextField;
    JComboBox sideWindowDropDown;
    JButton submitButton;
    JTextField auxNameTextField;
    JTextField fileNameTextField;
    //JTextField ;

    ArrayList<JLabel> sideWindowJLabels = new ArrayList<>();
    ArrayList<JTextField> sideWindowTextFields = new ArrayList<>();


    private void addAddPackComponents(JPanel section){
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        section.setLayout(new GridBagLayout());
        GridBagConstraints addPackGbc = new GridBagConstraints();


        //--header--
        JLabel sectionHeading = new JLabel("Add New Language Pack", SwingConstants.CENTER);
        //set font and size
        sectionHeading.setFont(new Font(sectionHeading.getFont().getFontName(),Font.BOLD, 15));
        sectionHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        addPackGbc.gridx = 0;
        addPackGbc.gridy = 0;
        addPackGbc.gridwidth = 2;
        section.add(sectionHeading, addPackGbc);



        //--category row--
        JLabel categoryLabel = new JLabel("Category");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 1;
        addPackGbc.gridwidth = 1;
        section.add(categoryLabel, addPackGbc);

        String categoryDropDownItems[] = {"Words", "Phrases", "Special"};
        categoryDropDown = new JComboBox(categoryDropDownItems);
        addPackGbc.gridx = 1;
        section.add(categoryDropDown, addPackGbc);

        //--display name row--
        JLabel displayNameLabel = new JLabel("Display Name");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 2;
        section.add(displayNameLabel, addPackGbc);

        displayNameTextField = new JTextField(15);
        addPackGbc.gridx = 1;
        section.add(displayNameTextField, addPackGbc);

        //--file name row--
        JLabel addFileNameLabel = new JLabel("Add File Name");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 3;
        section.add(addFileNameLabel, addPackGbc);

        fileNameTextField = new JTextField(15);
        addPackGbc.gridx = 1;
        section.add(fileNameTextField, addPackGbc);


        //--side windows row--
        JLabel sideWindowsLabel = new JLabel("Number of Side Windows");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 4;
        addPackGbc.gridwidth = 1;
        section.add(sideWindowsLabel, addPackGbc);

        String dropDownItems[] = {"0","1","2","3","4","5"};
        sideWindowDropDown = new JComboBox(dropDownItems);
        sideWindowDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSideWindowOptions(addPackGbc, section);
            }
        });
        addPackGbc.gridx = 1;
        section.add(sideWindowDropDown, addPackGbc);

        //--display name row--
        JLabel auxNameLabel = new JLabel("Title for aux window:");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 5;
        addPackGbc.gridwidth = 1;
        section.add(auxNameLabel, addPackGbc);

        auxNameTextField = new JTextField(15);
        addPackGbc.gridx = 1;
        section.add(auxNameTextField, addPackGbc);

        //-- Add button--
        submitButton = new JButton("Submit");
        addPackGbc.gridx = 0;
        addPackGbc.gridy = 14;
        submitButton.addActionListener(this);
        section.add(submitButton);
    }

    /**
     * Dynamically adds or removes options for the names of the side windows, depending on the selection
     * of {@link #sideWindowDropDown}.
     * @param gbc       Reference to the {@link GridBagConstraints} of the <i>add language pack</i> panel
     * @param section   Reference to the <i>add language pack</i> panel
     */
    private void updateSideWindowOptions(GridBagConstraints gbc, JPanel section){
        int desiredSideWindows = sideWindowDropDown.getSelectedIndex();

        //remove options if the new selected number of side windows is smaller than the currently displayed options
        if(desiredSideWindows < sideWindowJLabels.size()){
            for(int i = sideWindowJLabels.size(); i > desiredSideWindows; i--){
                System.out.println(i);
                section.remove(sideWindowJLabels.get(i-1));
                sideWindowJLabels.remove(i-1);
                section.remove(sideWindowTextFields.get(i-1));
                sideWindowTextFields.remove(i-1);
            }
        } else {
            //add options if the selected number of side windows is bigger than the currently displayed options
            //if the number is equal, the for loop will not execute, thus no options are added
            gbc.gridwidth = 1;
            for (int i = sideWindowJLabels.size() + 1; i <= desiredSideWindows; i++) {
                if(i != 0) {
                    JLabel sideNameLabel = new JLabel("Title for side window " + i);
                    gbc.gridx = 0;
                    gbc.gridy = 7 + i;
                    section.add(sideNameLabel, gbc);
                    sideWindowJLabels.add(sideNameLabel);

                    JTextField sideNameTextField = new JTextField(15);
                    gbc.gridx = 1;
                    section.add(sideNameTextField, gbc);
                    sideWindowTextFields.add(sideNameTextField);
                    System.out.println(sideWindowJLabels);
                }

            }
        }
        section.revalidate();
        section.repaint();
    }

    /**
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e){
        int category;
        String desiredDisplayName;
        String fileName;
        if(e.getSource() == submitButton){

            //store the category
            category = categoryDropDown.getSelectedIndex() + 1; //add 1 because index starts at 0 but first category is 1

            //check if display name is valid (only a-z, A-Z, 0-9, '(', ')', '-' and '_')
            //ToDo add support for different languages
            // \s = white space character \p{Alnum} = any alpha numeric character and chars - _ ()
            if(displayNameTextField.getText().trim().matches("^[\\s\\p{Alnum}-_()]+$")){
                desiredDisplayName = displayNameTextField.getText().trim();
            } else{
                //display error message and return
                JOptionPane.showMessageDialog(this,
                        "Display name has to have at least one non space character\n" +
                                "and can only contain the following characters:\n" +
                                "letters\n" +
                                "numbers\n" +
                                "spaces\n" +
                                "dash -\n" +
                                "underscore _\n" +
                                "opening and closing parenthesis ( )\n" +
                                "and have to have at least one non space character",
                        "Invalid Character",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            //  not used - regex: ^[]+ matches at least one of the contained items (all allowed ones except for white space)
            //  not used - regex: []*$ matches at least 0 of the contained items (all allowed ones including white space)
            //  not used - ^[\\p{Alnum}-_()]+[\\s\\p{Alnum}-_()]*$

            //allow only a-z, A-Z, 0-9, '(', ')', '-' and '_'

            if(fileNameTextField.getText().trim().matches("^[\\s\\p{Alnum}-_()]+$")){
                fileName = fileNameTextField.getText().trim() + ".txt";
            } else{
                //display error message and return
                JOptionPane.showMessageDialog(this,
                        "File name has to have at least one non space character\n" +
                                "and can only contain the following characters:\n" +
                                "letters\n" +
                                "numbers\n" +
                                "spaces\n" +
                                "dash -\n" +
                                "underscore _\n" +
                                "opening and closing parenthesis ( )\n" +
                                "and have to have at least one non space character",
                        "Invalid Character",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String windowNames;
            if(auxNameTextField.getText().trim().matches("^[\\s\\p{Alnum}-_()]+$")){
                windowNames = "[name aux]" + auxNameTextField.getText().trim();
            } else{
                //display error message and return
                JOptionPane.showMessageDialog(this,
                        "Aux window name has to have at least one non space character\n" +
                                "and can only contain the following characters:\n" +
                                "letters\n" +
                                "numbers\n" +
                                "spaces\n" +
                                "dash -\n" +
                                "underscore _\n" +
                                "opening and closing parenthesis ( )\n" +
                                "and have to have at least one non space character",
                        "Invalid Character",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }



            //get every created side window textField and check the input
            for(int i = 0; i < sideWindowTextFields.size(); i++){
                //collect and trim the string from the current textField
                String thisName = sideWindowTextFields.get(i).getText().trim();
                //match the string to the regex
                if(thisName.matches("^[\\s\\p{Alnum}-_()]+$")){
                    windowNames += "[name side " + (i+1) + "]" + thisName;
                } else{
                    //display error message and return
                    JOptionPane.showMessageDialog(this,
                            "Side window names have to have at least one non space character\n" +
                                    "and can only contain the following characters:\n" +
                                    "letters\n" +
                                    "numbers\n" +
                                    "spaces\n" +
                                    "dash -\n" +
                                    "underscore _\n" +
                                    "opening and closing parenthesis ( )\n" +
                                    "and have to have at least one non space character",
                            "Invalid Character",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }



            //ToDo display error message
            switch(myConfig.addLanguagePack(category, true, desiredDisplayName, fileName, windowNames)){
                case 0:
                    System.out.println("IO Error");
                    break;
                case 1:
                    //ToDo update the checkboxes on the right hand side
                    updateEnableDisablePackPanel();
                    break;
                case -1:
                    System.out.println("file path invalid");
                    break;
                case -2:
                    System.out.println("display name already in use");
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
