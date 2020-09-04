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

import java.util.Arrays;

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
                connection.send(Protocol.MESSAGES.SPEED(client.defaultSpeed));
                break;
            case Protocol.MESSAGES.VERSION:
                client.LOGGER.debug("Received VERSION from {}. Version mismatch!", connection.getSocket().getInetAddress().getHostAddress());
                connection.state = Connection.State.INIT;

                short serverVersion = Short.parseShort(tokens[1]);

                if (Protocol.VERSION < serverVersion) {
                    Main.showError("Client outdated");
                } else {
                    Main.showError("Server outdated");
                }

                break;
            case Protocol.MESSAGES.PASS:
                client.LOGGER.debug("Received PASS from {}. Password required.", connection.getSocket().getInetAddress().getHostAddress());

                if (client.password.isEmpty()) {
                    Main.showError("Password required");
                } else {
                    connection.send(Protocol.MESSAGES.PASS(client.password));
                }

                connection.state = Connection.State.INIT;
                break;
            case Protocol.MESSAGES.DENIED:
                client.LOGGER.debug("Received DENIED from {}. Incorrect password.", connection.getSocket().getInetAddress().getHostAddress());
                connection.state = Connection.State.INIT;
                Main.showError("Wrong password");
                break;
            case Protocol.MESSAGES.PRESET:
                if (tokens.length < 3) {
                    client.LOGGER.warn("Received invalid PRESET message.");
                    break;
                }

                client.maxSpeed = Short.parseShort(tokens[1]);
                client.defaultSpeed = Short.parseShort(tokens[2]);
                client.currentSpeed = client.defaultSpeed;

                client.LOGGER.debug("Received PRESET ({},{}) from {}.", client.maxSpeed, client.defaultSpeed, connection.getSocket().getInetAddress().getHostAddress());

                ClientManager.applyPreset();
                break;
            case Protocol.MESSAGES.UPDATE:
                if (tokens.length < 4) {
                    client.LOGGER.warn("Received invalid UPDATE message.");
                    break;
                }

                int id = Integer.parseInt(tokens[1]);
                int speed = Integer.parseInt(tokens[2]);
                String name = String.join(" ", Arrays.copyOfRange(tokens, 3, tokens.length));

                client.LOGGER.debug("Received UPDATE for client {} ({}) with speed {}.", id, name, speed);
                ClientManager.update(id, name, speed);
                break;
            case Protocol.MESSAGES.CLOSE:
                if (tokens.length == 1) {
                    client.LOGGER.debug("Server at {} closes connection.", connection.getSocket().getInetAddress().getHostAddress());
                    connection.state = Connection.State.DISCONNECTED;
                    Main.showError("Connection closed");
                } else {
                    client.LOGGER.debug("Client #{} disconnected from the server.", tokens[1]);
                    ClientManager.remove(Integer.parseInt(tokens[1]));
                }

                break;
            default:
                client.LOGGER.warn("Received unknown message: {}", msg);
                break;
        }
    }
}
