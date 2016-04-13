package de.fireearth.werri.werriscoiniator;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;

/**
 * Created by cristian on 12/17/15.
 */
public class SignEvents implements Listener {
	WerrisCoiniator bitQuest;
    public SignEvents(WerrisCoiniator plugin) {
        bitQuest = plugin;
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
    public void onSignChange(SignChangeEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {
    	 final  WerrisRPCAppInterface werris = null;
		final Player player = event.getPlayer();
    	// Check that the world is overworld
    	if(!event.getBlock().getWorld().getName().endsWith("_nether") && !event.getBlock().getWorld().getName().endsWith("_end")) {
    		final String specialCharacter = "^";
    		final String[] lines = event.getLines();
    		final String signText = lines[0] + lines[1] + lines[2] + lines[3];
    		Chunk chunk = event.getBlock().getWorld().getChunkAt(event.getBlock().getLocation());
    		final int x=chunk.getX();
    		final int z=chunk.getZ();

    		if (signText.length() > 0 && signText.substring(0,1).equals(specialCharacter) && signText.substring(signText.length()-1).equals(specialCharacter)) {

    			final String name = signText.substring(1,signText.length()-1);

    			if (WerrisCoiniator.REDIS.get("chunk" + x + "," + z + "owner") == null) {
    				final User user = new User(player);
    				player.sendMessage(ChatColor.YELLOW + "Claiming land...");
    				
    					
    				
    						Wallet paymentWallet;
							 	RPCApp rpcApp=bitQuest.getRPCApp();
								paymentWallet = new Wallet(WerrisCoiniator.LAND_BITCOIN_ADDRESS);
								double balance = rpcApp.getBalance(getName(player));
							
							if (balance > WerrisCoiniator.LAND_PRICE) {
								rpcApp.sendFrom(getName(player), WerrisCoiniator.LAND_BITCOIN_ADDRESS,WerrisCoiniator.LAND_PRICE);
								WerrisCoiniator.REDIS.set("chunk" + x + "," + z + "owner", player.getUniqueId().toString());
								WerrisCoiniator.REDIS.set("chunk" + x + "," + z + "name", name);
								player.sendMessage(ChatColor.GREEN + "Congratulations! You're now the owner of " + name + "!");
								
							} else {	
								
								
								if (balance < WerrisCoiniator.LAND_PRICE) {
									player.sendMessage(ChatColor.RED + "You don't have enough money! You need " + 
										ChatColor.BOLD + Math.ceil((WerrisCoiniator.LAND_PRICE-balance)) + ChatColor.RED + " more Gua.");
								} else {
									player.sendMessage(ChatColor.RED + "Claim payment failed. Please try again later.");
								}
							} 		

    			}else if (WerrisCoiniator.REDIS.get("chunk" + x + "," + z + "owner").equals(player.getUniqueId().toString())) {
					if (name.equals("abandon")) {
                        // Abandon land
                        WerrisCoiniator.REDIS.del("chunk" + x + "," + z + "owner");
                        WerrisCoiniator.REDIS.del("chunk" + x + "," + z + "name");
                    }else if (name.startsWith("transfer ") && name.length() > 9) {
                        // If the name starts with "trasnfer " and have at lest one more character,
                        // transfer land
                        final String newOwner = name.substring(9);
                        player.sendMessage(ChatColor.YELLOW+"Transfering land to " + newOwner + "...");

                        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                        scheduler.runTaskAsynchronously(bitQuest, new Runnable() {
                            
                            public void run() {
                                try {
                                    UUID newOwnerUUID = UUIDFetcher.getUUIDOf(newOwner);
                                    WerrisCoiniator.REDIS.set("chunk" + x + "," + z + "owner", newOwnerUUID.toString());
                                    player.sendMessage(ChatColor.GREEN + "This land now belongs to "+newOwner);
                                } catch (Exception e) {
                                    player.sendMessage(ChatColor.RED + "Could not get uuid of "+ newOwner);
                                }
                            }
                        });

                    }else if (WerrisCoiniator.REDIS.get("chunk" + x + "," + z + "name").equals(name)) {
    					player.sendMessage(ChatColor.RED + "You already own this land!");
    				} else {
    					// Rename land
    					player.sendMessage(ChatColor.GREEN + "You renamed this land to " + name + ".");
    					WerrisCoiniator.REDIS.set("chunk" + x + "," + z + "name", name);
    				}
    			}
    		}
        
    	} else if(event.getBlock().getWorld().getName().endsWith("_nether")) {
    		player.sendMessage(ChatColor.RED + "No claiming in the nether!");
    	} else if(event.getBlock().getWorld().getName().endsWith("_end")) {
    		player.sendMessage(ChatColor.RED + "No claiming in the end!");
    	}

    }
}

