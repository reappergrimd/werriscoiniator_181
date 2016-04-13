package de.fireearth.werri.werriscoiniator;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by explodi on 11/1/15.
 * Edited by Xeyler on 11/2/15
 */
public class BlockEvents implements Listener {
	
	WerrisCoiniator bitQuest;
	
	public BlockEvents(WerrisCoiniator plugin) {
		
		bitQuest = plugin;
		
	}
	
	@EventHandler
	void onBlockCatchFire(BlockIgniteEvent event) {
		if(event.getCause().equals(IgniteCause.FLINT_AND_STEEL)) {
			if(event.getPlayer() != null) {
				if (!bitQuest.canBuild(event.getBlock().getLocation(), event.getPlayer())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to do that!");
				}
			}
		} else if(event.getCause().equals(IgniteCause.SPREAD)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}
	
    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
    	// If block is bedrock, cancel the event
    	if(event.getBlock().getType().equals(Material.BEDROCK)) {
    		bitQuest.error(event.getPlayer(), "Removing bedrock is not allowed!");
    		event.setCancelled(true);
    	// If player is in a no-build zone, cancel the event
    	} else if (bitQuest.canBuild(event.getBlock().getLocation(), event.getPlayer()) == false) {
			event.setCancelled(true);
			bitQuest.error(event.getPlayer(), "You may not break blocks here!");
        } else {
			event.setCancelled(false);
		}
    }
	@EventHandler
	void onBlockPlace(BlockPlaceEvent event) {
		// set clan
		// first, we check if the player has permission to build
			if (bitQuest.canBuild(event.getBlock().getLocation(), event.getPlayer()) == false) {
				event.setCancelled(true);
            	bitQuest.error(event.getPlayer(), "You may not place blocks here!");
			} else if (event.getBlock().getType() == Material.STANDING_BANNER) {

				if (bitQuest.areaForLocation(event.getBlock().getLocation()) != null) {
					// Banner banner=(Banner)e.getBlock();
					Block belowBlock = event.getBlock().getRelative(0, -1, 0);
					Sign sign = null;
					if (belowBlock.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
						sign = (Sign) belowBlock.getRelative(BlockFace.EAST).getState();
					}
					if (belowBlock.getRelative(BlockFace.WEST).getType().equals(Material.WALL_SIGN)) {
						sign = (Sign) belowBlock.getRelative(BlockFace.WEST).getState();
					}
					if (belowBlock.getRelative(BlockFace.NORTH).getType().equals(Material.WALL_SIGN)) {
						sign = (Sign) belowBlock.getRelative(BlockFace.NORTH).getState();
					}
					if (belowBlock.getRelative(BlockFace.SOUTH).getType().equals(Material.WALL_SIGN)) {
						sign = (Sign) belowBlock.getRelative(BlockFace.SOUTH).getState();
					}
					if (sign != null) {
						String tag = sign.getLine(0).toLowerCase();
						if (tag.length() > 12) {
							bitQuest.error(event.getPlayer(), "Clan names are limited to a maximum of 12 characters.");
						} else if (tag.length() > 0) {
							// TODO: Find out if the clan name already exists and check player is invited
							// TODO: Add a clan property to area key
							// TODO: Add a clan property to user data
						} else {
							bitQuest.error(event.getPlayer(), "Please write the name of the clan on the first line of the sign.");
						}


					}
				} else {
					bitQuest.error(event.getPlayer(), "You can only place banners in your home land.");
					event.setCancelled(true);
				}
			} else if(event.getBlock().getType().equals(Material.BEDROCK)) {
				bitQuest.error(event.getPlayer(), "Placing bedrock is not allowed!");
    			event.setCancelled(true);
			} else {
				event.setCancelled(false);
			}

        
	}

    @EventHandler
    void onPistonExtends(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        List<Block> blocks = event.getBlocks();
        BlockFace direction = event.getDirection();

        if (!blocks.isEmpty()) {
            Block lastBlock = blocks.get(blocks.size() - 1);
            Block nextBlock = lastBlock.getRelative(direction);

            Chunk pistonChunk = piston.getChunk();
            Chunk blockChunk = nextBlock.getChunk();

            String owner1, owner2;
            if ((owner2 = bitQuest.REDIS.get("chunk" + blockChunk.getX() + "," + blockChunk.getZ() + "owner")) != null) {
                if ((owner1 = bitQuest.REDIS.get("chunk" + pistonChunk.getX() + "," + pistonChunk.getZ() + "owner")) != null) {
                    if (!owner1.equals(owner2)){
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    void onPistonRetract(BlockPistonRetractEvent event) {
        Block piston = event.getBlock();
        BlockFace direction = event.getDirection();
        Block nextBlock = piston.getRelative(direction, -2); // Direction is inverted?

        if (event.isSticky()) {
            Chunk pistonChunk = piston.getChunk();
            Chunk blockChunk = nextBlock.getChunk();

            String owner1, owner2;
            if ((owner2 = bitQuest.REDIS.get("chunk" + blockChunk.getX() + "," + blockChunk.getZ() + "owner")) != null) {
                if ((owner1 = bitQuest.REDIS.get("chunk" + pistonChunk.getX() + "," + pistonChunk.getZ() + "owner")) != null) {
                    if (!owner1.equals(owner2)){
                        event.setCancelled(true);
                        piston.getRelative(event.getDirection()).setType(Material.AIR);
                    }
                } else {
                    event.setCancelled(true);
                    piston.getRelative(event.getDirection()).setType(Material.AIR);
                }
            }
        }
    }

}
