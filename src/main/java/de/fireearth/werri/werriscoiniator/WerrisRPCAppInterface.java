/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fireearth.werri.werriscoiniator;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RpcInvalidResponseException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author ABC
 */
public class WerrisRPCAppInterface {

    private final WerrisCoiniator plugin;
    private final RPCApp rpcApp;
    private Map<String, String> properties;
    private String coinName;
    private double maxDepts;

    public WerrisRPCAppInterface(WerrisCoiniator plugin) {
        this.plugin = plugin;
        this.rpcApp = plugin.getRPCApp();
        this.properties = plugin.getConfProperties();
        this.coinName = plugin.getCoinName();
        this.maxDepts = plugin.getMaxDepts();
    }

    public WerrisCoiniator getPlugin() {
        return plugin;
    }

    public String checkAccount(String name) {
        boolean isAccountInList = false;
        try {
            isAccountInList = rpcApp.isAccountInList(name);
        } catch (Exception ex) {
            Logger.getLogger(WerrisCoiniatorCommand_wcoin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!isAccountInList) {
            try {
                String account = rpcApp.getNewAddress(name);
                return account;
            } catch (Exception ex) {
                Logger.getLogger(WerrisCoiniatorCommand_wcoin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    public double getBalance(String name) {
        checkAccount(name);
        double balance = 0;
        try {
            balance = rpcApp.getBalance(name);
            return balance;
        } catch (Exception ex) {
            Logger.getLogger(WerrisCoiniatorCommand_wcoin.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public double getBalance(Player player) {
        return getBalance(getName(player));
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
    
    public String getName(OfflinePlayer player) {
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
    
    public Player getPlayer(String name)
    {
        UUID fromString = null;
        Player player = null;
        try
        {
            fromString=UUID.fromString(name);
            player = Bukkit.getPlayer(fromString);
            if(player==null)
            {
                throw new NullPointerException();
            }
            return player;
        } catch (Throwable ex) {
            try {
                player = Bukkit.getPlayer(name);
                return player;
            } catch (Throwable ex2) {
                return null;
            } 
        }
    }
    
    public OfflinePlayer getOfflinePlayer(String name)
    {
        UUID fromString = null;
        OfflinePlayer player = null;
        try
        {
            fromString=UUID.fromString(name);
            player = Bukkit.getOfflinePlayer(fromString);
            if(player==null)
            {
                throw new NullPointerException();
            }
            return player;
        } catch (Throwable ex) {
            try {
                player = Bukkit.getOfflinePlayer(name);
                return player;
            } catch (Throwable ex2) {
                return null;
            } 
        }
    }

    public boolean hasAccountToManyDepts(String accountName) {
        double balance = getBalance(accountName);
        return balance < maxDepts;
    }

    public boolean hasAccountToManyDepts(Player player) {
        return hasAccountToManyDepts(getName(player));
    }

    public boolean hasEnoughBalanceForTransferOf(String accountName, double amount) {
        if (amount <= 0) {
            return false;
        }
        double balance = getBalance(accountName);

        if ((balance - amount) < maxDepts) {
            return false;
        }

        return true;
    }

    public boolean hasEnoughBalanceForTransferOf(Player player, double amount) {
        return hasEnoughBalanceForTransferOf(getName(player), amount);
    }

    public double getSendableAmount(double amount) {
        if (amount < 0) {
            amount = 0;
        }
        return amount;
    }

    public void sendBalanceMessage(Player player) {
        checkAccount(getName(player));
        player.sendMessage("Your Balance: " + getBalance(player) + " " + coinName);
    }

    public void payToPlayer(String accountName, String anotherPlayer, double amountD) {
        checkAccount(accountName);
        if (amountD <= 0) {
            return;
        }
        if (!hasEnoughBalanceForTransferOf(accountName, amountD)) {
            return;
        }
        checkAccount(anotherPlayer);
        try {
            boolean move = rpcApp.move(accountName, anotherPlayer, amountD);
            if (move) {
                return;
            } else {
                return;
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void depositPlayer(String fromAccountName, String accountName, double amountD) {
        checkAccount(accountName);
        checkAccount(fromAccountName);
        if (amountD <= 0) {
            return;
        }
        try {
            boolean move = rpcApp.move(fromAccountName, accountName, amountD);
            if (move) {
                return;
            } else {
                return;
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void withdrawPlayer(String fromAccountName, String accountName, double amountD) {
        checkAccount(accountName);
        checkAccount(fromAccountName);
        if (amountD <= 0) {
            return;
        }
        try {
            boolean move = rpcApp.move(fromAccountName, accountName, amountD);
            if (move) {
                return;
            } else {
                return;
            }
        } catch (Exception ex) {
            return;
        }
    }

    public void paymentTransfer(String accountName, String address, double amountD) {
        checkAccount(accountName);
        if (amountD <= 0) {
            return;
        }
        if (!hasEnoughBalanceForTransferOf(accountName, amountD)) {
            return;
        }
        try {
            rpcApp.sendFrom(accountName, address, amountD);
            return;
        } catch (Exception ex) {
            if (ex instanceof RpcInvalidResponseException) {
                return;
            }
        }
    }

    public void sendPayToPlayerMessage(Player player, String anotherPlayer, String amount) {
        checkAccount(getName(player));
        double amountD = 0;
        try {
            amountD = Double.parseDouble(amount);
            amountD = getSendableAmount(amountD);
        } catch (Exception ex) {
            amountD = 0;
        }
        if (amountD == 0) {
            player.sendMessage("The amount must be bigger than 0!");
            return;
        }
        if (!hasEnoughBalanceForTransferOf(getName(player), amountD)) {
            player.sendMessage("You don't have enough balance for this transfer!!");
            return;
        }
        String name = getName(player);
        Object player1 = getPlayer(anotherPlayer);
        if (!(player1 instanceof Player)) {
            player1 = getOfflinePlayer(anotherPlayer);
            if (!(player1 instanceof OfflinePlayer)) {
                player.sendMessage("The player " + anotherPlayer + " not exists!");
                return;
            } else {
                anotherPlayer = getName((OfflinePlayer)player1);
                checkAccount(anotherPlayer);
            }
        } else {
            anotherPlayer = getName((Player)player1);
            checkAccount(anotherPlayer);
        }
        try {
            boolean move = rpcApp.move(name, anotherPlayer, amountD);
            if (move) {
                player.sendMessage("You have payed " + amount + " " + coinName + " to " + anotherPlayer + "!");
                sendBalanceMessage(player);
            } else {
                player.sendMessage("Transaction error!");
                player.sendMessage("You don't have payed " + amount + " " + coinName + " to " + anotherPlayer + "!");
                sendBalanceMessage(player);
            }
        } catch (Exception ex) {
            Logger.getLogger(WerrisRPCAppInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPaymentAddress(String name) {
        String checkAccount = checkAccount(name);
        if (checkAccount.isEmpty()) {
            try {
                checkAccount = rpcApp.getAccountAddress(name);
            } catch (Exception ex) {
                Logger.getLogger(WerrisRPCAppInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return checkAccount;
    }

    public void sendPaymentTransferMessage(Player player, String address, String amount) {
        String name = getName(player);
        checkAccount(name);
        double amountD = 0;
        try {
            amountD = Double.parseDouble(amount);
            amountD = getSendableAmount(amountD);
        } catch (Exception ex) {
            amountD = 0;
        };
        if (amountD == 0) {
            player.sendMessage("The amount must be bigger than 0!");
            return;
        }
        if (!hasEnoughBalanceForTransferOf(player, amountD)) {
            player.sendMessage("You don't have enough balance for this transfer!!");
            return;
        }
        try {
            rpcApp.sendFrom(name, address, amountD);
            player.sendMessage("You have transfered " + amount + " " + coinName + " to " + address + "!");
            sendBalanceMessage(player);
        } catch (Exception ex) {
            if (ex instanceof RpcInvalidResponseException) {
                player.sendMessage("Transaction error!");
                player.sendMessage("You don't have tranfered " + amount + " " + coinName + " to " + address + "!");
                sendBalanceMessage(player);
                return;
            }
            Logger.getLogger(WerrisRPCAppInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendAccountDetailsMessage(Player player) {
        player.sendMessage("---WerrisCoiniator Bank Details---");
        String paymentAddress = getPaymentAddress(getName(player));
        player.sendMessage("Your account address:" + paymentAddress);
        sendBalanceMessage(player);
        player.sendMessage("----------------------------------");
    }

    public void sendHelpMessage(Player player) {
        player.sendMessage("---WerrisCoiniator----v 0.1 beta--");
        player.sendMessage("/wcoin pay <playerName> <amount> - pay to <playerName> a certain amount of coins!");
        player.sendMessage("/wcoin paymentTransfer <account address> <amount> - pay to <account address> a certain amount of coins!");
        player.sendMessage("/wcoin balance - get details of your account!");
        player.sendMessage("----------------------------------");
    }
}
