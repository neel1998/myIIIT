package com.example.neel.myiiit;

public class CredentialsClass {
    private static String username, pswd;
    public CredentialsClass(String un, String p) {
        username = un;
        pswd = p;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPswd() {
        return pswd;
    }
}
