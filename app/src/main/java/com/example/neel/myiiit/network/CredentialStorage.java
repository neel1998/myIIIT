package com.example.neel.myiiit.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

class CredentialStorage {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private String mUsername;
    private String mPassword;

    private SharedPreferences mSharedPreferences;

    private static CredentialStorage sInstance;

    static CredentialStorage getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CredentialStorage(context.getApplicationContext());
        }

        return sInstance;
    }

    private CredentialStorage(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        load();
    }

    void setCredentials(@NotNull String username, @NotNull String password) {
        mUsername = username;
        mPassword = password;

        save();
    }

    void removeCredentials() {
        mUsername = null;
        mPassword = null;

        save();
    }

    String getUsername() {
        return mUsername;
    }

    String getPassword() {
        return mPassword;
    }

    private void save() {
        SharedPreferences.Editor sharedPrefEditor = mSharedPreferences.edit();

        if (mUsername == null) {
            sharedPrefEditor.remove(KEY_USERNAME);
        } else {
            sharedPrefEditor.putString(KEY_USERNAME, mUsername);
        }

        if (mPassword == null) {
            sharedPrefEditor.remove(KEY_PASSWORD);
        } else {
            sharedPrefEditor.putString(KEY_PASSWORD, mPassword);
        }

        sharedPrefEditor.apply();
    }

    private void load() {
        mUsername = mSharedPreferences.getString(KEY_USERNAME, null);
        mPassword = mSharedPreferences.getString(KEY_PASSWORD, null);
    }
}
