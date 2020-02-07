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

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener {
    private final Server server;
    private int lastKey = -1;
    private int secondLastKey = -1;

    public KeyListener(Server server) {
        this.server = server;
    }

    public int getSecondLastKey() {
        return secondLastKey;
    }

    public int getLastKey() {
        return lastKey;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        server.LOGGER.trace("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        server.LOGGER.trace("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        secondLastKey = lastKey;
        lastKey = e.getRawCode();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // TODO: Find out when this triggers
        server.LOGGER.trace("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }
}
