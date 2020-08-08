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
import com.github.davue.pss.presets.Preset;
import com.github.davue.pss.presets.Presets;
import com.github.davue.pss.presets.impl.Custom;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class SetupController {
    @FXML
    public VBox root;
    @FXML
    public Button clientSpeedUp;
    @FXML
    public Button clientSpeedDown;
    @FXML
    public Button clientSync;
    @FXML
    public Button serverSpeedUp;
    @FXML
    public Button serverSpeedDown;
    @FXML
    public ChoiceBox<String> choiceBox;
    @FXML
    public TextField defaultSpeedBox;
    @FXML
    public TextField maxSpeedBox;
    @FXML
    public HBox customSettings;

    @FXML
    public void initialize() {
        Platform.runLater(() -> root.requestFocus());

        choiceBox.setItems(FXCollections.observableArrayList(Presets.getPresets().stream().map(Preset::getName).collect(Collectors.toList())));
        choiceBox.setValue(Presets.getPresetByID("DEFAULT").getName());
        Main.server.setPreset(Presets.getPresetByID("DEFAULT"));
        customSettings.setDisable(true);

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            Preset preset = Presets.getPresetByName(newValue);
            Main.server.setPreset(preset);
            defaultSpeedBox.setText(Short.toString(preset.getDefaultSpeed()));
            maxSpeedBox.setText(Short.toString(preset.getMaxSpeed()));

            customSettings.setDisable(!newValue.equals("Custom"));
        });

        if (Main.client.SPEED_UP_KEY == 0) {
            clientSpeedUp.setText("Nothing");
        } else {
            clientSpeedUp.setText(java.awt.event.KeyEvent.getKeyText(Main.client.SPEED_UP_KEY));
        }

        if (Main.client.SPEED_DOWN_KEY == 0) {
            clientSpeedDown.setText("Nothing");
        } else {
            clientSpeedDown.setText(java.awt.event.KeyEvent.getKeyText(Main.client.SPEED_DOWN_KEY));
        }

        if (Main.server.SPEED_UP_KEY == 0) {
            serverSpeedUp.setText("Nothing");
        } else {
            serverSpeedUp.setText(java.awt.event.KeyEvent.getKeyText(Main.server.SPEED_UP_KEY));
        }

        if (Main.server.SPEED_DOWN_KEY == 0) {
            serverSpeedDown.setText("Nothing");
        } else {
            serverSpeedDown.setText(java.awt.event.KeyEvent.getKeyText(Main.server.SPEED_DOWN_KEY));
        }
    }

    @FXML
    public void start(ActionEvent actionEvent) {
        ((Custom) Presets.getPresetByID("CUSTOM")).setDefaultSpeed(Short.parseShort(defaultSpeedBox.getText()));
        ((Custom) Presets.getPresetByID("CUSTOM")).setMaxSpeed(Short.parseShort(maxSpeedBox.getText()));
        Main.sceneManager.back();
    }

    @FXML
    public void close(ActionEvent actionEvent) {
        Main.stopBackgroundTasks();
        Platform.exit();
    }

    @FXML
    public void handleKeyBind(KeyEvent keyEvent) {
        Button sourceButton = ((Button) keyEvent.getSource());

        int code = keyEvent.getCode().getCode();

        if (sourceButton == clientSpeedUp) {
            Main.client.SPEED_UP_KEY = code;
        } else if (sourceButton == clientSpeedDown) {
            Main.client.SPEED_DOWN_KEY = code;
        } else if (sourceButton == clientSync) {
            Main.client.SYNC_KEY = code;
        } else if (sourceButton == serverSpeedUp) {
            Main.server.SPEED_UP_KEY = code;
        } else if (sourceButton == serverSpeedDown) {
            Main.server.SPEED_DOWN_KEY = code;
        }

        if (keyEvent.getCode().getCode() == 0) {
            sourceButton.setText("Nothing");
        } else {
            sourceButton.setText(java.awt.event.KeyEvent.getKeyText(keyEvent.getCode().getCode()));
        }

        root.requestFocus();
    }
}
