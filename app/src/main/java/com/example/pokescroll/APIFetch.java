package com.example.pokescroll;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIFetch {

    static String APIURL = "https://pokeapi.co/api/v2/pokemon/%s";


    public static JSONObject getPokeList() {
        try {
            URL url = new URL(String.format(APIURL, "?limit=151"));
            Log.d("URL", String.format(APIURL, "?limit=151"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStreamReader streamreader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(
                    streamreader);

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getPokemon(Context context, Integer pokenum) {
        try {
            URL url = new URL(String.format(APIURL, Integer.toString(pokenum)));
            Log.d("URL", String.format(APIURL, Integer.toString(pokenum)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStreamReader streamreader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(
                    streamreader);

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
