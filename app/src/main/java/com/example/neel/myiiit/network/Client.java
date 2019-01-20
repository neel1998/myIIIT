package com.example.neel.myiiit.network;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;

public class Client {
    private static OkHttpClient  mClient;

    static OkHttpClient getClient(Context context) {
        if (mClient == null) {
            ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            mClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        }
        return  mClient;
    }

    static void makeNull() {
        mClient = null;
    }
}
