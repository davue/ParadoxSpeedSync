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

package com.github.davue.pss.presets.impl;

import com.github.davue.pss.presets.Preset;

public class Custom implements Preset {
    short maxSpeed = 5;
    short defaultSpeed = 1;

    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public String getID() {
        return "CUSTOM";
    }

    @Override
    public short getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(short maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public short getDefaultSpeed() {
        return defaultSpeed;
    }

    public void setDefaultSpeed(short defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }
}
