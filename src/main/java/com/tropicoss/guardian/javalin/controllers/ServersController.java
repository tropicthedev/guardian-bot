package com.tropicoss.guardian.javalin.controllers;

import com.arakelian.faker.service.RandomPerson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class ServersController {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public void registerRoutes(Javalin app) {
        app.get("/api/servers", this::handlePlayers);
        app.delete("/api/servers/{id}", this::handleDelete);
    }

    private void handlePlayers(Context ctx) {
        List<String> list = new ArrayList<>();

        list.add("ONLINE");
        list.add("OFFLINE");

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode applications = objectMapper.createArrayNode();
        for (int i = 0; i <= 1000; i++) {
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("id", i);
            jsonObject.put("name", RandomPerson.get().next().getFirstName());
            jsonObject.put("status", getRandomElement(list));
            jsonObject.put("apiToken", generateNewToken());


            applications.add(jsonObject);
        }

        ctx.json(applications);
    }

    private void handleDelete(Context ctx) {
        String id = ctx.pathParam("id");

        ctx.status(200);
    }

    public String getRandomElement(List<String> strings)
    {
        Random rand = new Random();
        return strings.get(rand.nextInt(strings.size()));
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
