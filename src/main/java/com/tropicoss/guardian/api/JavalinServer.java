package com.tropicoss.guardian.api;

import com.tropicoss.guardian.api.controllers.ApplicationsController;
import com.tropicoss.guardian.api.controllers.AuthController;
import com.tropicoss.guardian.api.controllers.PlayersController;
import com.tropicoss.guardian.api.controllers.ServersController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class JavalinServer {

    private final Javalin app;

    public JavalinServer() {

        app = Javalin.create(config -> {
            config.spaRoot.addFile("/", "/static/index.html");
            config.bundledPlugins.enableCors(cors ->{
                    cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });

            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });

        });

//        app.before("/api/*", new JWTMiddleware());

        AuthController authController = new AuthController();
        PlayersController playersController = new PlayersController();
        ServersController serversController = new ServersController();
        ApplicationsController applicationsController = new ApplicationsController();

        authController.registerRoutes(app);
        playersController.registerRoutes(app);
        serversController.registerRoutes(app);
        applicationsController.registerRoutes(app);
    }

    public void startServer(int port) {
        app.start(port);
        System.out.println("Javalin server started on port " + port);
    }

    public void stopServer() {
        app.stop();
        System.out.println("Javalin server stopped.");
    }
}
