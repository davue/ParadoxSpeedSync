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

import com.github.davue.pss.Main;
import com.github.davue.pss.Protocol;
import com.github.davue.pss.presets.Preset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    public final Logger LOGGER = LoggerFactory.getLogger("Server");

    /**
     * An atomic counter to get unique client id's.
     */
    public final AtomicInteger nextClientID = new AtomicInteger(0);
    /**
     * A list of all connected clients.
     */
    public final List<Connection> connections = new CopyOnWriteArrayList<>();
    /**
     * The speed negotiator.
     */
    private final SpeedNegotiator speedNegotiator;
    /**
     * The port of the server.
     */
    public int port;
    /**
     * The password of the server, empty if no password.
     */
    public String password;

    /**
     * The key code for the in-game speed up key.
     */
    public int SPEED_UP_KEY = -1;
    /**
     * The key code for the in-game speed down key.
     */
    public int SPEED_DOWN_KEY = -1;
    /**
     * The speed of the host.
     */
    public short hostSpeed = 1;
    /**
     * If the server is running.
     */
    public volatile boolean isRunning = false;
    /**
     * The preset the server uses.
     */
    private Preset preset = null;

    public Server() {
        this.speedNegotiator = new SpeedNegotiator(this, connections);
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
    }

    @Override
    public void run() {
        if (port == 0) {
            port = Protocol.DEFAULT_PORT;
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof BindException) {
                Main.showError("Port already in use");
            }
            isRunning = false;
            return;
        }

        LOGGER.info("Server started. Waiting for connections on port {}.", port);
        isRunning = true;

        while (isRunning) {
            // Wait for connection
            try {
                Socket socket = serverSocket.accept();

                if (!isRunning) {
                    socket.close();
                    break;
                }

                // Start new thread for connection handling
                Connection newConnection = new Connection(this, socket);
                connections.add(newConnection);
                newConnection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Gracefully close all connections if server wants to stop
        for (Connection connection : connections) {
            if (connection.isConnected()) {
                connection.close();
            }
        }
    }

    /**
     * Returns the speed negotiator of the server.
     *
     * @return The speed negotiator.
     */
    public SpeedNegotiator getSpeedNegotiator() {
        return speedNegotiator;
    }

    /**
     * Resets the speed of the server and all clients
     */
    public void reset() {
        speedNegotiator.reset();
    }

    /**
     * Stops the server and closes all connections.
     */
    public void close() {
        isRunning = false;

        // Gracefully close all connections if server wants to stop
        for (Connection connection : connections) {
            if (connection.isConnected()) {
                connection.close();
            }
        }

        connections.clear();
    }
}
