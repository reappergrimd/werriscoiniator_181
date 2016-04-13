package de.fireearth.werri.werriscoiniator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handler for the /pos sample command.
 * @author SpaceManiac
 */
public class WerrisCoiniatorCommand_wcoin implements CommandExecutor {

    private final WerrisCoiniator plugin;
    private final WerrisRPCAppInterface werrisRPCAppInterface;

    public WerrisCoiniatorCommand_wcoin(WerrisCoiniator plugin) {
        this.plugin = plugin;
        this.werrisRPCAppInterface = plugin.getWerrisRPCAppInterface();
    }

    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        
        int length = split.length;
        switch (length) {
            case 3: {
                if (split[0].equalsIgnoreCase("pay")) {
                    werrisRPCAppInterface.sendPayToPlayerMessage(player, split[1], split[2]);
                }
                
                if (split[0].equalsIgnoreCase("paymentTransfer")) {
                    werrisRPCAppInterface.sendPaymentTransferMessage(player, split[1], split[2]);
                }
            }
            break;

            case 1: {
                if (split[0].equalsIgnoreCase("balance") || split[0].equalsIgnoreCase("bank") || split[0].equalsIgnoreCase("details") || split[0].equalsIgnoreCase("info")) {
                    werrisRPCAppInterface.sendAccountDetailsMessage(player);
                }
            }
            break;

            case 0: {
                werrisRPCAppInterface.sendHelpMessage(player);
            }
            break;

            default: {
                werrisRPCAppInterface.sendHelpMessage(player);
            }
            break;
        }

        return true;
    }
}
