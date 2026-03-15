package io.github.bindglam.nationwar.listeners;

import io.github.bindglam.nationwar.NationWarPlugin;
import io.github.bindglam.nationwar.guis.MainMenuGui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public final class MainMenuGuiListener implements Listener {
    private final NationWarPlugin plugin;

    public MainMenuGuiListener(NationWarPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if(player.isSneaking()) {
            event.setCancelled(true);

            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
            player.openInventory(new MainMenuGui(plugin, player).getInventory());
        }
    }
}
