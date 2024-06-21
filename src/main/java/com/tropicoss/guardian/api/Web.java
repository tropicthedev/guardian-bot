//package com.tropicoss.guardian.cli.api;
//
//import static spark.Spark.*;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.tropicoss.guardian.common.config.ServerConfig;
//import com.tropicoss.guardian.common.database.model.User;
//import okhttp3.*;
//import spark.Request;
//import spark.Response;
//import spark.Session;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.Optional;
//
//public class Web {
//    private static ServerConfig _config;
//    private static final String REDIRECT_URI = "http://localhost:4567/callback";
//    private static final String DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
//    private static final String DISCORD_USER_URL = "https://discord.com/api/users/@me";
//    private static final String DISCORD_REVOKE_URL = "https://discord.com/api/oauth2/token/revoke";
//    private static final String SCOPE = "identify";
//
//    public Web(ServerConfig config) {
//        _config = config;
//    }
//
//    public static void start() {
//        port(4567);
//        staticFiles.location("/public");
//
//        before((request, response) -> {
//            Session session = request.session(false);
//            if (session != null && session.attribute("user_id") != null) {
//                Optional<User> user = userDAO.findById(session.attribute("user_id"));
//                user.ifPresent(value -> request.attribute("user", value));
//            }
//        });
//
//        get("/", (req, res) -> {
//            User user = req.attribute("user");
//            if (user != null) {
//                return "Hello, " + user.getUsername() + "#" + user.getDiscriminator() + "!";
//            } else {
//                String authorizeUrl = "https://discord.com/api/oauth2/authorize" +
//                        "?client_id=" + _config.discordClientId +
//                        "&redirect_uri=" + REDIRECT_URI +
//                        "&response_type=code" +
//                        "&scope=" + SCOPE;
//                res.redirect(authorizeUrl);
//                return null;
//            }
//        });
//
//        get("/callback", Web::handleCallback);
//
//        get("/revoke", Web::handleRevoke);
//    }
//    private static Object handleCallback(Request req, Response res) throws Exception {
//        String code = req.queryParams("code");
//        if (code == null) {
//            return "Authorization code not found";
//        }
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("client_id", CLIENT_ID)
//                .add("client_secret", CLIENT_SECRET)
//                .add("grant_type", "authorization_code")
//                .add("code", code)
//                .add("redirect_uri", REDIRECT_URI)
//                .build();
//
//
//
//        try (okhttp3.Response response = client.newCall(
//                new okhttp3.Request.Builder()
//                        .url(DISCORD_TOKEN_URL)
//                        .post(formBody)
//                        .build()
//        ).execute()) {
//            if (!response.isSuccessful()) {
//                return "Failed to retrieve access token: " + response.body().string();
//            }
//
//            JsonObject tokenResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
//            String accessToken = tokenResponse.get("access_token").getAsString();
//
//            Request userRequest = new Request.Builder()
//                    .url(DISCORD_USER_URL)
//                    .addHeader("Authorization", "Bearer " + accessToken)
//                    .build();
//
//            try (Response userResponse = client.newCall(userRequest).execute()) {
//                if (!userResponse.isSuccessful()) {
//                    return "Failed to retrieve user info: " + userResponse.body().string();
//                }
//
//                JsonObject userResponseJson = JsonParser.parseString(userResponse.body().string()).getAsJsonObject();
//                String id = userResponseJson.get("id").getAsString();
//                String username = userResponseJson.get("username").getAsString();
//                String discriminator = userResponseJson.get("discriminator").getAsString();
//
//                // Create a user object and store the access token
//                User user = new User(id, username, discriminator, accessToken);
//                userDAO.save(user);
//
//                // Create a session and store the user ID
//                Session session = req.session(true);
//                session.attribute("user_id", id);
//
//                return "Hello, " + user.getUsername() + "#" + user.getDiscriminator() + "!";
//            }
//        }
//    }
//
//    private static Object handleRevoke(Request req, Response res) throws Exception {
//        User user = req.attribute("user");
//        if (user == null) {
//            res.redirect("/");
//            return null;
//        }
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("client_id", CLIENT_ID)
//                .add("client_secret", CLIENT_SECRET)
//                .add("token", user.getAccessToken())
//                .build();
//
//        Request request = new Request.Builder()
//                .url(DISCORD_REVOKE_URL)
//                .post(formBody)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                return "Failed to revoke token: " + response.body().string();
//            }
//
//            // Remove the user from the session and the user store
//            req.session().removeAttribute("user_id");
//            userDAO.deleteById(user.getId());
//
//            return "Token revoked successfully. You have been logged out.";
//        }
//    }
//
//    private static boolean isAccessTokenValid(String accessToken) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(DISCORD_USER_URL)
//                .addHeader("Authorization", "Bearer " + accessToken)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            return response.isSuccessful();
//        }
//    }
//}
