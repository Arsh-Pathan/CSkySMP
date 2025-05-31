package io.arsh.lifesteal;

import io.arsh.lifesteal.commands.Withdraw;
import io.arsh.lifesteal.events.Death;
import io.arsh.lifesteal.events.HeartClick;
import io.arsh.utils.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class HeartManager {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public HeartManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void initialize() {
        registerItems();
        plugin.getServer().getPluginManager().registerEvents(new Death(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new HeartClick(this), plugin);
        plugin.getCommand("withdraw").setExecutor(new Withdraw(this));
    }

    public void addHeart(Player player, int hearts) {
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + (double) hearts * 2);
    }

    public void addHeart(Player player) {
        addHeart(player, 1);
    }

    public void removeHeart(Player player, int hearts) {
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - (double) hearts * 2);
    }

    public void removeHeart(Player player) {
        removeHeart(player, 1);
    }

    public int getHearts(Player player) {
        return (int) (player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() / 2);
    }

    public boolean hasHaxHearts(Player player) {
        return getHearts(player) >= config.getInt("Life-Steal.Max-Heart");
    }

    public void dropHeart(Player player, int amount) {
        removeHeart(player, amount);
        player.getWorld().dropItemNaturally(player.getLocation(), getHeartItemStack(amount));
    }

    public void dropHeart(Player player) {
        removeHeart(player);
        player.getWorld().dropItemNaturally(player.getLocation(), getHeartItemStack(1));
    }

    public ItemStack getHeartItemStack(int amount) {
        ItemStack item = new ItemStack(Material.RED_DYE, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Color.colorize("&4Heart of Player"));
        List<String> lore = new ArrayList<>();
        lore.add(Color.colorize( "&r"));
        lore.add(Color.colorize( "&#F08080 A lifesteal item that"));
        lore.add(Color.colorize( "&#F08080 gives you a heart on"));
        lore.add(Color.colorize( "&#F08080 right click."));
        lore.add(Color.colorize( "&r"));
        lore.add(Color.colorize( "&4&lLEGENDARY ITEM"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ShapedRecipe heartRecipe() {
        ItemStack item = getHeartItemStack(1);
        NamespacedKey key = new NamespacedKey(plugin, "Heart");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("ABC", "DEF", "GHI");
        char[] alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        for(int i = 0; i < 9; ++i) {
            recipe.setIngredient(alphabet[i], Material.valueOf((String)config.get("Life-Steal.Heart-Recipe.Slot" + i)));
        }
        return recipe;
    }

    private void registerItems() {
        plugin.getServer().addRecipe(heartRecipe());
    }

}