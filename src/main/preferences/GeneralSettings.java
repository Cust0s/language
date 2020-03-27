package main.preferences;

import main.ConfigFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GeneralSettings extends JPanel {
    ConfigFile myConfig;

    //private JCheckBox instantSolutionCheckBox;
    public GeneralSettings(ConfigFile myConfig){
        super();
        this.myConfig = myConfig;

        JCheckBox instantSolutionCheckBox = new JCheckBox("Display instant solution", myConfig.isDisplayInstantSolution());
        instantSolutionCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //store the new settings in the config file
                if(!myConfig.setDisplayInstantSolution(instantSolutionCheckBox.isSelected())){
                    //if the settings couldn't be written, reset the checkbox.
                    instantSolutionCheckBox.setSelected(!instantSolutionCheckBox.isSelected());
                }
            }
        });
        add(instantSolutionCheckBox);
    }
}
