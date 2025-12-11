package org.inka.tPaper.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.inka.tPaper.TPaper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DelayedTP {

    private final TPaper plugin;
    private final Map<UUID, CompletableFuture<Boolean>> futures = new HashMap<>();

    public DelayedTP(TPaper plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> start(Player player) {
        UUID id = player.getUniqueId();

        cancel(id);

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        futures.put(id, future);

        Location startLoc = player.getLocation().clone();
        double startHealth = player.getHealth();

        new BukkitRunnable() {
            int seconds = 3;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    future.complete(false);
                    cancel();
                    futures.remove(id);
                    return;
                }

                if (player.getLocation().distanceSquared(startLoc) > 0.01) {
                    player.sendMessage(Component.text("Teleporte cancelado: você se moveu.", NamedTextColor.RED));

                    future.complete(false);
                    cancel();
                    futures.remove(id);
                    return;
                }

                if (player.getHealth() < startHealth) {
                    player.sendMessage(Component.text("Teleporte cancelado: você se moveu.", NamedTextColor.RED));
                    future.complete(false);
                    cancel();
                    futures.remove(id);
                    return;
                }

                if (seconds <= 0) {
                    future.complete(true);
                    cancel();
                    futures.remove(id);
                    return;
                }

                player.sendMessage(Component.text("Não se mexa! Teleportando em " + seconds + "...", NamedTextColor.YELLOW));
                seconds--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return future;
    }

    public void cancel(UUID id) {
        CompletableFuture<Boolean> f = futures.remove(id);
        if (f != null && !f.isDone()) f.complete(false);
    }
}
