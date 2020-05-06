package main;

import main.studySpace.StudySpace;
import mainMenu.MainMenuContent;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.*;


public class ConfigFile {
    /**
     * The name of the key for the specific file information. This is followed by a unique index integer
     */
    public static final String FILE_INFORMATION_PREFIX = "file_information_";

    /**
     * Stores the file information read from the properties file. Each file is stored as a String Array with
     * five values. Category (1 = Words, 2 = Phrases, 3 = Special), enabled/disabled, display name, the file path, config file key.
     */
    private ArrayList<String[]> filePaths;

    private String languageA;
    private String languageB;

    private boolean displayInstantSolution;
    private boolean displayWindowTitles;

    private Properties myProperties; //Properties object
    private String configFilePath;         //path to the configuration file
    private File configFile;         //file object for the configuration file
    private int maxFileIndex = 0;    //holds the max index for FILE_INFORMATION_PREFIX

    private MainMenuContent mainMenuContent = null;     //reference to update the three scroll panes in the main menu holding the checkboxes


    ConfigFile(String configFilePath){
        this.configFilePath = configFilePath;
        languageA = "Korean";
        languageB = "English";

        configFile = new File(configFilePath);
        myProperties = new Properties();

        try{
            if(configFile.createNewFile()){
                //new file created, populate with new properties
                generateNewProperties();
                System.out.println("Generated new file");
            }

            updateMaxIndex();
            //read the language packs
            readLanguagePacks();
            displayInstantSolution = Boolean.parseBoolean(myProperties.getProperty("displayInstantSolution"));
            displayWindowTitles = Boolean.parseBoolean(myProperties.getProperty("displayWindowTitles"));


            //System.out.println("add words lesson 1: " + addLanguagePack(Main.WORDS,true,"lesson 1", "words_lesson_1.txt", "[name aux]aux title[name side 1]side 1 title[name side 2]side 2 title"));
            //System.out.println("add words lesson 2: " + addLanguagePack(Main.WORDS,true,"lesson 2", "words_lesson_2.txt","[name aux]aux title[name side 1]side 1 title[name side 2]side 2 title"));
            //System.out.println("add words lesson invalid: " + addLanguagePack(Main.WORDS,true,"lesson invalid", "words_lesson_1.txt","[name aux]aux title[name side 1]side 1 title[name side 2]side 2 title"));

            //System.out.println("add phrases lesson 1: " + addLanguagePack(Main.PHRASES,true,"lesson 1", "phrases_lesson_1.txt","[name aux]aux title[name side 1]side 1 title[name side 2]side 2 title"));

            //System.out.println("add special lesson 3: " + addLanguagePack(Main.SPECIAL,true,"lesson 3","special_lesson_3.txt","[name aux]aux title[name side 1]side 1 title[name side 2]side 2 title"));

            updateMaxIndex();
            readLanguagePacks();
        }  catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getFilePaths() {
        return filePaths;
    }

    public String getLanguageA(){
        return languageA;
    }

    public String getLanguageB(){
        return languageB;
    }

    /**
     * Adds a reference to the MainMenuContent in order to be able to update the
     * checkboxes if the settings have changed something.
     * @param mainMenuContent   The reference to the MainMenuContent
     */
    public void addMainMenuContentReference(MainMenuContent mainMenuContent){
        this.mainMenuContent = mainMenuContent;
    }

    public void updateMainMenuCheckBoxes(){
        if(mainMenuContent == null){
            //ToDo print error
            System.out.println("Error updating the checkboxes!");
            return;
        }
        mainMenuContent.populateCheckBoxPanes();
    }

    /**
     * Updates the value for the maximum index found in the property keys for keys beginning
     * with the specified prefix in {@link #FILE_INFORMATION_PREFIX}.
     */
    private void updateMaxIndex(){
        try {
            FileReader myReader = new FileReader(configFile);

            myProperties.load(myReader);    //load properties

            //get the current max index for the "file_information_" files
            //E.g. "file_information_3" = index 3
            for(String key : myProperties.stringPropertyNames()){
                if(key.contains(FILE_INFORMATION_PREFIX)){
                    int thisIndex;
                    if((thisIndex = Integer.parseInt(key.substring(FILE_INFORMATION_PREFIX.length()))) > maxFileIndex){
                        maxFileIndex = thisIndex;
                    }
                }
            }
            myReader.close();

        } catch(NumberFormatException e) {
            //for the parse int
            System.out.println("The value parsed from the config was not an int!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            //file not found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the current properties and picks out the data related to the language packs.
     * This is then stored in the Arraylist {@link #filePaths}
     */
    public void readLanguagePacks(){
        try {
            FileReader myReader = new FileReader(configFile);

            myProperties.load(myReader);    //load properties

            //stores all the categories, name, file paths
            filePaths = new ArrayList<>();

            for(String key: myProperties.stringPropertyNames()){
                //key is one of the file paths
                if(key.contains(FILE_INFORMATION_PREFIX)){
                    String tempkey[] = myProperties.getProperty(key).split(",");

                    if(tempkey.length == 5){
                        //key has the correct length and is assumed to be valid
                        String[] tempKeyNew = Arrays.copyOf(tempkey, 6);
                        tempKeyNew[5] = key;   //add the object key to the end
                        filePaths.add(tempKeyNew);
                    } else{
                        //key did not have the correct size
                        System.out.println("Invalid Key! " + Arrays.toString(tempkey));
                    }
                }
            }
            myReader.close();

        } catch(NumberFormatException e) {
            //for the parse int
            System.out.println("The value parsed from the config was not an int!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            //file not found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the information for a new language pack to the properties and creates a new file if it does not exist yet
     *
     * @param category          The category for the file (1=Words, 2=Phrases, 3=Special)
     * @param enabled           Indicates enabled status for the file
     * @param displayName       The display displayName for the file
     * @param desiredFileName   The path to the file
     * @return   0 if IO error occurs
     *           1 if the information was successfully stored,
     *          -1 if the file path is invalid
     *          -2 if the object displayName is already in use,
     *          -3 if the object path is already in use (in properties file)
     *          -4 if the file already exists (on the computer)
     */
    public int addLanguagePack(int category, boolean enabled, String displayName, String desiredFileName, String windowNames){
        //Check if the desired new file path is valid
        try{
            Paths.get(desiredFileName);
        } catch( InvalidPathException | NullPointerException e){
            //file path is invalid
            return -1;
        }


        //check if file path or filename already exists in properties
        readLanguagePacks();    //get the newest file information
        for(String[] thisArray : filePaths){
            //only allow a display name to be taken twice if it is in a different category
            if(thisArray[2].equals(displayName) && thisArray[0].equals(Integer.toString(category))){
                //display displayName for the object is already in use
                return -2;
            } else if(thisArray[3].equals(desiredFileName)){
                //file path is already in use
                return -3;
            }
        }

        File tempFile = new File(desiredFileName);

        //check if file already exists on computer and create file
        try {
            if(!tempFile.createNewFile()){
                //file already exists
                //ToDo inform the user of the error
                return -4;
            } else{
                //ToDo generate as many [Side x] as there are selected window numbers
                FileWriter writer = new FileWriter(tempFile);
                writer.write(windowNames + "\r\n");
                writer.write("[Language A][Language B][Aux A][Aux B][Side 1][Side 2][Side 3][Side 4][Side 5]");
                writer.close();
            }
        } catch (IOException e) {
            return 0;
        }

        //add the new file information to the properties
        try {
            //add the new entry to the properties file and increase the maxFileIndex at the same time
            myProperties.setProperty(FILE_INFORMATION_PREFIX + ++maxFileIndex, category + "," + enabled + "," + displayName + "," + desiredFileName + "," + -1);

            FileWriter myWriter = new FileWriter(configFilePath);
            myProperties.store(myWriter,"Added new category with index " + maxFileIndex);    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }

    /**
     * Generates a set of default properties. Should be called if a new properties file is generated.
     */
    private void generateNewProperties(){
        try {
            //set the default properties here
            //myProperties.setProperty("key","value");

            myProperties.setProperty("displayInstantSolution", "true");
            myProperties.setProperty("displayWindowTitles", "true");



            FileWriter myWriter = new FileWriter(configFilePath);     //create new FileWriter
            myProperties.store(myWriter, "category, enabled/disabled, name, file path");    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLanguagePacksNumbers(){
        readLanguagePacks();
        for(String[] languagePack : filePaths){
            updateLanguagePackSelected(Integer.parseInt(languagePack[0]), Boolean.parseBoolean(languagePack[1]), languagePack[2], languagePack[3], languagePack[5]);
        }
    }

    /**
     * Update the language pack information in the properties file language pack files are not affected by these changes)
     * @param category
     * @param selected
     * @param name
     * @param filePath
     * @param configKey
     */
    public void updateLanguagePackSelected(int category, boolean selected, String name, String filePath, String configKey) {
        //ToDo get the current information from the properties file for the specified key and only change "selected"
        //as the first line in each language pack is a word, the counter is set to -1
        int counter = -1;
        try{
            Scanner languagePackReader = new Scanner(new File(filePath));
            while(languagePackReader.hasNextLine()){
                counter++;
                languagePackReader.nextLine();
            }
            languagePackReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Lines: " + counter);




        String value = category + "," + selected + "," + name + "," + filePath + "," + counter;
        try {
            //read the current properties
            FileReader myReader = new FileReader(configFile);
            myProperties.load(myReader);    //load properties
            myReader.close();

            FileWriter myWriter = new FileWriter(configFile);     //create new FileWriter
            myProperties.setProperty(configKey, value);
            myProperties.store(myWriter, "category ,enabled/disabled , name, file path");    //store the properties that were created

            // category + "," + enabled + "," + displayName + "," + desiredFileName + "," + -1);


            myWriter.close();

        } catch (FileNotFoundException e) {
            //file not found
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateMainMenuCheckBoxes();
    }


    //reference to the study space. Used to update the data
    private StudySpace studySpace = null;

    public void addStudySpace(StudySpace studySpace){
        this.studySpace = studySpace;
    }

    public boolean isDisplayWindowTitles(){
        return displayWindowTitles;
    }

    public boolean setDisplayWindowTitles(boolean state){
        try {
            //add the new entry to the properties file
            myProperties.setProperty("displayWindowTitles", state + "");

            FileWriter myWriter = new FileWriter(configFilePath);
            myProperties.store(myWriter,"Update displayWindowTitles");    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            return false;
        }
        displayWindowTitles = state;
        //if study space exists, update it
        if(studySpace != null){
            studySpace.updateStudySpace();
        }
        return true;
    }

    public boolean isDisplayInstantSolution(){
        return displayInstantSolution;
    }

    public boolean setDisplayInstantSolution(boolean state){
        try {
            //add the new entry to the properties file
            myProperties.setProperty("displayInstantSolution", state + "");

            FileWriter myWriter = new FileWriter(configFilePath);
            myProperties.store(myWriter,"Update displayInstantSolution");    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            return false;
        }
        displayInstantSolution = state;
        return true;
    }
}
