package io.github.bindglam.nationwar.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerUtil {
    private static final Gson GSON = new GsonBuilder().create();

    private static final Cache<UUID, String> CACHED_USERNAMES = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private PlayerUtil() {
    }

    public static @NotNull CompletableFuture<String> getUsername(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null)
            return CompletableFuture.completedFuture(player.getName());

        String cachedUsername = CACHED_USERNAMES.getIfPresent(uuid);
        if(cachedUsername != null)
            return CompletableFuture.completedFuture(cachedUsername);

        return CompletableFuture.supplyAsync(() -> {
            try(HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.minecraftservices.com/minecraft/profile/lookup/" + uuid))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if(response.statusCode() == 404 || response.statusCode() == 204)
                    return null;
                if(response.statusCode() != 200)
                    throw new RuntimeException("Mojang API error: " + response.statusCode());

                JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
                String username = json.get("name").getAsString();
                CACHED_USERNAMES.put(uuid, username);
                return username;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static @NotNull CompletableFuture<@Unmodifiable List<String>> getUsernames(@NotNull Iterable<UUID> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> usernames = new ArrayList<>();

            List<CompletableFuture<Void>> tasks = new ArrayList<>();
            for(UUID uuid : uuids) {
                Player player = Bukkit.getPlayer(uuid);
                if(player != null) {
                    usernames.add(player.getName());
                    continue;
                }

                tasks.add(CompletableFuture.runAsync(() -> usernames.add(getUsername(uuid).join())));
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

            return usernames;
        });
    }
}
