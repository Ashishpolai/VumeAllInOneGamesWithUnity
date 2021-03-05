package com.vume.allinonegames.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    private final static int MIN_PASSWORD_LENGTH = 8;
    private final static int MAX_PASSWORD_LENGTH = 20;
    private final static String[] ALLOWED_SPECIAL_CHARS_IN_PASSWORD = {"@", "#", "!", "~", "$", "%", "^", "&", "*", "(", ")", "-", "+", "/", ":", ".", ",", "<", ">", "?", "|"};
    private final static String[] DISALLOWED_CHARS_IN_PASSWORD = {" "};

    private final static int MIN_USERNAME_LENGTH = 3;
    private final static int MAX_USERNAME_LENGTH = 20;
    private final static String VALID_USERNAME_REGEX = "^[aA-zZ]\\w{"+(MIN_USERNAME_LENGTH-1)+","+(MAX_USERNAME_LENGTH-1)+"}$";

    private final static String VALID_EMAIL_REGEX = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    private final static String VALID_ACCOUNT_HOLDER_NAME_REGEX = "^[a-zA-Z]*$"; //ONLY ALPHABETS

    public static final int MIN_AGE = 18;

    public static boolean isValidPassword(String password)
    {
        if (!((password.length() >= MIN_PASSWORD_LENGTH)
                && (password.length() <= MAX_PASSWORD_LENGTH))) {
            return false;
        }

        // for not allowed  characters
        for(int x = 0; x<DISALLOWED_CHARS_IN_PASSWORD.length; x++){
            if(password.contains(DISALLOWED_CHARS_IN_PASSWORD[x])) {
                return false;
            }
        }

        if (true) {
            int count = 0;

            // check digits from 0 to 9
            for (int i = 0; i <= 9; i++) {

                // to convert int to string
                String str1 = Integer.toString(i);

                if (password.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                return false;
            }
        }

        // for special characters
        int specialcharcount = 0;
        for(int x = 0; x< ALLOWED_SPECIAL_CHARS_IN_PASSWORD.length; x++){
            if(password.contains(ALLOWED_SPECIAL_CHARS_IN_PASSWORD[x])) {
                specialcharcount++;
                break;
            }
        }
        if(specialcharcount ==  0)
            return false;


        if (true) {
            int count = 0;

            // checking capital letters
            for (int i = 65; i <= 90; i++) {

                // type casting
                char c = (char)i;

                String str1 = Character.toString(c);
                if (password.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                return false;
            }
        }

        if (true) {
            int count = 0;

            // checking small letters
            for (int i = 90; i <= 122; i++) {

                char c = (char)i;
                String str1 = Character.toString(c);

                if (password.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidUsername(String username){
        // Regex to check valid username.

        // Compile the ReGex
        Pattern p = Pattern.compile(VALID_USERNAME_REGEX);

        // If the username is empty
        // return false
        if (username == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(username);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    public static boolean isEmailValid(String email)
    {
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(VALID_EMAIL_REGEX,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public static boolean isValidAccHolderName(String accntHolderName)
    {
        Pattern pattern = Pattern.compile(VALID_ACCOUNT_HOLDER_NAME_REGEX,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(accntHolderName);

        return matcher.matches();
    }
}
