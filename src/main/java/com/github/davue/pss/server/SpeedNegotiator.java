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

package com.github.davue.pss.server;

import java.awt.*;
import java.util.List;

public class SpeedNegotiator {
    private final Server server;
    private final List<Connection> clients;
    private Robot robot = null;

    public SpeedNegotiator(Server server, List<Connection> clients) {
        this.server = server;
        this.clients = clients;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks for a new speed.
     */
    public void check() {
        if (clients.isEmpty())
            return;

        if (!WindowFocusListener.isGameFocused())
            return;

        boolean doNothing = false;
        if (server.SPEED_UP_KEY == 0) {
            server.LOGGER.warn("No ingame speed up key set!");
            doNothing = true;
        }

        if (server.SPEED_DOWN_KEY == 0) {
            server.LOGGER.warn("No ingame speed down key set!");
            doNothing = true;
        }

        if (doNothing)
            return;

        // Reset speed to auto-sync
        for (int i = 0; i < server.getPreset().getMaxSpeed(); i++) {
            robot.keyPress(server.SPEED_DOWN_KEY);
            robot.keyRelease(server.SPEED_DOWN_KEY);
        }

        server.hostSpeed = 1;

        short slowestClient = Short.MAX_VALUE;
        for (Connection client : clients) {
            if (client.clientSpeed < slowestClient) {
                slowestClient = client.clientSpeed;
            }
        }

        while (server.hostSpeed != slowestClient) {
            if (server.hostSpeed > slowestClient) {
                robot.keyPress(server.SPEED_DOWN_KEY);
                robot.keyRelease(server.SPEED_DOWN_KEY);
                server.hostSpeed--;
            } else {
                robot.keyPress(server.SPEED_UP_KEY);
                robot.keyRelease(server.SPEED_UP_KEY);
                server.hostSpeed++;
            }
        }

        server.LOGGER.info("Host is now running at speed: {}", server.hostSpeed);
    }

    /**
     * Resets the speed to one by pressing five times the speed down key.
     */
    public void reset() {
        for (int i = 0; i < 5; i++) {
            robot.keyPress(server.SPEED_DOWN_KEY);
            robot.keyRelease(server.SPEED_DOWN_KEY);
        }

        server.hostSpeed = 1;

        check();
    }
}
