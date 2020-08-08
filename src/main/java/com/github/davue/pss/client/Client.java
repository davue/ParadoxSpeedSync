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
import com.github.davue.pss.ui.SpeedController;
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
     * The unique ID of the client assigned by the server.
     */
    public int id = 0;
    /**
     * The speed controller of the client itself.
     */
    public SpeedController speedController;
    /**
     * If the key listener is registered.
     * This is used to prevent double registration.
     */
    public boolean isKeyListenerRegistered = false;
    /**
     * The client maximum speed. This should be communicated with the server.
     */
    public short maxSpeed = 5;
    /**
     * The default speed. This should be communicated with the server.
     */
    public short defaultSpeed = 1;
    public boolean abnormalDisconnect = true;

    /**
     * The connection of the client to the server
     */
    private Connection connection;
    /**
     * The clients current speed.
     */
    public short currentSpeed = 1;

    public void speedUp() {
        if (currentSpeed < maxSpeed) {
            currentSpeed++;
            ClientManager.redraw();

            connection.send(Protocol.MESSAGES.SPEED(currentSpeed));
        } else {
            connection.send(Protocol.MESSAGES.SPEED(maxSpeed));
        }
    }

    public void speedDown() {
        if (currentSpeed > Protocol.MIN_SPEED) {
            currentSpeed--;
            ClientManager.redraw();

            connection.send(Protocol.MESSAGES.SPEED(currentSpeed));
        } else {
            connection.send(Protocol.MESSAGES.SPEED(Protocol.MIN_SPEED));
        }
    }

    public void sync() {
        connection.send(Protocol.MESSAGES.SYNC);
    }

    public void start() {
        currentSpeed = 1;
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

        if (!connection.isConnected())
            return;

        // Client seems to be connected at this point so we can switch to the speed scene
        Main.sceneManager.activate("speed");

        // Register global key listener
        try {
            if (!isKeyListenerRegistered) {
                // Disable logger of global key listener library
                java.util.logging.Logger libLogger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
                libLogger.setLevel(Level.SEVERE);
                libLogger.setUseParentHandlers(false);

                GlobalScreen.addNativeKeyListener(new KeyListener(this));
                isKeyListenerRegistered = true;
            }

            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            LOGGER.error("Could not register native hook. Exiting.");
            System.exit(1);
        }

        // Send initial handshake
        connection.send(Protocol.MESSAGES.HELLO(name));
        //connection.send("HELLO 0 " + name);
    }

    public void close() {
        if (connection != null)
            connection.close();
    }
}
