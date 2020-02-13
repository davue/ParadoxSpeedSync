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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

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
    public void initialize() {
        Platform.runLater(() -> root.requestFocus());
    }

    @FXML
    public void start(ActionEvent actionEvent) {
        Main.sceneSwitcher.back();
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

        sourceButton.setText(Integer.toString(code));
        root.requestFocus();
    }
}
