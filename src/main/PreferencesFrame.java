package main;

import main.preferences.LanguagePacks;

import javax.swing.*;
import java.awt.*;

public class PreferencesFrame extends JDialog {

    public PreferencesFrame(JFrame parent, ConfigFile myConfig){
        super(parent, "Title", true);
        System.out.println("CREATED NEW PREFERENCE PANEL");
        getContentPane().setPreferredSize(new Dimension(854,480));
        //create content panel to hold the tabbed pane
        //JPanel content = new JPanel();
        //content.setBackground(Color.CYAN);
        //content.setLayout(new BorderLayout());

        //create JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        //add general settings tab
        JPanel pane1 = new JPanel();
        tabbedPane.add("Settings", pane1);
        tabbedPane.setToolTipTextAt(0, "General Settings");
        tabbedPane.setTabComponentAt(0,new TabComponent(tabbedPane));

        //add language packs tab

        LanguagePacks pane2 = new LanguagePacks(myConfig);
        tabbedPane.add("Language Packs", pane2);
        tabbedPane.setToolTipTextAt(1,"Add and remove language packs here");
        tabbedPane.setTabComponentAt(1, new TabComponent(tabbedPane));

        //getContentPane().add(content);
        getContentPane().add(tabbedPane);
        pack();
        setVisible(true);
    }

    /**
     * Custom component for the settings tabbed pane tab. Used to give each tab a different size.
     */
    private class TabComponent extends JPanel{
        private final JTabbedPane tabbedPane;
        TabComponent(final JTabbedPane tabbedPane){
            super();
            if(tabbedPane == null){
                throw new NullPointerException("JTabbedPane is null");
            }
            this.tabbedPane = tabbedPane;

            setOpaque(false);

            JLabel tabLabel = new JLabel(){
                public String getText(){
                    int i = tabbedPane.indexOfTabComponent(TabComponent.this);
                    if(i != -1){
                        return tabbedPane.getTitleAt(i);
                    }
                    return null;
                }
            };
            add(tabLabel);
        }
    }
}
