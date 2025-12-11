package org.inka.tPaper.teleports;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.inka.tPaper.TPaper;

import java.util.logging.Logger;

public class MaintownTP {

    private final TPaper plugin;
    private final Logger logger;

    public MaintownTP(TPaper plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void teleportToMainTown(Player player){

        double x = plugin.getConfig().getDouble("coordenades.x", 0);
        double y = plugin.getConfig().getDouble("coordenades.y", 0);
        double z = plugin.getConfig().getDouble("coordenades.z", 0);
        String worldName = plugin.getConfig().getString("coordenades.world", player.getWorld().getName());

        if (x == 0 && y == 0 && z == 0) {

            player.sendMessage(Component.text(
                    "Coordenadas não configuradas! Use /tpaper setCoords para definir.",
                    NamedTextColor.RED
            ));
            return;
        }

        World w = Bukkit.getWorld(worldName);
        Location tpLoc = new Location(w, x, y, z);

        player.teleport(tpLoc);

        logger.info("O jogador "
                    + player.getName()
                    + " foi teletransportado para a coordenada: "
                    + "X: "  + x + "  Y: " + y + "  Z: " + z);

        player.sendMessage(
                    Component.text("[TPaper] ", NamedTextColor.YELLOW)
                .append(Component.text("Teletransportado com sucesso " + player.getName(), NamedTextColor.WHITE))
                );
    }
}
