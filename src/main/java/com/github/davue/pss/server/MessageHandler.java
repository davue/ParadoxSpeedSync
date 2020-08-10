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

/**
 * Handles all messages received from a client.
 */
public class MessageHandler {
    private final Server server;
    private final Connection connection;

    public MessageHandler(Server server, Connection connection) {
        this.server = server;
        this.connection = connection;
    }

    public void handleMessage(String msg) {
        // Split messages by a single space
        String[] tokens = msg.split(" ");

        // Do not handle messages if the server declared the client as closed.
        if (connection.state == Connection.State.CLOSED)
            return;

        switch (tokens[0]) {
            case Protocol.MESSAGES.HELLO:
                // Ignore HELLO if the client is already connected
                if (connection.state != Connection.State.INIT) {
                    server.LOGGER.warn("Received HELLO after handshake from {}", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                if (tokens.length < 3) {
                    server.LOGGER.warn("Client at {} sent invalid HELLO message.", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                // If there is a protocol mismatch
                if (Protocol.VERSION != Short.parseShort(tokens[1])) {
                    connection.send(Protocol.MESSAGES.VERSION());
                    break;
                }

                if (server.password.isEmpty()) {
                    server.LOGGER.debug("Handshake from {}. Accept.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.name = tokens[2];
                    connection.id = server.nextClientID.getAndIncrement();
                    connection.send(Protocol.MESSAGES.HELLO(connection.id));
                    connection.state = Connection.State.READY;

                    server.LOGGER.info("{}#{} connected to the server.", connection.name, connection.id);
                    connection.send(Protocol.MESSAGES.PRESET(server.getPreset()));

                    // Send other client informations
                    for (Connection connection : server.connections) {
                        if (!this.connection.equals(connection)) {
                            this.connection.send(Protocol.MESSAGES.UPDATE(connection.id, connection.clientSpeed, connection.name));
                        }
                    }
                } else {
                    server.LOGGER.debug("Handshake from {}. Waiting for password.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.name = tokens[2];
                    connection.send(Protocol.MESSAGES.PASS);
                    connection.state = Connection.State.WAITING_FOR_PASS;
                }
                break;
            case Protocol.MESSAGES.PASS:
                // Ignore message if the server is not waiting for a password from the client
                if (connection.state != Connection.State.WAITING_FOR_PASS) {
                    server.LOGGER.warn("Received PASS from {} without expecting it.", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                if (tokens.length < 2) {
                    server.LOGGER.warn("Client at {} sent PASS without arguments.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.send(Protocol.MESSAGES.DENIED);
                    break;
                }

                if (server.password.equals(tokens[1])) {
                    server.LOGGER.debug("Received correct PASS from {}. Accept.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.id = server.nextClientID.getAndIncrement();
                    connection.send(Protocol.MESSAGES.HELLO(connection.id));
                    connection.state = Connection.State.READY;

                    server.LOGGER.info("{}#{} connected to the server.", connection.name, connection.id);
                    connection.send(Protocol.MESSAGES.PRESET(server.getPreset()));

                    // Send other client informations
                    for (Connection connection : server.connections) {
                        if (!this.connection.equals(connection)) {
                            this.connection.send(Protocol.MESSAGES.UPDATE(connection.id, connection.clientSpeed, connection.name));
                        }
                    }
                } else {
                    server.LOGGER.debug("Received incorrect PASS from {}. Sending DENIED.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.send(Protocol.MESSAGES.DENIED);
                }
                break;
            case Protocol.MESSAGES.SPEED:
                if (connection.state != Connection.State.READY) {
                    server.LOGGER.warn("Client at {} wanted to set speed to {} without handshake.", connection.getSocket().getInetAddress().getHostAddress(), tokens[1]);
                    break;
                }

                if (tokens.length < 2) {
                    server.LOGGER.warn("Client at {} sent SPEED without arguments.", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                connection.clientSpeed = Short.parseShort(tokens[1]);

                server.getSpeedNegotiator().check();

                // Send updates to all clients but the sender of the update
                for (Connection connection : server.connections) {
                    if (!this.connection.equals(connection)) {
                        connection.send(Protocol.MESSAGES.UPDATE(this.connection.id, this.connection.clientSpeed, this.connection.name));
                    }
                }

                break;
            case Protocol.MESSAGES.SYNC:
                if (connection.state != Connection.State.READY) {
                    server.LOGGER.warn("Client at {} wanted to sync without handshake.", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                server.LOGGER.debug("Resetting speed.");
                server.reset();
                break;
            default:
                server.LOGGER.warn("Received unknown message: {}", msg);
                break;
        }
    }
}
