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

public class Stellaris implements Preset {
    @Override
    public String getName() {
        return "Stellaris";
    }

    @Override
    public String getID() {
        return "STELLARIS";
    }

    @Override
    public short getMaxSpeed() {
        return 5;
    }

    @Override
    public short getDefaultSpeed() {
        return 3;
    }
}
