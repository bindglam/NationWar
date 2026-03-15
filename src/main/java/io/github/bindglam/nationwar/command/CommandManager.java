package io.github.bindglam.nationwar.command;

import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.Managerial;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.List;

public final class CommandManager implements Managerial {
    private final List<CommandRegistrar> commands = List.of(new CoreCommand(), new NationCommand());

    @Override
    public void start(Context context) {
        var manager = new LegacyPaperCommandManager<>(
                context.plugin(),
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        commands.forEach(it -> it.register(context, manager));
    }

    @Override
    public void end(Context context) {
    }
}
