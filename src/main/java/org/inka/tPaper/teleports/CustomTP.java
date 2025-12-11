package org.inka.tPaper.teleports;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.inka.tPaper.TPaper;

import java.util.logging.Logger;

public class CustomTP {

    private final TPaper plugin;
    private final Logger logger;

    public CustomTP(TPaper plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void teleportToCustom(Player player, Location location){

        player.teleport(location);

        logger.info("O jogador "
                    + player.getName()
                    + " foi teletransportado para a coordenada: "
                    + "X: "  + location.x() + "  Y: " + location.y() + "  Z: " + location.z());

        player.sendMessage(
                    Component.text("[TPaper] ", NamedTextColor.YELLOW)
                .append(Component.text("Teletransportado com sucesso " + player.getName(), NamedTextColor.WHITE))
                );
    }
}
