package de.fireearth.werri.werriscoiniator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.*;

/**
 * Created by explodi on 11/6/15.
 */
public class User {
	

    public Wallet wallet;
  public WerrisRPCAppInterface werrisRPCAppInterface = null;
    private String clan;
    private Player player;
    
    public User(Player player) throws ParseException, org.json.simple.parser.ParseException, IOException {
        this.player=player;
        werrisRPCAppInterface = new WerrisRPCAppInterface(new WerrisCoiniator());
        if(WerrisCoiniator.REDIS.get("private"+this.player.getUniqueId().toString())!=null&&WerrisCoiniator.REDIS.get("address"+this.player.getUniqueId().toString())!=null) {
            this.wallet=new Wallet(WerrisCoiniator.REDIS.get("address"+this.player.getUniqueId().toString()),this.player);
        }

    }
   
    // scoreboard objectives and teams
    public ScoreboardManager scoreboardManager;
    public Scoreboard walletScoreboard;
    // Team walletScoreboardTeam = walletScoreboard.registerNewTeam("wallet");
    public Objective walletScoreboardObjective;
    public void createScoreBoard() {
        scoreboardManager = Bukkit.getScoreboardManager();
        walletScoreboard= scoreboardManager.getNewScoreboard();
        walletScoreboardObjective = walletScoreboard.registerNewObjective("wallet","dummy");

    }

    public void addExperience(int exp) {
        WerrisCoiniator.REDIS.incrBy("experience.raw."+this.player.getUniqueId().toString(),exp);
        setTotalExperience(experience());

    }
    public int experience() {
        if(WerrisCoiniator.REDIS.get("experience.raw."+this.player.getUniqueId().toString())==null) {
            return 0;
        } else {
            return Integer.parseInt(WerrisCoiniator.REDIS.get("experience.raw."+this.player.getUniqueId().toString()));
        }
    }
    public void updateScoreboard() throws ParseException, org.json.simple.parser.ParseException, IOException {
    	 
    	if (walletScoreboardObjective == null) {
            createScoreBoard();
        }
            walletScoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            walletScoreboardObjective.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Bit" + ChatColor.GRAY + ChatColor.BOLD.toString() + "Quest");
            Score score = walletScoreboardObjective.getScore(ChatColor.GREEN + "Balance:"); //Get a fake offline player
           // score.setScore((int) new WerrisRPCAppInterface(new WerrisCoiniator()).getBalance(player));
            player.setScoreboard(walletScoreboard);


    }
    public void setTotalExperience(int rawxp) {
        // xp = the square root of raw exp
        int xp = (int)Math.sqrt((double)rawxp);
        if(xp<1) xp=1;
       // System.out.println(xp);
        /*
        AUTHOR: Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
        DESC: A simple and easy to use class that can get and set a player's total experience points.
        Feel free to use this class in both public and private plugins, however if you release your
        plugin please link to this gist publicly so that others can contribute and benefit from it.
        */
        //Levels 0 through 15
        if(xp >= 0 && xp < 351) {
            //Calculate Everything
            int a = 1; int b = 6; int c = -xp;
            int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
            int remainder = xp - xpForLevel;
            int experienceNeeded = (2 * level) + 7;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);
           // System.out.println("xpForLevel: " + xpForLevel);
           // System.out.println(experience);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
            //Levels 16 through 30
        } else if(xp >= 352 && xp < 1507) {
            //Calculate Everything
            double a = 2.5; double b = -40.5; int c = -xp + 360;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (5 * level) - 38;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);
           // System.out.println("xpForLevel: " + xpForLevel);
            // System.out.println(experience);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
            //Level 31 and greater
        } else {
            //Calculate Everything
            double a = 4.5; double b = -162.5; int c = -xp + 2220;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (9 * level) - 158;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);
           // System.out.println("xpForLevel: " + xpForLevel);
           // System.out.println(experience);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
        }
        setPlayerMaxHealth();
    }
    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd.floatValue();
    }

    public void setPlayerMaxHealth() {
        int health=20+new Double(player.getLevel()/6.4).intValue();
        if(health>40) health=40;
        player.setMaxHealth(health);
    }
    public String getAddress() {
        return WerrisCoiniator.REDIS.get("address"+this.player.getUniqueId().toString());
    }

    private boolean setClan(String tag) {
        // TODO: Write user clan info
        return false;
    }
}
