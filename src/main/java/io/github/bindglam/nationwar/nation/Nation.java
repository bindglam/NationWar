package io.github.bindglam.nationwar.nation;

import io.github.bindglam.nationwar.NationWar;
import io.github.bindglam.nationwar.core.Core;
import io.github.bindglam.nationwar.utils.Named;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorFlag;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class Nation implements Named {
    @Getter
    private final String name;
    @Getter
    private final UUID owner;
    @Getter
    private final Set<Core> ownedCores = new HashSet<>(); // getOwnedCores()로 직접 수정 X (데이터 로드시에만 직접 수정 가능)
    @Getter
    private final Set<UUID> members = new LinkedHashSet<>(); // getMembers()로 직접 수정 X (데이터 로드시에만 직접 수정 가능)

    public Nation(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members.add(owner);
    }

    public boolean occupyCore(Player whoOccupied, Core core) {
        if(core.getOwnerNation() != null && core.getOwnerNation().getName().equals(name))
            return false; // 이미 점령함
        if(!members.contains(whoOccupied.getUniqueId()))
            return false;

        if(core.getOwnerNation() != null) // 다른 국가가 점령했다면?
            core.getOwnerNation().deoccupyCore(core);
        core.setOwnerNation(this);
        ownedCores.add(core);

        // TODO : 디스코드 알림

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.2f);

            showOccupationTitleEffect(player,
                    Component.text("[ ").color(NamedTextColor.AQUA).append(Component.text("신상 안내").color(NamedTextColor.WHITE)).append(Component.text(" ]").color(NamedTextColor.AQUA)),
                    Component.text("<< ").color(NamedTextColor.AQUA)
                            .append(Component.text("'" + name + "'의 " + whoOccupied.getName() + "님께서 " + core.getName() + " 신상을 점령하셨습니다").color(NamedTextColor.WHITE))
                            .append(Component.text(" >>").color(NamedTextColor.AQUA))
            );
        });

        return true;
    }

    private void showOccupationTitleEffect(Player player, TextComponent title, TextComponent subTitle) {
        // Blink 두 번 반복
        for(int i = 0; i <= 2; i+=2) {
            Bukkit.getAsyncScheduler().runDelayed(NationWar.get(), task -> {
                player.showTitle(Title.title(
                        title,
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofMillis(750), Duration.ZERO)
                ));
            }, 10L*50L*i, TimeUnit.MILLISECONDS);
            Bukkit.getAsyncScheduler().runDelayed(NationWar.get(), task -> {
                player.showTitle(Title.title(
                        Component.empty(),
                        Component.empty(),
                        Title.Times.times(Duration.ZERO, Duration.ofMillis(750), Duration.ZERO)
                ));
            }, 10L*50L*(i+1), TimeUnit.MILLISECONDS);
        }
        Bukkit.getAsyncScheduler().runDelayed(NationWar.get(), task -> {
            player.showTitle(Title.title(
                    title,
                    subTitle,
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))
            ));
        }, 10L*50L*4, TimeUnit.MILLISECONDS);
    }

    public boolean deoccupyCore(Core core) {
        if(core.getOwnerNation() == null || !core.getOwnerNation().getName().equals(name))
            return false;

        core.setOwnerNation(null);
        ownedCores.remove(core);
        return true;
    }

    public boolean addMember(UUID memberUUID) {
        if(members.contains(memberUUID))
            return false;
        if(NationWar.get().getNationManager().getNations().values().stream().anyMatch(other -> other.getMembers().contains(memberUUID)))
            return false;

        // TODO : 멤버 추가 이펙트
        members.add(memberUUID);
        return true;
    }

    public boolean removeMember(UUID memberUUID) {
        if(!members.contains(memberUUID))
            return false;

        // TODO : 멤버 삭제 이펙트
        members.remove(memberUUID);
        return true;
    }

    public @Unmodifiable Set<Player> getOnlineMembers() {
        return members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
