package de.fireearth.werri.werriscoiniator;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.parser.ParseException;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;

/**
 * Created by cristian on 3/20/16.
 */
public class ServerEvents implements Listener {
	WerrisCoiniator bitQuest;

    public ServerEvents(WerrisCoiniator plugin) {

        bitQuest = plugin;
       
    }
    @EventHandler
    public void onServerListPing(ServerListPingEvent event)
    {

        event.setMotd(ChatColor.GOLD + ChatColor.BOLD.toString() + "Gua" + ChatColor.GRAY + ChatColor.BOLD.toString() + "Quest"+ChatColor.RESET+" - The server that runs on Guarany ");
    }
    
   
}
