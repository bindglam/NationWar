package io.github.bindglam.nationwar;

import org.jetbrains.annotations.ApiStatus;

public final class NationWar {
    private static NationWarPlugin plugin;

    private NationWar() {
    }

    public static NationWarPlugin get() {
        return plugin;
    }

    @ApiStatus.Internal
    static void register(NationWarPlugin plugin) {
        NationWar.plugin = plugin;
    }

    @ApiStatus.Internal
    static void unregister() {
        NationWar.plugin = null;
    }
}
