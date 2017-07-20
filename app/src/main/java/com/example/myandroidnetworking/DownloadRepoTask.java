package com.example.myandroidnetworking;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lenovo Desktop on 7/20/2017.
 */

public class DownloadRepoTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        try {
            return downloadData(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String downloadData (String urlString) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();

            return convertToString(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder string = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            string.append(line);
        }
        return string.toString();

    }

}
