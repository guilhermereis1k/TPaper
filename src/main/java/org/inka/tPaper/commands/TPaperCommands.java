package org.inka.tPaper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.inka.tPaper.CentralTP.CustomTPItem;
import org.inka.tPaper.CentralTP.MainTPItem;
import org.inka.tPaper.TPaper;

public class TPaperCommands {

    private final MainTPItem mainTPItem;
    private final CustomTPItem customTPItem;
    private final TPaper plugin;

    public TPaperCommands(MainTPItem mainTPItem, CustomTPItem customTPItem, TPaper plugin) {
        this.mainTPItem = mainTPItem;
        this.customTPItem = customTPItem;
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createStartCommand() {
        return Commands.literal("tpaper")
                .then(Commands.literal("getMainPaper")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) {
                                ctx.getSource().getSender().sendMessage(Component.text("Apenas jogadores podem executar este comando.", NamedTextColor.RED));
                                return 0;
                            }

                            ItemStack item = mainTPItem.createItem(plugin);
                            player.getInventory().addItem(item);
                            player.sendMessage(Component.text("Item de teleporte customizado criado e enviado!", NamedTextColor.GREEN));
                            return 1;
                        }))
                .then(Commands.literal("getCustomPaper")
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) {
                                ctx.getSource().getSender().sendMessage(Component.text("Apenas jogadores podem executar este comando.", NamedTextColor.RED));
                                return 0;
                            }

                            ItemStack item = customTPItem.createItem(plugin);
                            player.getInventory().addItem(item);
                            player.sendMessage(Component.text("Item de teleporte customizado criado e enviado!", NamedTextColor.GREEN));
                            return 1;
                        }))
                .then(Commands.literal("setCoords")
                        .requires(src -> src.getSender().isOp())
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {

                                                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                                                        ctx.getSource().getSender().sendMessage(Component.text("Só jogador.", NamedTextColor.RED));
                                                        return 0;
                                                    }

                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                    double z = DoubleArgumentType.getDouble(ctx, "z");

                                                    String worldName = player.getWorld().getName();

                                                    plugin.getConfig().set("coordenades.world", worldName);
                                                    plugin.getConfig().set("coordenades.x", x);
                                                    plugin.getConfig().set("coordenades.y", y);
                                                    plugin.getConfig().set("coordenades.z", z);
                                                    plugin.saveConfig();

                                                    player.sendMessage(Component.text(
                                                            "Coordenadas da Maintown atualizadas para: "
                                                                    + "X=" + x + " Y=" + y + " Z=" + z
                                                                    + " Mundo=" + worldName,
                                                            NamedTextColor.GREEN
                                                    ));
                                                    return 1;
                                                })))));
    }
}
