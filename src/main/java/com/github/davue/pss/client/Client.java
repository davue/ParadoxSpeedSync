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

import com.github.davue.pss.Protocol;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.logging.Level;

public class Client extends Thread {
    public final Logger LOGGER = LoggerFactory.getLogger("Client");

    /**
     * The connection of the client to the server
     */
    private final Connection connection;

    /**
     * The raw hardware key code of the key the client presses to send a speed up to the server.
     */
    public int SPEED_UP_KEY;

    /**
     * The raw hardware key code of the key the client presses to send a speed down to the server.
     */
    public int SPEED_DOWN_KEY;

    /**
     * The raw hardware key code of the key the client presses to request a re-sync from the server.
     */
    public int SYNC_KEY;

    /**
     * The clients current speed.
     */
    private int currentSpeed = 1;

    /**
     * The password the client will use to connect.
     */
    private final String password;

    /**
     * The name of the client.
     */
    private String name = "CLIENT_NAME";
    /**
     * The unique ID of the client assigned by the server.
     */
    public int id = 0;

    public Client(String hostname, int port) {
        this(hostname, port, "");
    }

    public Client(String hostname, int port, String password) {
        this.password = password;
        this.connection = new Connection(this, hostname, port);
        KeyListener keyListener = new KeyListener(this);

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
        // Note that we need to wait 200ms after every keypress to give the KeyListener enough time to run
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Please enter the speed up key.\n" +
                    "This is the key you press to suggest a speed up in-game.\n" +
                    "Press the key and then ENTER: ");
            scanner.nextLine();
            Thread.sleep(200);
            this.SPEED_UP_KEY = keyListener.getSecondLastKey();
            LOGGER.debug("Registered SPEED_UP to: {}", SPEED_UP_KEY);

            System.out.print("Please enter the speed down key.\n" +
                    "This is the key you press to suggest a speed down in-game.\n" +
                    "Press the key and then ENTER: ");
            scanner.nextLine();
            Thread.sleep(200);
            this.SPEED_DOWN_KEY = keyListener.getSecondLastKey();
            LOGGER.debug("Registered SPEED_DOWN to: {}", SPEED_DOWN_KEY);

            System.out.print("Please enter the speed sync key.\n" +
                    "This is the key you press to re-sync the speed with the actual in-game speed.\n" +
                    "Press the key and then ENTER: ");
            scanner.nextLine();
            Thread.sleep(200);
            this.SYNC_KEY = keyListener.getSecondLastKey();
            LOGGER.debug("Registered SYNC to: {}", SYNC_KEY);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

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

    @Override
    public void run() {
        connection.start();

        // Wait for connection to be established
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Send initial handshake
        connection.send(Protocol.MESSAGES.HELLO(name));
    }

    public String getPassword() {
        return password;
    }
}
