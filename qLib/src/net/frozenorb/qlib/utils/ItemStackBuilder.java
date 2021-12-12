package net.frozenorb.qlib.utils;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {

    public static ItemStack build(Material item, int amount, short data, String name, List<String> lore) {
        ItemStack is = new ItemStack(item, amount, data);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if(lore != null) {
            im.setLore(lore);
        }
        is.setItemMeta(im);
        return is;
    }


}