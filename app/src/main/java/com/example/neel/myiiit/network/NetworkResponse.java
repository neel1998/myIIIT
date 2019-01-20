package com.example.neel.myiiit.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Response;

public class NetworkResponse {
    private Document mSoup;
    private Response mResponse;

    NetworkResponse(Response response) {
        mResponse = response;

        try {
            mSoup = Jsoup.parse(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response getResponse() {
        return mResponse;
    }

    public int code() {
        return mResponse.code();
    }

    public Document getSoup() {
        return mSoup;
    }
}
