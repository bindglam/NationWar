package io.github.bindglam.nationwar.guis;

import io.github.bindglam.nationwar.NationWarPlugin;
import io.github.bindglam.nationwar.guis.drawing.ContainerItemGetter;
import io.github.bindglam.nationwar.utils.ComponentUtil;
import io.github.bindglam.nationwar.utils.InventoryUtil;
import io.github.bindglam.nationwar.utils.ItemBuilder;
import io.github.bindglam.nationwar.utils.constants.ItemStackConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class TeleportGui extends InventoryGui {
    private static final int PREVIOUS_PAGE_SLOT = InventoryUtil.toSlotIndex(4, 4);
    private static final int NEXT_PAGE_SLOT = InventoryUtil.toSlotIndex(7, 4);
    private static final int BACK_SLOT = InventoryUtil.toSlotIndex(8, 5);

    private final NationWarPlugin plugin;
    private final Player player;

    private int page = 0;

    public TeleportGui(NationWarPlugin plugin, Player player) {
        super(plugin, 9*6, Component.text("TELEPORT"), 10);
        this.plugin = plugin;
        this.player = player;
    }

    private void update() {
        canvas.fill(ItemStackConstants.FILLED);

        canvas.box(4, 1, 7, 3, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()).build());
        canvas.container(4, 1, 7, 3, page, plugin.getCoreManager().getCores().values().stream().map(core -> (ContainerItemGetter) slot -> {
            dataContainer.put(slot, "core", core);

            return ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                    .displayName(ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하기").color(NamedTextColor.YELLOW))
                    .build();
        }).toList());

        canvas.pixel(PREVIOUS_PAGE_SLOT, ItemBuilder.of(Material.ARROW)
                .displayName(ComponentUtil.componentNonItalic("이전 페이지").color(NamedTextColor.WHITE))
                .lore(ComponentUtil.componentNonItalic("현재 페이지: " + (page+1) + "쪽").color(NamedTextColor.GRAY))
                .build());
        canvas.pixel(NEXT_PAGE_SLOT, ItemBuilder.of(Material.ARROW)
                .displayName(ComponentUtil.componentNonItalic("다음 페이지").color(NamedTextColor.WHITE))
                .lore(ComponentUtil.componentNonItalic("현재 페이지: " + (page+1) + "쪽").color(NamedTextColor.GRAY))
                .build());

        canvas.pixel(BACK_SLOT, ItemBuilder.of(Material.BARRIER)
                .displayName(ComponentUtil.componentNonItalic("뒤로").color(NamedTextColor.RED))
                .build());
    }

    @Override
    protected void onTick() {
        update();
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        int clickedSlot = event.getRawSlot();

        event.setCancelled(true);

        if(clickedSlot == PREVIOUS_PAGE_SLOT) {
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 2f);

            page = Math.max(0, page-1);
            update();
        } else if(clickedSlot == NEXT_PAGE_SLOT) {
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 2f);

            page++;
            update();
        } else if(clickedSlot == BACK_SLOT) {
            player.openInventory(new MainMenuGui(plugin, player).getInventory());
        }
    }
}
