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

package com.github.davue.pss.ui;

import com.github.davue.pss.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    public VBox root;

    @FXML
    public TextField addressField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nameField;

    @FXML
    public Button settingsButton;
    @FXML
    public Button closeButton;

    @FXML
    public Button connectButton;
    @FXML
    public Button hostButton;

    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    public HBox messageBox;
    @FXML
    public Pane dragPane;
    public Label message;

    @FXML
    public void initialize() {
        Platform.runLater(() -> root.requestFocus());

        messageBox.setVisible(false);
        messageBox.setManaged(false);
        Main.mainController = this;
    }

    @FXML
    public void connect() {
        if (addressField.getText().strip().isEmpty()) {
            Main.showError("Please enter an address");
            return;
        }

        if (nameField.getText().strip().isEmpty()) {
            Main.showError("Please enter a name");
            return;
        }

        Main.client.hostname = addressField.getText();
        Main.client.password = passwordField.getText().strip();
        Main.client.name = nameField.getText().strip();

        Main.client.start();
    }

    @FXML
    public void host() {
        try {
            Main.server.port = Integer.parseInt(addressField.getText());
        } catch (NumberFormatException e) {
            if (!addressField.getText().strip().isEmpty()) {
                Main.showError("Invalid port");
                return;
            }
        }

        if (nameField.getText().strip().isEmpty()) {
            Main.showError("Please enter a name");
            return;
        }

        Main.server.password = passwordField.getText().strip();

        new Thread(Main.server).start();

        long start = System.currentTimeMillis();
        while (!Main.server.isRunning) {
            if (start + 5000 < System.currentTimeMillis()) {
                Main.showError("Failed to start server");
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Main.client.hostname = "127.0.0.1";
        Main.client.port = Main.server.port;
        Main.client.password = passwordField.getText().strip();
        Main.client.name = nameField.getText().strip();

        Main.client.start();
    }

    @FXML
    void openSettings() {
        Main.sceneSwitcher.activate("setup");
    }

    @FXML
    public void close() {
        Main.stopBackgroundTasks();
        Platform.exit();
    }
}
