package com.tropicoss.guardian.api.controllers;

import com.arakelian.faker.service.RandomData;
import com.arakelian.faker.service.RandomPerson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tropicoss.guardian.database.dao.impl.MemberDAOImpl;
import com.tropicoss.guardian.database.model.Member;
import com.tropicoss.guardian.utils.PlayerInfoFetcher;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayersController {

    private final MemberDAOImpl memberDAO = new MemberDAOImpl();

    public PlayersController() throws SQLException {
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/players", this::handlePlayers);
    }

    private void handlePlayers(Context ctx) throws SQLException {

        List<Member> memberList = memberDAO.getAllMembers();

        List<String> list = new ArrayList<>();

        list.add("ACTIVE");
        list.add("INACTIVE");
        list.add("NEW");
        list.add("VACATION");

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode players = objectMapper.createArrayNode();
        for (Member member : memberList) {
            PlayerInfoFetcher.Profile profile = member.getPlayerProfile();
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("id", member.getMemberId());
            jsonObject.put("name", profile.data.player.username);
            jsonObject.put("status", getRandomElement(list));
            jsonObject.put("joinDate", member.getCreatedAt().toString());
            jsonObject.put("purgeDate", RandomData.get().nextDate("purgeDate").toLocalDate().toString());
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
