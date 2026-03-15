package io.github.bindglam.nationwar.command;

import io.github.bindglam.nationwar.Context;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public interface CommandRegistrar {
    void register(Context context, BukkitCommandManager<CommandSender> commands);
}
