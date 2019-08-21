package com.example.webdatabaseacess.util;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtil {

    static String TAG = "NetworkUtil";

    final static String GITHUB_BASE_URL ="https://api.github.com/search/repositories";
    final static String EVO_CRENDENCIADA_URL = "https://pouppe.websiteseguro.com/sistema/v2/api/credenciadas";
    final static String IMG_CLINICS_URL = "https://pouppe.websiteseguro.com/evosaude/sistema/fotos";
    final static String PARAM_QUERY = "q";
    final static String PARAM_SORT = "sort";
    final static String sortBy = "stars";

    public static URL buildUrl(String githubSearchQuery) {
        Uri buildUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_SORT, sortBy).build();
        URL url =  null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlImg(String uniqId, String nameImg) {
        Uri buildUri = Uri.parse(IMG_CLINICS_URL).buildUpon()
                .appendPath(uniqId)
                .appendPath(nameImg).build();
        URL url =  null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlCredenciadasEvo() {
        Uri buildUri = Uri.parse(EVO_CRENDENCIADA_URL).buildUpon().build();
        URL url =  null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, "metodo getResponseFromHttpUrl");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}