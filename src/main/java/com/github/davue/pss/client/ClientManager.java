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

package com.github.davue.pss.client;

import com.github.davue.pss.Main;
import com.github.davue.pss.ui.SpeedController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the information about all other clients received from the server.
 */
public class ClientManager {
    private static HashMap<Integer, SpeedController> clients = new HashMap<>();

    public static void remove(int id) {
        clients.remove(id);
        Main.sceneManager.removeClientPane(id);
    }

    public static void redraw() {
        int ownSpeed = Main.client.getSpeed();
        int slowestSpeed = ownSpeed;
        for (Map.Entry<Integer, SpeedController> client : clients.entrySet()) {
            SpeedController controller = client.getValue();
            int clientSpeed = controller.getSpeed();
            if (clientSpeed < ownSpeed) {
                controller.setRed(clientSpeed);
            } else {
                controller.setGreen(clientSpeed);
            }

            if (clientSpeed < slowestSpeed)
                slowestSpeed = clientSpeed;
        }

        Main.client.speedController.setGreen(ownSpeed);
        if (slowestSpeed < ownSpeed) {
            Main.client.speedController.setRed(slowestSpeed, false);
        }
    }

    public static void update(int id, String name, int speed) {
        // If it's a new client
        if (!clients.containsKey(id)) {
            try {
                Pane speedPane = FXMLLoader.load(ClientManager.class.getResource("/speed_sub.fxml"));
                Main.sceneManager.addClientPane(id, speedPane);

                SpeedController controller = (SpeedController) speedPane.getUserData();
                controller.setName(name);

                clients.put(id, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        clients.get(id).setSpeed(speed);

        redraw();
    }
}
