package main;

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


    private Properties myProperties; //Properties object
    private String filePath;         //path to the configuration file
    private File configFile;         //file object for the configuration file
    private int maxFileIndex = 0;    //holds the max index for FILE_INFORMATION_PREFIX


    ConfigFile(String filePath){
        System.out.println("Created Config file!");
        this.filePath = filePath;
        languageA = "Korean";
        languageB = "English";

        configFile = new File(filePath);
        myProperties = new Properties();

        try{
            if(configFile.createNewFile()){
                //new file created, populate with new properties
                generateNewProperties();
                System.out.println("Generated new file");
            }

            updateMaxIndex();

            System.out.println("Reading File Information");
            updateFileInformation();
            System.out.println("");

            addFileInformation(Main.WORDS, true,"First new Set", "nextFile.txt");
            addFileInformation(Main.WORDS, true,"Second new Set", "nextFile2.txt");
            addFileInformation(Main.WORDS, true,"Second new Set", "nextFile4.txt");
            addFileInformation(Main.WORDS, true,"Fourth new Set", "nextFile2.txt");
            addFileInformation(Main.WORDS, true,"Third new Set", "nextFile3.txt");

            addFileInformation(Main.PHRASES,true,"ENTER", "words5Enter.txt");
            addFileInformation(Main.PHRASES, true, "ENTER", "words5Exit.txt");
            addFileInformation(Main.SPECIAL,true, "ENTER" , "words5Special.txt");

            updateMaxIndex();
            System.out.println("Reading File Information");
            updateFileInformation();
            System.out.println("");

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
     * Reads the current properties and picks out the data related to the file information.
     * This is then stored in the Arraylist {@link #filePaths}
     */
    public void updateFileInformation(){
        try {
            FileReader myReader = new FileReader(configFile);

            myProperties.load(myReader);    //load properties

            //stores all the categories, name, file paths
            filePaths = new ArrayList<>();

            for(String key: myProperties.stringPropertyNames()){
                //key is one of the file paths
                if(key.contains(FILE_INFORMATION_PREFIX)){
                    String tempkey[] = myProperties.getProperty(key).split(",");

                    if(tempkey.length == 4){
                        //key has the correct length and is assumed to be valid
                        String[] tempKeyNew = Arrays.copyOf(tempkey, 5);
                        tempKeyNew[4] = key;   //add the object key to the end
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
     * Adds the information for a new file to the properties.
     *
     * @param category          The category for the file (1=Words, 2=Phrases, 3=Special)
     * @param enabled           Indicates enabled status for the file
     * @param displayName       The display displayName for the file
     * @param desiredFilePath   The path to the file
     * @return   1 if the information was successfully stored,
     *          -1 if the object displayName is already in use,
     *          -2 if the object path is already in use
     */
    int addFileInformation(int category, boolean enabled, String displayName, String desiredFilePath){
        //check if file path or filename already exists in properties
        updateFileInformation();    //get the newest file information
        for(String[] thisArray : filePaths){
            //only allow a display name to be taken twice if it is in a different category
            if(thisArray[2].equals(displayName) && thisArray[0].equals(Integer.toString(category))){
                //display displayName for the object is already in use
                return -1;
            } else if(thisArray[3].equals(desiredFilePath)){
                //file path is already in use
                return -2;
            }
        }

        //add the new file information to the properties
        try {
            //add the new entry to the properties file and increase the maxFileIndex at the same time
            myProperties.setProperty(FILE_INFORMATION_PREFIX + ++maxFileIndex, category + "," + enabled + "," + displayName + "," + desiredFilePath);

            FileWriter myWriter = new FileWriter(filePath);
            myProperties.store(myWriter,"Added new category with index " + maxFileIndex);    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Generates a set of default properties. Should be called if a new properties file gets generated.
     */
    private void generateNewProperties(){
        try {
            //set the default properties here
            myProperties.setProperty(FILE_INFORMATION_PREFIX + 1,"1,true,lesson1,words1.txt");
            myProperties.setProperty(FILE_INFORMATION_PREFIX + 2,"1,true,lesson2,words2.txt");
            myProperties.setProperty(FILE_INFORMATION_PREFIX + 3,"1,true,lesson3,tooMuch,words3.txt");
            myProperties.setProperty(FILE_INFORMATION_PREFIX + 4,"1,true,lesson4,words4.txt");
            myProperties.setProperty(FILE_INFORMATION_PREFIX + 5,"1,true,lesson5,words5.txt");


            FileWriter myWriter = new FileWriter(filePath);     //create new FileWriter
            myProperties.store(myWriter, "category, enabled/disabled, name, file path");    //store the properties that were created
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putUpdatedFileInformation(int category, boolean selected, String name, String filePath, String configKey) {
        String value = category + "," + selected + "," + name + "," + filePath;
        try {
            //read the current properties
            FileReader myReader = new FileReader(configFile);
            myProperties.load(myReader);    //load properties
            myReader.close();

            FileWriter myWriter = new FileWriter(configFile);     //create new FileWriter
            myProperties.setProperty(configKey, value);
            myProperties.store(myWriter, "category ,enabled/disabled , name, file path");    //store the properties that were created
            myWriter.close();

        } catch (FileNotFoundException e) {
            //file not found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ToDo implement this
    /**
     * Creates a new language file and adds its information to the config file.
     * @param category      The category for the file (1=Words, 2=Phrases, 3=Special)
     * @param displayName   The display displayName for the file
     * @param filePath      The path to the file
     * @return              Successful creation
     */
    public boolean addLanguageFile(int category, boolean enabled, String displayName, String filePath){
        //check if the filePath is valid
        try{
            Paths.get(filePath);
        } catch( InvalidPathException | NullPointerException e){
            //ToDo inform the user of the error
            return false;
        }


        switch(addFileInformation(category, enabled, displayName, filePath)){
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
