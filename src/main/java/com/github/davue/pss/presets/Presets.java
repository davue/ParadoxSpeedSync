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

package com.github.davue.pss.presets;

import com.github.davue.pss.presets.impl.Custom;
import com.github.davue.pss.presets.impl.Default;
import com.github.davue.pss.presets.impl.Stellaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Presets {
    private static final Map<String, Preset> nameMap = new HashMap<>();
    private static final Map<String, Preset> idMap = new HashMap<>();

    static {
        Default defaultPreset = new Default();
        idMap.put(defaultPreset.getID(), defaultPreset);
        nameMap.put(defaultPreset.getName(), defaultPreset);

        Stellaris stellarisPreset = new Stellaris();
        idMap.put(stellarisPreset.getID(), stellarisPreset);
        nameMap.put(stellarisPreset.getName(), stellarisPreset);

        Custom customPreset = new Custom();
        idMap.put(customPreset.getID(), customPreset);
        nameMap.put(customPreset.getName(), customPreset);
    }

    public static Preset getPresetByID(String name) {
        return idMap.get(name);
    }

    public static Preset getPresetByName(String name) {
        return nameMap.get(name);
    }

    public static List<Preset> getPresets() {
        return new ArrayList<>(idMap.values());
    }
}
