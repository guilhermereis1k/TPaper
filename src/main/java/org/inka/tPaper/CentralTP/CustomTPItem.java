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

import java.util.List;
import java.util.UUID;

public class CustomTPItem {

    public ItemStack createItem(TPaper plugin) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();

        NamespacedKey customKey = new NamespacedKey(plugin, "custom_teleport");
        meta.getPersistentDataContainer().set(customKey, PersistentDataType.INTEGER, 1);

        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "unique_id"),
                PersistentDataType.STRING,
                UUID.randomUUID().toString()
        );


        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.lore(List.of(Component.text("Clique com o botão direito no bloco para definir local.")));
        meta.displayName(
                Component.text("Teleporte customizado", NamedTextColor.AQUA)
        );

        item.setItemMeta(meta);
        item.setAmount(1);
        return item;
    }
}
