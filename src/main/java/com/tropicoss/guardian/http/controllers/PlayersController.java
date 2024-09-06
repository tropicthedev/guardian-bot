package com.tropicoss.guardian.http.controllers;

import com.arakelian.faker.service.RandomData;
import com.arakelian.faker.service.RandomPerson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tropicoss.guardian.database.DatabaseManager;
import com.tropicoss.guardian.model.Member;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayersController {


    public PlayersController() throws SQLException {
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/players", this::handlePlayers);
    }

    private void handlePlayers(Context ctx) throws SQLException {

        DatabaseManager db = new DatabaseManager();
        // Calculate purge dates and statuses
        Map<String, LocalDate> purgeDates = db.calculatePurgeDates();
        Map<String, String> userStatuses = db.calculateUserStatuses();

        // Retrieve member list
        List<Member> memberList = db.getAllMembers();
        db.close();

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode players = objectMapper.createArrayNode();

        for (Member member : memberList) {
            PlayerInfoFetcher.Profile profile = member.getPlayerProfile();
            ObjectNode jsonObject = objectMapper.createObjectNode();

            String discordId = member.getDiscordId();
            String status = userStatuses.getOrDefault(discordId, "UNKNOWN");
            LocalDate purgeDate = purgeDates.getOrDefault(discordId, LocalDate.now().plusDays(30)); // Default to 30 days from now if not found

            jsonObject.put("id", discordId);
            jsonObject.put("name", profile.data.player.username);
            jsonObject.put("status", status);
            jsonObject.put("joinDate", member.getCreatedAt().toString());
            jsonObject.put("purgeDate", purgeDate.toString());
            jsonObject.put("avatar", profile.data.player.avatar);

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
