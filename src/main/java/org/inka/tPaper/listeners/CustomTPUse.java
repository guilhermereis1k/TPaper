package org.inka.tPaper.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitRunnable;
import org.inka.tPaper.TPaper;
import org.inka.tPaper.teleports.CustomTP;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomTPUse implements Listener {

    private final TPaper plugin;
    private final CustomTP customTP;
    private final DelayedTP delayedTP;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    private final int MAX_USES = 3;
    private final int COOLDOWN_SECONDS = 11;

    public CustomTPUse(TPaper plugin, CustomTP customTP, DelayedTP delayedTP) {
        this.plugin = plugin;
        this.customTP = customTP;
        this.delayedTP = delayedTP;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        var pdc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey customKey = new NamespacedKey(plugin, "custom_teleport");
        if (!pdc.has(customKey, PersistentDataType.INTEGER)) return;

        NamespacedKey definedKey = new NamespacedKey(plugin, "tp_defined");
        boolean isDefined = pdc.getOrDefault(definedKey, PersistentDataType.BOOLEAN, false);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (isDefined && cooldowns.containsKey(uuid)) {
            long expireTime = cooldowns.get(uuid);
            if (now < expireTime) {
                long secondsLeft = ((expireTime - now) / 1000);
                player.sendMessage(ChatColor.RED + "Espere " + secondsLeft + "s para usar novamente.");
                return;
            }
        }

        if (isDefined) {
            teleportPlayer(player, item);
        } else {
            defineLocation(player, item, event);
        }

    }

    private void defineLocation(Player player, ItemStack item, PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            player.sendMessage(Component.text("Você deve clicar em um bloco!", NamedTextColor.RED));
            return;
        }

        if (event.getBlockFace() != BlockFace.UP) {
            player.sendMessage(Component.text("Clique no topo do bloco para definir o TP.", NamedTextColor.RED));
            return;
        }

        var feet = clicked.getRelative(BlockFace.UP);
        var head = feet.getRelative(BlockFace.UP);
        if (!feet.isEmpty() || !head.isEmpty()) {
            player.sendMessage(Component.text("Não há espaço para te teleportar aqui.", NamedTextColor.RED));
            return;
        }

        double x = feet.getX() + 0.5;
        double y = feet.getY();
        double z = feet.getZ() + 0.5;

        ItemMeta meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();
        NamespacedKey keyX = new NamespacedKey(plugin, "tp_x");
        NamespacedKey keyY = new NamespacedKey(plugin, "tp_y");
        NamespacedKey keyZ = new NamespacedKey(plugin, "tp_z");
        NamespacedKey usesKey = new NamespacedKey(plugin, "tp_uses_remaining");
        NamespacedKey definedKey = new NamespacedKey(plugin, "tp_defined");

        pdc.set(keyX, PersistentDataType.DOUBLE, x);
        pdc.set(keyY, PersistentDataType.DOUBLE, y);
        pdc.set(keyZ, PersistentDataType.DOUBLE, z);
        pdc.set(usesKey, PersistentDataType.INTEGER, MAX_USES);
        pdc.set(definedKey, PersistentDataType.BOOLEAN, true);

        meta.lore(List.of(
                Component.text("Local definido:", NamedTextColor.GREEN),
                Component.text("XYZ: " + (int) x + ", " + (int) y + ", " + (int) z, NamedTextColor.GRAY),
                Component.text("Usos: " + MAX_USES, NamedTextColor.GOLD)
        ));
        item.setItemMeta(meta);

        player.sendMessage(Component.text("Local de teleporte registrado no item!", NamedTextColor.GREEN));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
    }

    private void teleportPlayer(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        NamespacedKey usesKey = new NamespacedKey(plugin, "tp_uses_remaining");
        NamespacedKey keyX = new NamespacedKey(plugin, "tp_x");
        NamespacedKey keyY = new NamespacedKey(plugin, "tp_y");
        NamespacedKey keyZ = new NamespacedKey(plugin, "tp_z");

        int remaining = pdc.getOrDefault(usesKey, PersistentDataType.INTEGER, MAX_USES);

        if (remaining <= 0) {
            player.getInventory().remove(item);
            player.sendMessage(Component.text("O item não tem mais usos.", NamedTextColor.RED));
            return;
        }

        double x = pdc.get(keyX, PersistentDataType.DOUBLE);
        double y = pdc.get(keyY, PersistentDataType.DOUBLE);
        double z = pdc.get(keyZ, PersistentDataType.DOUBLE);

        Location tpLoc = new Location(player.getWorld(), x, y, z);

        delayedTP.start(player).thenAccept(success -> {
            if (!success) return;

            customTP.teleportToCustom(player, tpLoc);

            int newRemaining = remaining - 1;

            if (newRemaining <= 0) {
                player.getInventory().remove(item);
                player.sendMessage(Component.text("O item foi consumido.", NamedTextColor.RED));
                return;
            }

            pdc.set(usesKey, PersistentDataType.INTEGER, newRemaining);

            meta.lore(List.of(
                    Component.text("XYZ: " + (int) x + ", " + (int) y + ", " + (int) z, NamedTextColor.GRAY),
                    Component.text("Usos restantes: " + newRemaining, NamedTextColor.GOLD)
            ));
            item.setItemMeta(meta);

            player.sendMessage(Component.text("Teleportado! Usos restantes: " + newRemaining, NamedTextColor.GREEN));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        });
    }
}
