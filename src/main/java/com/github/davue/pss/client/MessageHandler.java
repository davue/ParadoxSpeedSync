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

/**
 * Handles all messages received from the server.
 */
public class MessageHandler {
    private final Connection connection;
    private final Client client;

    public MessageHandler(Client client, Connection connection) {
        this.connection = connection;
        this.client = client;
    }

    public void handleMessage(String msg) {
        // Split messages by a single space
        String[] tokens = msg.split(" ");

        switch (tokens[0]) {
            case Protocol.MESSAGES.HELLO:
                if (tokens.length < 2) {
                    client.LOGGER.warn("Server at {} sent handshake without specifying our ID. Protocol mismatch?", connection.getSocket().getInetAddress().getHostAddress());
                    break;
                }

                client.LOGGER.debug("Received handshake from {} with ID: {}. Connection ready.", connection.getSocket().getInetAddress().getHostAddress(), tokens[1]);
                client.LOGGER.info("Connected to {}:{}.", connection.getSocket().getInetAddress().getHostAddress(), connection.getSocket().getPort());
                client.id = Integer.parseInt(tokens[1]);
                connection.state = Connection.State.READY;
                break;
            case Protocol.MESSAGES.PASS:
                client.LOGGER.debug("Received PASS from {}. Password required.", connection.getSocket().getInetAddress().getHostAddress());

                if (client.getPassword().isEmpty()) {
                    client.LOGGER.info("Server requires password but no password was specified. Exiting.");
                    System.exit(1);
                }

                connection.send(Protocol.MESSAGES.PASS(client.getPassword()));
                connection.state = Connection.State.INIT;
                break;
            case Protocol.MESSAGES.DENIED:
                client.LOGGER.debug("Received DENIED from {}. Password required.", connection.getSocket().getInetAddress().getHostAddress());
                System.exit(1);
                connection.state = Connection.State.INIT;
                break;
            case Protocol.MESSAGES.CLOSE:
                client.LOGGER.debug("Server at {} closes connection.", connection.getSocket().getInetAddress().getHostAddress());
                connection.state = Connection.State.DISCONNECTED;
                // TODO: Handle this correctly
                break;
            default:
                client.LOGGER.warn("Received unknown message: {}", msg);
                break;
        }
    }
}
