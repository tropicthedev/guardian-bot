package com.tropicoss.guardian.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tropicoss.guardian.api.DiscordURLHandler;
import com.tropicoss.guardian.config.Config;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.HttpStatus;

import java.io.UnsupportedEncodingException;
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

    private String generateOAuthUrl(String clientId, String redirectUri, String scope) {
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

    private void handleLogin(Context ctx) {
        String oauthUrl = generateOAuthUrl(clientId, redirectUri, scope);
        ctx.redirect(oauthUrl);
    }

    private void handleCallback(Context ctx) throws ExecutionException, InterruptedException {
        String code = ctx.queryParam("code");

        if (code != null) {
            String tokenUrl = "https://discord.com/api/v10/oauth2/token";

            // Prepare the request body with URL encoding
            String body = String.format(
                    "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s",
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                    URLEncoder.encode(clientSecret, StandardCharsets.UTF_8),
                    URLEncoder.encode(code, StandardCharsets.UTF_8),
                    URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            );

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // Send the request
            CompletableFuture<HttpResponse<String>> response;

            try (HttpClient client = HttpClient.newHttpClient()) {
                response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            }

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

        } else {
            ctx.status(400).result("Authorization code missing");
        }
    }
}

