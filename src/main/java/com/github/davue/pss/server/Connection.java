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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread {
    private final Server server;
    private final Socket socket;
    private final MessageHandler messageHandler;
    private PrintWriter out;

    /**
     * The speed the client of this connection wants to run.
     */
    public volatile short clientSpeed = 1;

    /**
     * The current state of the connection to the client.
     */
    public State state = State.INIT;

    public String name;

    public int id;

    public Connection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.messageHandler = new MessageHandler(server, this);
    }

    @Override
    public void run() {
        try {
            if (!socket.isConnected()) {
                server.LOGGER.error("Socket not connected on client start.");
                return;
            }

            if (socket.isClosed()) {
                server.LOGGER.error("Socket closed on client start.");
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            server.LOGGER.debug("New connection from {}. Waiting for handshake.", socket.getInetAddress().getHostAddress());

            // Main loop
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                server.LOGGER.trace("Received msg: {}", line);
                messageHandler.handleMessage(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Remove connection from server connection list on error
            server.connections.remove(this);
            server.getSpeedNegotiator().check();
        }
    }

    /**
     * Closes a connection to a client.
     */
    public void close() {
        // Send a message to the client, informing it of the connection close.
        this.send("CLOSE");

        // Flush output stream before closing socket
        out.flush();

        // Close socket
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.state = State.CLOSED;
    }

    /**
     * Tries to send a message to the client.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    /**
     * Returns whether or not a connection is still alive.
     *
     * @return True if the connection is still alive, false otherwise.
     */
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    /**
     * Returns the server of this connection.
     *
     * @return The server which is handling the connection.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Returns the socket of this connection.
     *
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * An enum to represent the current state of the connection. <br>
     * <br>
     * CONNECTED: A TCP connection was established but no handshake was done yet. <br>
     * WAITING_FOR_PASS: The server is waiting for the client to send the password. <br>
     * READY: The handshake was exchanged and the client is ready to start.
     */
    public enum State {
        CLOSED, INIT, WAITING_FOR_PASS, READY
    }
}
