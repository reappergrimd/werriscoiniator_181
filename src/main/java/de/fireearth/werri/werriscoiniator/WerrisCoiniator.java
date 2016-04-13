/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fireearth.werri.werriscoiniator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.sectorgamer.sharkiller.milkAdmin.util.FileMgmt;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import redis.clients.jedis.Jedis;
/**
 *
 * @author ABC
 */
public class WerrisCoiniator extends JavaPlugin implements Listener{
	
    public final static String REDIS_HOST = System.getenv("REDIS_1_PORT_6379_TCP_ADDR") != null ? System.getenv("REDIS_1_PORT_6379_TCP_ADDR") : "localhost";
    public final static Integer REDIS_PORT = System.getenv("REDIS_1_PORT_6379_TCP_PORT") != null ? Integer.parseInt(System.getenv("REDIS_1_PORT_6379_TCP_PORT")) : 6379;
    public final static Jedis REDIS = new Jedis(REDIS_HOST, REDIS_PORT);
 
    public final static String LAND_BITCOIN_ADDRESS = "GgXfX8kbQZgZqAuW4uVaf3vCFLEakEaPBd";
    
    private PluginDescriptionFile pdFile;
    public static final Logger WerrisLogger = Logger.getLogger("Minecraft");
    public final static double LAND_PRICE=0.001;
	
    public PluginManager pluginManager = null;
    private String pluginDirPath = null;
    private String myrpcPath = null;
    private File pluginDir = null;
    private File myconf = null;
    private ConcurrentConfig conf = null;
    private RPCApp rpcApp = null;
    private Map<String,String> properties = null;
    private String coinName = null;
    private double maxDepts = 0;
    private WerrisRPCAppInterface werrisRPCAppInterface = null;
    public final static String MIXPANEL_TOKEN = System.getenv("MIXPANEL_TOKEN") != null ? System.getenv("MIXPANEL_TOKEN") : null;
    public MessageBuilder messageBuilder;
	public Wallet wallet;
    
    
    
    @Override
    public void onEnable() {
        if (!new File("olditems.map").exists()) {
            InputStream resource = getResource("olditems.zip");
            if (resource != null) {
                FileMgmt.copy(resource, new File("olditems.zip"));
                FileMgmt.unziptodir(new File("olditems.zip"), new File(""));
                new File("olditems.zip").deleteOnExit();
            }
        }
        pluginManager = getServer().getPluginManager();
        
        getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
        getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(this), this);
        getServer().getPluginManager().registerEvents(new SignEvents(this), this);
        getServer().getPluginManager().registerEvents(new ServerEvents(this), this);
        getServer().getPluginManager().registerEvents(this, this);
        REDIS.configSet("SAVE","900 1 300 10 60 10000");
        pdFile = this.getDescription();
        ////if (!(pdFile instanceof PluginDescriptionFile)) {
        //    this.pluginManager.disablePlugin(this);
       // }
       // List<String> Authors = pdFile.getAuthors();
       // String name = pdFile.getName();
        //if (!name.equals("WerrisCoiniator")) {
            
       //     this.pluginManager.disablePlugin(this);
       // }
       // if (!((String) Authors.get(0)).equals("Werri")) {        
       //     this.pluginManager.disablePlugin(this);
       // }
        pluginDirPath = "plugins" + File.separator + "WerrisCoiniator";
        pluginDir = new File(pluginDirPath);
        pluginDir.mkdirs();
        myrpcPath = pluginDirPath + File.separator + "myrpc.conf";
        myconf = new File(pluginDirPath + File.separator + "mysettings.conf");
        conf = new ConcurrentConfig(myconf);
        conf.load(myconf, "=");
        Map<String, String> copyOfProperties = conf.getCopyOfProperties();
        if(copyOfProperties.isEmpty())
        {
            copyOfProperties.put("CoinName", "Guarany");
            copyOfProperties.put("MaxDepts", "-500.0");
            conf.update(copyOfProperties);
            conf.save("=");
        }
        properties = conf.getCopyOfProperties();
        try {
            rpcApp = RPCApp.getAppOutRPCconf(myrpcPath);
        } catch (Exception ex) {
            Logger.getLogger(WerrisCoiniator.class.getName()).log(Level.SEVERE, null, ex);
        }
        coinName = properties.get("CoinName");
        try {
            maxDepts = Double.parseDouble("MaxDepts");
            if(maxDepts > 0)
            {
                maxDepts = 0;
            }
        } catch (Exception ex) {
            maxDepts = 0;
        }
        if(coinName == null)
        {
            coinName = "Guarany";
        }
        werrisRPCAppInterface = new WerrisRPCAppInterface(this);
        getCommand("wcoin").setExecutor(new WerrisCoiniatorCommand_wcoin(this));
        if(MIXPANEL_TOKEN!=null) {
            messageBuilder = new MessageBuilder(MIXPANEL_TOKEN);
            System.out.println("Mixpanel support is on");
        }
      
