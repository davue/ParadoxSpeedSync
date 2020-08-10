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

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

public class WindowFocusListener {
    private static final User32 user32 = User32.INSTANCE;
    private static final Kernel32 kernel32 = Kernel32.INSTANCE;
    private static final Psapi psapi = Psapi.INSTANCE;

    private static final String[] validGames = {"ck2game", "stellaris", "hoi4", "eu4"};

    public static boolean isGameFocused() {
        if (Platform.isWindows()) {
            String processPath = getFocusedProcess();
            String[] split = processPath.split("\\\\");
            String executableName = split[split.length - 1].toLowerCase();
            for (String game : validGames) {
                String validName = game + ".exe";
                if (executableName.substring(0, validName.length()).equals(validName))
                    return true;
            }

            return false;
        } else
            return true;
    }

    private static String getFocusedProcess() {
        int PROCESS_QUERY_INFORMATION = 0x0400;
        WinDef.HWND windowHandle = user32.GetForegroundWindow();
        IntByReference pid = new IntByReference();
        user32.GetWindowThreadProcessId(windowHandle, pid);
        WinNT.HANDLE processHandle = kernel32.OpenProcess(PROCESS_QUERY_INFORMATION, true, pid.getValue());

        byte[] filename = new byte[512];
        Psapi.INSTANCE.GetModuleFileNameExA(processHandle, null, filename, filename.length);
        return new String(filename);
    }
}
