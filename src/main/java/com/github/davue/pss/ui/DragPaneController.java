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
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DragPaneController {
    @FXML
    public Pane dragPane;

    private double xStart = 0;
    private double yStart = 0;
    private double xWindowStart = 0;
    private double yWindowStart = 0;

    @FXML
    public void dragPanePress(MouseEvent event) {
        xStart = event.getScreenX();
        xWindowStart = Main.window.getX();
        yStart = event.getScreenY();
        yWindowStart = Main.window.getY();
    }

    @FXML
    public void dragPaneDrag(MouseEvent event) {
        Main.window.setX(xWindowStart + (event.getScreenX() - xStart));
        Main.window.setY(yWindowStart + (event.getScreenY() - yStart));
    }
}
