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

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.HashMap;

public class SceneManager {
    private final HashMap<String, Pane> rootMap = new HashMap<>();
    private final HashMap<Integer, Pane> clientPanes = new HashMap<>();
    private final Stage stage;
    private final Scene main;
    private Parent lastPane;

    public SceneManager(Stage stage, Scene main) {
        this.stage = stage;
        this.main = main;
    }

    public void addClientPane(int id, Pane pane) {
        clientPanes.put(id, pane);

        if (!rootMap.containsKey("speed")) {
            Main.LOGGER.error("Could not find speed root pane.");
            Platform.exit();
        }

        Platform.runLater(() -> {
            rootMap.get("speed").getChildren().add(pane);

            if (main.getRoot().equals(rootMap.get("speed")))
                stage.sizeToScene();
        });
    }

    public void removeClientPane(int id) {
        Pane removed = clientPanes.remove(id);

        if (!rootMap.containsKey("speed")) {
            Main.LOGGER.error("Could not find speed root pane.");
            Platform.exit();
        }

        Platform.runLater(() -> {
            rootMap.get("speed").getChildren().remove(removed);

            if (main.getRoot().equals(rootMap.get("speed")))
                stage.sizeToScene();
        });
    }

    public void addRoot(String name, Pane pane) {
        rootMap.put(name, pane);
    }

    public void back() {
        if (lastPane == null)
            lastPane = rootMap.get("main");

        main.setRoot(lastPane);
        stage.sizeToScene();
        lastPane.requestFocus();

        try {
            // Only re-register native hook if the client is connected
            if (!Main.client.hostname.equals("")) {
                GlobalScreen.registerNativeHook();
            }
        } catch (NativeHookException e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public void activate(String name) {
        if (name.equals("setup")) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
                Platform.exit();
            }
        }

        Platform.runLater(() -> {
            lastPane = main.getRoot();

            main.setRoot(rootMap.get(name));
            stage.sizeToScene();
            rootMap.get(name).requestFocus();
        });
    }
}