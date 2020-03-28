package main.studySpace;

import javax.swing.*;

public class SidePanel extends JPanel {
    JLabel titleLabel;
    JLabel contentLabel;
    SidePanel(String titleText, String contentText){
        super();
        //needed because the box layout only respects either preferred height OR width
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));


        titleLabel = new JLabel(titleText);
        contentLabel = new JLabel(contentText);

        innerPanel.add(titleLabel);
        innerPanel.add(contentLabel);
        add(innerPanel);

    }
}
