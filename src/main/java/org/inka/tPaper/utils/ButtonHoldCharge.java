package org.inka.tPaper.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.inka.tPaper.TPaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ButtonHoldCharge {

    private final TPaper plugin;
    private final int requiredTicks;

    private final Map<UUID, Long> lastRightClick = new HashMap<>();
    private final Map<UUID, BukkitRunnable> runningTasks = new HashMap<>();

    public ButtonHoldCharge(TPaper plugin, int requiredTicks) {
        this.plugin = plugin;
        this.requiredTicks = requiredTicks;
    }

    /** Marca que o player clicou e provavelmente está segurando */
    public void markRightClick(Player player) {
        lastRightClick.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /** Inicia o carregamento segurando o botão */
    public void startCharge(Player player, ItemStack item, Runnable onFinish) {
        UUID uuid = player.getUniqueId();

        if (runningTasks.containsKey(uuid)) return;

        int slot = player.getInventory().getHeldItemSlot();
        ItemStack initialCopy = item.clone();

        player.sendMessage(ChatColor.YELLOW + "Segure o botão direito para marcar a posição (" + (requiredTicks / 20.0) + "s)...");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, 1f, 1f);

        BukkitRunnable task = new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                long last = lastRightClick.getOrDefault(uuid, 0L);
                if (System.currentTimeMillis() - last > 150) {
                    cancelCharge(player, uuid);
                    return;
                }

                if (!player.isOnline()
                        || player.getInventory().getHeldItemSlot() != slot
                        || player.getInventory().getItemInMainHand() == null
                        || !player.getInventory().getItemInMainHand().isSimilar(initialCopy)) {

                    cancelCharge(player, uuid);
                    return;
                }

                ticks++;

                // Barra de progresso
                double progress = Math.min(1.0, (double) ticks / requiredTicks);
                int filled = (int) Math.round(progress * 10);
                String bar = "§a" + "█".repeat(filled) + "§7" + "█".repeat(10 - filled);
                player.sendActionBar(Component.text("Carregando: ").append(Component.text(bar)));

                if (ticks % 20 == 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BREATH, 0.8f, 1f);
                }

                if (ticks >= requiredTicks) {
                    runningTasks.remove(uuid);
                    cancel();
                    onFinish.run();
                }
            }
        };

        runningTasks.put(uuid, task);
        task.runTaskTimer(plugin, 0L, 1L);
    }

    private void cancelCharge(Player player, UUID uuid) {
        player.sendActionBar(Component.text("Carregamento cancelado", NamedTextColor.RED));
        player.sendMessage(ChatColor.RED + "Carregamento cancelado.");
        BukkitRunnable r = runningTasks.remove(uuid);
        if (r != null) r.cancel();
    }

    /** Apaga qualquer carregamento ativo (caso use depois) */
    public void cancel(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitRunnable r = runningTasks.remove(uuid);
        if (r != null) r.cancel();
    }
}
