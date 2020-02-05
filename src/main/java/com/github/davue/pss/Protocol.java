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

package com.github.davue.pss;

public class Protocol {
    public static final short MIN_SPEED = 1;
    public static final short MAX_SPEED = 5;

    public static class MESSAGES {
        /**
         * The HELLO message used to do a handshake between client and server.
         */
        public static final String HELLO = "HELLO";

        /**
         * The PASS message used to either notify the client to send a password or send a password to the server.
         */
        public static final String PASS = "PASS";

        /**
         * Constructs a PASS message with the given password.
         *
         * @param password The password to send.
         * @return The constructed message ready to be sent.
         */
        public static String PASS(String password) {
            return PASS + " " + password;
        }

        /**
         * The DENIED message which a client receives after sending an incorrect password.
         */
        public static final String DENIED = "DENIED";

        /**
         * The CLOSE message a client receives if the is closing.
         */
        public static final String CLOSE = "CLOSE";

        /**
         * The SPEED message sent to the server to tell him the current client speed.
         */
        public static final String SPEED = "SPEED";

        /**
         * Constructs a SPEED message with the given speed.
         *
         * @param speed The speed to send.
         * @return The constructed message ready to be sent.
         */
        public static String SPEED(int speed) {
            return SPEED + " " + speed;
        }

        /**
         * The RESET message sent to all clients on server reset.
         */
        public static final String SYNC = "SYNC";
    }
}
