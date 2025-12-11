package org.inka.tPaper.CentralTP;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.inka.tPaper.TPaper;

public class MainTPItem {

    public ItemStack createItem(TPaper plugin) {
        ItemStack item = new ItemStack(Material.MOJANG_BANNER_PATTERN);
        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey(plugin, "main_teleport");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.displayName(
                Component.text("Teleporte para Cidade", NamedTextColor.GOLD)
        );

        item.setItemMeta(meta);
        return item;
    }
}
