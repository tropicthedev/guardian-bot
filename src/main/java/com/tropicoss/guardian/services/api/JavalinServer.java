package com.tropicoss.guardian.services.api;

import com.tropicoss.guardian.config.Config;
import com.tropicoss.guardian.services.api.controllers.ApplicationsController;
import com.tropicoss.guardian.services.api.controllers.AuthController;
import com.tropicoss.guardian.services.api.controllers.PlayersController;
import com.tropicoss.guardian.services.api.controllers.ServersController;
import com.tropicoss.guardian.services.api.middleware.JWTMiddleware;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;

import java.sql.SQLException;

import static com.tropicoss.guardian.Mod.LOGGER;

public class JavalinServer {

    private final Javalin app;

    public JavalinServer() throws SQLException {

        app = Javalin.create(config -> {
            config.spaRoot.addFile("/", "/static/index.html");
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });

            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });

        });

        app.before("/api/*", new JWTMiddleware());

        AuthController authController = new AuthController();
        PlayersController playersController = new PlayersController();
        ServersController serversController = new ServersController();
        ApplicationsController applicationsController = new ApplicationsController();

        authController.registerRoutes(app);
        playersController.registerRoutes(app);
        serversController.registerRoutes(app);
        applicationsController.registerRoutes(app);
    }

    public void startServer() {
        app.start(Config.getInstance().getConfig().getServer().getPort());
        LOGGER.info("Javalin server started on port: {}", Config.getInstance().getConfig().getServer().getPort());
    }

    public void stopServer() {
        app.stop();
        LOGGER.info("Javalin server stopped.");
    }
}
