package com.example.team_project;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

public class PublicVariables {
    public static final String[] SURVEY_ITEMS = {"Restaurants", "Sightseeing", "NightLife", "Shopping", "Concerts",
            "Fairs", "Beauty" ,"Working Out", "Parks", "Upscale", "Outdoors", "Indoors", "Family Friendly"};
    public static Boolean isEvent;
    public static String newLoc;
    public static String newLocName;
    //TODO save currentlocation on Parse
    public static boolean isCurLoc = true;
    public static String googleApi;
    public static String eventfulApi;
    public static double latitude;
    public static double longitude;
    public static boolean type;

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
                return "dessert";
            case 3:
                return "dinner";
            case 4:
                return "museum";
            case 5:
                return "nightlife";
            case 6:
                return "shopping";
            case 7:
                return "concert";
            case 8:
                return "fair";
            case 9:
                return "salon";
            case 10:
                return "gym";
            case 11:
                return "park";
            default:
                return "";
        }
    }

    public static String getUserInput(int i) {
        switch (i) {
            case 0:
                return "breakfast";
            case 1:
                return "brunch";
            case 2:
                return "dessert";
            case 3:
                return "dinner";
            case 4:
                return "museum";
            case 5:
                return "bar";
            case 6:
                return "shopping";
            case 7:
                return "concert";
            case 8:
                return "fair";
            case 9:
                return "salon";
            case 10:
                return "gym";
            case 11:
                return "park";
            default:
                return "";
        }
    }

    public static ArrayList<String> getTags(int category) {
        ArrayList<String> mSubTags = new ArrayList<>();
        switch (category) {
            case 0:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 1:
                mSubTags.clear();
                mSubTags.add("bottomless");
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 2:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 3:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 4:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("museum");
                mSubTags.add("HotSpots verified");
                break;
            case 5:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("young");
                mSubTags.add("clubby");
                mSubTags.add("food available");
                mSubTags.add("rooftop");
                mSubTags.add("HotSpots verified");
                break;
            case 6:
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("mall");
                mSubTags.add("HotSpots verified");
                break;
            case 7:
                mSubTags.clear();
                mSubTags.add("indoors");
                mSubTags.add("outdoors");
                mSubTags.add("upscale");
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 8:
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 9:
                mSubTags.clear();
                mSubTags.add("barber");
                mSubTags.add("spa");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            case 10:
                mSubTags.clear();
                mSubTags.add("classes");
                mSubTags.add("trails");
                mSubTags.add("gyms");
                mSubTags.add("HotSpots verified");
                break;
            case 11:
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("HotSpots verified");
                break;
            default:
                return null;
        }
        return mSubTags;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
