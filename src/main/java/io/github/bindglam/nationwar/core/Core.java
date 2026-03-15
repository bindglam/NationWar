package io.github.bindglam.nationwar.core;

import io.github.bindglam.nationwar.NationWar;
import io.github.bindglam.nationwar.nation.Nation;
import io.github.bindglam.nationwar.utils.Named;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class Core implements Named {
    @Getter
    private final String name;
    @Getter
    private final Location location; // 신상은 서버 시작시에 설정, 고정
    @Getter @Setter
    private int health;
    @Getter
    private final int maxHealth;
    @Getter @Setter
    private Nation ownerNation; // 신상 소유 국가
    private final Map<UUID, Integer> cooldownMap = new ConcurrentHashMap<>();

    private final ScheduledTask tickTask;

    public Core(String name, Location location, int health, int maxHealth, Nation ownerNation) {
        this.name = name;
        this.location = location;
        this.health = health;
        this.maxHealth = maxHealth;
        this.ownerNation = ownerNation;

        this.tickTask = Bukkit.getAsyncScheduler().runAtFixedRate(NationWar.get(), task -> {
            Set<UUID> forRemoval = new HashSet<>();
            cooldownMap.forEach((uuid, leftTime) -> {
                cooldownMap.put(uuid, leftTime-1);

                if(leftTime-1 <= 0)
                    forRemoval.add(uuid);
            });
            forRemoval.forEach(cooldownMap::remove);
        }, 50L, 50L, TimeUnit.MILLISECONDS);
    }

    public Core(String name, Location location, int maxHealth) {
        this(name, location, maxHealth, maxHealth, null);
    }

    public int getLeftTeleportCooldown(Player player) {
        return cooldownMap.getOrDefault(player.getUniqueId(), 0);
    }

    public boolean teleportPlayer(Player player) {
        if(cooldownMap.containsKey(player.getUniqueId()))
            return false;

        // TODO : Safe Teleport
        // TODO : Timed Teleport
        player.teleportAsync(location);

        cooldownMap.put(player.getUniqueId(), NationWar.get().config().core.teleportCooldown.value());
        return true;
    }
}
