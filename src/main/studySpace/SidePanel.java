package main.studySpace;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    JLabel titleLabel;
    JLabel contentLabel;
    SidePanel(String titleText, String contentText, Font titleFont, Font contentFont){
        super();
        //needed because the box layout only respects either preferred height OR width
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));

        titleLabel = new JLabel(titleText);
        titleLabel.setFont(titleFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentLabel = new JLabel(contentText);
        contentLabel.setFont(contentFont);
        contentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        innerPanel.add(titleLabel);
        innerPanel.add(contentLabel);
        add(innerPanel);

    }
}
