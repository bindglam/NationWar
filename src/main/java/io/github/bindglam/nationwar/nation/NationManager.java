package io.github.bindglam.nationwar.nation;

import io.github.bindglam.nationwar.Context;
import io.github.bindglam.nationwar.Managerial;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class NationManager implements Managerial {
    private final Map<String, Nation> nations = new HashMap<>();

    private Context context;

    private NationRepository repository;

    @Override
    public void start(Context context) {
        this.context = context;

        repository = new NationRepository(context.plugin());
        repository.createTable().join();
        loadAll();
    }

    @Override
    public void end(Context context) {
        saveAll(false);
    }

    private void loadAll() {
        context.logger().info("국가 데이터 로드 중...");

        repository.loadAll().thenAccept(it -> {
            nations.putAll(it);

            context.logger().info("국가 데이터 로드 완료!");
        });
    }

    public void saveAll(boolean async) {
        context.logger().info("국가 데이터 저장 중... 서버를 끄지 마세요.");

        var tasks = CompletableFuture.allOf(nations.values().stream().map(repository::save).toList().toArray(new CompletableFuture[0]));
        if(async) {
            tasks.thenRun(() ->
                    context.logger().info("국가 데이터 저장 완료!"));
        } else {
            tasks.join();
            context.logger().info("국가 데이터 저장 완료!");
        }
    }

    public @Nullable Nation getNation(String name) {
        return nations.get(name);
    }

    public @Unmodifiable Map<String, Nation> getNations() {
        return Map.copyOf(nations);
    }

    public boolean registerNation(Nation nation) {
        if(nations.containsKey(nation.getName()))
            return false;
        if(nations.values().stream().anyMatch(other -> other.getMembers().contains(nation.getOwner())))
            return false;

        nations.put(nation.getName(), nation);
        return true;
    }

    public @Nullable Nation getNationByMember(UUID memberUUID) {
        return nations.values().stream().filter(nation -> nation.getMembers().contains(memberUUID)).findFirst().orElse(null);
    }
}
