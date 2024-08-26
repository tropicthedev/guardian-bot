package com.tropicoss.guardian.api;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class JavalinServer {

    private Javalin app;

    public JavalinServer() {
        // Initialize Javalin
        app = Javalin.create(config -> {
            config.spaRoot.addFile("/", "/static/index.html");

            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });

        }).get("/api/*", ctx -> ctx.status(400));
    }

    // Method to start the server
    public void startServer(int port) {
        app.start(port);
        System.out.println("Javalin server started on port " + port);
    }

    // Method to stop the server
    public void stopServer() {
        app.stop();
        System.out.println("Javalin server stopped.");
    }
}
