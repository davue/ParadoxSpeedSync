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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SpeedController {
    @FXML
    private HBox speedBox;
    @FXML
    private Label name;
    private int speed = 1;

    public HBox getSpeedBox() {
        return speedBox;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setName(String name) {
        Platform.runLater(() -> this.name.setText(name));
    }

    public void setGreen(int speed, boolean clear) {
        Platform.runLater(() -> {
            ObservableList<Node> children = this.speedBox.getChildren();

            for (int i = 0; i < children.size(); i++) {
                if (i + 1 == speed) {
                    children.get(i).getStyleClass().clear();
                    children.get(i).getStyleClass().add("rectangle-green");
                } else if (clear) {
                    children.get(i).getStyleClass().clear();
                    children.get(i).getStyleClass().add("rectangle");
                }
            }
        });
    }

    public void setGreen(int speed) {
        setGreen(speed, true);
    }

    public void setRed(int speed, boolean clear) {
        Platform.runLater(() -> {
            ObservableList<Node> children = this.speedBox.getChildren();

            for (int i = 0; i < children.size(); i++) {
                if (i + 1 == speed) {
                    children.get(i).getStyleClass().clear();
                    children.get(i).getStyleClass().add("rectangle-red");
                } else if (clear) {
                    children.get(i).getStyleClass().clear();
                    children.get(i).getStyleClass().add("rectangle");
                }
            }
        });
    }

    public void setRed(int speed) {
        setRed(speed, true);
    }

    @FXML
    private void initialize() {
        setGreen(Main.client.defaultSpeed);
    }

    @FXML
    public void openSettings(ActionEvent actionEvent) {
        Main.sceneManager.activate("setup");
    }

    @FXML
    public void close() {
        Main.stopBackgroundTasks();
        Platform.exit();
    }
}
