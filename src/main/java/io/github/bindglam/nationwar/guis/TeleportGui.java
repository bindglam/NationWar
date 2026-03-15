package io.github.bindglam.nationwar.guis;

import io.github.bindglam.nationwar.NationWarPlugin;
import io.github.bindglam.nationwar.core.Core;
import io.github.bindglam.nationwar.guis.drawing.ContainerItemGetter;
import io.github.bindglam.nationwar.nation.Nation;
import io.github.bindglam.nationwar.utils.ComponentUtil;
import io.github.bindglam.nationwar.utils.InventoryUtil;
import io.github.bindglam.nationwar.utils.ItemBuilder;
import io.github.bindglam.nationwar.utils.constants.ItemStackConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class TeleportGui extends InventoryGui {
    private static final int PREVIOUS_PAGE_SLOT = InventoryUtil.toSlotIndex(4, 4);
    private static final int NEXT_PAGE_SLOT = InventoryUtil.toSlotIndex(7, 4);
    private static final int BACK_SLOT = InventoryUtil.toSlotIndex(8, 5);

    private static final String SLOT_DATA_CORE_KEY = "core";
    private static final String SLOT_DATA_OCCUPIED_KEY = "occupied";

    private final NationWarPlugin plugin;
    private final Player player;
    private final Nation nation;

    private int page = 0;

    public TeleportGui(NationWarPlugin plugin, Player player) {
        super(plugin, 9*6, Component.text("TELEPORT"), 10);
        this.plugin = plugin;
        this.player = player;
        this.nation = plugin.getNationManager().getNationByMember(player.getUniqueId());
    }

    private void update() {
        canvas.fill(ItemStackConstants.FILLED);

        canvas.box(4, 1, 7, 3, ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.empty()).build());
        canvas.container(4, 1, 7, 3, page, plugin.getCoreManager().getCores().values().stream().map(core -> (ContainerItemGetter) slot -> {
            boolean occupied = core.getOwnerNation() != null && core.getOwnerNation().getName().equals(nation.getName());

            dataContainer.put(slot, SLOT_DATA_CORE_KEY, core);
            dataContainer.put(slot, SLOT_DATA_OCCUPIED_KEY, occupied);

            ItemBuilder item;

            if(occupied) {
                int leftTeleportCooldown = core.getLeftTeleportCooldown(player);

                if(leftTeleportCooldown <= 0) {
                    item = ItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                            .displayName(ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하기").color(NamedTextColor.YELLOW))
                            .lore(
                                    ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하려면 클릭하세요.").color(NamedTextColor.GRAY),
                                    Component.empty()
                            );
                } else {
                    item = ItemBuilder.of(Material.YELLOW_STAINED_GLASS_PANE)
                            .displayName(ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하기").color(NamedTextColor.YELLOW))
                            .lore(
                                    ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하려면 남은 쿨타임을 기다려주세요.").color(NamedTextColor.GRAY),
                                    Component.empty(),
                                    ComponentUtil.componentNonItalic("남은 쿨타임: " + (leftTeleportCooldown/20/60 > 0 ? leftTeleportCooldown/20/60 + "분 " : "") + (leftTeleportCooldown/20%60) + "초")
                                            .color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD),
                                    Component.empty()
                            );
                }
            } else {
                item = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                        .displayName(ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하기").color(NamedTextColor.YELLOW))
                        .lore(
                                ComponentUtil.componentNonItalic(core.getName() + "(으)로 이동하기 위해선 " + core.getName() + " 신상을 점령해주세요.").color(NamedTextColor.GRAY),
                                Component.empty()
                        );
            }
            item.addLore(
                    ComponentUtil.componentNonItalic("위치: " + String.format("%.1f", core.getLocation().getX()) + " " + String.format("%.1f", core.getLocation().getY()) + " " + String.format("%.1f", core.getLocation().getZ()))
                            .color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD)
            );

            return item.build();
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
        } else {
            Core core = (Core) dataContainer.get(clickedSlot, SLOT_DATA_CORE_KEY);
            if(core != null && dataContainer.get(clickedSlot, SLOT_DATA_OCCUPIED_KEY) == Boolean.TRUE) {
                if(core.teleportPlayer(player)) {
                    player.closeInventory();
                }
            }
        }
    }
}
