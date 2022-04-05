package com.ikkeware.rambooster.utils;

public class Utils {

    public static String shortenText(String text,int limit){
        return (text.length()>limit)?text.substring(0,limit)+"...":text;
    }

}