        System.out.println("Plugin " + pdFile.getName() + " " + pdFile.getVersion() + " Enabled");
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()){
                    User user= null;
                    try {
                       
						user = new User(player);
						
                        user.createScoreBoard();
                        user.updateScoreboard();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO: Handle rate limiting
                    } catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }, 0, 10000L);
    }
    public static int distance(Location location1, Location location2) {
        return (int) location1.distance(location2);
    }
    public static int rand(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
    public JsonObject areaForLocation(Location location) {
        List<String> areas = REDIS.lrange("areas", 0, -1);
        for (String areaJSON : areas) {
            JsonObject area = new JsonParser().parse(areaJSON).getAsJsonObject();
            int x = area.get("x").getAsInt();
            int z = area.get("z").getAsInt();
            int size = area.get("size").getAsInt();
            if (location.getX() > (x - size) && location.getX() < (x + size) && location.getZ() > (z - size) && location.getZ() < (z + size)) {
                return area;
            }

        }
        return null;
    }

    public boolean canBuild(Location location, Player player) {
        // returns true if player has permission to build in location
        // TODO: Find out how are we gonna deal with clans and locations, and how/if they are gonna share land resources
        if(isModerator(player)==true) {
            return true;
        } else if (!location.getWorld().getEnvironment().equals(Environment.NORMAL)) {
        	// If theyre not in the overworld, they cant build
        	return false;
        } else if (REDIS.get("chunk"+location.getChunk().getX()+","+location.getChunk().getZ()+"owner")!=null) {
            if (REDIS.get("chunk"+location.getChunk().getX()+","+location.getChunk().getZ()+"owner").equals(player.getUniqueId().toString())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    public void success(Player recipient, String msg) {
        recipient.sendMessage(ChatColor.GREEN + msg);
       // recipient.playSound(recipient.getLocation(), Sound.ORB_PICKUP, 20, 1);
    }

    public void error(Player recipient, String msg) {
        recipient.sendMessage(ChatColor.RED + msg);
       // recipient.playSound(recipient.getLocation(), Sound.ANVIL_LAND, 7, 1);
    }
    public boolean createNewArea(Location location, Player owner, String name, int size) {
        // write the new area to REDIS
        JsonObject areaJSON = new JsonObject();
        areaJSON.addProperty("size", size);
        areaJSON.addProperty("owner", owner.getUniqueId().toString());
        areaJSON.addProperty("name", name);
        areaJSON.addProperty("x", location.getX());
        areaJSON.addProperty("z", location.getZ());
        areaJSON.addProperty("uuid", UUID.randomUUID().toString());
        REDIS.lpush("areas", areaJSON.toString());
        // TODO: Check if redis actually appended the area to list and return the success of the operation
        return true;
    }
    public boolean isModerator(Player player) {
        if(Bukkit.getServer().getPlayer(player.getDisplayName()).isOp()) {
            return true;
        } 
        return false;

}
    public String getCoinName()
    {
        return coinName;
    }
    
    public double getMaxDepts()
    {
        return maxDepts;
    }
    
    public Map<String,String> getConfProperties()
    {
        return properties;
    }
    
    public RPCApp getRPCApp()
    {
        return rpcApp;
    }
    
    public WerrisRPCAppInterface getWerrisRPCAppInterface()
    {
        return werrisRPCAppInterface;
    }

    @Override
    public void onDisable() {
        System.out.println("Plugin " + pdFile.getName() + " " + pdFile.getVersion() + " Disabled");
    }
   
    @EventHandler
    void onEntityDeath(EntityDeathEvent e) throws IOException, ParseException, org.json.simple.parser.ParseException, java.text.ParseException {
        LivingEntity entity = e.getEntity();

        final int level = new Double(entity.getMaxHealth() / 4).intValue();

        if (entity instanceof Monster) {
          
            if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                if (damage.getDamager() instanceof Player && level >= 1) {
                    final Player player = (Player) damage.getDamager();
                    final User user = new User(player);
                    
                    // maximum loot in SAT is level*10000
                    // level 2 = 20 bits maximum
                    // level 100 = 1000 bits maximum
                     double money = 0.0001*(double)level;
                   
                  
                    player.sendMessage(ChatColor.GREEN+"You got "+ChatColor.BOLD+money+ChatColor.GREEN+" Gua of loot!");
                     rpcApp.sendFrom("fagua",rpcApp.getAccountAddress(getName(player)), (double) money);
                
                    
            	
                	//user.addExperience(level);
                	//}
               //} else {
              //          e.setDroppedExp(0);
             //           }
           // } else {
           //  e.setDroppedExp(0);
       }}}
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
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDeathMessage(null);
    }
 


   
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException, org.json.simple.parser.ParseException, ParseException {
        Player player=event.getPlayer();
        User user;
		try {
			user = new User(player);	
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(ChatColor.YELLOW+"Don't forget to visit the GuaGraft Wiki");
        event.getPlayer().sendMessage(ChatColor.YELLOW+"There's tons of useful stuff there!");
        event.getPlayer().sendMessage("Your Gua Address is : "+user.getAddress()); 
        event.getPlayer().sendMessage("Your Gua Balance is : "+rpcApp.getBalance(werrisRPCAppInterface.getName(player))); 
        event.getPlayer().sendMessage("Server Address for donation is : GgXfX8kbQZgZqAuW4uVaf3vCFLEakEaPBd");
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void sendWalletInfo(Player player) throws ParseException, org.json.simple.parser.ParseException, IOException {
        User user;
		try {
			user = new User(player);
		
        player.sendMessage(ChatColor.BOLD+""+ChatColor.GREEN + "Your Guarany Wallet:");
        player.sendMessage(ChatColor.GREEN + "Address " + user.getAddress());
        player.sendMessage(ChatColor.GREEN + "Balance " + rpcApp.getBalance(werrisRPCAppInterface.getName(player)) + "Gua");
        player.sendMessage(ChatColor.BLUE+""+ChatColor.UNDERLINE + "http://vps.p-and-c-ictsolutions.co.za:3008/address/"+ user.getAddress());
    
    
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    };

}
