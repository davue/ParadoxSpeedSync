/*
 * A cross-platform tool to overcome the limitations of the speed controls of Paradox Interactive games.
 * Copyright (C) 2020 David Enderlin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.davue.pss;

import com.github.davue.pss.client.Client;
import com.github.davue.pss.server.Server;
import com.github.davue.pss.ui.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    public static final Logger LOGGER = LoggerFactory.getLogger("UI");

    /**
     * The main stage of the application.
     */
    public static Stage window;

    /**
     * The scene switcher of the application.
     */
    public static SceneSwitcher sceneSwitcher;

    /**
     * The client of the application.
     */
    public static Client client;

    /**
     * The server of the application.
     */
    public static Server server;

    /**
     * The controller of the main scene.
     */
    public static MainController mainController;

    public static void main(String[] args) {
        launch();
    }

    public static void stopBackgroundTasks() {
        client.close();
        server.close();
    }

    /**
     * Closes the client and server and switches back to the connection screen while showing an error message.
     *
     * @param message The message to show.
     */
    public static void showError(String message) {
        stopBackgroundTasks();

        Platform.runLater(() -> {
            mainController.messageBox.setManaged(true);
            mainController.messageBox.setVisible(true);
            mainController.message.setText(message);
            sceneSwitcher.activate("main");
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        // Load different scenes
        Pane setup = FXMLLoader.load(getClass().getResource("/setup.fxml"));
        Pane main = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Pane speed = FXMLLoader.load(getClass().getResource("/speed.fxml"));

        client = new Client();
        server = new Server();

        Scene scene = new Scene(setup);
        sceneSwitcher = new SceneSwitcher(primaryStage, scene);
        sceneSwitcher.addRoot("setup", setup);
        sceneSwitcher.addRoot("main", main);
        sceneSwitcher.addRoot("speed", speed);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Paradox Speed Sync");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        window.close();

        System.exit(0);
    }
}
