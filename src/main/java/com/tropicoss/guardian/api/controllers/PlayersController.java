package com.tropicoss.guardian.api.controllers;

import com.arakelian.faker.service.RandomData;
import com.arakelian.faker.service.RandomPerson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayersController {

    public void registerRoutes(Javalin app) {
        app.get("/api/players", this::handlePlayers);
    }

    private void handlePlayers(Context ctx) {
        List<String> list = new ArrayList<>();

        list.add("ACTIVE");
        list.add("INACTIVE");
        list.add("NEW");
        list.add("VACATION");

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode players = objectMapper.createArrayNode();
        for (int i = 0; i <= 1000; i++) {
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("id", i);
            jsonObject.put("name", RandomPerson.get().next().getFirstName());
            jsonObject.put("status", getRandomElement(list));
            jsonObject.put("joinDate", RandomData.get().nextDate("joinDate").toLocalDate().toString());
            jsonObject.put("purgeDate", RandomData.get().nextDate("purgeDate").toLocalDate().toString());
            jsonObject.put("avatar", "https://xsgames.co/randomusers/assets/avatars/pixel/44.jpg");


            players.add(jsonObject);
        }

        ctx.json(players);
    }

    public String getRandomElement(List<String> strings)
    {
        Random rand = new Random();
        return strings.get(rand.nextInt(strings.size()));
    }
}
