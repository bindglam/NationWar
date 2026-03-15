package io.github.bindglam.nationwar.guis;

import io.github.bindglam.nationwar.utils.ComponentUtil;
import io.github.bindglam.nationwar.utils.InventoryUtil;
import io.github.bindglam.nationwar.utils.ItemBuilder;
import io.github.bindglam.nationwar.utils.constants.ItemStackConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainMenuGui extends InventoryGui {
    private static final int MY_PROFILE_SLOT = InventoryUtil.toSlotIndex(1, 1);
    private static final int TELEPORT_SLOT = InventoryUtil.toSlotIndex(3, 1);

    public MainMenuGui(JavaPlugin plugin, Player player) {
        super(plugin, 9*3, Component.text("MAIN MENU"));

        canvas.fill(ItemStackConstants.FILLED);

        canvas.pixel(MY_PROFILE_SLOT, ItemBuilder.of(InventoryUtil.createPlayerHead(player))
                .displayName(ComponentUtil.componentNonItalic("내 프로필").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .build());
        canvas.pixel(TELEPORT_SLOT, ItemBuilder.of(Material.CAMPFIRE)
                .displayName(ComponentUtil.componentNonItalic("특정 구역으로 텔레포트").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .build());
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();

        event.setCancelled(true);

        if(clickedSlot == MY_PROFILE_SLOT) {
            // TODO
        } else if(clickedSlot == TELEPORT_SLOT) {
            // TODO
        }
    }
}
