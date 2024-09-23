package com.tropicoss.guardian.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.tropicoss.guardian.Mod.LOGGER;

public class PlayerInfoFetcher {

    public static Profile getProfile(String playerId) {
        try {
            URL url = new URI("https://playerdb.co/api/player/minecraft/" + playerId).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                return gson.fromJson(response.toString(), Profile.class);
            } else {
                LOGGER.error("HTTP request failed with response code: {}", responseCode);
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Error fetching player info: {}", e.getMessage());
        }
        return null;
    }

    public static class Profile {
        public Data data;

        public Profile(Data data) {
            this.data = data;
        }

        public static class Data {
            public Player player;
        }

        public static class Player {
            public String username;
            public String id;
            public String raw_id;
            public String avatar;
        }
    }
}
