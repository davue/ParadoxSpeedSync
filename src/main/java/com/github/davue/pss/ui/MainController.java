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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
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

    @FXML
    public void connect() {
        System.out.println("Connect was pressed.");
    }

    @FXML
    public void host() {
        System.out.println("Host was pressed.");
    }

    @FXML
    void openSettings() {

    }

    @FXML
    public void close() {
        Platform.exit();
    }
}
