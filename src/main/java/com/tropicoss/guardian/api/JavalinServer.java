package com.tropicoss.guardian.api;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class JavalinServer {

    private Javalin app;

    public JavalinServer() {
        // Initialize Javalin
        app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/static"; // The folder inside resources to serve files from
                staticFiles.location = Location.CLASSPATH;
            });
        });
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

    // Optional: Add routes or other configurations here
    public void addRoutes() {
        app.get("/", ctx -> ctx.result("Hello, Javalin!"));
        // Add more routes here
    }
}
