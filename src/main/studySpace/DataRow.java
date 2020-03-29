package main.studySpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class DataRow{
    private int randomNumber;
    private String languageA = null;
    private String languageB = null;
    private String aux = null;
    private int noOfSideWindows = 0;
    private ArrayList<String> sideWindows = null;

    private String auxName = null;
    private ArrayList<String> windowNames = null;

    DataRow(String thisLine, StudySpace studySpace, String windowNames){

        //store the window names for each item
        this.windowNames = new ArrayList<>(Arrays.asList(windowNames.split("\\[name aux\\]|\\[name side \\d\\]")));
        //remove the empty first string that gets created by String.split()
        if(this.windowNames.size() > 0) {
            this.windowNames.remove(0);
        }




        //sideWindows = new ArrayList<>(Collections.nCopies(noOfSideWindows, null));
        //set the random number used in the mixed language setting
        Random rnd = new Random();
        randomNumber = rnd.nextInt(2);

        ArrayList<String> temp = new ArrayList<>(Arrays.asList(thisLine.split("\\[Language A\\]|\\[Language B\\]|\\[Aux\\]|\\[Side \\d\\]")));
        if(temp.size() <= 1){
            //ToDo handle case
            return;
        }
        //remove the empty first string that gets created by String.split()
        temp.remove(0);

        //set the language
        languageA = temp.get(0);
        temp.remove(0);
        languageB = temp.get(0);
        temp.remove(0);


        if(temp.size() == 1){
            //aux but no side windows
            this.aux = temp.get(0);
            studySpace.setHasAux(true);
        } else if(temp.size() > 1){
            //aux possibly exists and side windows exist
            if(temp.get(0).equals("")){
                this.aux = null;
            } else{
                this.aux = temp.get(0);
                studySpace.setHasAux(true);
            }
            temp.remove(0);

            //set the number of side windows
            this.noOfSideWindows = temp.size();
            //update the maximum number of side windows if needed
            if(noOfSideWindows > studySpace.getMaxSideWindows()){
                studySpace.setMaxSideWindows(noOfSideWindows);
            }
            //fill side windows arrayList with data for each side window or null for unused side windows
            sideWindows = new ArrayList<>();
            for(int i = 0; i < temp.size(); i++){
                String thisSideWindow = temp.get(i);
                if(!thisSideWindow.equals("")){
                    sideWindows.add(i, thisSideWindow);
                } else {
                    sideWindows.add(i, null);
                }
            }
        }
    }

    /**
     * Returns an ArrayList with all the text for the side windows. If one of the side windows is not used,
     * the String will be null. If no side window exists for the row, this method will return null.
     * @return  An ArrayList containing the text for the side windows or null if no side windows exist
     */
    public ArrayList<String> getSideWindows(){
        if(noOfSideWindows == 0){
            return null;
        }else {
            return sideWindows;
        }
    }

    public ArrayList<String> getWindowNames() {
        return windowNames;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public String getLanguageA() {
        return languageA;
    }

    public String getLanguageB() {
        return languageB;
    }

    public String getAux() {
        return aux;
    }

    public int getNoOfSideWindows() {
        return noOfSideWindows;
    }

    @Override
    public String toString() {
        String sideWindowsString = null;
        if(sideWindows != null){
            sideWindowsString = Arrays.toString(sideWindows.toArray());
        }
        return "[" + randomNumber
                + ", " + languageA
                + ", " + languageB
                + ", " + aux
                + ", " + noOfSideWindows
                + ", " + sideWindowsString + "]";
    }
}
