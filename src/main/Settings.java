package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Settings {
    private String languageA;
    private String languageB;
    private ConfigFile myConfig;


    //Hint: use this to get the filePath tempFile.getName();

    public Settings(ConfigFile myConfig){
        this.myConfig = myConfig;
        //DEBUG - to be replaced with values from settings
        languageA = "Korean";
        languageB = "English";
    }

    public String getLanguageA(){
        return languageA;
    }

    public String getLanguageB(){
        return languageB;
    }


    /**
     * Creates a new language file and adds its information to the config file.
     * @param category      The category for the file (1=Words, 2=Phrases, 3=Special)
     * @param displayName   The display displayName for the file
     * @param filePath      The path to the file
     * @return              Successful creation
     */
    public boolean addLanguageFile(int category, String displayName, String filePath){
        //check if the filePath is valid
        try{
            Paths.get(filePath);
        } catch( InvalidPathException | NullPointerException e){
            //ToDo inform the user of the error
            return false;
        }


        switch(myConfig.addFileInformation(category, displayName, filePath)){
            case -1:
                //display name already in use
                //ToDo inform the user of the error
                return false;
            case -2:
                //file path already in use
                //ToDo inform the user of the error
                return false;
            case 1:
                //added successfully
                break;
        }

        File tempFile = new File(filePath);

        try {
            if(tempFile.createNewFile()){
                //created new file
                return true;
            } else {
                //file already exists
                //ToDo inform the user of the error
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}
