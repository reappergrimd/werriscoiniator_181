/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fireearth.werri.werriscoiniator;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.sectorgamer.sharkiller.milkAdmin.util.FileMgmt;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.Bukkit;

/**
 *
 * @author ABC
 */
public class WerrisCoiniator extends JavaPlugin {

    private PluginDescriptionFile pdFile;
    public static final Logger WerrisLogger = Logger.getLogger("Minecraft");
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
        pdFile = this.getDescription();
        if (!(pdFile instanceof PluginDescriptionFile)) {
            this.pluginManager.disablePlugin(this);
        }
        List<String> Authors = pdFile.getAuthors();
        String name = pdFile.getName();
        if (!name.equals("WerrisCoiniator")) {
            System.out.println("Den Plugin hat Werri erfunden aka Inhaber von demon-craft.de @EM");
            this.pluginManager.disablePlugin(this);
        }
        if (!((String) Authors.get(0)).equals("Werri")) {
            System.out.println("Den Plugin hat Werri erfunden aka Inhaber von demon-craft.de @EM");
            this.pluginManager.disablePlugin(this);
        }
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
            copyOfProperties.put("CoinName", "Bitcraft");
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
            coinName = "Bitcraft";
        }
        werrisRPCAppInterface = new WerrisRPCAppInterface(this);
        getCommand("wcoin").setExecutor(new WerrisCoiniatorCommand_wcoin(this));
        
        //ipSperre();
        System.out.println("Plugin " + pdFile.getName() + " " + pdFile.getVersion() + " Enabled");
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

    public void ipSperre() {
        List<String> whitelistIps = new LinkedList<String>();
        whitelistIps.add("176.9.35.229");
        whitelistIps.add("176.9.35.230");
        whitelistIps.add("176.9.35.231");
        whitelistIps.add("176.9.35.232");
        whitelistIps.add("176.9.35.233");
        whitelistIps.add("176.9.35.234");
        whitelistIps.add("176.9.35.235");
        String serverIp = Bukkit.getServer().getIp();
        String[] split = null;
        if (serverIp == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (serverIp.isEmpty()) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (!serverIp.contains(".")) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        split = serverIp.split("\\.");

        if (split == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (split.length != 4) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (whitelistIps == null) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        if (whitelistIps.isEmpty()) {
            System.out.println("Du darfst den Plugin nicht haben!");
            this.pluginManager.disablePlugin(this);
            return;
        }

        for (String whitelistIp : whitelistIps) {
            if (serverIp.equals(whitelistIp)) {
                System.out.println("Du darfst den Plugin haben!");
                return;
            }
        }

        System.out.println("Ip Adresse: " + serverIp);
        System.out.println("Du darfst den Plugin nicht haben!");
        this.pluginManager.disablePlugin(this);
    }
}
