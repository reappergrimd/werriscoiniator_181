package de.fireearth.werri.werriscoiniator;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.APICalls;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;

/**
 * Created by cristian on 11/27/15.
 */
public class InventoryEvents implements Listener {
	WerrisCoiniator bitQuest;
    ArrayList<Trade> trades;
    public static Inventory marketInventory;
    final  WerrisRPCAppInterface werris = null;
    public InventoryEvents(WerrisCoiniator plugin) {
        bitQuest = plugin;
        trades=new ArrayList<Trade>();
        trades.add(new Trade(new ItemStack(Material.DIAMOND,8),0.001));
        trades.add(new Trade(new ItemStack(Material.COOKED_BEEF,32),0.001));
        trades.add(new Trade(new ItemStack(Material.WOOL,64),0.001));
        trades.add(new Trade(new ItemStack(Material.PRISMARINE,64),0.001));
        trades.add(new Trade(new ItemStack(Material.SEA_LANTERN,64),0.001));
        trades.add(new Trade(new ItemStack(Material.QUARTZ_BLOCK,64),0.001));
        trades.add(new Trade(new ItemStack(Material.GLASS,64),0.001));
        trades.add(new Trade(new ItemStack(Material.SMOOTH_BRICK,64),0.001));
        trades.add(new Trade(new ItemStack(Material.WOOD,64),0.001));
        trades.add(new Trade(new ItemStack(Material.FENCE,64),0.001));
        trades.add(new Trade(new ItemStack(Material.COMPASS,1),0.001));
        trades.add(new Trade(new ItemStack(Material.EYE_OF_ENDER,1),0.001));
        trades.add(new Trade(new ItemStack(Material.SANDSTONE,64),0.001));
        trades.add(new Trade(new ItemStack(Material.RED_SANDSTONE,64),0.001));
        trades.add(new Trade(new ItemStack(Material.ELYTRA,1),0.001));
        trades.add(new Trade(new ItemStack(Material.EMERALD_BLOCK,64),0.001));
        trades.add(new Trade(new ItemStack(Material.DIAMOND_BLOCK,2),0.001));
        
        marketInventory = Bukkit.getServer().createInventory(null,  45, "Market");
        for (int i = 0; i < trades.size(); i++) {
            ItemStack button = new ItemStack(trades.get(i).itemStack);
            ItemMeta meta = button.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("Price: "+trades.get(i).price+" Gua");
            meta.setLore(lore);
            button.setItemMeta(meta);
            marketInventory.setItem(i, button);
        }
    }
    @EventHandler
    void onInventoryClick(final InventoryClickEvent event) throws IOException, ParseException, org.json.simple.parser.ParseException {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory();
        // Merchant inventory
        if(inventory.equals(marketInventory)) {
        	if(event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                final ItemStack clicked = event.getCurrentItem();
                if(clicked.getType()!=Material.AIR) {
                	 
                	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

                    player.sendMessage(ChatColor.YELLOW + "Purchasing " + clicked.getType() + "...");

                    player.closeInventory();
                    event.setCancelled(true);
                    scheduler.runTaskAsynchronously(bitQuest, new Runnable() {
                        @Override
                        public void run() {

                                // TODO: try/catch
                                
                        	 User user;
                             try {
                                 user = new User(player);
                                  RPCApp rpcApp=bitQuest.getRPCApp();
                                    // TODO: use the SAT amount from the Trade object
                                  double balance =  rpcApp.getBalance(getName(player));
                                 Log.info(balance);
                                 player.sendMessage("Gua Value"+balance);
  								
      							if (balance > WerrisCoiniator.LAND_PRICE) {
                                    	rpcApp.sendFrom(bitQuest.getName(player),WerrisCoiniator.LAND_BITCOIN_ADDRESS,0.01);
                                        ItemStack item = event.getCurrentItem();
                                        ItemMeta meta = item.getItemMeta();
                                        ArrayList<String> Lore = new ArrayList<String>();
                                        meta.setLore(null);
                                        item.setItemMeta(meta);
                                        player.getInventory().addItem(item);
                                        player.sendMessage(ChatColor.GREEN + "" + clicked.getType() + " purchased");
                                        if(bitQuest.messageBuilder!=null) {

                                            // Create an event
                                            org.json.JSONObject sentEvent = bitQuest.messageBuilder.event(player.getUniqueId().toString(), "Purchase", null);


                                            ClientDelivery delivery = new ClientDelivery();
                                            delivery.addMessage(sentEvent);

                                            MixpanelAPI mixpanel = new MixpanelAPI();
                                            mixpanel.deliver(delivery);
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "transaction failed");
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (org.json.simple.parser.ParseException e) {
                                    e.printStackTrace();
                                }
                        
                        }
                    });
                    
                }
        		
        	} else {
        		event.setCancelled(true);
        	}

        }
        // compass inventory
        if (inventory.getName().equals("Compass") && !player.hasMetadata("teleporting")) {
            final User bp = new User(player);

            ItemStack clicked = event.getCurrentItem();
            // teleport to other part of the world
            boolean willTeleport = false;
            if (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName() != null) {
                int x = 0;
                int z = 0;
                // TODO: Go to the actual destination selected on the inventory, not 0,0
                
                player.sendMessage(ChatColor.GREEN + "Teleporting to " + clicked.getItemMeta().getDisplayName() + "...");
                System.out.println("[teleport] " + player.getName() + " teleported to " + x + "," + z);
                player.closeInventory();

                player.setMetadata("teleporting", new FixedMetadataValue(bitQuest, true));
                Chunk c = new Location(bitQuest.getServer().getWorld("world"), x, 72, z).getChunk();
                if (!c.isLoaded()) {
                    c.load();
                }
                final int tx = x;
                final int tz = z;
                bitQuest.getServer().getScheduler().scheduleSyncDelayedTask(bitQuest, new Runnable() {

                    public void run() {
                        Location location = Bukkit
                                .getServer()
                                .getWorld("world")
                                .getHighestBlockAt(tx, tz).getLocation();
                        player.teleport(location);
                        player.removeMetadata("teleporting", bitQuest);
                    }
                }, 60L);

            }

            event.setCancelled(true);
        }
    }
    public String getName(Player player) {
        String uuid = "";
        String name = "";
        try {
            uuid = player.getUniqueId().toString();
            name = uuid;
        } catch (Throwable ex) {
            uuid = "";
            name = player.getName();
        }
        return name;
    }
    @EventHandler
    void onInteract(PlayerInteractEntityEvent event) {
        // VILLAGER
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            event.setCancelled(true);
            // compass

            // open menu
            event.getPlayer().openInventory(marketInventory);
        }

    }
}
