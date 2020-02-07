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

import com.github.davue.pss.Protocol;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Server extends Thread {
    public final Logger LOGGER = LoggerFactory.getLogger("Server");
    // The custom key binds
    public int SPEED_UP_KEY;
    public int SPEED_DOWN_KEY;
    private final KeyListener keyListener;
    /**
     * A list of all connected clients.
     */
    private final List<Connection> connections = new CopyOnWriteArrayList<>();
    /**
     * The speed negotiator.
     */
    private final SpeedNegotiator speedNegotiator;
    /**
     * The port of the server.
     */
    private final int port;
    /**
     * The speed of the host.
     */
    public short hostSpeed = 1;
    /**
     * If the server is running.
     */
    private boolean isRunning = true;
    /**
     * If the server is also the current host, this is true by default.
     */
    private boolean isHost = true;
    /**
     * The client which is the current host, null if the server is host.
     */
    private Connection host = null;
    /**
     * The password of the server, null if no password.
     */
    private String password = "";

    public Server(int port) {
        this.port = port;
        this.speedNegotiator = new SpeedNegotiator(this, connections);
        this.keyListener = new KeyListener(this);

        // Register global key listener
        try {
            // Disable logger of global key listener library
            java.util.logging.Logger libLogger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            libLogger.setLevel(Level.SEVERE);
            libLogger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(keyListener);
        } catch (NativeHookException e) {
            LOGGER.error("Could not register native hook. Exiting.");
            System.exit(1);
        }

        // Initialize key bindings
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Please enter the speed up key.\n" +
                    "This is the key, the server will simulate to speed up in-game.\n" +
                    "Press the key and then ENTER: ");
            scanner.nextLine();
            Thread.sleep(200);
            this.SPEED_UP_KEY = keyListener.getSecondLastKey();
            LOGGER.debug("Registered SPEED_UP to: {}", SPEED_UP_KEY);

            System.out.print("Please enter the speed down key.\n" +
                    "This is the key, the server will simulate to speed down in-game.\n" +
                    "Press the key and then ENTER: ");
            scanner.nextLine();
            Thread.sleep(200);
            this.SPEED_DOWN_KEY = keyListener.getSecondLastKey();
            LOGGER.debug("Registered SPEED_DOWN to: {}", SPEED_DOWN_KEY);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Server started. Waiting for connections on port {}.", port);

        while (isRunning && serverSocket != null) {
            // Wait for connection
            try {
                Socket socket = serverSocket.accept();

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
     * Returns the password of the server.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
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

        // Reset all clients
        // TODO: Do we actually want this?
        for (Connection connection : connections) {
            connection.send(Protocol.MESSAGES.SYNC);
        }
    }

    /**
     * Stops the server and closes all connections.
     */
    public void close() {
        isRunning = false;
    }
}
