package net.frozenorb.qlib.utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Config
        extends YamlConfiguration {
    private final String fileName;
    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Config(JavaPlugin plugin, String fileName, String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    public String getFileName() {
        return this.fileName;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public ItemStack getStackBuilder(String l) {
        List<String> lore = new ArrayList<>();
        getStringList(l + ".lore").forEach(string -> lore.add(C.c(string)));
        try {
            return ItemStackBuilder.build(Material.valueOf(getString(l + ".material")), getInt(l + ".amount"), (short)getInt(l + ".data"), getString(l + ".name"), lore);
        }catch(IllegalArgumentException e) {
            return ItemStackBuilder.build(Material.REDSTONE_BLOCK, 1, (short)0, ChatColor.RED + "ERROR!", null);
        }


    }

    private void createFile() {
        File folder = this.plugin.getDataFolder();
        try {
            File file = new File(folder, this.fileName);
            if (!file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }
            } else {
                this.load(file);
                this.save(file);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        File folder = this.plugin.getDataFolder();
        try {
            this.save(new File(folder, this.fileName));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        Config config = (Config)((Object)o);
        if (this.fileName != null ? !this.fileName.equals(config.fileName) : config.fileName != null) {
            return false;
        }
        if (this.plugin != null) {
            return this.plugin.equals((Object)config.plugin);
        }
        return config.plugin == null;
    }

    public int hashCode() {
        int result = this.fileName != null ? this.fileName.hashCode() : 0;
        result = 31 * result + (this.plugin != null ? this.plugin.hashCode() : 0);
        return result;
    }
}

