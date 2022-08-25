package mikeshafter.iciwi.FareGates;

import mikeshafter.iciwi.Iciwi;
import mikeshafter.iciwi.config.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;


public class GateCreateListener implements Listener {
  Plugin plugin = Iciwi.getPlugin(Iciwi.class);
  Lang lang = new Lang(plugin);
  
  @EventHandler
  public void onGateCreate(SignChangeEvent event) {
    String[] lines = event.getLines();
    Player player = event.getPlayer();
  
    // Entry
    if (ChatColor.stripColor(lines[0]).contains(lang.getString("entry"))) {
      if (player.hasPermission("iciwi.create")) {
        player.sendMessage(lang.getString("create-entry-sign"));
      } else event.setCancelled(true);
    }

    // Exit
    else if (ChatColor.stripColor(lines[0]).contains(lang.getString("exit"))) {
      if (player.hasPermission("iciwi.create")) {
        player.sendMessage(lang.getString("create-exit-sign"));
      } else event.setCancelled(true);
    }

    // HL-style faregate
    else if (ChatColor.stripColor(lines[0]).contains(lang.getString("faregate"))) {
      if (player.hasPermission("iciwi.create")) {
        player.sendMessage(lang.getString("create-faregate-sign"));
      } else event.setCancelled(true);
    }

    // HL-style validator
    else if (ChatColor.stripColor(lines[0]).contains(lang.getString("validator"))) {
      if (player.hasPermission("iciwi.create")) {
        player.sendMessage(lang.getString("create-validator-sign"));
      } else event.setCancelled(true);
    }
  
  
  }
}
