package com.trivedi.neel.myiiit.network;

import android.content.Context;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    private static String getUrl(String url, boolean intranet) {
        HttpUrl httpUrl = HttpUrl.get(url);

        if (intranet || httpUrl.host().equals("reverseproxy.iiit.ac.in")) {
            return url;
        }

        HttpUrl browseUrl = HttpUrl.parse("https://reverseproxy.iiit.ac.in/browse.php");

        String final_url = browseUrl.newBuilder()
                .setQueryParameter("u", url)
                .build()
                .toString();

        return final_url;
    }

    public static NetworkResponse request(Context context, RequestBody body, String url) throws AuthenticationException, IOException {
        return request(context, body, url, true);
    }

    private static NetworkResponse request(Context context, RequestBody body, String url, boolean canAttemptLogin) throws AuthenticationException, IOException {
        OkHttpClient client = Client.getClient(context);

        boolean intranet = OnIntranet(context);

        Log.d("Network",  "URL: " + url);
        url = getUrl(url, intranet);
        Log.d("Network",  "Final URL: " + url);

        Request.Builder requestBuilder = new Request.Builder().url(url);

        if (body != null) {
            requestBuilder.post(body);
        }

        if (!intranet) {
            CredentialStorage credentialStorage = CredentialStorage.getInstance(context);
            String credentials = Credentials.basic(credentialStorage.getUsername(), credentialStorage.getPassword());

            requestBuilder.header("Authorization", credentials);
        }

        Request request = requestBuilder.build();

        NetworkResponse response;

        try {
            response = new NetworkResponse(client.newCall(request).execute());

            if (!intranet && response.code() == 401) {
                // Auth failed
                throw new AuthenticationException();
            }

            boolean shouldRetry = false;

            if (canAttemptLogin && shouldInitReverseproxy(response.getSoup())) {
                Log.d("Network", "Initializing reverseproxy");

                response = initReverseproxy(context);

                if (response.code() == 401) {
                    // Auth failed
                    throw new AuthenticationException();
                }

                shouldRetry = true;
            }

            if (canAttemptLogin && isLoginPage(response.getSoup())) {
                Log.d("Network", "Attempting login");
                if (!loginCore(context, response.getSoup())) {
                    throw new AuthenticationException();
                }
                shouldRetry = true;
            }

            if (shouldRetry) {
                Log.d("Network", "Retrying URL: " + url);
                client = Client.getClient(context);
                response = new NetworkResponse(client.newCall(request).execute());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return response;
    }

    private static boolean isLoginPage(Document soup) {
        return soup.title().equals("Login - CAS – Central Authentication Service") &&
                soup.selectFirst("input#username") != null &&
                soup.selectFirst("input#password") != null;
    }

    private static boolean shouldInitReverseproxy(Document soup) {
        return soup.title().equals("reverseproxy.iiit.ac.in Glype® proxy");
    }

    private static NetworkResponse initReverseproxy(Context context) throws AuthenticationException, IOException {
        RequestBody body = new FormBody.Builder()
                .add("u", "login.iiit.ac.in")
                .add("allowCookies", "on")
                .build();

        String final_url = "https://reverseproxy.iiit.ac.in/includes/process.php?action=update";

        return Network.request(context, body, final_url, false);
    }

    private static boolean loginCore(Context context, Document casSoup) throws AuthenticationException, IOException {
        CredentialStorage credentialStorage = CredentialStorage.getInstance(context);

        Element form = casSoup.getElementById("fm1");
        String loginUrl = "https://login.iiit.ac.in/cas/login";

        Elements fields = form.getElementsByTag("input");

        FormBody.Builder loginBodyBuilder = new FormBody.Builder();
        for (Element field : fields) {
            switch (field.attr("name")) {
                case "username":
                    loginBodyBuilder.add(field.attr("name"), credentialStorage.getUsername());
                    break;
                case "password":
                    loginBodyBuilder.add(field.attr("name"), credentialStorage.getPassword());
                    break;
                default:
                    loginBodyBuilder.add(field.attr("name"), field.attr("value"));
                    break;
            }
        }

        NetworkResponse response = Network.request(context, loginBodyBuilder.build(), loginUrl, false);

        return response.code() == 200 && !isLoginPage(response.getSoup());
    }

    public static boolean login(Context context) throws AuthenticationException, IOException {
        NetworkResponse response = Network.request(context, null, "https://login.iiit.ac.in");
        return response.code() == 200 && !isLoginPage(response.getSoup());
    }

    public static void setCredentials(Context context, String username, String password) {
        CredentialStorage credentialStorage = CredentialStorage.getInstance(context);
        credentialStorage.setCredentials(username, password);
    }

    public static void removeCredentials(Context context) {
        CredentialStorage credentialStorage = CredentialStorage.getInstance(context);
        credentialStorage.removeCredentials();
        Client.makeNull();
    }
    public static  boolean isCredentialAvailable (Context context) {
        CredentialStorage credentialStorage = CredentialStorage.getInstance(context);
        return credentialStorage.getUsername() != null && credentialStorage.getPassword() != null;
    }
    private static boolean OnIntranet(Context context) {
        boolean result = false;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://reverseproxy.iiit.ac.in/")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            result = true;
        }
        return  result;
    }
}


