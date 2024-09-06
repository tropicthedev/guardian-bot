package com.tropicoss.guardian.http.middleware;

import io.javalin.http.UnauthorizedResponse;
import io.jsonwebtoken.Claims;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import com.tropicoss.guardian.http.utils.JWTUtil;
import org.jetbrains.annotations.NotNull;

public class JWTMiddleware implements Handler {

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            // Retrieve token from cookie
            String token = ctx.cookie("token");
            if (token == null) {
                throw new UnauthorizedResponse();
            }

            // Validate and parse JWT
            Claims claims = (Claims) JWTUtil.validateToken(token);
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);

            // Store user details in context for later use
            ctx.attribute("userId", userId);
            ctx.attribute("username", username);

        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
            ctx.redirect("/");
        }
    }
}
