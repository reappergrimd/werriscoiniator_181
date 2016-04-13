package de.fireearth.werri.werriscoiniator;

import org.bukkit.inventory.ItemStack;

/**
 * Created by cristian on 12/21/15.
 */
public class Trade {
    public double price;
    public ItemStack itemStack;
    public Trade(ItemStack itemStack,double d) {
        this.itemStack=itemStack;
        this.price=d;
    }
}
