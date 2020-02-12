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
import com.github.davue.pss.Protocol;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class Client {
    public final Logger LOGGER = LoggerFactory.getLogger("Client");

    /**
     * The raw hardware key code of the key the client presses to send a speed up to the server.
     */
    public int SPEED_UP_KEY = 0;
    /**
     * The raw hardware key code of the key the client presses to send a speed down to the server.
     */
    public int SPEED_DOWN_KEY = 0;
    /**
     * The raw hardware key code of the key the client presses to request a re-sync from the server.
     */
    public int SYNC_KEY = 0;
    /**
     * The password the client will use to connect.
     */
    public String password = "";

    /**
     * The clients current speed.
     */
    private int currentSpeed = 1;
    /**
     * The host the client will connect to.
     */
    public String hostname = "";
    /**
     * The port the client will connect to.
     */
    public int port = 0;
    /**
     * The name of the client.
     */
    public String name = "CLIENT_NAME";
    /**
     * The connection of the client to the server
     */
    private Connection connection;
    /**
     * The unique ID of the client assigned by the server.
     */
    public int id = 0;

    public void speedUp() {
        if (currentSpeed < Protocol.MAX_SPEED) {
            currentSpeed++;

            connection.send(Protocol.MESSAGES.SPEED(currentSpeed));
        } else {
            connection.send(Protocol.MESSAGES.SPEED(Protocol.MAX_SPEED));
        }
    }

    public void speedDown() {
        if (currentSpeed > Protocol.MIN_SPEED) {
            currentSpeed--;

            connection.send(Protocol.MESSAGES.SPEED(currentSpeed));
        } else {
            connection.send(Protocol.MESSAGES.SPEED(Protocol.MIN_SPEED));
        }
    }

    public void sync() {
        connection.send(Protocol.MESSAGES.SYNC);
    }

    public void start() {
        this.connection = new Connection(this, hostname, port == 0 ? Protocol.DEFAULT_PORT : port);
        connection.start();

        // Wait for connection to be established
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!connection.isAlive())
            return;

        // Client seems to be connected at this point so we can switch to the speed scene
        Main.sceneSwitcher.activate("speed");

        // Register global key listener
        try {
            // Disable logger of global key listener library
            java.util.logging.Logger libLogger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            libLogger.setLevel(Level.SEVERE);
            libLogger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new KeyListener(this));
        } catch (NativeHookException e) {
            LOGGER.error("Could not register native hook. Exiting.");
            System.exit(1);
        }

        // Send initial handshake
        connection.send(Protocol.MESSAGES.HELLO(name));
    }

    public void close() {
        if (connection != null)
            connection.close();
    }
}
