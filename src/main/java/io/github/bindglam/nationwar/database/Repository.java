package io.github.bindglam.nationwar.database;

import io.github.bindglam.nationwar.utils.Named;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<T extends Named> {
    @NotNull CompletableFuture<Void> createTable();

    @NotNull CompletableFuture<Optional<T>> load(String name);

    @NotNull CompletableFuture<@Unmodifiable Map<String, T>> loadAll();

    @NotNull CompletableFuture<Void> save(T object);
}
