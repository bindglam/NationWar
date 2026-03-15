package io.github.bindglam.nationwar.utils.constants;

import io.github.bindglam.nationwar.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemStackConstants {
    public static final ItemStack FILLED = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).displayName(Component.empty()).build();

    private ItemStackConstants() {
    }
}
