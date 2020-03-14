package main;

public class Settings {
    private String languageA;
    private String languageB;

    public Settings(){

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

}
