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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread {
    private final String hostname;
    private final int port;

    private final MessageHandler messageHandler;
    private final Client client;
    private Socket socket;
    private PrintWriter out;

    public State state = State.INIT;

    public Connection(Client client, String hostname, int port) {
        this.client = client;
        this.hostname = hostname;
        this.port = port;
        this.messageHandler = new MessageHandler(client, this);
    }

    @Override
    public void run() {
        client.LOGGER.info("Connecting to {}:{}.", hostname, port);

        try {
            socket = new Socket(hostname, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Notify client that connection was established
            synchronized (client) {
                client.notify();
            }

            // Main loop
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                client.LOGGER.trace("Received msg: {}", line);
                messageHandler.handleMessage(line);
            }
        } catch (IOException e) {
            if (client.abnormalDisconnect) {
                e.printStackTrace();
                if (e.getMessage().endsWith(": connect")) {
                    Main.showError(e.getMessage().replace(": connect", ""));
                } else {
                    Main.showError("Connection closed");
                }
            }
        } finally {
            synchronized (client) {
                client.notify();
            }
        }
    }

    /**
     * Returns the socket of the client connection.
     *
     * @return The socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Tries to send a message to the server.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public void close() {
        if (socket == null)
            return;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public enum State {
        DISCONNECTED, INIT, READY
    }
}
