package com.example.team_project;

import java.util.ArrayList;

public class PublicVariables {
    public static final String[] SURVEY_ITEMS = {"Restaurants", "Sightseeing", "NightLife", "Shopping", "Concerts",
            "Fairs", "Beauty" ,"Working Out", "Parks", "Upscale", "Outdoors", "Indoors", "Family Friendly"};
    public static final String splitindicator = "\\(\\)";
    public static final String separator = "()";
    public ArrayList<String> events;
    public static Boolean isEvent;
    public static String newLoc;
    public static String newLocName;
    public static boolean isCurLoc = true;

    public static Boolean isEvent(String apiId){
        if ('E' != apiId.charAt(0)) {
            PublicVariables.isEvent = true;
        } else {
            PublicVariables.isEvent = false;
        }
        return PublicVariables.isEvent;
    }

    public static String[] primTagRef = new String[]{"TrendyCity verified", "bottomless", "upscale", "young",
            "dress cute", "rooftop", "dress comfy", "insta-worthy", "outdoors", "indoors",
            "clubby", "mall", "food available", "barber", "spa", "classes", "trails",
            "gyms", "family friendly", "museums"};

    public static String getTagStr(int i) {
        switch (i) {
            case 0:
                return "TrendyCity verified";
            case 1:
                return "bottomless";
            case 2:
                return "upscale";
            case 3:
                return "young";
            case 4:
                return "dress cute";
            case 5:
                return "rooftop";
            case 6:
                return "dress comfy";
            case 7:
                return "insta-worthy";
            case 8:
                return "outdoors";
            case 9:
                return "indoors";
            case 10:
                return "clubby";
            case 11:
                return "mall";
            case 12:
                return "food available";
            case 13:
                return "barber";
            case 14:
                return "spa";
            case 15:
                return "class";
            case 16:
                return "trail";
            case 17:
                return "gym";
            case 18:
                return "family friendly";
            case 19:
                return "museum";
            default:
                return "";
        }
    }

    public static String getCategoryStr(int i) {
        switch (i) {
            case 0:
                return "breakfast";
            case 1:
                return "brunch";
            case 2:
                return "lunch";
            case 3:
                return "dinner";
            case 4:
                return "sights";
            case 5:
                return "bar";
            case 6:
                return "shopping";
            case 7:
                return "concert";
            case 8:
                return "fair";
            case 9:
                return "spa";
            case 10:
                return "gym";
            case 11:
                return "park";
            default:
                return "";
        }
    }
}
