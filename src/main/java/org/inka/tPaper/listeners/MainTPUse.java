package org.inka.tPaper.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.NamespacedKey;
import org.inka.tPaper.TPaper;
import org.inka.tPaper.teleports.MaintownTP;

public class MainTPUse implements Listener {

    private final MaintownTP mainTp;
    private final TPaper plugin;
    private final DelayedTP delayedTP;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    private final int COOLDOWN_SECONDS = 11;

    public MainTPUse(TPaper plugin, MaintownTP mainTp, DelayedTP delayedTP) {
        this.plugin = plugin;
        this.mainTp = mainTp;
        this.delayedTP = delayedTP;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        NamespacedKey key = new NamespacedKey(plugin, "main_teleport");
        if (!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {
            long expireTime = cooldowns.get(uuid);
            if (now < expireTime) {
                long secondsLeft = ((expireTime - now) / 1000) + 1;
                player.sendMessage(ChatColor.RED + "Espere " + secondsLeft + "s para usar novamente.");
                return;
            }
        }

        delayedTP.start(player).thenAccept(success -> {
            if (!success) {
                return;
            }

            cooldowns.put(uuid, System.currentTimeMillis() + COOLDOWN_SECONDS * 1000L);

            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldowns.remove(uuid);
                }
            }.runTaskLater(plugin, 20 * COOLDOWN_SECONDS);

            mainTp.teleportToMainTown(player);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        });

    }
}
