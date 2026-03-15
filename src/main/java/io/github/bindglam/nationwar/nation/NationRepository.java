package io.github.bindglam.nationwar.nation;

import io.github.bindglam.nationwar.NationWarPlugin;
import io.github.bindglam.nationwar.database.DatabaseManager;
import io.github.bindglam.nationwar.database.Repository;
import io.github.bindglam.nationwar.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class NationRepository implements Repository<Nation> {
    public static final String TABLE_NAME = "nations";

    private final NationWarPlugin plugin;
    private final DatabaseManager databaseManager;

    public NationRepository(NationWarPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    @Override
    public @NotNull CompletableFuture<Void> createTable() {
        return CompletableFuture.runAsync(() -> {
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(Statement statement = connection.createStatement()){
                    statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                            "nation_name TEXT PRIMARY KEY," +
                            "owner VARCHAR(36)," +
                            "members TEXT" +
                            ")");
                }
            });
        });
    }

    private @NotNull Nation load(ResultSet result) throws SQLException {
        Nation nation = new Nation(
                result.getString("nation_name"),
                UUID.fromString(result.getString("owner"))
        );

        nation.getMembers().addAll(ObjectUtil.map(result.getString("members"),
                raw -> Arrays.stream(raw.split(",")).map(UUID::fromString).collect(Collectors.toSet())));

        return nation;
    }

    @Override
    public @NotNull CompletableFuture<Optional<Nation>> load(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Nation[] nation = new Nation[] { null }; // 💀
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + TABLE_NAME + " WHERE nation_name = ?"
                )) {
                    try(ResultSet result = statement.executeQuery()) {
                        if(result.next()) {
                            nation[0] = load(result);
                        }
                    }
                }
            });
            return Optional.ofNullable(nation[0]);
        });
    }

    @Override
    public @NotNull CompletableFuture<@Unmodifiable Map<String, Nation>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Nation> nations = new HashMap<>();
            databaseManager.getSqlDatabase().getResource(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + TABLE_NAME
                )) {
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            Nation nation = load(result);
                            nations.put(nation.getName(), nation);
                        }
                    }
                }
            });
            return nations;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> save(Nation nation) {
        return CompletableFuture.runAsync(() -> {
            databaseManager.getSqlDatabase().getResource(connection -> {
                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT OR REPLACE INTO " + TABLE_NAME + " (nation_name, owner, members) VALUES (?, ?, ?)"
                )) {
                    statement.setString(1, nation.getName());
                    statement.setString(2, nation.getOwner().toString());
                    statement.setString(3, String.join(",", nation.getMembers().stream().map(UUID::toString).toList()));
                    statement.executeUpdate();
                }
            });
        });
    }
}
