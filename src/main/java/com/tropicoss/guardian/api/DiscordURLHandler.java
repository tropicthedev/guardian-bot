package com.tropicoss.guardian.api;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DiscordURLHandler {
    // Method to generate OAuth2 URL
    public String generateOAuthUrl(String clientId, String redirectUri, String scope) {
        try {
            String encodedRedirectUri = URLEncoder.encode(redirectUri, "UTF-8");
            String encodedScope = URLEncoder.encode(scope, "UTF-8");

            return String.format(
                    "https://discord.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=%s",
                    clientId, encodedRedirectUri, encodedScope
            );

        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // Method to extract parameters from the URL fragment
    public Map<String, String> extractParametersFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String query = url.getRef(); // This will get everything after the '#'

            // Split the query into key-value pairs
            String[] params = query.split("&");
            Map<String, String> paramMap = new HashMap<>();

            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    paramMap.put(pair[0], pair[1]);
                } else {
                    paramMap.put(pair[0], ""); // If no value is present
                }
            }

            return paramMap;

        } catch (Exception e) {
            return null;
        }
    }
}
