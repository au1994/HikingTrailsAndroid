package com.grofers.hikingTrailsMap.maphiking;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by abhishekupadhyay on 10/06/16.
 */
public class PostJsonData {
    private JSONObject jsonObject;
    private String urlString;

    public PostJsonData(JSONObject jsonObject, String url) {
        this.jsonObject = jsonObject;
        this.urlString = url;
    }

    public String postData() throws IOException {

        String response="";
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);

        OutputStream out = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")
        );
        writer.write(jsonObject.toString());
        writer.flush();
        writer.close();
        out.close();
        urlConnection.connect();
        int responseCode  = urlConnection.getResponseCode();
        if(responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }

        return response;


    }


}
