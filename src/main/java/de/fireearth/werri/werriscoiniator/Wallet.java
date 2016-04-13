package de.fireearth.werri.werriscoiniator;
import org.bukkit.entity.Player;

/**
 * Created by cristian on 12/15/15.
 */
public class Wallet {
    public Wallet(String address,Player player) {
        this.address=address;
        this.player=player;
     
    }
    public Wallet(String address) {
        this.address=address;
    }
   
    public String address=null;
    private Player player=null;
    
    

  
}
