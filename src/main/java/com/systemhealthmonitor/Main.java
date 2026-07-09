package com.systemhealthmonitor;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting System Health Monitor");
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Initializing primary stage");
            // db start
            DatabaseManager.getInstance().initialize();

            primaryStage.setTitle("System Health Monitor");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);

            primaryStage.show();

            logger.info("App launched successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException("App starup failed", e);
        }
    }

        @Override
        public void stop() {
            logger.info("APP shutting down");
            DatabaseManager.getInstance().close();


        }


    }


