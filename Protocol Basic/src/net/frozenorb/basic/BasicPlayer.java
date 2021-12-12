package net.frozenorb.basic;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qlib.utils.BukkitSerialization;
import net.frozenorb.qlib.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class BasicPlayer {

    @Getter private static HashSet<BasicPlayer> basicPlayers = new HashSet<>();

    private UUID uuid;
    private boolean soundsEnabled = true, privateMessagesEnabled = true;
    private ArrayList<UUID> ignored = new ArrayList<>();
    private Location backLocation, lastLocation;
    private ItemStack[] lastArmor, lastInv, deathArmor, deathInv;
    private UUID messagingPlayer = null;

    public BasicPlayer(UUID uuid) {
        this.uuid = uuid;
        basicPlayers.add(this);
        loadSettings();
    }


    public void loadSettings() {
        Config c = Basic.getInstance().getPlayerDataConfig();
        this.soundsEnabled = c.getBoolean("data." + this.uuid.toString() + ".sounds");
        this.privateMessagesEnabled = c.getBoolean("data." + this.uuid.toString() + ".privatemessages");
        try {
            this.lastArmor = BukkitSerialization.decodeItems(c.getString("data." + this.uuid.toString() + ".lastinv.armor"));
            this.lastInv = BukkitSerialization.decodeItems(c.getString("data." + this.uuid.toString() + ".lastinv.inventory"));
            this.deathArmor = BukkitSerialization.decodeItems(c.getString("data." + this.uuid.toString() + ".deathinv.armor"));
            this.deathInv = BukkitSerialization.decodeItems(c.getString("data." + this.uuid.toString() + ".deathinv.inventory"));
        } catch (Exception ignored) {}
        this.lastLocation = BukkitSerialization.decodeLocation(c.getString("data." + this.uuid.toString() + ".lastlocation"));
        c.getStringList("data." + this.uuid.toString() + ".ignored").forEach(s -> ignored.add(UUID.fromString(s)));

    }

    public void save() {
        Config c = Basic.getInstance().getPlayerDataConfig();
        c.set("data." + this.uuid.toString() + ".sounds", soundsEnabled);
        c.set("data." + this.uuid.toString() + ".privatemessages", privateMessagesEnabled);
        List<String> kek = new ArrayList<>();
        ignored.forEach(uuid1 -> kek.add(uuid1.toString()));
        c.set("data." + this.uuid.toString() + ".ignored", kek);
        if(Bukkit.getPlayer(this.uuid) != null) {
            c.set("data." + this.uuid.toString() + ".lastinv.armor", BukkitSerialization.encodeItems(Bukkit.getPlayer(this.uuid).getInventory().getArmorContents()));
            c.set("data." + this.uuid.toString() + ".lastinv.inventory", BukkitSerialization.encodeItems(Bukkit.getPlayer(this.uuid).getInventory().getContents()));
            c.set("data." + this.uuid.toString() + ".lastlocation", BukkitSerialization.encodeLocation(Bukkit.getPlayer(this.uuid).getLocation()));

        }else {
            c.set("data." + this.uuid.toString() + ".lastinv.armor", BukkitSerialization.encodeItems(lastArmor));
            c.set("data." + this.uuid.toString() + ".lastinv.inventory", BukkitSerialization.encodeItems(lastInv));
            c.set("data." + this.uuid.toString() + ".lastlocation", BukkitSerialization.encodeLocation(lastLocation));

        }
        if(deathInv != null && deathArmor != null) {
            c.set("data." + this.uuid.toString() + ".deathinv.armor", BukkitSerialization.encodeItems(deathArmor));
            c.set("data." + this.uuid.toString() + ".deathinv.inventory", BukkitSerialization.encodeItems(deathInv));
        }

        c.save();
        if(!Bukkit.getOfflinePlayer(this.uuid).isOnline()) basicPlayers.remove(this);
    }



    public ArrayList<String> getIgnored() {
        ArrayList<String> yes = new ArrayList<>();
        ignored.forEach(uuid1 -> yes.add(Bukkit.getOfflinePlayer(uuid1).getName()));
        return yes;
    }

    public void addIgnored(UUID uuid) {
        ignored.add(uuid);
    }

    public void removeIgnored(UUID uuid) {
        ignored.remove(uuid);
    }

    public boolean isIgnored(UUID uuid) {
        if(ignored.contains(uuid)) {
            return true;
        }
        return false;
    }

    public static BasicPlayer getBasicPlayer(UUID uuid) {
        if(Bukkit.getPlayer(uuid) != null) {
            return basicPlayers.stream().filter(basicPlayer -> basicPlayer.getUuid().toString().equals(uuid.toString())).findFirst().orElse(null);
        }
        for(String s : Basic.getInstance().getPlayerDataConfig().getConfigurationSection("data").getKeys(false)) {
            if(s.contains(uuid.toString())) {
                BasicPlayer basicPlayer = new BasicPlayer(uuid);
                basicPlayer.loadSettings();
                return basicPlayer;
            }
        }
        return null;
    }

    public static BasicPlayer getBasicPlayer(Player player) {
        return getBasicPlayer(player.getUniqueId());
    }

}
