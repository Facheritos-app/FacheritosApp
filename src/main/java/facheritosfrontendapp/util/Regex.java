package facheritosfrontendapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    public static Boolean emails(String text){
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).find();
    }

    public static Boolean onlyDigits(String text){
        Pattern pattern = Pattern.compile("^\\d+$");
        return pattern.matcher(text).find();
    }


    public static Boolean password(String text){
        Pattern patternNumber = Pattern.compile("(.)*(\\d)(.)*", Pattern.CASE_INSENSITIVE);
        Pattern patternCapital =  Pattern.compile("(.)*[A-Z](.)*", Pattern.CASE_INSENSITIVE);
        Pattern patternSymbol = Pattern.compile("(.)*[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-](.)*", Pattern.CASE_INSENSITIVE);

        return (patternNumber.matcher(text).find() && patternCapital.matcher(text).find() &&
                patternSymbol.matcher(text).find());
    }

    public static Boolean noDigits(String text){
        Pattern pattern = Pattern.compile("^\\D*$");
        return pattern.matcher(text).find();
    }

    public static Boolean onlyLetters(String text){
        return text.matches("[a-zA-Z ]+");
    }

}
