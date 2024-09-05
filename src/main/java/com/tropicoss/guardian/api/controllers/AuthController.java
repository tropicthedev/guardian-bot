package com.tropicoss.guardian.api.controllers;
import com.tropicoss.guardian.config.Config;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.HttpStatus;

//import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.tropicoss.guardian.api.utils.JWTUtil;
import io.javalin.http.SameSite;
import org.json.JSONObject;

public class AuthController {
    private final Config config = Config.getInstance();
    private final String clientId = config.getConfig().getBot().getClientId();
    private final String clientSecret = config.getConfig().getBot().getClientSecret();
    private final String redirectUri = config.getConfig().getBot().getRedirectUri();
    private final String scope = "identify email";

    public void registerRoutes(Javalin app) {
        app.get("/auth/login", this::handleLogin);
        app.get("/auth/callback", this::handleCallback);
    }

    private void handleLogin(Context ctx) {
        String authUrl = "https://discord.com/api/oauth2/authorize" +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=identify";
        ctx.redirect(authUrl);
    }

    private void handleCallback(Context ctx) throws ExecutionException, InterruptedException {
        String code = ctx.queryParam("code");

        if (code == null) {
            ctx.result("Authentication failed");
            return;
        }

        JSONObject tokenResponse = exchangeCodeForToken(code);
        if (tokenResponse == null) {
            ctx.result("Token exchange failed");
            return;
        }

        String accessToken = tokenResponse.getString("access_token");
        JSONObject userInfo = fetchUserInfo(accessToken);

        // Here you would typically create a session, store user info, etc.
        ctx.result("Authentication successful for user: " + userInfo.getString("username"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "1234567890");
        claims.put("username", "John Doe");
        claims.put("response", response.get().body());

        String jwt = JWTUtil.generateToken(claims, "jwt");

        Cookie jwtCookie = new Cookie(
                "token",
                jwt,
                "/",
                86400,
                true,
                1,
                true,
                null,
                "http://localhost",
                SameSite.NONE
        );

        ctx.cookie(jwtCookie);

        ctx.redirect("http://localhost:5173/players", HttpStatus.PERMANENT_REDIRECT);

    }

    private static JSONObject exchangeCodeForToken(String code) {
        HttpRequest tokenRequest = HttpRequest.post("https://discord.com/api/oauth2/token")
                .form("client_id", CLIENT_ID)
                .form("client_secret", CLIENT_SECRET)
                .form("grant_type", "authorization_code")
                .form("code", code)
                .form("redirect_uri", REDIRECT_URI);

        if (tokenRequest.code() == 200) {
            return new JSONObject(tokenRequest.body());
        }
        return null;
    }

    private static JSONObject fetchUserInfo(String accessToken) {
        HttpRequest userRequest = HttpRequest.get("https://discord.com/api/users/@me")
                .header("Authorization", "Bearer " + accessToken);

        if (userRequest.code() == 200) {
            return new JSONObject(userRequest.body());
        }
        return null;
    }
}

