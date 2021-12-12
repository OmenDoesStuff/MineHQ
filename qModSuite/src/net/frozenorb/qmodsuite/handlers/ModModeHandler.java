package net.frozenorb.qmodsuite.handlers;

import lombok.Getter;
import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.bridge.bukkit.ranks.Rank;
import net.frozenorb.pclientapi.PClientAPI;
import net.frozenorb.pclientapi.utils.StaffModule;
import net.frozenorb.qlib.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ModModeHandler {

    public List<Player> inModMode;
    public List<Player> vanished;
    public List<Player> hidingStaff;
    public Map<Player, ItemStack[]> modInventories;
    @Getter private String defaultPrefix;

    public ModModeHandler(){
        setDefaultPrefix("&f");
        inModMode = new ArrayList<>();
        vanished = new ArrayList<>();
        hidingStaff = new ArrayList<>();
        modInventories = new HashMap<>();
    }

    public boolean isInModMode(Player p){
        if(inModMode.contains(p)) return true;
        return false;
    }

    public boolean isVanished(Player p){
        if(vanished.contains(p)) return true;
        return false;
    }

    public void setDefaultPrefix(String prefix){
        defaultPrefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public void setModMode(Player p, Boolean bool){
        Rank r = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank();
        if(bool){
            if(!inModMode.contains(p)){
                for(Player online : Bukkit.getOnlinePlayers()){
                    p.showPlayer(online);
                }
                for(Player inmod : inModMode){
                    setTags(Arrays.asList(p), inmod, true, isVanished(inmod));
                }
                setModInventory(p, true);
                p.setAllowFlight(true);
                setVanish(p, true);
                inModMode.add(p);
                setTags(new ArrayList(Bukkit.getOnlinePlayers()), p, true, true);
                if(r.getPriority() >= 85) PClientAPI.getInstance().setStaffModule(p, StaffModule.NOCLIP, true);
                if(r.getPriority() >= 80) p.setGameMode(GameMode.CREATIVE);
                if(r.getPriority() >= 65) PClientAPI.getInstance().setStaffModule(p, StaffModule.BUNNYHOP, true);
                if(r.getPriority() >= 55) PClientAPI.getInstance().setStaffModule(p, StaffModule.XRAY, true);
                if(r.getPriority() >= 55) PClientAPI.getInstance().setStaffModule(p, StaffModule.NAMETAGS, true);
            }
        }
        else {
            if(inModMode.contains(p)){
                inModMode.remove(p);
                setModInventory(p, false);
                setVanish(p, false);
                setTags(new ArrayList(Bukkit.getOnlinePlayers()), p, false, false);
                p.setGameMode(GameMode.SURVIVAL);
                PClientAPI.getInstance().setStaffModule(p, StaffModule.BUNNYHOP, false);
                PClientAPI.getInstance().setStaffModule(p, StaffModule.XRAY, false);
                PClientAPI.getInstance().setStaffModule(p, StaffModule.NAMETAGS, false);
                PClientAPI.getInstance().setStaffModule(p, StaffModule.NOCLIP, false);
            }
        }
    }

    public void setModInventory(Player p, Boolean bool){
        if(bool){
            modInventories.put(p, p.getInventory().getContents());
            p.getInventory().clear();
            p.getInventory().setItem(0, new ItemBuilder(Material.COMPASS,1).displayName("§bCompass").build());
            p.getInventory().setItem(1, new ItemBuilder(Material.BOOK,1).displayName("§bInspection Book").build());
            if(p.getPlayer().hasPermission("worldedit.wand")){
                p.getInventory().setItem(2, new ItemBuilder(Material.WOOD_AXE,1).displayName("§bWorldEdit Wand").build());
                p.getInventory().setItem(3, new ItemBuilder(Material.CARPET,1).displayName("§7 §8").data((short)1).build());
            } else {
                p.getInventory().setItem(2, new ItemBuilder(Material.CARPET,1).displayName("§7 §8").data((short)1).build());
            }
            p.getInventory().setItem(7, new ItemBuilder(Material.SKULL_ITEM,1).displayName("§bOnline Staff").data((short) 3).build());
            p.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Visible").data((short) 8).build());
        }
        else {
            if(modInventories.containsKey(p)){
                p.getInventory().setContents(modInventories.get(p));
            }
            else {
                p.getInventory().clear();
            }
        }
    }

    public void openOnlineStaff(Player p){
        List<Player> staffs = new ArrayList<>(Bukkit.getOnlinePlayers());
        staffs.remove(p);
        for(Player filter : staffs){
            Rank r = Bridge.getProfileManager().getProfileByUUID(filter.getUniqueId()).getCurrentGrant().getRank();
            if(!r.isStaff()) staffs.remove(filter);
        }
        int rows = (int) (Math.ceil((double)staffs.size()/9.0)*9);
        if(rows == 0) rows = 9;
        Inventory inv = Bukkit.createInventory(null, rows,"Online staff");
        for(Player staff : staffs){
            Rank r = Bridge.getProfileManager().getProfileByUUID(staff.getUniqueId()).getCurrentGrant().getRank();
            inv.addItem(new ItemBuilder(Material.SKULL_ITEM, 1).data((short) 3).displayName(r.getColor() + staff.getName())
                    .lore("", "§6Mod Mode: " + (isInModMode(staff) ? "§aEnabled" : "§cDisabled"), "§6Invisible: " + (isVanished(staff) ? "§aYes" : "§cNo"), "", "§eClick to teleport.")
                    .build());

        }
        p.openInventory(inv);
    }



    public void setVanish(Player p, boolean bool) {
        if(bool){
            if(!vanished.contains(p)) vanished.add(p);
            setTags(new ArrayList(Bukkit.getOnlinePlayers()), p, isInModMode(p), true);
            for(Player online : Bukkit.getOnlinePlayers()){
                if(!isInModMode(online)){
                    online.hidePlayer(p);
                } else if(hidingStaff.contains(online)) {
                    online.hidePlayer(p);
                }
            }
        } else {
            if(vanished.contains(p)) vanished.remove(p);
            setTags(new ArrayList(Bukkit.getOnlinePlayers()), p, isInModMode(p), false);
            for(Player online : Bukkit.getOnlinePlayers()){
                online.showPlayer(p);
            }
        }
    }

    public void setTags(List<Player> players, Player p, Boolean modmode, Boolean vanished){
        List<String> tags = new ArrayList<>();
        String tag1 = defaultPrefix + p.getName();
        if(vanished){
            tag1 = ChatColor.GRAY + "*" + defaultPrefix + p.getName();
        }
        tags.add(tag1);
        if(modmode){
            tags.add(ChatColor.GRAY + "[Mod Mode]");
        }
        if(!vanished && !modmode){
            PClientAPI.getInstance().updateNametag(p, players, null);
            return;
        }
        PClientAPI.getInstance().updateNametag(p, players, tags);
    }

    public boolean switchModMode(Player p){
        setModMode(p, !inModMode.contains(p));
        return inModMode.contains(p);
    }

}
