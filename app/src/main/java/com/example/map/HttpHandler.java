package com.example.map;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHandler {
    public static String getRequest(String urlStr){
        StringBuilder builder = new StringBuilder();
        try {
            // Step 1: Connect to the web server
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)
                    url.openConnection();

            // Step 2: Read the input from the server
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            String line="";
            while ((line = reader.readLine())!=null){
                builder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();

    }

    public static String postRequest(String urlStr, Location location){
        String status="";
        try {
            //Step 1 - prepare the connection
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Step 2 - prepare the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", location.name);
            jsonObject.put("members", location.memberIDs);
            jsonObject.put("latitude", location.latitude);
            jsonObject.put("longitude", location.longitude);
            //Step 3 - Writing data to the web server
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObject.toString());
            os.flush();
            os.close();
            status = conn.getResponseCode() + ": " + conn.getResponseMessage();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
}
